package com.project.harriet.service;

import com.project.harriet.dto.AggregatedPriceDTO;
import com.project.harriet.dto.ticker.BinancePriceDTO;
import com.project.harriet.dto.ticker.HuobiPriceDTO;
import com.project.harriet.model.AggregatedPrice;
import com.project.harriet.repository.AggregatedPriceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class PriceAggregatorService {

    @Value("${third-party.binance.price_url}")
    private String binancePriceUrl;

    @Value("${third-party.huobi.price_url}")
    private String huobiPriceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final Logger logger = LoggerFactory.getLogger(PriceAggregatorService.class);
    public PriceAggregatorService( AggregatedPriceRepository aggregatedPriceRepository) {
        this.aggregatedPriceRepository = aggregatedPriceRepository;
    }

    @Scheduled(fixedRate = 10000)
    void fetchBestPrice(){
 // fetchBinancePrice and Huo concurrently
        //wait for both threads to finish, set time out
        //get highest bid price and lowest ask price
        // save best prices to db
        //try catch log warn error
    }

    Map<String, AggregatedPriceDTO> fetchBinancePrice(){
        Map<String, AggregatedPriceDTO> binancePrice = new HashMap<>();
        //rest template pass in list of ETHUSDT and BTCUSDT
        String[] queryParams = new String[]{"binance", "huobi"};
        ResponseEntity<BinancePriceDTO[]> binancePriceResponse = restTemplate.getForEntity(binancePriceUrl, BinancePriceDTO[].class);
        //parse the response map to BinancePriceDTO
        if ( binancePriceResponse.getStatusCode().is2xxSuccessful() && binancePriceResponse.getBody() != null) {
            try{
                for (BinancePriceDTO binancePriceDTO : binancePriceResponse.getBody()) {
                    if ("ETHUSDT".equalsIgnoreCase(binancePriceDTO.getSymbol())
                            || "BTCUSDT".equalsIgnoreCase(binancePriceDTO.getSymbol())){
                    String symbol = binancePriceDTO.getSymbol();
                    double bidPrice = binancePriceDTO.getBidPrice();
                    double askPrice = binancePriceDTO.getAskPrice();
                    AggregatedPriceDTO binancePriceOneSymbol = new AggregatedPriceDTO();
                    binancePriceOneSymbol.setBidPrice(bidPrice);
                    binancePriceOneSymbol.setAskPrice(askPrice);
                    binancePrice.put(symbol, binancePriceOneSymbol)
                    }
                }
            }catch (Exception ex){
                log.warn
                return binancePrice;

            }
        } else {
            return binancePrice;
        }

    }

    List<AggregatedPriceDTO> fetchHuobiPrice(){
        List<HuobiPriceDTO> huobiPriceDTOList = new ArrayList<>();

    }


    private final AggregatedPriceRepository aggregatedPriceRepository;

    List<AggregatedPrice> getAggregatedPrices(String asset, String orderType){
        if(StringUtils.isEmpty(asset)){
            return new ArrayList<>();
        }
        else if( StringUtils.isEmpty(orderType)){
            return aggregatedPriceRepository.findByAsset(asset);
        } else {
            return aggregatedPriceRepository.findAll();
        }
    }
}
