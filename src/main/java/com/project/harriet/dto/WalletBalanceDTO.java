package com.project.harriet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletBalanceDTO {
    private Long userId;
    private String asset;
    private BigDecimal balance;
}
