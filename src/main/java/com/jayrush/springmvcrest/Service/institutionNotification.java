package com.jayrush.springmvcrest.Service;

import com.google.gson.Gson;
import com.jayrush.springmvcrest.Repositories.TransactionRepository;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.hostResponse;
import org.apache.commons.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

import static com.jayrush.springmvcrest.freedom.freedomSync.SyncTrans;

/**
 * @author JoshuaO
 */

@EnableScheduling
@Component
public class institutionNotification {
    private static final Logger logger = LoggerFactory.getLogger(institutionNotification.class);

    @Autowired
    TransactionInterface transactionInterface;

    @Autowired
    TransactionRepository transactionRepository;

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void notifyInstitution(){
        logger.info("Starting transaction notification to Institution");
        String response = null;
        List<TerminalTransactions> transactions = transactionInterface.getAllUnnotifiedTransactions(false, true);
        logger.info("transaction Size is {}",transactions.size());
        if (transactions.isEmpty()){
            logger.info("No Pending Transaction Notification");
        }
        else {
            for (TerminalTransactions transaction : transactions) {
                try {
                    response = SyncTrans(transaction);
                } catch (IOException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | DecoderException e) {
                    logger.info(e.getMessage());
                }
                TerminalTransactions transactionLog = transactionRepository.findById(transaction.getId()).get();
                Gson g = new Gson();
                hostResponse hostResponse = g.fromJson(response, hostResponse.class);

                if (Objects.isNull(response)){
                    logger.info("No response from Institution");
                    return;
                }

                if (hostResponse.getRespCode().equals("00")){
                    transactionLog.setProcessed(true);
                    transactionLog.setInstitutionResponseCode(hostResponse.getRespCode());
                    transactionLog.setInstitutionResponseDesc(hostResponse.getRespDesc());
                }
                else {
                    transactionLog.setInstitutionResponseCode(hostResponse.getRespCode());
                    transactionLog.setInstitutionResponseDesc(hostResponse.getRespDesc());
                    transactionLog.setProcessed(false);
                }
                transactionLog.setResponseFromFreedom(response);
                transactionRepository.save(transactionLog);
                logger.info("transaction successfully sent {}",transactionLog);


            }
        }
    }
}
