package com.project.harriet.service;

import com.project.harriet.constant.AppConstant;
import com.project.harriet.dto.WalletBalanceDTO;
import com.project.harriet.model.Transaction;
import com.project.harriet.model.WalletBalance;
import com.project.harriet.repository.WalletBalanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.project.harriet.constant.AppConstant.TransactionStatus.COMPLETED;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletBalanceRepository walletBalanceRepository;
    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);
    public WalletServiceImpl(WalletBalanceRepository walletBalanceRepository) {
        this.walletBalanceRepository = walletBalanceRepository;
    }

    // assume access control logic is already in place to make sure only authorized user can invoke
    @Override
    public List<WalletBalanceDTO> retrieveWalletBalance(Long userId, String asset) {
        List<WalletBalance> walletBalances = new ArrayList<>();
        List<WalletBalanceDTO> walletBalanceDTOs = new ArrayList<>();
        if (asset.isEmpty()) {
            walletBalances= walletBalanceRepository.findByUserId(userId);
        } else {
            Optional<WalletBalance> walletBalanceOpt = walletBalanceRepository.findByUserIdAndAsset(userId, asset);
            if (walletBalanceOpt.isEmpty()) {return List.of();} else{
                walletBalances = List.of(walletBalanceOpt.get());
            }
        }
        walletBalances.forEach(walletBalance -> {
            WalletBalanceDTO walletBalanceDTO = new WalletBalanceDTO();
            BeanUtils.copyProperties(walletBalance, walletBalanceDTO);
            walletBalanceDTOs.add(walletBalanceDTO);
        });
        return walletBalanceDTOs;
    }

    @Override
    public void updateWalletBalance(Transaction transaction) {
        if (!COMPLETED.name().equalsIgnoreCase(transaction.getOrderStatus())) {
            throw new RuntimeException("cannot update wallet balance if transaction is not completed");
        }
        WalletBalance assetWalletBalance = walletBalanceRepository.findByUserIdAndAsset(transaction.getUserId(), transaction.getAsset()).orElseThrow(()-> new NoSuchElementException("Wallet balance not found for given asset"));
        WalletBalance currencyWalletBalance = walletBalanceRepository.findByUserIdAndAsset(transaction.getUserId(), transaction.getCurrency()).orElseThrow(()-> new NoSuchElementException("Wallet balance not found for given asset"));

        BigDecimal quantity = transaction.getQuantity();
        BigDecimal price = BigDecimal.valueOf(transaction.getPrice());
        BigDecimal totalAmountInCurrency = quantity.multiply(price);
        if (AppConstant.OrderType.BUY.name().equalsIgnoreCase(transaction.getOrderType())) {
            logger.info("subtracting currency, adding asset");
            currencyWalletBalance.setBalance(currencyWalletBalance.getBalance().subtract(totalAmountInCurrency));
            assetWalletBalance.setBalance(currencyWalletBalance.getBalance().add(quantity));
        } else if (AppConstant.OrderType.SELL.name().equalsIgnoreCase(transaction.getOrderType())){
            logger.info("adding currency, subtracting asset");
            currencyWalletBalance.setBalance(currencyWalletBalance.getBalance().subtract(totalAmountInCurrency));
            assetWalletBalance.setBalance(currencyWalletBalance.getBalance().add(quantity));
        }
        walletBalanceRepository.save(assetWalletBalance);
        walletBalanceRepository.save(currencyWalletBalance);
    }

}
