//package com.jayrush.springmvcrest.fep;
//
//import integration._3line.CredentialManager;
//import integration._3line.events.NetworkEventListener;
//import integration._3line.exceptions.CryptoException;
//import integration._3line.exceptions.RequestProcessingException;
//import integration._3line.fcmb.service.ISOService;
//import integration._3line.model.*;
//import integration._3line.processors.*;
//import integration._3line.transaction.model.Transaction;
//import integration._3line.transaction.repository.TransactionRepository;
//import integration._3line.transaction.service.TransactionService;
//import integration._3line.utility.EncryptionUtil;
//import integration._3line.utility.IsoLogger;
//import integration._3line.utility.LogHelper;
//import org.apache.commons.codec.DecoderException;
//import org.apache.commons.codec.binary.Hex;
//import org.jpos.iso.ISOMsg;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.MessageSource;
//import org.springframework.context.i18n.LocaleContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * This class handles application level connection to the remote entity
// * It ensures, sign-on, key exchange and polling is done to properly establish connection between remote application
// * And handles sending of transaction messages to the remote application and returning responses to the caller
// * <p>
// * Created by ... on 24/11/2018.
// */
//@Component
//public class NetworkClient implements NetworkEventListener {
//
//    private static final Logger logger = LoggerFactory.getLogger(NetworkClient.class);
//
//    @Autowired
//    private SignOnProcessor signOnProcessor;
//    @Autowired
//    private KeyExchangeProcessor keyExchangeProcessor;
//    @Autowired
//    private PollingProcessor pollingProcessor;
//    @Autowired
//    private MessageSender messageSender;
//    @Autowired
//    private ThreelineMessageSender threelineMessageSender;
//    @Autowired
//    private CredentialManager credentialManager;
//    @Autowired(required = false)
//    private List<TransactionProcessor> processors = new ArrayList<>();
//    @Autowired
//    private ISOService isoService;
//    @Autowired
//    private TransactionRepository transactionRepository;
//    @Autowired
//    private TransactionService transactionService;
//    @Autowired
//    private MessageSource messageSource;
//    private Locale locale = LocaleContextHolder.getLocale();
//
//    @Value("${fcmb.nipaccount}")
//    private String fcmbNipAccount;
//
//    @Value("${fcmb.connection.poll.interval.millis:90000}")
//    private long pollInterval;
//
//    @Value("${fcmb.key:0E49CBCD5EA26BFE1F89F229528531BF}")
//    private String exchangeKey;
//
//    @Value("${switch.key}")
//    private String switchExchangeKey;
//
//    @Value("${tms.key:11111111111111111111111111111111}")
//    private String tmsKey;
//
//    @Value("${fcmb.kcv:60b2f2}")
//    private String kcv;
//
//    @Value("${bill.terminalid}")
//    private String billTerminalId;
//
//    @Value("${fcmb.3lineglaccount}")
//    private String fcmb3lineGlAccount;
//
//    @Value("${fcmb.easyclubaccount}")
//    private String fcmbEasyClubAccount;
//
//    private boolean isSignedUp;
//    private boolean isKeyExchanged;
//    private boolean isStarted;
//    private Timer signOnTimer;
//    private Timer keyExchangeTimer;
//    private Timer pollingTimer;
//    private final ExecutorService executors = Executors.newFixedThreadPool(10);
//
//
//    private final Object LOCK = new Object();
//
//    @Override
//    public void notifyConnect() {
//        logger.info("Connection event notified");
//        executors.submit(() -> {
//            doSignOn();
//        });
//    }
//
//    @Override
//    public void notifyConnectForThreeline() {
//        logger.info("Connection event for Threeline notified");
//        executors.submit(() -> {
//            doSignOnForThreeline();
//        });
//    }
//
//    @Override
//    public void notifyDisconnect() {
//        reset();
//    }
//
//    private void doSignOn() {
//        signOnTimer = new Timer("sign-on-timer");
//        signOnTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    synchronized (LOCK) {
//                        if (isSignedUp) {
//                            this.cancel();
//                            signOnTimer.cancel();
//                            doKeyExchange();
//                            return;
//                        }
//                        ISOMsg signOnMsg = signOnProcessor.createSignOnMessage();
//
//                        logger.info("Sign on message -> {}", IsoLogger.dump(signOnMsg));
//
//                        ISOMsg response = messageSender.processMessage(signOnMsg);
//
//                        logger.info("Sign on response -> {}", IsoLogger.dump(response));
//                        if (!isSuccessfulResponse(response)) {
//                            logger.error("Sign-on failed");
//                            return;
//                        }
//
//                        this.cancel();
//                        signOnTimer.cancel();
//                        isSignedUp = true;
//                        doKeyExchange();
//                    }
//                } catch (Exception e) {
//                    logger.error("Could not process sign-on message", e);
//                }
//            }
//        }, 0, 1000);
//    }
//
//    private void doKeyExchange() {
//        keyExchangeTimer = new Timer("key-exchange-timer");
//        keyExchangeTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    synchronized (LOCK) {
//                        if (isKeyExchanged) {
//                            this.cancel();
//                            keyExchangeTimer.cancel();
//                            return;
//                        }
//                        ISOMsg keyExchangeMsg = keyExchangeProcessor.createKeyExchangeMessage();
//
//                        logger.info("Key exchange message -> {}", IsoLogger.dump(keyExchangeMsg));
//
//                        ISOMsg response = messageSender.processMessage(keyExchangeMsg);
//
//                        logger.info("Key exchange response  -> {}", IsoLogger.dump(response));
//
//                        if (!isSuccessfulResponse(response)) {
//                            logger.error("Key exchange failed");
//                            return;
//                        }
//
//                        keyExchangeProcessor.processKeyExchangeResponse(response);
//                        this.cancel();
//                        keyExchangeTimer.cancel();
//                        isKeyExchanged = true;
//                        isStarted = true;
//                        doPolling();
//                    }
//                } catch (Exception e) {
//                    logger.error("There was an error doing key exchange", e);
//                }
//            }
//        }, 0, 1000);
//    }
//
//    private void doPolling() {
//        pollingTimer = new Timer("polling-timer");
//        final AtomicInteger failedAttemptCount = new AtomicInteger(0);
//        final int maxFailedAttemptCount = 5;
//        pollingTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    synchronized (LOCK) {
//                        if (failedAttemptCount.intValue() >= maxFailedAttemptCount) {
//                            this.cancel();
//                            pollingTimer.cancel();
//                            reset();
//                            messageSender.resetNetwork();
//                            return;
//                        }
//                        ISOMsg pollingMsg = pollingProcessor.createPollingMessage();
//
//                        logger.info("Polling message -> {}", IsoLogger.dump(pollingMsg));
//
//                        ISOMsg response = messageSender.processMessage(pollingMsg);
//
//                        logger.info("Polling Response  -> {}", IsoLogger.dump(response));
//
//                        if (!isSuccessfulResponse(response)) {
//                            failedAttemptCount.incrementAndGet();
//                        }
//                    }
//
//                } catch (Exception e) {
//                    logger.error("There was an error during polling", e);
//                }
//            }
//        }, 0, pollInterval);
//    }
//
//    private boolean isSuccessfulResponse(ISOMsg response) {
//        return response != null && ResponseCode.APPROVED.equals(response.getString(39));
//    }
//
//    private void reset() {
//        this.isKeyExchanged = false;
//        this.isSignedUp = false;
//        this.isStarted = false;
//        if (signOnTimer != null) {
//            signOnTimer.cancel();
//        }
//        if (keyExchangeTimer != null) {
//            keyExchangeTimer.cancel();
//        }
//        if (pollingTimer != null) {
//            pollingTimer.cancel();
//        }
//    }
//
//    /**
//     * Sends a {@link TransactionRequest} to a remote entity
//     * @param isoMsg the request to be sent
//     * @return a {@link TransactionResponse}
//     */
//    public ISOMsg sendRequest(ISOMsg isoMsg){
//        ISOMsg response = new ISOMsg();
//
//        if (isoMsg == null) {
//            TransactionResponse.fromCodeAndMessage(ResponseCode.ERROR, "Invalid request");
//            response.set(0, "0210");
//            response.set(39, "71");
//            response.set(112, messageSource.getMessage("71", null, locale));
//            return response;
//        }
//        //Check if bin exists in Fcmb database
//        boolean binResponse = isoService.checkIfBinExists(isoMsg);
//        logger.info("Log to know if BIN Exists {} ", binResponse);
//        String payeeDetails = isoMsg.getString(98);
//
//        //Save the transaction in the database
//        Transaction transaction = new Transaction();
//
//        try {
//            if (payeeDetails == null || payeeDetails.isEmpty()){
//                payeeDetails = "0000000000|PIN|0:0:0:0   ";
//                isoMsg.set(98, "0000000000|PIN|0:0:0:0   ");
//            }
//
//            TransactionProcessor processor = getProcessorThatCanProcess(payeeDetails);
//
//            if (!isoMsg.getString(0).equalsIgnoreCase("0420")) {
//                logger.info("Saving The Normal Transaction");
//                transaction = transactionService.saveTransactionRequest(isoMsg, processor);
//            }else {
//                logger.info("Saving A Reversal Transaction");
//                transaction = transactionService.saveReversalTransactionRequest(isoMsg);
//            }
//
//            logger.info("Logging Request From POS {} iso request -> {}", IsoLogger.dump(isoMsg));
//
//            //This block determines the method to use for various types of transaction
//            response = checkWhichTransactionToProcess(payeeDetails,isoMsg,transaction,binResponse);
//            System.out.println("The Iso Response " + response);
//
//            logger.info("Received iso response -> {}", IsoLogger.dump(response));
//            //Update already saved transaction with response details
//            transactionService.saveOrUpdateTranRequest(response, transaction);
//
//            TransactionResponse transactionResponse = processor.toTransactionResponse(response);
//
//            logger.info("Transaction response -> {}", LogHelper.dump(transactionResponse));
//
//            return response;
//        }
//        catch (Exception e) {
//            logger.error("Could not send request", e);
//            TransactionResponse.fromCodeAndMessage(ResponseCode.ERROR, "Error sending request");
//            String responseMessage = messageSource.getMessage("74", null, locale);
//            response.set(0, "0210");
//            response.set(39, "74");
//            response.set(112, responseMessage);
//            transaction.setStatus("FAILED");
//            transaction.setResponseMessage(responseMessage);
//            transactionRepository.save(transaction);
//            return response;
//        }
//    }
//
//    private ISOMsg checkWhichTransactionToProcess(String payeeDetails, ISOMsg isoMsg, Transaction transaction, boolean binResponse){
//        ISOMsg response= new ISOMsg();
//        try {
//            String payeeString = org.apache.commons.lang3.StringUtils.substringBetween(payeeDetails, "|", "|");
//            logger.info("The Payee String In The Check Method {}", payeeString);
//
//            switch (payeeString){
//                case "WDL":
//                    logger.info("In The Withdrawal Switch Case");
//                    if (binResponse) {
//                        logger.info("On us withdrawal");
//                        response = messageSender.processMessage(isoMsg);
//                        return response;
//                    } else {
//                        logger.info("Not on us withdrawal");
//                        transactionService.updateNotOnUs(isoMsg, transaction);
//                        String field52 = isoMsg.getString(52);
//                        String field52Decrypted = decryptPinBlock(field52);
//                        isoMsg.set(52, encryptPinBlockForThreeline(field52Decrypted));
//                        response = threelineMessageSender.processMessage(isoMsg);
//                        return response;
//                    }
//
//                case "FTR":
//                    logger.info("In The Transfer Switch Case");
//                    response = doTransfer(payeeDetails, isoMsg, isoMsg);
//                    return response;
//
//                case "BIL":
//                    logger.info("In The Bill Payment Switch Case");
//                    response = doBillPayment(isoMsg, isoMsg);
//                    return response;
//
//                case "EAC":
//                    logger.info("In The Easy Club Switch Case");
//                    response = doEasyClubPayment(isoMsg);
//                    return response;
//
//                default:
//                    logger.info("In The Default Switch Case");
//                    response = messageSender.processMessage(isoMsg);
//                    return response;
//
//            }
//
//        } catch (IOException e) {
//            logger.error("An IO error occurred", e);
//            TransactionResponse.fromCodeAndMessage(ResponseCode.ISSUER_OR_SWITCH_INOPERATIVE, "An IO error occurred");
//            String responseMessage = messageSource.getMessage("72", null, locale);
//            response.set(0, "0210");
//            response.set(39, "72");
//            response.set(112, responseMessage);
//            transaction.setStatus("FAILED");
//            transaction.setResponseMessage(responseMessage);
//            transactionRepository.save(transaction);
//            return response;
//        } catch (Exception e) {
//            logger.error("Could not send request", e);
//            TransactionResponse.fromCodeAndMessage(ResponseCode.ERROR, "Error sending request");
//            String responseMessage = messageSource.getMessage("73", null, locale);
//            response.set(0, "0210");
//            response.set(39, "73");
//            response.set(112, responseMessage);
//            transaction.setStatus("FAILED");
//            transaction.setResponseMessage(responseMessage);
//            transactionRepository.save(transaction);
//            return response;
//        }
////        logger.info("Check Transaction Iso Response {}", response);
////        return response;
//
//    }
//
//    private ISOMsg doBillPayment(ISOMsg isoMsg, ISOMsg intraIso){
//        ISOMsg isoResponse = new ISOMsg();
//        try {
//            logger.info("Doing Bill Payment");
//                isoMsg.unset(103);
//                isoMsg.set(103, fcmb3lineGlAccount);
//                intraIso.unset(103);
//                intraIso.set(103, fcmb3lineGlAccount);
//                intraIso.set(105, "1");
//                intraIso.set(104, "000003190712174020"+isoMsg.getString(37));
//                intraIso.set(115, "000010");
//                intraIso.set(117, isoMsg.getString(4));
//                String payeeIntra = (intraIso.getString(98)).replace("BIL", "FTR");
//                intraIso.set(98, payeeIntra);
//                intraIso.set(110, "INTRA");
//                logger.info("Sending Intra For Billpayment {} iso request -> {}", IsoLogger.dump(intraIso));
//                ISOMsg fundsTransferResponse = messageSender.processMessage(intraIso);
//                String responseCode = fundsTransferResponse.getString(39);
//                String mtii = fundsTransferResponse.getString(0);
//                if (responseCode != null && responseCode.equals("00") && mtii.equalsIgnoreCase("0210")) {
//                    isoResponse = transactionService.processBillPayment(isoMsg);
//                } else {
//                    isoResponse = fundsTransferResponse;
//                }
//        }catch (Exception ex){
//            logger.error("Error Bill Payment ==> ", ex);
//        }
//        return isoResponse;
//    }
//
//    private ISOMsg doEasyClubPayment(ISOMsg isoMsg){
//        ISOMsg isoResponse = new ISOMsg();
//        try {
//            logger.info("Doing Easy Club Payment");
//            isoMsg.unset(103);
//            isoMsg.set(103, fcmbEasyClubAccount);
//            isoMsg.set(104, "000003190712174020"+isoMsg.getString(37));
//            isoMsg.set(117, isoMsg.getString(4));
//            String payeeIntra = (isoMsg.getString(98)).replace("EAC", "FTR");
//            isoMsg.set(98, payeeIntra);
//            logger.info("Sending Intra For EasyClub Payment {} iso request -> {}", IsoLogger.dump(isoMsg));
//            isoResponse = messageSender.processMessage(isoMsg);
//            return isoResponse;
//        }catch (Exception ex){
//            logger.error("Error Easy Club Payment ==> ", ex);
//        }
//        return isoResponse;
//    }
//
//
//    private ISOMsg doTransfer(String payee, ISOMsg balIsoMsg, ISOMsg isoMsg){
//        ISOMsg response = new ISOMsg();
//        logger.info("The Amount {} ", isoMsg.getString(4));
//        logger.info("The Payee Details For Transfer {} ", payee);
//        String amount = isoMsg.getString(4);
//        String account = isoMsg.getString(103);
//        String bankIndicator = isoMsg.getString(110);
//        String magtipon = isoMsg.getString(105);
//        logger.info("The indicator {}", bankIndicator);
//            try {
//                    if (bankIndicator != null && bankIndicator.equalsIgnoreCase("INTRA")) {
//                        String processingCode = isoMsg.getString(3);
//                        String balanceInquiryProCode = StringUtils.replace(processingCode, "50", "31");
//                        logger.info("The new Process Code Bal Inq {} ", balanceInquiryProCode);
//                        balIsoMsg.set(3, balanceInquiryProCode);
//                        balIsoMsg.set(4, "");
//                        balIsoMsg.unset(103);
//                        String payeeBalInq = (balIsoMsg.getString(98)).replace("FTR", "BAL");
//                        balIsoMsg.set(98, payeeBalInq);
//                        logger.info("Sending Balance Inquiry Intra {} iso request -> {}", IsoLogger.dump(balIsoMsg));
//                        ISOMsg balInqResponse = messageSender.processMessage(balIsoMsg);
//                        String balInqResponseCode = balInqResponse.getString(39);
//                        String mtii = balInqResponse.getString(0);
//                        logger.info("Bal Inquiry Response Code {} ", balInqResponseCode);
//                        if (balInqResponseCode != null && balInqResponseCode.equals("00")) {
//                            isoMsg.set(4, amount);
//                            isoMsg.set(37, balInqResponse.getString(37));
//                            isoMsg.set(103, account);
//                            isoMsg.unset(102);
//                            isoMsg.set(102, balInqResponse.getString(102));
//                            isoMsg.set(98, payee);
//                            logger.info("Before calling the transfer method ");
//                            logger.info("The Amount {} ", isoMsg.getString(4));
//                            response = transactionService.processTranfer(isoMsg, isoMsg);
//                        } else {
//                            response.set(0, "0210");
//                            response.set(39, balInqResponseCode);
//                        }
//                    } else {
//                        String field103 = isoMsg.getString(103);
//                        logger.info("The Field 103 => {}", field103);
//                        isoMsg.unset(103);
//                        isoMsg.set(103, fcmbNipAccount);
//                        logger.info("Sending Intra For NIP {} iso request -> {}", IsoLogger.dump(isoMsg));
//                        response = messageSender.processMessage(isoMsg);
//                        String mtii = response.getString(0);
//                        String intraIsoResponse = response.getString(39);
//                        logger.info("For Inter Bank, Intra Iso Response Code {}", intraIsoResponse);
//                        if (intraIsoResponse != null && intraIsoResponse.equals("00") && mtii.equalsIgnoreCase("0210")) {
//                            isoMsg.unset(103);
//                            isoMsg.set(103, field103);
//                            response = transactionService.processTranfer(isoMsg, isoMsg);
//                        } else {
//                            response.set(0, "0210");
//                            response.set(39, intraIsoResponse);
//                        }
//                    }
//                return response;
//            }catch (Exception e) {
//                logger.error("An IO error occurred", e);
//                TransactionResponse.fromCodeAndMessage(ResponseCode.ISSUER_OR_SWITCH_INOPERATIVE, "An IO error occurred");
//                response.set(0, "0210");
//                response.set(39, "79");
//                return response;
//
//        }
////        return response;
//    }
//
//    private TransactionProcessor getProcessorThatCanProcess(String code) throws RequestProcessingException {
//        logger.info("The processing code {} ", code);
//        for (TransactionProcessor processor : processors) {
//            if (processor.canProcess(code)) {
//                return processor;
//            }
//        }
//        throw new RequestProcessingException("Cannot find processor to process the request");
//    }
//
//    private String encryptPinBlock(String pinBlock) throws CryptoException {
//        if (StringUtils.isEmpty(pinBlock)) {
//            return pinBlock;
//        }
//        byte[] clearPinBlockBytes;
//        byte[] zpk;
//        try {
//            clearPinBlockBytes = Hex.decodeHex(pinBlock.toCharArray());
//            zpk = Hex.decodeHex(exchangeKey.toCharArray());
//        } catch (DecoderException e) {
//            throw new CryptoException("Could not decode pin block", e);
//        }
//
//        byte[] encryptedPinBlockBytes = EncryptionUtil.tdesEncryptECB(clearPinBlockBytes, zpk);
//
//        return new String(Hex.encodeHex(encryptedPinBlockBytes));
//
//    }
//
//    private String decryptPinBlock(String pinBlock) throws CryptoException {
//        try {
//            byte[] tmsKeyBytes = Hex.decodeHex(tmsKey.toCharArray());
//            byte[] pinBlockBytes = Hex.decodeHex(pinBlock.toCharArray());
//
//            byte[] clearPinBlockBytes = EncryptionUtil.tdesDecryptECB(pinBlockBytes, tmsKeyBytes);
//
//            return new String(Hex.encodeHex(clearPinBlockBytes));
//        } catch (DecoderException e) {
//            throw new CryptoException("Could not decode hex key", e);
//        }
//    }
//
//    private String encryptPinBlockForThreeline(String pinBlock) throws CryptoException {
//        logger.info("The pin block bytes {} ", pinBlock);
//        if (StringUtils.isEmpty(pinBlock)) {
//            return pinBlock;
//        }
//        byte[] clearPinBlockBytes;
//        byte[] zpk;
//        try {
//            clearPinBlockBytes = Hex.decodeHex(pinBlock.toCharArray());
//            logger.info("The clear pin block bytes {} ", clearPinBlockBytes);
//            zpk = Hex.decodeHex(switchExchangeKey.toCharArray());
//            logger.info("The clear zpk {} ", switchExchangeKey.toCharArray());
//        } catch (DecoderException e) {
//            throw new CryptoException("Could not decode pin block for Threeline", e);
//        }
//
//        byte[] encryptedPinBlockBytes = EncryptionUtil.tdesEncryptECB(clearPinBlockBytes, zpk);
//
//        return new String(Hex.encodeHex(encryptedPinBlockBytes));
//
//    }
//
//    private void doSignOnForThreeline() {
//        signOnTimer = new Timer("sign-on-timer");
//        signOnTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    synchronized (LOCK) {
//                        if (isSignedUp) {
//                            this.cancel();
//                            signOnTimer.cancel();
////                            doKeyExchangeForThreeline();
//                            return;
//                        }
//                        ISOMsg signOnMsg = signOnProcessor.createSignOnMessage();
//
//                        logger.info("Three Line Sign on message -> {}", IsoLogger.dump(signOnMsg));
//
//                        ISOMsg response = threelineMessageSender.processMessage(signOnMsg);
//
//                        logger.info("Three Line Sign on response -> {}", IsoLogger.dump(response));
//                        if (!isSuccessfulResponse(response)) {
//                            logger.error("Three Line Sign-on failed");
//                            return;
//                        }
//
//                        this.cancel();
//                        signOnTimer.cancel();
//                        isSignedUp = true;
////                        doKeyExchangeForThreeline();
//                    }
//                } catch (Exception e) {
//                    logger.error("Could not process Three Line sign-on message", e);
//                }
//            }
//        }, 0, 1000);
//    }
//
//    private void doKeyExchangeForThreeline() {
//        keyExchangeTimer = new Timer("key-exchange-timer");
//        keyExchangeTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    synchronized (LOCK) {
//                        if (isKeyExchanged) {
//                            this.cancel();
//                            keyExchangeTimer.cancel();
//                            return;
//                        }
//                        ISOMsg keyExchangeMsg = keyExchangeProcessor.createKeyExchangeMessage();
//
//                        logger.info("Three Line Key exchange message -> {}", IsoLogger.dump(keyExchangeMsg));
//
//                        ISOMsg response = threelineMessageSender.processMessage(keyExchangeMsg);
//
//                        logger.info("Three Line Key exchange response  -> {}", IsoLogger.dump(response));
//
//                        if (!isSuccessfulResponse(response)) {
//                            logger.error("Three Line Key exchange failed");
//                            return;
//                        }
//
//                        keyExchangeProcessor.processKeyExchangeResponse(response);
//                        this.cancel();
//                        keyExchangeTimer.cancel();
//                        isKeyExchanged = true;
//                        isStarted = true;
//                        doPollingForThreeline();
//                    }
//                } catch (Exception e) {
//                    logger.error("There was an error doing key exchange", e);
//                }
//            }
//        }, 0, 1000);
//    }
//
//    private void doPollingForThreeline() {
//        pollingTimer = new Timer("polling-timer");
//        final AtomicInteger failedAttemptCount = new AtomicInteger(0);
//        final int maxFailedAttemptCount = 5;
//        pollingTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    synchronized (LOCK) {
//                        if (failedAttemptCount.intValue() >= maxFailedAttemptCount) {
//                            this.cancel();
//                            pollingTimer.cancel();
//                            reset();
//                            threelineMessageSender.resetNetwork();
//                            return;
//                        }
//                        ISOMsg pollingMsg = pollingProcessor.createPollingMessage();
//
//                        logger.info("Three Line Polling message -> {}", IsoLogger.dump(pollingMsg));
//
//                        ISOMsg response = threelineMessageSender.processMessage(pollingMsg);
//
//                        logger.info("Three Line Polling Response  -> {}", IsoLogger.dump(response));
//
//                        if (!isSuccessfulResponse(response)) {
//                            failedAttemptCount.incrementAndGet();
//                        }
//                    }
//
//                } catch (Exception e) {
//                    logger.error("There was an error during Three Line polling", e);
//                }
//            }
//        }, 0, pollInterval);
//    }
//
//
//}
