package com.project.harriet.controller;

import com.project.harriet.dto.WalletBalanceDTO;
import com.project.harriet.service.WalletService;
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

    public List<WalletBalanceDTO> retrieveWalletBalance(Long userId) {
        return walletService.retrieveWalletBalance(userId);
    }
}
