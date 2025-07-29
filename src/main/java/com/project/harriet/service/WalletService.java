package com.project.harriet.service;

import com.project.harriet.dto.WalletBalanceDTO;
import com.project.harriet.model.Transaction;

import java.util.List;

public interface WalletService {
    List<WalletBalanceDTO> retrieveWalletBalance(Long userId, String asset);
    void updateWalletBalance(Transaction transaction);
}
