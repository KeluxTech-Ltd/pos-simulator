//package com.jayrush.springmvcrest.fep;
//
//import integration._3line.agentlogin.*;
//import integration._3line.exceptions.RequestProcessingException;
//import integration._3line.fcmb.service.ISOService;
//import integration._3line.model.Response;
//import integration._3line.packager.PostBridgePackager;
//import integration._3line.utility.Utils;
//import org.jpos.iso.ISOException;
//import org.jpos.iso.ISOMsg;
//import org.jpos.iso.ISOPackager;
//import org.jpos.iso.ISOUtil;
//import org.modelmapper.ModelMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//
//import java.io.*;
//import java.net.Socket;
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//
//
//public class ClientHandler extends Thread {
//    final DataInputStream dis;
//    final DataOutputStream dos;
//    final Socket s;
//
//
//    private ISOPackager packager = new PostBridgePackager();
//
//    private ISOSocket isoSocket = new ISOSocket();
//
//    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);
//
//    @Autowired
//    private LoginService loginRequest;
//
//    @Autowired
//    private ISOService isoService;
//
//    @Autowired
//    private ModelMapper modelMapper;
//
//    @Value("${fcmb.key:0E49CBCD5EA26BFE1F89F229528531BF}")
//    private String exchangeKey;
//
//
//    // Constructor
//    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
//        this.s = s;
//        this.dis = dis;
//        this.dos = dos;
//    }
//
//    @Override
//    public void run() {
//
//        while (true) {
//            try {
//
//                // Ask user what he wants
//
//                // receive the answer from client
//                System.out.println("Waiting for data");
////                StringBuilder dataString = readInputAsByte();
//                Response dataString = readInputAsObject();
//                System.out.println("input data : " + dataString);
//                AgentResponse response;
//                Response resp = new Response();
//                if (Objects.nonNull(dataString)) {
//                    if (dataString.getRespCode().equals("1001")) {
//                        logger.info("Account processing...");
//                        IsoHolder isoHolder = modelMapper.map(dataString.getRespBody(), IsoHolder.class);
//                        logger.info("Iso string holder :: {}", isoHolder.getIsoData());
//                        logger.info("*************RECIEVED****RESPONSE*********");
//                        response = readStringToISO(isoHolder.getIsoData());
//                        sendReponse(dataString.getRespCode(),response);
//
//                    } else if (dataString.getRespCode().equals("1002")) {
//                        logger.info("Account opening...");
//                        CustomerAccountOpening cust = modelMapper.map(dataString.getRespBody(), CustomerAccountOpening.class);
//                        AccountResponseMessage act = loginRequest.openAccount(cust);
//                        if(Objects.nonNull(act)) {
//                            if (act.getSuccessMessage() != null) {
//                                resp.setRespCode(dataString.getRespCode());
//                                System.out.println("Account Created!! " + "(" + act.getSuccessMessage().getSuccessCode() + ")" + "  " + act.getSuccessMessage());
//                            } else {
//                                resp.setRespCode("23");
//                               logger.info("Account created failed {}" , act.getErrorMessages().getErrorMessage());
//                            }
//                        }
//                        else{
//                            logger.info("An error occured while creating account");
//                            resp.setRespCode("99");
//                        }
//                        resp.setRespBody(cust);
//                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(dos);
//                        objectOutputStream.writeObject(resp);
//
//                    } else if (Objects.nonNull(dataString.getRespBody()) && dataString.getRespCode().equals("1003")) {
//                        IsoHolder isoHolder = modelMapper.map(dataString.getRespBody(), IsoHolder.class);
//                        logger.info("Iso string holder :: {}", isoHolder.getIsoData());
//                        response = readStringToISO(isoHolder.getIsoData());
//                        logger.info("*************RECIEVED****RESPONSE*********");
//                        sendReponse(dataString.getRespCode(),response);
//                    }
//                }
//                logger.info("*********END HERE*********");
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    private StringBuilder readInputAsByte() throws IOException {
//        //                char dataType = dis.readChar();
//
//        int length = dis.readInt();
//
//        System.out.println("length of the input stream " + length);
//        byte[] messageByte = new byte[length];
//        boolean end = false;
//        StringBuilder dataString = new StringBuilder(length);
//        int totalBytesRead = 0;
//        while (!end) {
//            int currentBytesRead = dis.read(messageByte);
//            totalBytesRead = currentBytesRead + totalBytesRead;
//            if (totalBytesRead <= length) {
//                dataString
//                        .append(new String(messageByte, 0, currentBytesRead, StandardCharsets.UTF_8));
//            } else {
//                dataString
//                        .append(new String(messageByte, 0, length - totalBytesRead + currentBytesRead,
//                                StandardCharsets.UTF_8));
//            }
//            if (dataString.length() >= length) {
//                end = true;
//            }
//        }
//        return dataString;
//    }
//
//
//    private Response readInputAsObject() {
//        Response listOfMessages = new Response();
//        // create a DataInputStream so we can read data from it.
//        ObjectInputStream objectInputStream = null;
//        try {
//            objectInputStream = new ObjectInputStream(dis);
//
//
//            // read the list of messages from the socket
//            listOfMessages = (Response) objectInputStream.readObject();
//
//
//            System.out.println("Received [" + listOfMessages + "] messages from: " + s);
//            // print out the text of every message
//            System.out.println("All messages:");
//            return listOfMessages;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return listOfMessages;
//    }
//
//    private void sendReponse(String tag, AgentResponse response) throws IOException {
//        System.out.println(response);
//        ISOMsg isoMsg = new ISOMsg();
//
//        logger.info("***About sending response***");
//        try {
//            if (Objects.nonNull(response) && response.getProcessingCode().equals("000000")) {
//                logger.info("sending logging response");
//                if (response.getRespCode().equals("00")) {
//                    isoMsg.setMTI("0810");
//                    isoMsg.set(39, "00");
//                    isoMsg.set(111, response.getRespDescription());
//                    setIsoMsg(response, isoMsg);
//                    logger.info("The Response in Field 120 {}", response.getRespBody().getBvn());
//                    logger.info("The Response in Field 119 {}", response.getRespBody().getToken());
//                    logger.info("Logging successful");
//                    setProcess(tag, isoMsg);
//                } else {
//                    isoMsg.setMTI("0810");
//                    isoMsg.set(111, response.getRespDescription());
//                    logger.info("Logging failed");
//                    setProcess(tag, isoMsg);
//                }
//            } else if (Objects.nonNull(response) && response.getProcessingCode().equals("000001")) {
//                if (Objects.nonNull(response.getRespBody())) {
//                    logger.info("The Response in Field 107 {}", response.getRespBody().getDeviceKey());
//                    isoMsg.set(107, response.getRespBody().getDeviceKey());
//                }
//                setProcess(tag, isoMsg);
//            } else {
//                logger.info("Response message in string {}", response.getIsoMessage());
////                dos.writeInt(response.getIsoMessage().getBytes().length);
////                dos.writeUTF(response.getIsoMessage());
//               getMapResponse(tag,response.getIsoMessage());
//            }
//
//        } catch (ISOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void setProcess(String tag, ISOMsg isoMsg) throws ISOException, IOException {
//
//        byte[] data;
//        isoMsg.setPackager(packager);
//        data = isoMsg.pack();
//        logISOMesssage(isoMsg);
//        logger.info("length of the output stream {} ", data.length);
//        logger.info("The Data {} ", data);
//        String convertedHexString = ISOUtil.hexString(data);
//        logger.info(convertedHexString);
////        dos.writeInt(data.length);
////        dos.write(data);
//        getMapResponse(tag, convertedHexString);
//        dos.flush();
//    }
//
//    private void getMapResponse(String tag, String convertedHexString) throws IOException {
//        Response response = new Response();
//        ObjectOutputStream obj = new ObjectOutputStream(dos);
//        Map<String, Object> objectMap = new HashMap<>();
//        objectMap.put("isoData", convertedHexString);
//        response.setRespCode(tag);
//        response.setRespBody(objectMap);
//        obj.writeObject(response);
//    }
//
//    private void setIsoMsg(AgentResponse response, ISOMsg isoMsg) {
//        isoMsg.set(112, response.getRespBody().getBvn());
//        isoMsg.set(119, response.getRespBody().getToken());
//        isoMsg.set(117, response.getRespBody().getName());
//        isoMsg.set(116, response.getRespBody().getAddress());
//        isoMsg.set(11, response.getRespBody().getStan());
//        isoMsg.set(113, response.getRespBody().getPhone());
//        isoMsg.set(110, response.getRespBody().getOperator());
//        isoMsg.set(114, response.getExchangeKey());
//        isoMsg.set(115, response.getRespBody().getAccount());
//    }
//
//    private void readByteToISO(byte[] messageByte) {
//        System.out.println("Message: " + new String(messageByte));
//        ISOMsg isoMsg = new ISOMsg();
//        try {
//            packager.unpack(isoMsg, messageByte);
//        } catch (ISOException e) {
//            logger.error("Could not unpack response iso bytes", e);
//        }
//    }
//
//    private AgentResponse readStringToISO(String isoMessage) {
//
//
//        ISOMsg isoMsg = isoSocket.stringToISOMessage(isoMessage);
//        logISOMesssage(isoMsg);
//
//        String msg_type_id = isoMsg.getString(0);
//        String processing_code = isoMsg.getString(3);
//        String agent_id = isoMsg.getString(4);
//        String agent_pin = isoMsg.getString(5);
//        String device_id = isoMsg.getString(35);
//        String terminal_id = isoMsg.getString(41);
//        System.out.println(agent_id + "--" + agent_pin + "--" + device_id + "--" + terminal_id);
//
//        LoginDetails details = new LoginDetails();
//        details.setTerminalId(terminal_id);
//        details.setAgentId(Utils.removeLeadingZeros(agent_id));
//        details.setPin(Utils.removeLeadingZeros(agent_pin));
//        details.setDeviceId(device_id);
//        ModelMapper modelMapper = new ModelMapper();
//
//        logger.info("About pushing request from terminal to portal");
//        logger.info(" Set all the user details: " + details);
//        AgentResponse agentResponse = new AgentResponse();
//        Response response = new Response();
//        switch (processing_code) {
//            case "000000":
//                logger.info("Logging...");
//                response = loginRequest.loginRequest(details);
//                agentResponse.setExchangeKey("0E49CBCD5EA26BFE1F89F229528531BF");
//                logger.info(" processing code => {}: " + processing_code);
//                agentResponse.setProcessingCode(processing_code);
//                break;
//            case "000001":
//                logger.info("Setting up service...");
//                response = loginRequest.agentSetup(details);
//                logger.info(" processing code => {}: " + processing_code);
//                agentResponse.setProcessingCode(processing_code);
//                break;
//
//            default:
//                logger.info("Other transaction other than specify");
//
//                try {
//                    logger.info(" processing code => {}: " + processing_code);
//                    agentResponse.setProcessingCode(processing_code);
//                    String message = isoService.processRequest(isoMessage);
//                    logger.info("get the string response {}", message);
//                    agentResponse.setIsoMessage(message);
//
//                } catch (RequestProcessingException e) {
//                    e.printStackTrace();
//                }
//
//                break;
//
//        }
//
//        if (Objects.nonNull(response.getRespBody())) {
//            RespBody respBody = modelMapper.map(response.getRespBody(), RespBody.class);
//            agentResponse.setRespBody(respBody);
//        }
//        agentResponse.setRespCode(response.getRespCode());
//        agentResponse.setRespDescription(response.getRespDescription());
//
//        return agentResponse;
//    }
//
//
//    private void logISOMesssage(ISOMsg msg) {
//        System.out.println("----ISO MESSAGE-----");
//        try {
//            System.out.println("    MTI :  " + msg.getMTI());
//            for (int i = 1; i <= msg.getMaxField(); i++) {
//                if (msg.hasField(i)) {
//                    System.out.println("    Field-" + i + " : " + msg.getString(i));
//                }
//            }
//        } catch (ISOException e) {
//            e.printStackTrace();
//        } finally {
//            System.out.println("--------------------");
//        }
//    }
//}
