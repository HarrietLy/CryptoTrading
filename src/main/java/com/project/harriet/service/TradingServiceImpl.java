package com.project.harriet.service;

import com.project.harriet.dto.TransactionDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TradingServiceImpl implements TradingService {

    @Override
    public Long placeOrder(String orderType, String asset, BigDecimal quantity) {
        return 0L;
    }

    @Override
    public List<TransactionDTO> retrieveTransactionHistorybyUser(Long userId) {
        return List.of();
    }
}
