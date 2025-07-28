package com.project.harriet.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="Transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private Long userId;

    private String orderType; //BUY or SELL
    private String asset;
    private BigDecimal quantity;

    private String orderExecutor;
    private LocalDateTime orderCreatedTime;
    private LocalDateTime orderCompletedTime;

    private String orderStatus;
}
