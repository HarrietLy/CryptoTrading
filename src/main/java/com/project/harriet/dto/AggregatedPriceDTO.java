package com.project.harriet.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AggregatedPriceDTO {

    private String asset;

    private Double bidPriceInUSDT;
    private Double askPriceInUSDT;

    private LocalDateTime lastUpdatedTime;
}
