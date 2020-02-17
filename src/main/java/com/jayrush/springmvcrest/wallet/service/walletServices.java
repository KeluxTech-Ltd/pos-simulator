package com.jayrush.springmvcrest.wallet.service;

import com.jayrush.springmvcrest.wallet.models.dtos.walletAccountdto;
import com.jayrush.springmvcrest.wallet.models.walletAccount;

import java.util.List;

/**
 * @author JoshuaO
 */
public interface walletServices {
    //Create
    walletAccount createWalletAccount(walletAccountdto walletAccountdto);
    //Read
    List<walletAccount> getWalletAccount();

    walletAccount getWalletAccountByWalletNumber(String walletNumber);
    //Update
    walletAccount updateWalletAccount(walletAccountdto walletAccountdto);

}
