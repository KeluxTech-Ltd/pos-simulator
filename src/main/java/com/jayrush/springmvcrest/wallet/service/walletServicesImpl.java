package com.jayrush.springmvcrest.wallet.service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.wallet.models.dtos.walletAccountdto;
import com.jayrush.springmvcrest.wallet.models.walletAccount;
import com.jayrush.springmvcrest.wallet.repository.walletAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author JoshuaO
 */
@Service
public class walletServicesImpl implements walletServices {
    private static Logger logger = LoggerFactory.getLogger(walletServicesImpl.class);
    @Autowired
    walletAccountRepository walletAccountRepository;
    @Autowired
    InstitutionRepository institutionRepository;

    @Override
    public walletAccount createWalletAccount(walletAccountdto walletAccountdto) {
        walletAccount walletAccount = new walletAccount();
        Date date = new Date();
        Institution institution = institutionRepository.findByInstitutionID(walletAccountdto.getInstitutionID());
        if (Objects.nonNull(institution)){
            walletAccount.setWalletNumber(institution.getInstitutionID());
            walletAccount.setAvailableBalance(0.0);
            walletAccount.setLedgerBalance(0.0);
            walletAccount.setLastTranDate(date);
            walletAccount.setPurpose("Institution Wallet Account");
            walletAccount.setMinimumCharge(walletAccountdto.getMinimumCharge());
            walletAccount.setMinimumCharge(walletAccountdto.getMaximumCharge());
            walletAccount.setFeePercentage(walletAccountdto.getFeePercentage());
            try {
                walletAccountRepository.save(walletAccount);
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
            return walletAccount;
        }else {
            return null;
        }
    }

    @Override
    public List<walletAccount> getWalletAccount() {
        try {
            List<walletAccount> walletAccountList = walletAccountRepository.findAll();
            return walletAccountList;

        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        }

    }

    @Override
    public walletAccount getWalletAccountByWalletNumber(String walletNumber) {
        try {
            walletAccount walletAccount = walletAccountRepository.findByWalletNumber(walletNumber);
            return walletAccount;

        } catch (Exception e) {
            logger.info(e.getMessage());
            return null;
        }
    }

    @Override
    public walletAccount updateWalletAccount(walletAccountdto walletAccountdto) {
        walletAccount Account = walletAccountRepository.findByWalletNumber(walletAccountdto.getInstitutionID());
        if (Objects.nonNull(Account)){
            Account.setFeePercentage(walletAccountdto.getFeePercentage());
            Account.setMinimumCharge(walletAccountdto.getMinimumCharge());
            Account.setMaximumCharge(walletAccountdto.getMaximumCharge());
            walletAccountRepository.save(Account);
            return Account;
        }
        else {
            return null;
        }
    }
}
