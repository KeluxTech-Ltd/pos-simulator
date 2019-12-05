//package com.jayrush.springmvcrest.fep;
//
//import integration._3line.exceptions.RequestProcessingException;
//import integration._3line.exceptions.ResponseMatcherException;
//import integration._3line.fcmb.service.ISOService;
//import integration._3line.matchers.ResponseMatcher;
//import integration._3line.matchers.ResponseMatcherFactory;
//import integration._3line.packager.PostBridgePackager;
//import integration._3line.transaction.service.TransactionService;
//import integration._3line.utility.IsoLogger;
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
//import java.util.function.Consumer;
//
///**
// * Created by ... on 24/11/2018.
// */
//@Component
//public class MessageSender implements Consumer<byte[]> {
//
//    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);
//
//    @Autowired
//    private NetworkConfig networkConfig;
//    @Autowired
//    private ResponseMatcherFactory responseMatcherFactory;
//    @Autowired
//    private ISOService isoService;
//    @Autowired
//    private TransactionService transactionService;
//
//    @Value("${fcmb.connection.timeout:90000}")
//    private int timeout;
//
//    private ISOPackager packager = new PostBridgePackager();
//    private ConcurrentHashMap<String, ResponseMatcher> responseMatcherMap = new ConcurrentHashMap<>();
//
//    @PostConstruct
//    public void init() {
//        networkConfig.setResponseConsumer(this);
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
//                networkConfig.write(fullMessage);
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
//            logger.info("The Message Timed Out");
//            transactionService.updateStatusAfterTimeout(request);
//            throw new IOException("The message timed out");
//        }
//
//        return responseMatcher.getResponse();
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
//        logger.info("The Response Matcher Received In Accept{} ", responseMatchKey);
//        if (responseMatchKey != null) {
//            ResponseMatcher responseMatcher = responseMatcherMap.get(responseMatchKey);
//            if (responseMatcher != null) {
//                logger.info("The Response Matcher Single Matcher In Accept {} ", responseMatchKey);
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
//        networkConfig.reset();
//    }
//}
