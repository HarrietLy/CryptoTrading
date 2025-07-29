package com.project.harriet.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name="Wallet_Balance")
public class WalletBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String asset;
    private BigDecimal balance;

}
