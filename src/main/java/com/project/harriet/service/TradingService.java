package com.project.harriet.service;

import com.project.harriet.dto.TransactionDTO;

import java.math.BigDecimal;
import java.util.List;

public interface TradingService {

    Long placeOrder(String orderType, String asset, BigDecimal quantity);
    List<TransactionDTO> retrieveTransactionHisotyrbyUser(Long userId);


}
