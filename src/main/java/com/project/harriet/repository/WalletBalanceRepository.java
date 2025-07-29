package com.project.harriet.repository;

import com.project.harriet.model.WalletBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletBalanceRepository extends JpaRepository<WalletBalance, Long> {
    List<WalletBalance> findByUserId(Long userId);

    Optional<WalletBalance> findByUserIdAndAsset(Long userId, String asset);
}
