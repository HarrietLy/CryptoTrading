package com.project.harriet.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {
    private Long transactionId;

    private Long userId;

    private String orderType; //BUY or SELL
    private String asset;
    private BigDecimal quantity;

    private LocalDateTime orderCreatedTime;
    private LocalDateTime orderCompletedTime;

    private String orderStatus;
}
