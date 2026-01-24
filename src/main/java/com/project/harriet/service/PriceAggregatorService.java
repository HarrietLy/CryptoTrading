package com.project.harriet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.harriet.constant.AppConstant;
import com.project.harriet.dto.AggregatedPriceDTO;
import com.project.harriet.dto.ticker.BinancePriceDTO;
import com.project.harriet.dto.ticker.HuobiPriceDTO;
import com.project.harriet.dto.ticker.HuobiPriceWrapper;
import com.project.harriet.model.AggregatedPrice;
import com.project.harriet.repository.AggregatedPriceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
public class PriceAggregatorService {

    private final ObjectMapper objectMapper;
    @Value("${third-party.binance.price_url}")
    private String binancePriceUrl;

    @Value("${third-party.huobi.price_url}")
    private String huobiPriceUrl;

    @Value("${third-party.price.timeoutsec}")
    private int priceTimeoutSec;

    private final RestTemplate restTemplate = new RestTemplate();
    private final AggregatedPriceRepository aggregatedPriceRepository;

    private static final Logger logger = LoggerFactory.getLogger(PriceAggregatorService.class);

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public PriceAggregatorService(AggregatedPriceRepository aggregatedPriceRepository, ObjectMapper objectMapper) {
        this.aggregatedPriceRepository = aggregatedPriceRepository;
        this.objectMapper = objectMapper;
    }

    private static final String[] assetList = {"ETH", "BTC"};

    @Scheduled(fixedRate = 10000)
    void fetchBestPrice() throws ExecutionException, InterruptedException, TimeoutException {

        logger.info("start scheduled price fetching, fetching BinancePrice and Huobi price concurrently");
        CompletableFuture<Map<String, Double[]>> binancePriceFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return fetchBinancePrice();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, executorService);

        CompletableFuture<Map<String, Double[]>> huobiPriceFuture = CompletableFuture.supplyAsync(() -> fetchHuobiPrice(), executorService);
        //wait for both threads to finish, set time out
        CompletableFuture.allOf(binancePriceFuture, huobiPriceFuture).get(priceTimeoutSec, TimeUnit.SECONDS);

        Map<String, Double[]> binancePrice = binancePriceFuture.getNow(Collections.emptyMap());
        Map<String, Double[]> huobiPrice = huobiPriceFuture.getNow(Collections.emptyMap());

        //get highest bid price and lowest ask price
        List<AggregatedPrice> aggregatedPriceEntities = aggregateBestPrice(binancePrice, huobiPrice);
        validatePrice(aggregatedPriceEntities);

