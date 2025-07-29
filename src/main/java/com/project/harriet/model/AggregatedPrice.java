package com.project.harriet.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="Aggregated_Price")
@Data
public class AggregatedPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String asset; //eth or btc

    private Double bidPrice;
    private Double askPrice;

    private String currency;

    private String bidPriceSource; // Binance or Huobi
    private String askPriceSource; // Binance or Huobi

    private LocalDateTime lastUpdatedTime;
}
