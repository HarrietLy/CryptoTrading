package com.project.harriet.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name="Wallet_Balance")
public class WalletBalance {
    @Id
    private Long id;

    private String asset;
    private BigDecimal balance;

}
