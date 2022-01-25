package com.jayrush.springmvcrest.wallet.repository;

import com.jayrush.springmvcrest.wallet.models.walletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author JoshuaO
 */
public interface walletTransactionRepository extends JpaRepository<walletTransaction,Long> {
    @Query(value = "select t from walletTransaction t  order by t.id desc")
    Page<walletTransaction> SelectAll(Pageable pageable);
    Page<walletTransaction>findTopByWalletNumberAndTranDateBetween(String institutionID, String from, String to, Pageable pageable);

    Page<walletTransaction> findTopByWalletNumber(String walletNumber, Pageable paged);
}
