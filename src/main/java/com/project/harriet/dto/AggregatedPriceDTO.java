package com.project.harriet.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AggregatedPriceDTO {

    private String asset;

    private Double bidPrice;
    private Double askPrice;

    private String currency;
    private LocalDateTime lastUpdatedTime;
}
