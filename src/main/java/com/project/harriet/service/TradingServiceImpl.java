package com.project.harriet.service;

import com.project.harriet.constant.AppConstant;
import com.project.harriet.dto.TransactionDTO;
import com.project.harriet.model.Transaction;
import com.project.harriet.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TradingServiceImpl implements TradingService {

    private final TransactionRepository transactionRepository;

    private static final Logger logger = LoggerFactory.getLogger(TradingServiceImpl.class);
    public TradingServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // assume access control logic is already in place to make sure only authorized user can invoke
    @Override
    public Long placeTrade(Long userId, String orderType, String asset, BigDecimal quantity, String currency) {
        // Validate input
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (!AppConstant.OrderType.BUY.name().equalsIgnoreCase(orderType) && !AppConstant.OrderType.SELL.name().equalsIgnoreCase(orderType)) {
            throw new IllegalArgumentException("Order type must be BUY or SELL");
        }

        Transaction newTransaction = new Transaction();
        newTransaction.setOrderType(orderType);
        newTransaction.setAsset(asset);
        newTransaction.setQuantity(quantity);
        newTransaction.setOrderStatus(AppConstant.TransactionStatus.CREATED.name());
        Transaction savedTransaction  = transactionRepository.save(newTransaction);
        logger.info("savedTransaction {} ", savedTransaction);

        return savedTransaction.getTransactionId();
    }




    // assume access control logic is already in place to make sure only authorized user can invoke
    @Override
    public List<TransactionDTO> retrieveTransactionHistorybyUser(Long userId) {
        return List.of();
    }
}
