package com.project.harriet.dto.ticker;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HuobiPriceWrapper {
    private List<HuobiPriceDTO> data;
}
