package com.project.harriet.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="Transaction")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private Long userId;

    private String orderType; //BUY or SELL
    private String asset;
    private BigDecimal quantity;
    private Double price;
    private String currency;

    private String orderExecutor;
    private LocalDateTime orderCreatedTime;
    private LocalDateTime orderCompletedTime;

    private String orderStatus;

    private Boolean locked;
    private LocalDateTime lastLockedTime;
}
