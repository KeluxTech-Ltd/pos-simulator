package com.jayrush.springmvcrest.fep;

import com.jayrush.springmvcrest.Nibss.processor.IsoProcessor;
import com.jayrush.springmvcrest.PostBridgePackager;
import com.jayrush.springmvcrest.iso8583.IsoMessage;
import com.jayrush.springmvcrest.iso8583.MessageFactory;
import org.jpos.iso.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.jayrush.springmvcrest.Nibss.processor.IsoProcessor.printIsoFields;

/**
 * @author JoshuaO
 */
public class ISWprocessor {
    private static final Logger logger = LoggerFactory.getLogger(ISWprocessor.class);

//    public byte[] toFEP(byte[]fromPOS) throws IOException, ParseException {
//        StanCounter stanCounter = new StanCounter();
//        final IsoMessage isoMessage = null;
//        final MessageFactory<IsoMessage> responseMessageFactory = (MessageFactory<IsoMessage>) new MessageFactory();
//        responseMessageFactory.addMessageTemplate(isoMessage);
//        responseMessageFactory.setAssignDate(true);
//        responseMessageFactory.setUseBinaryBitmap(false);
//        responseMessageFactory.setUseBinaryMessages(false);
//        responseMessageFactory.setEtx(-1);
//        responseMessageFactory.setIgnoreLastMissingField(false);
//        responseMessageFactory.setConfigPath(IsoProcessor.CONFIG_FILE);
//        IsoMessage responseMessage = responseMessageFactory.parseMessage(fromPOS, 0);
//        printIsoFields(responseMessage, "ISO message ====> ");
//
//
//        ISOPackager packager = new PostBridgePackager();
//        ISOMsg msg = new ISOMsg();
//
//        try {
//            for (int i = 0; i<128; i++){
////                if (responseMessage.hasField(i)){
////                    System.out.println("Field value "+responseMessage.getObjectValue(i).toString());
////                    msg.set(i,responseMessage.getObjectValue(i).toString());
////                }
//            }
//            String fromPOSmessage = bytesToHex(fromPOS);
//            String mti = toAscii(fromPOSmessage);
//            if (mti.startsWith("0800")){
//                ISOMsg isoMsg = new ISOMsg();
//                Date now = new Date();
//
//
//                msg.setMTI("0800");
//                msg.set(7, ISODate.getDateTime(now));
//                isoMsg.set(11, stanCounter.getStan());
//                isoMsg.set(12, ISODate.getTime(now));
//                isoMsg.set(13, ISODate.getDate(now));
//                isoMsg.set(70, "101");
//
//            }
//            else if(mti.startsWith("0200")){
//                msg.setMTI("0200");
//            }
//            logger.info("Iso Message : {}" , msg);
//            msg.setPackager(packager);
//            byte[] data=msg.pack();
//            byte[] fullMessage = prependLenBytes(data);
//
//            return fullMessage;
//        } catch (ISOException e) {
//            logger.info(e.getMessage());
//            return null;
//        }
//    }
    public byte[] toFEP(byte[]fromPOS) throws IOException, ParseException, RequestProcessingException, ISOException {
        StanCounter stanCounter = new StanCounter();
        ISOPackager packager = new PostBridgePackager();
        ISOMsg isoMsg = new ISOMsg();
        Date now = new Date();
        try {
            isoMsg.setMTI("0800");
        } catch (ISOException e) {
            throw new RequestProcessingException("Could not set request mti", e);
        }

        isoMsg.set(7, ISODate.getDateTime(now));
        isoMsg.set(11, stanCounter.getStan());
        isoMsg.set(12, ISODate.getTime(now));
        isoMsg.set(13, ISODate.getDate(now));
        isoMsg.set(70, "001");

        byte[] message;
        try {
            isoMsg.setPackager(packager);
            message = isoMsg.pack();
        } catch (ISOException e) {
            throw new RequestProcessingException("Could not pack sign-on iso message", e);
        }

        byte[] fullMessage = prependLenBytes(message);

        return fullMessage;
    }

    private static String toAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static byte[] prependLenBytes(byte[] data) {
        short len = (short) data.length;
        byte[] newBytes = new byte[len + 2];
        newBytes[0] = (byte) (len / 256);
        newBytes[1] = (byte) (len & 255);
        System.arraycopy(data, 0, newBytes, 2, len);
        return newBytes;
    }

    public static void main(String[]args){
        ISOPackager packager = new PostBridgePackager();
        ISOMsg msg = new ISOMsg();

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            msg.setPackager(packager);
            msg.setMTI("0800");
            msg.set(7,"1210121715");
            msg.set(11,"000584");
            msg.set(12,"121715");
            msg.set(13,"1210");
            msg.set(70,"301");

            byte[]bytes = msg.pack();
            msg.dump(ps,"");

            byte[] fullMessage = prependLenBytes(bytes);
            String packedHex = new String(fullMessage);
            String messagesent = bytesToHex(fullMessage);
            System.out.println(messagesent);
            msg.unpack(bytes);

            msg.dump(ps,"");

            System.out.println(msg);
        } catch (ISOException e) {
            e.printStackTrace();
        }
    }

//    public ISOMsg processMessage(ISOMsg request) throws RequestProcessingException, IOException {
//        PostBridgePackager packager = new PostBridgePackager();
//        request.setPackager(packager);
//
//        byte[] message;
//        try {
//            message = request.pack();
//        } catch (ISOException e) {
//            throw new RequestProcessingException("Could not pack sign-on iso message", e);
//        }
//
//
//        byte[] fullMessage = prependLenBytes(message);
//
//        try {
//            networkConfig.write(fullMessage);
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


}
