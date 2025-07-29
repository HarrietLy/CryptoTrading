package com.project.harriet.service;

import com.project.harriet.dto.WalletBalanceDTO;
import com.project.harriet.repository.WalletBalanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletBalanceRepository walletBalanceRepository;

    public WalletServiceImpl(WalletBalanceRepository walletBalanceRepository) {
        this.walletBalanceRepository = walletBalanceRepository;
    }


    @Override
    public List<WalletBalanceDTO> retrieveWalletBalance(Long userId) {
        return List.of();
    }
}
