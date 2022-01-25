package com.jayrush.springmvcrest.wallet.repository;

import com.jayrush.springmvcrest.wallet.models.walletAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author JoshuaO
 */

@Repository
public interface walletAccountRepository extends JpaRepository<walletAccount,Long> {
    walletAccount findByWalletNumber(String walletNumber);
}
