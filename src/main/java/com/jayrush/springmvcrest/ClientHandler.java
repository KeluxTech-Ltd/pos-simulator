package com.jayrush.springmvcrest;

import com.globasure.nibss.tms.client.lib.utils.StringUtils;
import com.jayrush.springmvcrest.Nibss.factory.NibssRequestsFactory;
import com.jayrush.springmvcrest.Nibss.models.store.OfflineCTMK;
import com.jayrush.springmvcrest.Nibss.models.transaction.ISO8583TransactionResponse;
import com.jayrush.springmvcrest.Nibss.network.ChannelSocketRequestManager;
import com.jayrush.springmvcrest.Nibss.processor.IsoProcessor;
import com.jayrush.springmvcrest.Nibss.repository.DataStore;
import com.jayrush.springmvcrest.Nibss.utils.DataUtil;
import com.jayrush.springmvcrest.Repositories.TerminalRepository;
import com.jayrush.springmvcrest.Repositories.TransactionRepository;
import com.jayrush.springmvcrest.Repositories.terminalKeysRepo;
import com.jayrush.springmvcrest.Service.email.service.MailService;
import com.jayrush.springmvcrest.Service.nibssToIswInterface;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.Terminals;
import com.jayrush.springmvcrest.domain.domainDTO.Response;
import com.jayrush.springmvcrest.domain.domainDTO.host;
import com.jayrush.springmvcrest.domain.domainDTO.keys;
import com.jayrush.springmvcrest.domain.terminalKeyManagement;
import com.jayrush.springmvcrest.fep.ISWprocessor;
import com.jayrush.springmvcrest.fep.RequestProcessingException;
import com.jayrush.springmvcrest.iso8583.IsoMessage;
import com.jayrush.springmvcrest.iso8583.IsoType;
import com.jayrush.springmvcrest.iso8583.IsoValue;
import com.jayrush.springmvcrest.iso8583.MessageFactory;
import com.jayrush.springmvcrest.utility.CryptoException;
import com.jayrush.springmvcrest.utility.Utils;
import org.jpos.iso.ISOException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static com.jayrush.springmvcrest.Nibss.processor.IsoProcessor.generateHash256Value;
import static com.jayrush.springmvcrest.Nibss.processor.IsoProcessor.printIsoFields;
import static com.jayrush.springmvcrest.utility.MainConverter.hexify;


@PropertySource("classpath:application.properties")
public class ClientHandler extends Thread {
    private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private static String encryptedSessionKey;
    private byte[] clearSessionKey;
    private String encryptedMasterKey;
    private byte[] clearMasterKey;
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    @Value("${nibss-socket-ipAddress}")
    String ipAddress;
    @Value("${nibss-socket-port}")
    int port;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TerminalRepository terminalRepository;

    @Autowired
    terminalKeysRepo terminalKeysRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MailService mailService;

