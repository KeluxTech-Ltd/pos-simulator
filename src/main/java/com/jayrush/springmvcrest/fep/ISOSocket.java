//package com.jayrush.springmvcrest.fep;
//
//import integration._3line.packager.PostBridgePackager;
//import integration._3line.utility.EncryptionUtil;
//import integration._3line.utility.MainConverter;
//import integration._3line.utility.Utils;
//import org.apache.commons.codec.binary.Hex;
//import org.jpos.iso.ISOException;
//import org.jpos.iso.ISOMsg;
//import org.jpos.iso.ISOPackager;
//import org.jpos.iso.ISOUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.net.*;
//import java.security.SecureRandom;
//
///**
// * @author JoyU
// * @date 11/12/2018
// */
//
//@Service
//public class ISOSocket {
//
//    Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Value("${fcmb.url}")
//    private String fcmbUrl;
//
//    @Value("${fcmb.port}")
//    private int fcmbPort;
//
//    @Value("${3line.port}")
//    private int socketPort;
//
//    private String zpk;
//
//    private PrintWriter out;
//    private BufferedReader in;
//    private ServerSocket serverSocket ;
//    private Socket clientSocket;
//
//    private ISOPackager packager = new PostBridgePackager();
//
//
//    public ISOMsg stringToISOMessage(String isoData){
//
//        String messageLenHex = setMessageLengthHexed(isoData);
//        logger.info("Message Len Hex {} ", messageLenHex);
//        byte[] request = ISOUtil.hex2byte(isoData.getBytes(), 0, isoData.length() / 2);
//        logger.info("The unpacked Byte {} ", request);
//        ISOMsg isoMsg = unpackISOMessage(request);
//
//        return isoMsg;
//    }
//
//    public ISOMsg sendISORequest(String isoData){
//       ISOMsg isoMsg = new ISOMsg();
//        InputStream inputStream = null;
//
//        String messageLenHex = setMessageLengthHexed(isoData);
//        byte[] b = MainConverter.parseHexString(messageLenHex);
//
//            try {
//                //Create socket address with bank ip and port
//                InetAddress inetAddress = InetAddress.getByName(fcmbUrl);
//                SocketAddress socketAddress = new InetSocketAddress(inetAddress, fcmbPort);
//
//                //Instantiate socket; set keepalive and timeout
//                Socket socket = new Socket();
//                socket.connect(socketAddress);
//                socket.setKeepAlive(true);
//                socket.setSoTimeout(60000);
//
//                //Get socket output stream
//                OutputStream outputStream = socket.getOutputStream();
//                inputStream = socket.getInputStream();
//                outputStream.write(b);
//                outputStream.flush();
//                logger.info("The Output Stream {} ", outputStream);
//
//                //Trying to get response from server
//                if (socket.isConnected()) {
//                    System.out.println("-- RESPONSE recieved---");
//                    logger.info("Input Stream {} ", inputStream);
//                    byte[] responseByte = readResponse(inputStream);
//                    isoMsg = unpackISOMessage(responseByte);
//                    logger.info("The Iso Message Received {} ", isoMsg);
//                }
//            }catch (Exception ex){
//                ex.printStackTrace();
//                logger.error("The ISO Socket Error {} ", ex.getCause().getMessage());
//            }
//
//            return isoMsg;
//    }
//
//    private static String setMessageLengthHexed(String messageHex) {
//        // Determine the length of message
//        int messageLength = messageHex.length() / 2;
//        //Determination of the header in the length of two bytes according to ARKSYS v2.2
//        String messageLengthHexed = Integer.toString(messageLength, 16);
//        while (messageLengthHexed.length() < 4)
//            messageLengthHexed = "0" + messageLengthHexed;
//
//        return messageLengthHexed.toUpperCase() + messageHex;
//    }
//
//    private ISOMsg unpackISOMessage(byte[] message){
//        ISOMsg isoMsg = new ISOMsg();
//        try {
//            //Setting the Post packager
//            isoMsg.setPackager(packager);
//            isoMsg.unpack(message);
//            logger.info("The Unpacked Message {} ", isoMsg);
////            logISOMsg(isoMsg);
//        } catch (ISOException e) {
//            e.printStackTrace();
//        }
//        return isoMsg;
//    }
//
//
//    public byte[] readResponse(InputStream inputStream) throws IOException {
//        String messageHexStringResponse = "";
//
//        byte[] messageLengthByteArray = new byte[2];
//        int messageLengthByte = inputStream.read(messageLengthByteArray);
//        logger.info("The message Length Byte {} ", messageLengthByte);
//
//        StringBuffer sb = null;
//        String messageLengthHex = null;
//        String messageDataHex = "";
//        int messageDataByte = 0;
//        logger.info("Logging The message Data Byte");
//
//
//        messageLengthHex = MainConverter.hexify(messageLengthByteArray);
//
//        messageDataHex = Utils.removeLeadingZeros(messageLengthHex);
//        messageDataByte = Integer.parseInt(messageDataHex, 16);
//        logger.info("The message Data Byte {} ", messageDataByte);
//
//        try {
//            sb = new StringBuffer(50);
//            sb.append("messageLengthByte:").append(messageLengthByte);
//            messageLengthHex = MainConverter.hexify(messageLengthByteArray);
//            sb.append(", messageLengthHex:").append(messageLengthHex);
//            messageDataHex = Utils.removeLeadingZeros(messageLengthHex);
//            sb.append(", messageDataHex:").append(messageDataHex);
//            messageDataByte = Integer.parseInt(messageDataHex, 16);
//            sb.append(", messageDataByte:").append(messageDataByte);
//        } finally {
//        }
//
//        // Reading the message itself; MessageDataProcitedDateByte is the number of received bytes
//        byte[] messageByteArray = new byte[messageDataByte];
//        int messageDataProcitedDataByte = inputStream.read(messageByteArray); // IOException - if an I/O error occurs
//        messageHexStringResponse = MainConverter.hexify(messageByteArray);
//        logger.info("ISO Message {} ", messageHexStringResponse);
//        logger.info("ISO Message Byte Array {} ", messageByteArray);
//        return messageByteArray;
//
//    }
//
//    public byte[] read(String messageLengthHex){
//        StringBuffer sb = null;
////        String messageLengthHex = null;
//        String messageDataHex = "";
//        int messageDataByte = 0;
//        logger.info("Logging The message Data Byte");
//
////        messageLengthHex = MainConverter.hexify(messageLengthByteArray);
////        messageDataHex = Utils.removeLeadingZeros(messageLengthHex);
//        messageDataByte = Integer.parseInt(messageLengthHex, 16);
//        logger.info("The message Data Byte {} ", messageDataByte);
//
//        try {
//            sb = new StringBuffer(50);
////            sb.append("messageLengthByte:").append(messageLengthByte);
////            messageLengthHex = MainConverter.hexify(messageLengthByteArray);
//            sb.append(", messageLengthHex:").append(messageLengthHex);
//            messageDataHex = Utils.removeLeadingZeros(messageLengthHex);
//            sb.append(", messageDataHex:").append(messageDataHex);
//            messageDataByte = Integer.parseInt(messageDataHex, 16);
//            sb.append(", messageDataByte:").append(messageDataByte);
//        } finally {
//        }
//
//        // Reading the message itself; MessageDataProcitedDateByte is the number of received bytes
//        byte[] messageByteArray = new byte[messageDataByte];
////        int messageDataProcitedDataByte = inputStream.read(messageByteArray); // IOException - if an I/O error occurs
////        messageHexStringResponse = MainConverter.hexify(messageByteArray);
////        logger.info("ISO Message {} ", messageHexStringResponse);
//        logger.info("ISO Message Byte Array {} ", messageByteArray);
//        return messageByteArray;
//
//    }
//
//    //This method helps to Log your ISO message
//    private static void logISOMsg(ISOMsg msg) {
//        System.out.println("----ISO MESSAGE-----");
//        try {
//            System.out.println("  MTI : " + msg.getMTI());
//            for (int i=1;i<=msg.getMaxField();i++) {
//                if (msg.hasField(i)) {
//                    System.out.println("    Field-"+i+" : "+msg.getString(i));
//                }
//            }
//        } catch (ISOException e) {
//            e.printStackTrace();
//        } finally {
//            System.out.println("--------------------");
//        }
//    }
//
//    public void generateSessionKey() throws Exception {
//        byte[] bytes = new byte[16];
//        SecureRandom.getInstanceStrong().nextBytes(bytes);
//        byte[] zpkKcv = EncryptionUtil.generateKeyCheckValue(bytes);
//
//        String session = Hex.encodeHexString(bytes);
//        this.zpk = session ;
//        System.out.println("session key is {}" + session);
//        String generatedKcv = new String(Hex.encodeHex(zpkKcv));
//        System.out.println("kcv is {}" + generatedKcv);
//        String sess = generatedKcv + session;
//        logger.info("The Key {} ", sess);
////        this.sessionKey = Hex.encodeHexString(EncryptionUtil.tdesEncryptECB(bytes ,Hex.decodeHex(interChange.getFullZmk().toCharArray())))+generatedKcv ;
//
//    }
//
//}
