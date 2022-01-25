package com.jayrush.springmvcrest.wallet.service;

import com.jayrush.springmvcrest.Repositories.InstitutionRepository;
import com.jayrush.springmvcrest.Repositories.UserRepository;
import com.jayrush.springmvcrest.domain.Institution;
import com.jayrush.springmvcrest.domain.tmsUser;
import com.jayrush.springmvcrest.jwt.JwtTokenUtil;
import com.jayrush.springmvcrest.rolesPermissions.models.Permissions;
import com.jayrush.springmvcrest.rolesPermissions.repositories.permissionRepository;
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
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    UserRepository userRepository;
    @Autowired
    permissionRepository permissionRepository;


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
//            walletAccount.setPurpose("Institution Wallet Account");
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
        String User = jwtTokenUtil.getUsernameFromToken(walletAccountdto.getToken());
        tmsUser user = userRepository.findByusername(User);
        Permissions permissions = permissionRepository.findByName("UPDATE_WALLET");
        if (Objects.nonNull(user)){
            if (Objects.nonNull(permissions)){
                walletAccount Account = walletAccountRepository.findByWalletNumber(walletAccountdto.getInstitutionID());
                if (Objects.nonNull(Account)){
                    Double fee = walletAccountdto.getFeePercentage()/100;
                    Account.setFeePercentage(fee);
                    Account.setMinimumCharge(walletAccountdto.getMinimumCharge());
                    Account.setMaximumCharge(walletAccountdto.getMaximumCharge());
                    walletAccountRepository.save(Account);
                    return Account;
                }
                else {
                    logger.info("Wallet Account not found");
                    return null;
                }
            }else {
                logger.info("Update wallet permission not found");
            }

        }else {
            logger.info("User not found");
            return null;
        }
        return null;

    }
}