    @Autowired
    nibssToIswInterface nibssToIswInterface;


    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;

    }


    @Override
    public void run() {
        try {
            //method to generate keys for all terminals
//            getKeysForAllTerminalID();
            logger.info("AWaiting data------------------------------------------");
            final byte[] lenBytes = new byte[2];
            dis.readFully(lenBytes);
            final int contentLength = DataUtil.bytesToShort(lenBytes);
            final byte[] resp = new byte[contentLength];
            dis.readFully(resp);


            //log the request message
            String messagesent = hexify(resp);
            String mti = logMessageType(messagesent);
            host host = new host();

            //to log the request message
            final TerminalTransactions request = parseRequest(resp);

            Terminals terminals = terminalRepository.findByterminalID(request.getTerminalID());
            if (Objects.isNull(terminals)) {
                logger.info("Terminal ID not registered {}", request.getTerminalID());
            }

            //getting the profile setting to route transaction based on set profile for terminal ID
            String profile = terminals.getProfile().getProfileName();
            host.setHostIp(terminals.getProfile().getProfileIP());
            host.setHostPort(terminals.getProfile().getPort());

            //to save only transaction messages to database on transaction initialization
            if (mti.equals("0200")) {
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
                String date = simpleDateFormat1.format(new Date());
                request.setInstitutionID(terminals.getInstitution().getInstitutionID());
                request.setRequestDateTime(date);

                //masked pan
                request.setPan(Utils.maskPanForReceipt(request.getPan()));
                transactionRepository.save(request);
            }

            //Host Switching based on profile setting gotten
            switch (profile) {
                case "ISW":
                    if (mti.equals("0200")) {
                        //decrypt pin block
                        byte[] translatedMsg = translatePin(resp, profile);
                        interswitchProfile(translatedMsg, host, request);
                    } else {
                        interswitchProfile(resp, host, request);
                    }
                    break;
                case "POSVAS":
                case "EPMS":
                    if (mti.equals("0200")) {
                            byte[] translatedMsg = translatePin(resp, profile);
                            nibssProfile(translatedMsg, host, request);
                    } else {
                        nibssProfile(resp, host, request);
                    }
                    break;
                default:
                    logger.info("Profile does not exist for {}", profile);
                    break;
            }
        } catch (IOException | ParseException | RequestProcessingException | ISOException | CryptoException e) {
            logger.info(e.getMessage());

        } finally {
            try {
                dos.close();
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        }
    }

    private byte[] translatePin(byte[] resp, String profile) throws IOException, CryptoException {
        IsoMessage msg = toISo(resp);
        terminalKeyManagement terminalKeyManagement = terminalKeysRepo.findByTerminalID(msg.getObjectValue(41));

        byte[] sessionKeyBytes = StringUtils.hexStringToByteArray(terminalKeyManagement.getSessionKey());
        byte[] msgtosend;
        String pinblock = msg.getObjectValue(52);
        if (Objects.nonNull(pinblock)) {
            //decrypt pinblock using static keys shared with terminals
            String tmsKey = "28300518865986737073478883921518";
            String newPinblock = nibssToIswInterface.decryptPinBlock(pinblock, tmsKey);
            if (profile.equals("ISW")) {
                String iswkey = "11111111111111111111111111111111";//isw static key
                String iswPinblock = nibssToIswInterface.encryptPinBlock(newPinblock, iswkey);
                final IsoValue<String> field52 = (IsoValue<String>) new IsoValue(IsoType.ALPHA, (Object) iswPinblock.toUpperCase(), 16);

                msg.setField(52, (IsoValue) field52);

            } else {
                //get nibss pinkey for terminalID
                String nibssPinBlock = nibssToIswInterface.encryptPinBlock(newPinblock, terminalKeyManagement.getPinKey());
                final IsoValue<String> field52 = (IsoValue<String>) new IsoValue(IsoType.ALPHA, (Object) nibssPinBlock.toUpperCase(), 16);
                msg.setField(52, (IsoValue) field52);
            }

            final byte[] bites = msg.writeData();
            final int length = bites.length;
            final byte[] temp = new byte[length - 64];
            if (length >= 64) {
                System.arraycopy(bites, 0, temp, 0, length - 64);
            }
            final String hashHex = generateHash256Value(temp, sessionKeyBytes);
            final IsoValue<String> field128update = (IsoValue<String>) new IsoValue(IsoType.ALPHA, (Object) hashHex, 64);
            msg.setField(128, (IsoValue) field128update);
            System.out.println("Message was hashed " + msg.getObjectValue(128));
            msgtosend= msg.writeData();

        }else {
            final byte[] bites = msg.writeData();
            final int length = bites.length;
            final byte[] temp = new byte[length - 64];
            if (length >= 64) {
                System.arraycopy(bites, 0, temp, 0, length - 64);
            }
            final String hashHex = generateHash256Value(temp, sessionKeyBytes);
            final IsoValue<String> field128update = (IsoValue<String>) new IsoValue(IsoType.ALPHA, (Object) hashHex, 64);
            msg.setField(128, (IsoValue) field128update);
            System.out.println("Message was hashed " + msg.getObjectValue(128));
            msgtosend= msg.writeData();
        }
        return msgtosend;

    }

//    private byte[] translatePin(byte[] resp, String profile) throws IOException, CryptoException {
//        IsoMessage msg = toISo(resp);
//        terminalKeyManagement terminalKeyManagement = terminalKeysRepo.findByTerminalID(msg.getObjectValue(41));
//        encryptedSessionKey = terminalKeyManagement.getSessionKey();
//        encryptedMasterKey = terminalKeyManagement.getMasterKey();
//        OfflineCTMK offlineCTMK= new OfflineCTMK();
//        offlineCTMK.setComponentOne("386758793DE364F88319EA0D4C7091EF");
//        offlineCTMK.setComponentTwo("67A78CB3D9C1FE38C1DAB6F154D634D6");
//        String ctmkString = ISOUtil.hexor(offlineCTMK.getComponentOne(), offlineCTMK.getComponentTwo());
//        byte[] ctmk = StringUtils.hexStringToByteArray(ctmkString);
//        final byte[] encryptedBytes = com.jayrush.springmvcrest.Nibss.utils.StringUtils.hexStringToByteArray(encryptedMasterKey);
//        final byte[] clearTMKBytes;
//        try {
//            this.clearMasterKey = com.jayrush.springmvcrest.Nibss.utils.CryptoUtil.decrypt(encryptedBytes, ctmk, "DESede", "DESede/ECB/NoPadding");
//        } catch (InvalidKeyException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException | NoSuchPaddingException | NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        byte[] sessionKeyBytes = StringUtils.hexStringToByteArray(encryptedSessionKey);
//        try {
//            this.clearSessionKey = CryptoUtil.decrypt(sessionKeyBytes, clearMasterKey, "DESede", "DESede/ECB/NoPadding");
//        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidKeySpecException | NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        String pinblock = msg.getObjectValue(52);
//        if (Objects.nonNull(pinblock)) {
//            //decrypt pinblock using static keys shared with terminals
//            String tmsKey = "28300518865986737073478883921518";
//            String newPinblock = nibssToIswInterface.decryptPinBlock(pinblock, tmsKey);
//            if (profile.equals("ISW")) {
//                String iswkey = "11111111111111111111111111111111";//isw static key
//                String iswPinblock = nibssToIswInterface.encryptPinBlock(newPinblock, iswkey);
//                final IsoValue<String> field52 = (IsoValue<String>) new IsoValue(IsoType.ALPHA, (Object) iswPinblock.toUpperCase(), 16);
//
//                msg.setField(52, (IsoValue) field52);
//
//            } else {
//                //get nibss pinkey for terminalID
//                String nibssPinBlock = nibssToIswInterface.encryptPinBlock(newPinblock, terminalKeyManagement.getPinKey());
//                final IsoValue<String> field52 = (IsoValue<String>) new IsoValue(IsoType.ALPHA, (Object) nibssPinBlock.toUpperCase(), 16);
//                msg.setField(52, (IsoValue) field52);
//            }
//            final byte[] bites = msg.writeData();
//            final int length = bites.length;
//            final byte[] temp = new byte[length - 64];
//            if (length >= 64) {
//                System.arraycopy(bites, 0, temp, 0, length - 64);
//            }
//            final String hashHex = generateHash256Value(temp, terminalKeyManagement.getSessionKey().getBytes());
//            final IsoValue<String> field128update = (IsoValue<String>) new IsoValue(IsoType.ALPHA, (Object) hashHex, 64);
//            msg.setField(128, (IsoValue) field128update);
//            System.out.println("Message was hashed " + msg.getObjectValue(128));
//            IsoMessage message = toISo(msg.writeData());
//
//        }
//        return msg.writeData();
//    }

    private void nibssProfile(byte[] resp, host host, TerminalTransactions request) throws IOException, CryptoException {
        byte[] receivedResponse = sendTransactionToProcess(resp, host, request);
        assert receivedResponse != null;
        dos.write(receivedResponse);
        dos.flush();
        logger.info("************Response Sent*********");
        dos.close();
    }

    private void interswitchProfile(byte[] resp, host host, TerminalTransactions request) throws IOException, ParseException, RequestProcessingException, ISOException {
        byte[] fepMessage;
        ISWprocessor processor = new ISWprocessor();
        fepMessage = processor.toFEP(resp);
        byte[] receivedResponse = sendTransactionToProcess(fepMessage, host, request);
        assert receivedResponse != null;
        dos.write(receivedResponse);
        dos.flush();
        logger.info("************Response Sent*********");
        dos.close();
    }

    private String logMessageType(String messagesent) {
        String asciiMessage = hexToAscii(messagesent);
        String mti = asciiMessage.substring(0, 4);

        switch (mti) {
            case "0800":
                logger.info("ISO Network Management ( 0800 )--->{}", asciiMessage);
                return mti;
            case "0200":
                logger.info("Transaction Message ( 200 )--->{}", asciiMessage);
                return mti;
            case "0100":
                logger.info("Authorization Message ( 0100 )---> {}", asciiMessage);
                return mti;
            default:
                return mti;
        }
    }

    private byte[] sendTransactionToProcess(byte[] messagePayload, host host, TerminalTransactions request) {
        ISO8583TransactionResponse response = null;
        ChannelSocketRequestManager socketRequester = null;
        TerminalTransactions transactions = new TerminalTransactions();
        Response responseObject = new Response();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String Date = simpleDateFormat.format(new Date());
        String message = hexify(messagePayload);
        String ascii = hexToAscii(message);
        String mti = ascii.substring(0, 4);
        try {
            //Host Connection connection
            logger.info("Host details {}", host);
            socketRequester = new ChannelSocketRequestManager(host.getHostIp(), host.getHostPort());

            //send transaction to Processor
            if (host.getHostPort() == 7002) {
                responseObject = socketRequester.toISW(messagePayload, host);
            } else {
                responseObject = socketRequester.toNIBSS(messagePayload);
            }


            modelMapper.map(responseObject.getResponseMsg(), transactions);

            //save transaction response to db if response was gotten
            transaction(transactions);

            return responseObject.getResponseByte();
        } catch (IOException e) {
            if (mti.equals("0200")) {
                TerminalTransactions transaction = transactionRepository.findByrrnAndId(request.getRrn(), request.getId());
                transaction.setDateCreated(Date);
                transaction.setResponseDesc("Transaction Timed out");
                transaction.setResponseCode("-1");
                transaction.setTranComplete(true);
                transaction.setStatus("Failed");
                transactionRepository.save(transaction);
            }
            logger.info("Failed to get Transaction response due to IO exception ", e);
            logger.info("Request Timed Out ", e);
            return null;
        } catch (CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException | KeyManagementException | ParseException | ISOException e) {
            logger.info(e.toString());
            return null;
        } finally {
            if (socketRequester != null) {
                try {
                    socketRequester.disconnect();
                } catch (IOException ex) {
                    logger.info("Failed to disconnect socket ");
                }
            }
        }
    }

    private void transaction(TerminalTransactions transactions) {
        if (transactions.getAmount() != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String date = simpleDateFormat.format(new Date());
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
            String date1 = simpleDateFormat1.format(new Date());
            transactions.setDateCreated(date);
            //to update the transaction status after response is gotten from host


            TerminalTransactions transactionRequest = transactionRepository.findByrrnAndTerminalID(transactions.getRrn(), transactions.getTerminalID());
            if (Objects.nonNull(transactionRequest)) {
                transactionRequest.setResponseCode(transactions.getResponseCode());
                transactionRequest.setResponseDesc(transactions.getResponseDesc());
                transactionRequest.setMti(transactions.getMti());
                transactionRequest.setDateCreated(date);
                transactionRequest.setDateTime(transactions.getDateTime());
                transactionRequest.setResponseDateTime(date);
                transactionRequest.setTranComplete(true);
                if (transactions.getResponseCode().equals("00")) {
                    transactionRequest.setStatus("Success");
                } else {
                    transactionRequest.setStatus("Failed");
                }
                transactionRepository.save(transactionRequest);
            } else {
                transactions.setResponseDesc("Cannot Find transaction using rrn and Terminal ID to map response");
                transactions.setStatus("Failed");
                transactionRepository.save(transactions);
            }
        }
    }

//    public void getKeysForAllTerminalID(){
//        List<Terminals> terminalsList = terminalRepository.findAll();
//        for(int i = 0; i<terminalsList.size(); i++){
//            keyManagement(terminalsList.get(i).getTerminalID());
//            logger.info("Keys downloaded for Terminal {}",terminalsList.get(i).getTerminalID());
//        }
//    }
//
//    public void keyManagement(String terminalID) {
//        Terminals terminals = terminalRepository.findByterminalID(terminalID);
//        host host = new host();
//        if (Objects.nonNull(terminals)){
//            host.setHostIp(terminals.getProfile().getProfileIP());
//            host.setHostPort(terminals.getProfile().getPort());
//        }
//        DataStore dataStore1 = new DataStore() {
//            @Override
//            public void putString(String p0, String p1) {
//
//            }
//
//            @Override
//            public void putInt(String p0, int p1) {
//
//            }
//
//            @Override
//            public String getString(String p0) {
//                return null;
//            }
//
//            @Override
//            public int getInt(String p0) {
//                return 0;
//            }
//        };
//        dataStore1.putString(ThamesStoreKeys.THAMES_STRING_CONFIG_COMMUNICATION_HOST_ID, host.getHostIp());
//        dataStore1.putString(ThamesStoreKeys.THAMES_STRING_CONFIG_COMMUNICATION_PORT_DETAILS,String.valueOf(host.getHostPort()));
//
//        NibssRequestsFactory factory = new NibssRequestsFactory(dataStore1, terminalID,terminalKeysRepo);
//        OfflineCTMK offlineCTMK = new OfflineCTMK();
//        //switch based on profile name
//        switch (terminals.getProfile().getProfileName()){
//            case "POSVAS":
//                //best convention is to get the zpk from the terminal profile
//
//                offlineCTMK.setComponentOne("386758793DE364F88319EA0D4C7091EF");
//                offlineCTMK.setComponentTwo("67A78CB3D9C1FE38C1DAB6F154D634D6");
//                try {
//                    getKeys_Params(factory, offlineCTMK,host);
//
//                } catch (Exception ex) {
//                    logger.info("Failed to fetch all keys ",ex);
//
//                }
//                break;
//
//            case "EPMS":
//                offlineCTMK.setComponentOne("3BB9648A624F32C17C4037C81AD0B5CB");
//                offlineCTMK.setComponentTwo("6491A2BFEC1AD668F7CBFEC4CE1301AD");
//                try {
//                    getKeys_Params(factory, offlineCTMK,host);
//
//                } catch (Exception ex) {
//                    logger.info("Failed to fetch all keys ",ex);
//
//                }
//                break;
//            case "ISW":
//                offlineCTMK.setComponentOne("10101010101010101010101010101010");
//                offlineCTMK.setComponentTwo("01010101010101010101010101010101");
//                //make route for interswitch
//                break;
//            default:
//                logger.info("No ZPK exists for profile");
//                break;
//        }
//
//
//
//
//
//    }
//
//    private static void getKeys_Params(NibssRequestsFactory factory, OfflineCTMK offlineCTMK, host host) {
//        if (!factory.getMasterKey(offlineCTMK,host)) {
//            logger.info("Failed to download Master Key");
//        }
//
//        logger.info("Master Key Downloaded");
//
//        if (!factory.getSessionKey(host)) {
//            logger.info("Failed to download Session Key");
//
//        }
//
//        if (!factory.getPinKey(host)) {
//            logger.info("Failed to download Pin Key");
//
//        }
//
//        if (!factory.getParameters(host)) {
//            logger.info("Failed to download Parameters");
//        }
//    }

//    public void getKeysForAllTerminalID( List<Terminals> terminalsList){
//
//        for (int i = 0; i<terminalsList.size(); i++){
//            terminalKeyManagement terminalKeys = keyManagement(terminalsList.get(i));
//            terminalKeysRepo.save(terminalKeys);
//        }
//
//    }

    public terminalKeyManagement keyManagement(Terminals terminals) {
        host host = new host();
        if (Objects.nonNull(terminals)) {
            host.setHostIp(terminals.getProfile().getProfileIP());
            host.setHostPort(terminals.getProfile().getPort());
        }
        DataStore dataStore1 = new DataStore() {
            @Override
            public void putString(String p0, String p1) {

            }

            @Override
            public void putInt(String p0, int p1) {

            }

            @Override
            public String getString(String p0) {
                return null;
            }

            @Override
            public int getInt(String p0) {
                return 0;
            }
        };
        dataStore1.putString(ThamesStoreKeys.THAMES_STRING_CONFIG_COMMUNICATION_HOST_ID, host.getHostIp());
        dataStore1.putString(ThamesStoreKeys.THAMES_STRING_CONFIG_COMMUNICATION_PORT_DETAILS, String.valueOf(host.getHostPort()));

        NibssRequestsFactory factory = new NibssRequestsFactory(dataStore1, terminals.getTerminalID(), terminalKeysRepo);
        terminalKeyManagement terminalKeys = new terminalKeyManagement();
        OfflineCTMK offlineCTMK = new OfflineCTMK();
        //switch based on profile name
        switch (terminals.getProfile().getProfileName()) {
            case "POSVAS":
                //best convention is to get the zpk from the terminal profile
                offlineCTMK.setComponentOne("386758793DE364F88319EA0D4C7091EF");
                offlineCTMK.setComponentTwo("67A78CB3D9C1FE38C1DAB6F154D634D6");
                try {
                    keys keys = getKeys_Params(factory, offlineCTMK, host);
                    terminalKeys.setId(terminals.getId());
                    terminalKeys.setTerminalID(terminals.getTerminalID());
                    terminalKeys.setMasterKey(keys.getMasterkey());
                    terminalKeys.setSessionKey(keys.getSessionKey());
                    terminalKeys.setPinKey(keys.getPinKey());
                    terminalKeys.setParameterDownloaded(keys.getParameters());
                    return terminalKeys;
                } catch (Exception ex) {
                    logger.info("Failed to fetch all keys ", ex);
                }
                break;

            case "EPMS":
                offlineCTMK.setComponentOne("3BB9648A624F32C17C4037C81AD0B5CB");
                offlineCTMK.setComponentTwo("6491A2BFEC1AD668F7CBFEC4CE1301AD");
                try {
                    keys keys = getKeys_Params(factory, offlineCTMK, host);
                    terminalKeys.setId(terminals.getId());
                    terminalKeys.setTerminalID(terminals.getTerminalID());
                    terminalKeys.setMasterKey(keys.getMasterkey());
                    terminalKeys.setSessionKey(keys.getSessionKey());
                    terminalKeys.setPinKey(keys.getPinKey());
                    terminalKeys.setParameterDownloaded(keys.getParameters());
                    return terminalKeys;
                } catch (Exception ex) {
                    logger.info("Failed to fetch all keys ", ex);

                }
                break;
            case "ISW":

                terminalKeys.setParameterDownloaded("static parameters");
                terminalKeys.setMasterKey("static parameters");
                terminalKeys.setSessionKey("static parameters");
                terminalKeys.setPinKey("11111111111111111111111111111111");
                terminalKeys.setTerminalID(terminals.getTerminalID());
                return terminalKeys;

            default:
                logger.info("No ZPK exists for profile");
                return null;

        }
        return terminalKeys;

    }


    private keys getKeys_Params(NibssRequestsFactory factory, OfflineCTMK offlineCTMK, host host) {
        keys keys = new keys();
        String masterKey;
        String sessionKey;
        String pinKey;
        String parameters;

        masterKey = factory.getMasterKey(offlineCTMK, host);
        sessionKey = factory.getSessionKey(host);
        pinKey = factory.getPinKey(host);
        parameters = factory.getParameters(host);

        keys.setMasterkey(masterKey);
        keys.setSessionKey(sessionKey);
        keys.setPinKey(pinKey);
        keys.setParameters(parameters);
        return keys;


//        if (!factory.getMasterKey(offlineCTMK,host)) {
//            logger.info("Failed to download Master Key");
//        }
//
//        logger.info("Master Key Downloaded");

//        if (!factory.getSessionKey(host)) {
//            logger.info("Failed to download Session Key");
//
//        }

//        if (!factory.getPinKey(host)) {
//            logger.info("Failed to download Pin Key");
//
//        }
//
//        if (!factory.getParameters(host)) {
//            logger.info("Failed to download Parameters");
//        }
    }

    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    private TerminalTransactions parseRequest(final byte[] message) throws IOException {
        final TerminalTransactions response = new TerminalTransactions();
        final IsoMessage isoMessage = null;
        final MessageFactory<IsoMessage> responseMessageFactory = (MessageFactory<IsoMessage>) new MessageFactory();
        responseMessageFactory.addMessageTemplate(isoMessage);
        responseMessageFactory.setAssignDate(true);
        responseMessageFactory.setUseBinaryBitmap(false);
        responseMessageFactory.setUseBinaryMessages(false);
        responseMessageFactory.setEtx(-1);
        responseMessageFactory.setIgnoreLastMissingField(false);
        responseMessageFactory.setConfigPath(IsoProcessor.CONFIG_FILE);
        IsoMessage responseMessage = null;
        try {
            responseMessage = responseMessageFactory.parseMessage(message, 0);
            response.setTerminalID(responseMessage.getObjectValue(41).toString());
            printIsoFields(responseMessage, "ISO message ====> ");
        } catch (Exception e2) {
            return response;
        }

        if (responseMessage.hasField(4) && (responseMessage.hasField(37))) {
            response.setMti("0200");
            if (responseMessage.hasField(2)) {
                response.setPan(responseMessage.getObjectValue(2).toString());
            }
            if (responseMessage.hasField(4)) {
                response.setAmount(responseMessage.getObjectValue(4).toString());
            }
            if (responseMessage.hasField(7)) {
                response.setDateTime(responseMessage.getObjectValue(7).toString());
            }
            if (responseMessage.hasField(11)) {
                response.setStan(responseMessage.getObjectValue(11).toString());
            }
            if (responseMessage.hasField(12)) {
                response.setTime(responseMessage.getObjectValue(12).toString());
            }
            if (responseMessage.hasField(13)) {
                response.setDate(responseMessage.getObjectValue(13).toString());
            }
            if (responseMessage.hasField(37)) {
                response.setRrn(responseMessage.getObjectValue(37).toString());
            }
            if (responseMessage.hasField(41)) {
                response.setTerminalID(responseMessage.getObjectValue(41).toString());
            }
            if (responseMessage.hasField(42)) {
                response.setAgentLocation(responseMessage.getObjectValue(42).toString());
            }

        }
        response.setResponseCode("-1");
        response.setResponseDesc("Processing");
        logger.info("Response: {}", response.getResponseCode());
        return response;

    }

    private IsoMessage toISo(final byte[] message) throws IOException {
        final TerminalTransactions response = new TerminalTransactions();
        final IsoMessage isoMessage = null;
        final MessageFactory<IsoMessage> responseMessageFactory = (MessageFactory<IsoMessage>) new MessageFactory();
        responseMessageFactory.addMessageTemplate(isoMessage);
        responseMessageFactory.setAssignDate(true);
        responseMessageFactory.setUseBinaryBitmap(false);
        responseMessageFactory.setUseBinaryMessages(false);
        responseMessageFactory.setEtx(-1);
        responseMessageFactory.setIgnoreLastMissingField(false);
        responseMessageFactory.setConfigPath(IsoProcessor.CONFIG_FILE);
        try {
            IsoMessage responseMessage = responseMessageFactory.parseMessage(message, 0);
//            response.setTerminalID(responseMessage.getObjectValue(41).toString());
            printIsoFields(responseMessage, "ISO message ====> ");
            return responseMessage;
        } catch (Exception e2) {
            logger.info(e2.getMessage());
            return null;
        }
    }

}
//@EnableScheduling
//@Component
//class newclass{
//    @Autowired
//    TerminalRepository terminalRepository;
//    @Autowired
//    terminalKeysRepo terminalKeysRepo;
//    private static Logger logger = LoggerFactory.getLogger(newclass.class);
//
//    @Scheduled(fixedDelay = 1000)
//    public void keyManagement() {
//        String terminalID = "2101CX82";
//        Terminals terminals = terminalRepository.findByterminalID(terminalID);
//        host host = new host();
//        if (Objects.nonNull(terminals)){
//            host.setHostIp(terminals.getProfile().getProfileIP());
//            host.setHostPort(terminals.getProfile().getPort());
//        }
//        DataStore dataStore1 = new DataStore() {
//            @Override
//            public void putString(String p0, String p1) {
//
//            }
//
//            @Override
//            public void putInt(String p0, int p1) {
//
//            }
//
//            @Override
//            public String getString(String p0) {
//                return null;
//            }
//
//            @Override
//            public int getInt(String p0) {
//                return 0;
//            }
//        };
//        dataStore1.putString(ThamesStoreKeys.THAMES_STRING_CONFIG_COMMUNICATION_HOST_ID, host.getHostIp());
//        dataStore1.putString(ThamesStoreKeys.THAMES_STRING_CONFIG_COMMUNICATION_PORT_DETAILS,String.valueOf(host.getHostPort()));
//
//        NibssRequestsFactory factory = new NibssRequestsFactory(dataStore1, terminalID,terminalKeysRepo);
//        OfflineCTMK offlineCTMK = new OfflineCTMK();
//        //switch based on profile name
//        switch (terminals.getProfile().getProfileName()){
//            case "POSVAS":
//                //best convention is to get the zpk from the terminal profile
//                offlineCTMK.setComponentOne("3BB9648A624F32C17C4037C81AD0B5CB");
//                offlineCTMK.setComponentTwo("6491A2BFEC1AD668F7CBFEC4CE1301AD");
//                try {
//                    getKeys_Params(factory, offlineCTMK,host);
//
//                } catch (Exception ex) {
//                    logger.info("Failed to fetch all keys ",ex);
//
//                }
//                break;
//            case "EPMS":
//                offlineCTMK.setComponentOne("3BB9648A62s4F3217C4037C81AD0B5CB");
//                offlineCTMK.setComponentTwo("6491A2BFEdC1AD68F7CBFEC4CE1301AD");
//                try {
//                    getKeys_Params(factory, offlineCTMK,host);
//
//                } catch (Exception ex) {
//                    logger.info("Failed to fetch all keys ",ex);
//
//                }
//                break;
//            case "ISW":
//                offlineCTMK.setComponentOne("3BB9648A624F32C1C4037C81AD0B5CB");
//                offlineCTMK.setComponentTwo("6491A2BFEC1AD68F7CBFEC4CE1301AD");
//                //make route for interswitch
//                break;
//            default:
//                logger.info("No ZPK exists for profile");
//                break;
//        }
//
//
//
//
//
//    }
//    private static void getKeys_Params(NibssRequestsFactory factory, OfflineCTMK offlineCTMK, host host) {
//        if (!factory.getMasterKey(offlineCTMK,host)) {
//            logger.info("Failed to download Master Key");
//        }
//
//        logger.info("Master Key Downloaded");
//
//        if (!factory.getSessionKey(host)) {
//            logger.info("Failed to download Session Key");
//
//        }
//
//        if (!factory.getPinKey(host)) {
//            logger.info("Failed to download Pin Key");
//
//        }
//
//        if (!factory.getParameters(host)) {
//            logger.info("Failed to download Parameters");
//        }
//    }
//}