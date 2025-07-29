package com.project.harriet.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name="Wallet_Balance")
@Data
public class WalletBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String asset;
    private BigDecimal balance;

}
