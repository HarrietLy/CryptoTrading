package com.project.harriet.dto.ticker;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BinancePriceDTO {

    private String symbol;
    private Double bidPrice;
    private Double askPrice;
}
