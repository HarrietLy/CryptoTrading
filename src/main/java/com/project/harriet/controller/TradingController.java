package com.project.harriet.controller;

import com.project.harriet.dto.TransactionDTO;
import com.project.harriet.service.TradingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trading")
public class TradingController {

    private final TradingService tradingService;

    public TradingController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    @PostMapping("/{userId}/{orderType}/{asset}/{quantity}/{currency}")
    public ResponseEntity<String> placeTrade(@PathVariable Long userId
            , @PathVariable String orderType, @PathVariable String asset
            , @PathVariable BigDecimal quantity, @PathVariable String currency) {
        try {
            Long transactionId = tradingService.placeTrade(userId, orderType, asset, quantity, currency);
            return ResponseEntity.ok(transactionId.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }

    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<TransactionDTO>> fetchTrade(@PathVariable Long userId) {
        try {
            List<TransactionDTO> transactionDTOS= tradingService.retrieveTransactionHistorybyUser(userId);
            return ResponseEntity.ok().body(transactionDTOS);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of());
        }
    }

}



