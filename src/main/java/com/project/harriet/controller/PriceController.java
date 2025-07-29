package com.project.harriet.controller;

import com.project.harriet.dto.AggregatedPriceDTO;
import com.project.harriet.repository.AggregatedPriceRepository;
import com.project.harriet.service.PriceAggregatorService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bestprice")
public class PriceController {

    private final PriceAggregatorService priceAggregatorService;

    public PriceController(PriceAggregatorService priceAggregatorService) {
        this.priceAggregatorService = priceAggregatorService;
    }

    @GetMapping
    @Operation(summary = "retrieve the latest best aggregated price")
    public List<AggregatedPriceDTO> fetchLatestBestPrice(@RequestParam(required = false) String asset) {
            return priceAggregatorService.getAggregatedPrices(asset);
    }
}
