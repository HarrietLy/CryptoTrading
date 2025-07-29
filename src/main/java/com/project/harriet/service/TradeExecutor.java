package com.project.harriet.service;

import com.project.harriet.constant.AppConstant;
import com.project.harriet.model.AggregatedPrice;
import com.project.harriet.model.Transaction;
import com.project.harriet.repository.AggregatedPriceRepository;
import com.project.harriet.repository.TransactionRepository;
import com.project.harriet.repository.WalletBalanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.project.harriet.constant.AppConstant.TransactionStatus.*;

@Component
public class TradeExecutor {

    private final AggregatedPriceRepository aggregatedPriceRepository;
    private final TransactionRepository transactionRepository;
    private final WalletBalanceRepository walletBalanceRepository;
    private final WalletService walletService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private final Logger logger = LoggerFactory.getLogger(TradeExecutor.class);
    private static final long TIMEOUT_MILLIS = 24 * 60 * 60 * 1000;

    public TradeExecutor(AggregatedPriceRepository aggregatedPriceRepository, TransactionRepository transactionRepository, WalletBalanceRepository walletBalanceRepository, WalletService walletService) {
        this.aggregatedPriceRepository = aggregatedPriceRepository;
        this.transactionRepository = transactionRepository;
        this.walletBalanceRepository = walletBalanceRepository;
        this.walletService = walletService;
    }

    @Scheduled(fixedRate = 1000)
    public void executePendingTrades() {
        // query max 10 oldest created/ processing transactions
        List<Transaction> pendingTransactions = transactionRepository.findTop20ByOrderStatusInOrderByOrderCreatedTimeAsc(List.of(AppConstant.TransactionStatus.CREATED.name(), PROCESSING.name()));
        // if not timeout, using a thread pool executor service to spwan 3 threads to execute the trades concurrently
        for (Transaction transaction : pendingTransactions) {
            long age = Duration.between(LocalDateTime.now(), transaction.getOrderCreatedTime()).toMillis();
            if (age > TIMEOUT_MILLIS) {
                transaction.setOrderStatus(AppConstant.TransactionStatus.TIMEOUT.name());
                transaction.setLocked(false);
                transactionRepository.save(transaction);
            }
            if (acquirelock(transaction.getTransactionId())) {
                executorService.submit(() -> {executeTrade(transaction.getTransactionId());});
            }
        }
    }

    private void executeTrade(Long transactionId) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(transactionId);
        if (transactionOptional.isEmpty()) {
            return;
        }
        Transaction transaction = transactionOptional.get();
        try {
            transaction.setOrderStatus(PROCESSING.name());
            transactionRepository.save(transaction);

            AggregatedPrice price = aggregatedPriceRepository.findByAsset(transaction.getAsset()).orElseThrow(()-> new NoSuchElementException("Price not found for given asset"));

            boolean success = false;
            // placing trade via exchange that offers the best price
            if(AppConstant.OrderType.BUY.name().equalsIgnoreCase(transaction.getOrderType())) {
                if (AppConstant.PriceDataSource.BINANCE.name().equalsIgnoreCase(price.getAskPriceSource())) {
                    logger.info("best ask price is from binance");
                    transaction.setPrice(price.getAskPrice());
                    transaction.setOrderExecutor(AppConstant.Exchange.BINANCE.name());
                    success= executeTradeViaBinance(transaction);
                } else if (AppConstant.PriceDataSource.HUOBI.name().equalsIgnoreCase(price.getAskPriceSource())) {
                    logger.info("best ask price is from HUOBI");
                    transaction.setPrice(price.getAskPrice());
                    transaction.setOrderExecutor(AppConstant.Exchange.HUOBI.name());
                    success =executeTradeViaHuobi(transaction);
                }
            } else if(AppConstant.OrderType.SELL.name().equalsIgnoreCase(transaction.getOrderType())) {
                if (AppConstant.PriceDataSource.BINANCE.name().equalsIgnoreCase(price.getBidPriceSource())) {
                    logger.info("best bid price is from BINANCE");
                    transaction.setPrice(price.getBidPrice());
                    transaction.setOrderExecutor(AppConstant.Exchange.BINANCE.name());
                    success = executeTradeViaBinance(transaction);
                } else if (AppConstant.PriceDataSource.HUOBI.name().equalsIgnoreCase(price.getBidPriceSource())) {
                    logger.info("best bid price is from HUOBI");
                    transaction.setPrice(price.getBidPrice());
                    transaction.setOrderExecutor(AppConstant.Exchange.HUOBI.name());
                    success = executeTradeViaHuobi(transaction);
                }
            }
            if (success) {
                walletService.updateWalletBalance(transaction);
                transaction.setOrderStatus(COMPLETED.name());
                transaction.setOrderCompletedTime(LocalDateTime.now());
            }else{
                logger.error("Failed to place order via exchange");
                transaction.setOrderStatus(FAIL.name());
            }
        } catch (Exception e) {
            logger.error("Execution of trade failed for transaction {} ", transactionId, e);
            transaction.setOrderStatus(FAIL.name());
        } finally {
            transaction.setLocked(false);
            transactionRepository.save(transaction);
        }
    }

    private boolean acquirelock(Long transactionId) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(transactionId);
        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();
            if (!transaction.getLocked()) {
                transaction.setLocked(true);
                transaction.setLastLockedTime(LocalDateTime.now());
                transactionRepository.save(transaction);
                logger.error("lock acquired for transaction {} ", transactionId);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

//    private void releaseLock(Long transactionId) {
//        transactionRepository.findById(transactionId).ifPresent(transaction -> {
//            transaction.setLocked(false);
//            transactionRepository.save(transaction);
//            logger.info("Released lock for transaction {} ", transactionId);
//        });
//    }

    private boolean executeTradeViaBinance(Transaction transaction) throws InterruptedException {
        //simulating a blocking synchronous order placement to Binance exchange
        // todo: for asynchronous processing, needs to send trades message queue, and have another component listening to the exchange response instead
        logger.info("placing order via Binance");
        Thread.sleep(10 * 1000);
        return true;
    }

    private boolean executeTradeViaHuobi(Transaction transaction) throws InterruptedException {
        //simulating place order to Huobi exchange
        logger.info("placing order via Huobi");
        Thread.sleep(20 * 1000);
        return true;
    }

}