        logger.info("finished price fetching, updating aggregated price in database");
        aggregatedPriceEntities.forEach(aggregatedPrice -> {
            AggregatedPrice priceEntity = aggregatedPriceRepository.findByAsset(aggregatedPrice.getAsset()).orElseThrow(() -> new NoSuchElementException("price not found for given asset "));
            Long entityId =priceEntity.getId();
            BeanUtils.copyProperties(aggregatedPrice, priceEntity);
            priceEntity.setId(entityId);
            aggregatedPriceRepository.save(priceEntity);
        });

    }

    private void validatePrice(List<AggregatedPrice> aggregatedPriceEntities) {
        aggregatedPriceEntities.forEach(aggregatedPrice -> {
            if (aggregatedPrice.getAskPrice() < AppConstant.PRICE_MIN || aggregatedPrice.getBidPrice() < AppConstant.PRICE_MIN) {
                throw new RuntimeException("Price must be greater than or equal to price min");
            }
        });
    }

    private List<AggregatedPrice> aggregateBestPrice(Map<String, Double[]> binancePrice, Map<String, Double[]> huobiPrice) {
        List<AggregatedPrice> aggregatedPriceList = new ArrayList<>();
        Double binanceBid = 0d;
        Double binanceAsk = 0d;
        Double huobiBid = 0d;
        Double huobiAsk = 0d;
        for (String asset : this.assetList) {
            AggregatedPrice aggregatedPrice = new AggregatedPrice();
            aggregatedPrice.setAsset(asset);
            if (binancePrice.containsKey(asset) && binancePrice.get(asset) != null && binancePrice.get(asset).length > 1) {
                binanceBid = binancePrice.get(asset)[0];
                binanceAsk = binancePrice.get(asset)[1];
            }
            if (huobiPrice.containsKey(asset) && huobiPrice.get(asset) != null && huobiPrice.get(asset).length > 1) {
                huobiBid = huobiPrice.get(asset)[0];
                huobiAsk = huobiPrice.get(asset)[1];
            }
            // the higher the bid price, the higher the user can get for selling their asset, hence the better
            aggregatedPrice.setBidPrice(binanceBid >= huobiBid ? binanceBid : huobiBid);
            aggregatedPrice.setBidPriceSource(binanceBid >= huobiBid ? AppConstant.PriceDataSource.BINANCE.name() : AppConstant.PriceDataSource.HUOBI.name());
            // the lower the ask price, the less user must pay to buy an asset, hence the better
            aggregatedPrice.setAskPrice(binanceAsk <= huobiAsk ? binanceAsk : huobiAsk);
            aggregatedPrice.setAskPriceSource(binanceAsk <= huobiAsk ? AppConstant.PriceDataSource.BINANCE.name() : AppConstant.PriceDataSource.HUOBI.name());
            logger.info("Aggregated Price for asset {} is {}", asset, aggregatedPrice);

            aggregatedPrice.setLastUpdatedTime(LocalDateTime.now());
            aggregatedPrice.setCurrency("USDT");
            aggregatedPriceList.add(aggregatedPrice);
        }
        return aggregatedPriceList;
    }

    Map<String, Double[]> fetchBinancePrice() throws UnsupportedEncodingException, JsonProcessingException {
        Map<String, Double[]> binancePrice = new HashMap<>();
        //rest template pass in list of ETHUSDT and BTCUSDT
        String[] queryParams = {"ETHUSDT", "BTCUSDT"};
        String queryParamsJson = objectMapper.writeValueAsString(queryParams);
        String binanceQueryUrl = binancePriceUrl + "?symbols=" + queryParamsJson;
        ResponseEntity<BinancePriceDTO[]> binancePriceResponse = restTemplate.getForEntity(binanceQueryUrl, BinancePriceDTO[].class);
        //parse the response map to BinancePriceDTO
        if (binancePriceResponse.getStatusCode().is2xxSuccessful() && binancePriceResponse.getBody() != null) {
            try {
                for (BinancePriceDTO binancePriceDTO : binancePriceResponse.getBody()) {
                    if ("ETHUSDT".equalsIgnoreCase(binancePriceDTO.getSymbol())
                            || "BTCUSDT".equalsIgnoreCase(binancePriceDTO.getSymbol())) {
                        double bidPrice = binancePriceDTO.getBidPrice();
                        double askPrice = binancePriceDTO.getAskPrice();
                        logger.info("Binance Price found for symbol: {}, bid {}, ask {} ", binancePriceDTO.getSymbol(), bidPrice, askPrice);
                        String asset = "ETHUSDT".equalsIgnoreCase(binancePriceDTO.getSymbol()) ? "ETH" : "BTC";
                        binancePrice.put(asset, new Double[]{bidPrice, askPrice});
                    }
                }
            } catch (Exception ex) {
                logger.warn("Error while parsing a binance price response", ex);
            }
        } else {
            logger.warn("Unable to get successful price response from Binance, statuscode{}, body{}", binancePriceResponse.getStatusCode(), binancePriceResponse.getBody());
            return binancePrice;
        }
        return binancePrice;
    }

    Map<String, Double[]> fetchHuobiPrice() {
        Map<String, Double[]> houbiPrice = new HashMap<>();
        ResponseEntity<HuobiPriceWrapper> huobiPriceResponse = restTemplate.getForEntity(huobiPriceUrl, HuobiPriceWrapper.class);
        if (huobiPriceResponse.getStatusCode().is2xxSuccessful() && huobiPriceResponse.getBody() != null) {
            List<HuobiPriceDTO> houbiPriceDTOList = huobiPriceResponse.getBody().getData();
            for (HuobiPriceDTO huobiPriceDTO : houbiPriceDTOList) {
                if ("ETHUSDT".equalsIgnoreCase(huobiPriceDTO.getSymbol())
                        || "BTCUSDT".equalsIgnoreCase(huobiPriceDTO.getSymbol())) {
                    double bidPrice = huobiPriceDTO.getBid();
                    double askPrice = huobiPriceDTO.getAsk();
                    logger.info("Huobi Price found for symbol: {}, bid {}, ask {} ", huobiPriceDTO.getSymbol(), bidPrice, askPrice);
                    String asset = "ETHUSDT".equalsIgnoreCase(huobiPriceDTO.getSymbol()) ? "ETH" : "BTC";
                    houbiPrice.put(asset, new Double[]{bidPrice, askPrice});
                }
            }

        } else {
            logger.warn("Unable to get successful price response from Huobi, statuscode{}, body{}", huobiPriceResponse.getStatusCode(), huobiPriceResponse.getBody());
            return houbiPrice;
        }
        return houbiPrice;
    }


    public List<AggregatedPriceDTO> getAggregatedPrices(String asset) {
        List<AggregatedPriceDTO> aggregatedPriceDTOS = new ArrayList<>();
        List<AggregatedPrice> aggregatedPriceList = new LinkedList<>();
        if (StringUtils.isEmpty(asset)) {
            aggregatedPriceList= aggregatedPriceRepository.findAll();
        } else {
            AggregatedPrice aggregatedPrice = aggregatedPriceRepository.findByAsset(asset).orElseThrow(() -> new NoSuchElementException("price not found for given asset"));
            aggregatedPriceList = List.of(aggregatedPrice);
        }
        aggregatedPriceList.forEach(price -> {
            AggregatedPriceDTO dto = new AggregatedPriceDTO();
            BeanUtils.copyProperties(price, dto);
            aggregatedPriceDTOS.add(dto);
        });
        return aggregatedPriceDTOS;
    }


}
