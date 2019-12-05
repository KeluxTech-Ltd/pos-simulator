//package com.jayrush.springmvcrest.fep;
//
//import integration._3line.exceptions.RequestProcessingException;
//import integration._3line.exceptions.ResponseMatcherException;
//import integration._3line.fcmb.model.wallet.WalletFCMBTransfer;
//import integration._3line.fcmb.service.ISOService;
//import integration._3line.fcmb.service.WalletService;
//import integration._3line.matchers.ResponseMatcher;
//import integration._3line.matchers.ResponseMatcherFactory;
//import integration._3line.model.Response;
//import integration._3line.packager.PostBridgePackager;
//import integration._3line.processors.TransferProcessor;
//import integration._3line.transaction.service.TransactionService;
//import integration._3line.utility.IsoLogger;
//import integration._3line.utility.IsoMsgAdapter;
//import org.jpos.iso.ISOException;
//import org.jpos.iso.ISOMsg;
//import org.jpos.iso.ISOPackager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.function.Consumer;
//
///**
// * Created by ... on 24/11/2018.
// */
//@Component
//public class ThreelineMessageSender implements Consumer<byte[]> {
//
//    private static final Logger logger = LoggerFactory.getLogger(ThreelineMessageSender.class);
//
//    @Autowired
//    private ThreelineSwitchConfig threelineSwitchConfig;
//    @Autowired
//    private ResponseMatcherFactory responseMatcherFactory;
//    @Autowired
//    private ISOService isoService;
//    @Autowired
//    private MessageSender messageSender;
//    @Autowired
//    private TransactionService transactionService;
//    @Autowired
//    private TransferProcessor transferProcessor;
//    @Autowired
//    private WalletService walletService;
//    private final ExecutorService executors = Executors.newFixedThreadPool(2);
//
//    @Value("${fcmb.connection.timeout:90000}")
//    private int timeout;
//
//    @Value("${fcmb.notonus.pan}")
//    private String fcmbNotOnUsPan;
//
//    @Value("${3line.incomeamount}")
//    private Double _3lineIncomeAmount;
//
//    private ISOPackager packager = new PostBridgePackager();
//    private ConcurrentHashMap<String, ResponseMatcher> responseMatcherMap = new ConcurrentHashMap<>();
//
//    @PostConstruct
//    public void init() {
//        threelineSwitchConfig.setResponseConsumer(this);
//    }
//
//    /**
//     * Handles ISOMsg processing, takes an ISOMsg as a request, packages it using the specified packager
//     * and sends to the remote entity using the network client
//     *
//     * @param request the request to be sent out
//     * @return an ISOMsg as the response received from the remote entity
//     * @throws RequestProcessingException if the message cannot be packed as bytes, a response matcher could not be gotten or the matcher key is not unique
//     * @throws IOException                if the networkClient cannot write the message to the remote entity or a timeout occurred
//     */
//    public ISOMsg processMessage(ISOMsg request) throws RequestProcessingException, IOException {
//        String field117=request.getString(117);
//        String field118=request.getString(118);
//        String field119=request.getString(119);
//        request.unset(117);
//        request.unset(118);
//        request.unset(119);
//        request.setPackager(packager);
//
//        byte[] message;
//        try {
//            message = request.pack();
//        } catch (ISOException e) {
//            throw new RequestProcessingException("Could not pack sign-on iso message", e);
//        }
//
//        ResponseMatcher responseMatcher = getResponseMatcher(request);
//        logger.info("The Response Matcher {} ", responseMatcher.getKey());
//        if (responseMatcher == null) {
//            throw new RequestProcessingException("Could not get response matcher for request");
//        }
//
//        if (responseMatcherMap.containsKey(responseMatcher.getKey())) {
//            throw new RequestProcessingException("Could not get unique matcher key for request");
//        }
//
//        responseMatcherMap.put(responseMatcher.getKey(), responseMatcher);
//        logger.info("The Response Matcher Received {} ", responseMatcherMap.toString());
//
//        byte[] fullMessage = prependLenBytes(message);
//
//        try {
//                System.out.println("In the Three Line Switch");
//                threelineSwitchConfig.write(fullMessage);
//        } catch (IOException e) {
//            responseMatcherMap.remove(responseMatcher.getKey());
//            throw e;
//        }
//
//        try {
//            synchronized (responseMatcher) {
//                responseMatcher.wait(timeout);
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            throw new IOException("There was an error waiting for response from upstream", e);
//        } finally {
//            responseMatcherMap.remove(responseMatcher.getKey());
//        }
//
//        if (responseMatcher.getResponse() == null) {
//            throw new IOException("The message timed out");
//        }
//
//        ISOMsg isoResponse = responseMatcher.getResponse();
//        String processCode = isoResponse.getString(3);
//        if (processCode != null && processCode.contains("50") && isoResponse.getString(39).equals("00") && isoResponse.getString(0).equals("0210")) {
//            request.set(117, field117);
//            request.set(118, field118);
//            request.set(119, field119);
//            isoResponse = doCashOutWithdrawal(isoResponse, request);
//        }
//
//        return isoResponse;
//    }
//
//
//    public ISOMsg doCashOutWithdrawal(ISOMsg isoMsg, ISOMsg isoMsgRequest){
//        try {
//            WalletFCMBTransfer walletFCMBTransfer = IsoMsgAdapter.isoRequestToObjectIntraBankWithdrwal(isoMsg, isoMsgRequest);
//            walletFCMBTransfer.setPan(fcmbNotOnUsPan);
//            String totalFee = walletFCMBTransfer.getFee();
//            Double fee = 0.0;
//            if (totalFee != null && !totalFee.isEmpty()){
//                Double totalFeeDouble = Double.valueOf(totalFee);
//                if ( _3lineIncomeAmount < totalFeeDouble){
//                    fee = totalFeeDouble - _3lineIncomeAmount;
//                    logger.info("The Fee After 3line's income {}", fee);
//                    walletFCMBTransfer.setFee(String.valueOf(fee));
//                }
//            }
//            Response response = walletService.transferToFCMBWallet(walletFCMBTransfer);
//            logger.info("Logging the response gotten from Settlement {} ", response);
//            transactionService.saveTransactionRequest(isoMsg, transferProcessor);
//            logger.info("Settlement done successfully {} ", response);
//            isoMsg.set(119, walletFCMBTransfer.toString());
//            isoMsg.set(120, response.toString());
//            if(response.getRespCode() != null && Integer.valueOf(response.getRespCode()) < 100 ){
//                isoMsg.set(39, response.getRespCode());
//            }else {
//                isoMsg.set(39, "97");
//            }
//            if(!response.getRespCode().equals("00")){
//                isoMsgRequest.unset(117);
//                isoMsgRequest.unset(118);
//                isoMsgRequest.unset(119);
//                isoMsgRequest.set(0, "0420");
//                processMessage(isoMsgRequest);
//            }
//        }catch (Exception e){
//            logger.error("Error ", e);
//        }
//        return isoMsg;
//    }
//
//    private ResponseMatcher getResponseMatcher(ISOMsg msg) throws RequestProcessingException {
//        try {
//            return responseMatcherFactory.getMatcher(msg);
//        } catch (ResponseMatcherException e) {
//            throw new RequestProcessingException("There was an error getting response matcher", e);
//        }
//    }
//
//    private static byte[] prependLenBytes(byte[] data) {
//        short len = (short) data.length;
//        byte[] newBytes = new byte[len + 2];
//        newBytes[0] = (byte) (len / 256);
//        newBytes[1] = (byte) (len & 255);
//        System.arraycopy(data, 0, newBytes, 2, len);
//        return newBytes;
//    }
//
//    /**
//     * Accepts a byte array and converts to an ISOMsg used to notify waiting responseMatcher threads of returned responses
//     *
//     * @param bytes the data to be processed as a response
//     */
//    @Override
//    public void accept(byte[] bytes) {
//        ISOMsg responseIso = new ISOMsg();
//        try {
//            packager.unpack(responseIso, bytes);
//        } catch (ISOException e) {
//            logger.error("Could not unpack response iso bytes", e);
//        }
//
//        String responseMatchKey = responseMatcherFactory.getMatcherKey(responseIso);
//        logger.info("The Response Matcher Received {} ", responseMatchKey);
//        if (responseMatchKey != null) {
//            ResponseMatcher responseMatcher = responseMatcherMap.get(responseMatchKey);
//            if (responseMatcher != null) {
//                logger.info("The Response Matcher Single Matcher {} ", responseMatchKey);
//                responseMatcher.setResponse(responseIso);
//                logger.info("The Actual Response Gotten {}", IsoLogger.dump(responseIso));
//                synchronized (responseMatcher) {
//                    responseMatcher.notify();
//                }
//                return;
//            }
//        }
//        logger.error("Could not get response matcher for response {}", IsoLogger.dump(responseIso));
//    }
//
//    public void resetNetwork() {
//        threelineSwitchConfig.reset();
//    }
//}
