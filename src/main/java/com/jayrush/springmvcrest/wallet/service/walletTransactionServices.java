package com.jayrush.springmvcrest.wallet.service;

import com.jayrush.springmvcrest.wallet.models.dtos.WalletTransactionHistoryDTO;
import com.jayrush.springmvcrest.wallet.models.dtos.WalletTransactionListDTO;

/**
 * @author JoshuaO
 */
public interface walletTransactionServices {
    WalletTransactionListDTO getWalletTransactionHistory(WalletTransactionHistoryDTO walletTransactionHistory);
}
