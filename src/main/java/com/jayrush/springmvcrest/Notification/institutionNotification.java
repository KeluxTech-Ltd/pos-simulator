package com.jayrush.springmvcrest.Notification;

import com.google.gson.Gson;
import com.jayrush.springmvcrest.ClientHandler;
import com.jayrush.springmvcrest.Repositories.TerminalRepository;
import com.jayrush.springmvcrest.Repositories.TransactionRepository;
import com.jayrush.springmvcrest.Repositories.terminalKeysRepo;
import com.jayrush.springmvcrest.Service.TransactionInterface;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.Terminals;
import com.jayrush.springmvcrest.domain.hostResponse;
import com.jayrush.springmvcrest.domain.terminalKeyManagement;
import jdk.nashorn.internal.ir.Terminal;
import org.apache.commons.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

//import static com.jayrush.springmvcrest.freedom.freedomSync.SyncTrans;


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

    @Autowired
    MedusaNotification freedomSync;

    @Scheduled(fixedDelay = 20000)
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
//                    response = SyncTrans(transaction);
                    // TODO: 2/9/2020 calculate the actual amount to be settled then add it to their wallet with date
                    //Only for interswitch transactions
                    //get the institution from the transaction model
                    //get the fee from the institution model
                    //calculate the notification amount using the fee
                    //save the transaction to new transaction table
                    //call matipon to credit them less the new amount
                    response = freedomSync.NotifyInstitution(transaction);
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
                try {
                    transactionRepository.save(transactionLog);
                } catch (Exception e) {
                    logger.info(e.getMessage());
                }
                logger.info("transaction notification sent successfully {}",transactionLog);
            }
        }
    }

    @Scheduled(cron = "0 1 1 * * *")
//    @Scheduled(fixedDelay = 86400000)
    public void start(){
        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        ClientHandler clientHandler = new ClientHandler(s, dis,dos);
        List<Terminals> terminalsList = terminalRepository.findAll();
        for (int i = 0; i<terminalsList.size(); i++)
        {
            terminalKeyManagement key = clientHandler.keyManagement(terminalsList.get(i));
            terminalKeyManagement terminalKeyManagement = terminalKeysRepo.findByTerminalID(key.getTerminalID());
            if (Objects.nonNull(terminalKeyManagement)){
                terminalKeyManagement.setParameterDownloaded(key.getParameterDownloaded());
                terminalKeyManagement.setMasterKey(key.getMasterKey());
                terminalKeyManagement.setSessionKey(key.getSessionKey());
                terminalKeyManagement.setPinKey(key.getPinKey());
                terminalKeyManagement.setLastExchangeDateTime(key.getLastExchangeDateTime());
                terminalKeysRepo.save(terminalKeyManagement);
            }else {
                terminalKeysRepo.save(key);
            }
        }
//        //todo check for failed exchanges
//        boolean flag = Boolean.parseBoolean(null);
//        int count = 0;
//        while (count<5){
//            flag = todoCheckForFailed(clientHandler);
//            if (flag==false){
//                break;
//            }
//            else {
//                flag = todoCheckForFailed(clientHandler);
//                count++;
//            }
//
//        }
//        //send email of completed and not completed

    }

    public String KeyExchangePerTID(String TerminalID){
        Socket s = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;
        ClientHandler clientHandler = new ClientHandler(s, dis,dos);

        Terminals terminal = terminalRepository.findByterminalID(TerminalID);
        if (Objects.nonNull(terminal)){
            terminalKeyManagement key = clientHandler.keyManagement(terminal);
            terminalKeyManagement terminalKey = terminalKeysRepo.findByTerminalID(TerminalID);
            if (Objects.nonNull(terminalKey)){

                terminalKey.setParameterDownloaded(key.getParameterDownloaded());
                terminalKey.setMasterKey(key.getMasterKey());
                terminalKey.setSessionKey(key.getSessionKey());
                terminalKey.setPinKey(key.getPinKey());
                terminalKey.setLastExchangeDateTime(key.getLastExchangeDateTime());
                terminalKeysRepo.save(terminalKey);

            }
            else {
                terminalKeysRepo.save(key);
            }
            return "Key Exchange Successful";
        }
        else {
            logger.info("Terminal Id exist Error");
            return "Terminal Id exist Error";
        }
    }


}


