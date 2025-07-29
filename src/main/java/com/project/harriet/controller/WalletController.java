package com.project.harriet.controller;

import com.project.harriet.dto.WalletBalanceDTO;
import com.project.harriet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{userId}/{asset}")
    @Operation(summary = "retrieve the user’s crypto currencies wallet balance")
    public List<WalletBalanceDTO> retrieveWalletBalance(@PathVariable Long userId, @PathVariable(required = false) String asset) {
        return walletService.retrieveWalletBalance(userId, asset);
    }
}
