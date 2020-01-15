package com.jayrush.springmvcrest.Service;

import com.google.gson.Gson;
import com.jayrush.springmvcrest.ClientHandler;
import com.jayrush.springmvcrest.Repositories.TerminalRepository;
import com.jayrush.springmvcrest.Repositories.TransactionRepository;
import com.jayrush.springmvcrest.Repositories.terminalKeysRepo;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.Terminals;
import com.jayrush.springmvcrest.domain.hostResponse;
import com.jayrush.springmvcrest.domain.terminalKeyManagement;
import com.jayrush.springmvcrest.utility.CryptoException;
import org.apache.commons.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

import static com.jayrush.springmvcrest.freedom.freedomSync.SyncTrans;
import static com.jayrush.springmvcrest.utility.AppUtility.randomNumber;
import static com.jayrush.springmvcrest.utility.AppUtility.randomString;

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

    @Autowired
    TerminalRepository terminalRepository;

    @Autowired
    terminalKeysRepo terminalKeysRepo;

//    @Scheduled(fixedDelay = 20000)
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

                }

                if (hostResponse.getRespCode().equals("00")){
                    transactionLog.setProcessed(true);
                    transactionLog.setInstitutionResponseCode(hostResponse.getRespCode());
                    transactionLog.setInstitutionResponseDesc("Processed");
                }
                else {
                    transactionLog.setInstitutionResponseCode(hostResponse.getRespCode());
                    transactionLog.setInstitutionResponseDesc("Not Processed");
                    transactionLog.setProcessed(false);
                }
                transactionLog.setResponseFromFreedom(response);
                transactionRepository.save(transactionLog);
                logger.info("transaction successfully sent {}",transactionLog);


            }
        }
    }

//    @Scheduled(fixedDelay = 86400000)
    public void start(){

        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        ClientHandler clientHandler = new ClientHandler(s, dis,dos);
        List<Terminals> terminalsList = terminalRepository.findAll();
        for (int i = 0; i<terminalsList.size(); i++){
            terminalKeyManagement key = clientHandler.keyManagement(terminalsList.get(i));
            key.setId(terminalsList.get(i).getId());
            terminalKeysRepo.save(key);
        }

    }



}


