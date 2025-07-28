package com.project.harriet.dto.ticker;

import lombok.Data;

import java.io.Serializable;

@Data
public class HuobiPriceDTO {
    private String symbol;
    private Double bid;
    private Double ask;


}
