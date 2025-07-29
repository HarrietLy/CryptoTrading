package com.project.harriet.service;

import com.project.harriet.dto.TransactionDTO;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

public interface TradingService {

    Long placeTrade(Long userId, String orderType, String asset, BigDecimal quantity, String currency);
    List<TransactionDTO> retrieveTransactionHistorybyUser(Long userId);
}
