package com.project.harriet.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name="Aggregated_Price")
public class AggregatedPrice {
    @Id
    private Long id;

    private String asset;

    private Double bidPrice;
    private Double askPrice;

    private String bidPriceSource;
    private String askPriceSource;

    private LocalDateTime lastUpdatedTime;
}
