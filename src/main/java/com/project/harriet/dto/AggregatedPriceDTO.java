package com.project.harriet.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AggregatedPriceDTO {

    private String asset;

    private Double bidPrice; //in USDT
    private Double askPrice; // in USDT

    private LocalDateTime lastUpdatedTime;
}
