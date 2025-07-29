package com.project.harriet.service;

import com.project.harriet.dto.WalletBalanceDTO;

import java.util.List;

public interface WalletService {
    List<WalletBalanceDTO> retrieveWalletBalance(Long userId);
}
