package com.jayrush.springmvcrest;

import com.jayrush.springmvcrest.Nibss.factory.NibssRequestsFactory;
import com.jayrush.springmvcrest.Nibss.models.store.OfflineCTMK;
import com.jayrush.springmvcrest.Nibss.models.transaction.ISO8583TransactionResponse;
import com.jayrush.springmvcrest.Nibss.network.ChannelSocketRequestManager;
import com.jayrush.springmvcrest.Nibss.processor.IsoProcessor;
import com.jayrush.springmvcrest.Nibss.repository.DataStore;
import com.jayrush.springmvcrest.Nibss.utils.DataUtil;
import com.jayrush.springmvcrest.Repositories.TerminalRepository;
import com.jayrush.springmvcrest.Repositories.TransactionRepository;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.Terminals;
import com.jayrush.springmvcrest.domain.domainDTO.Response;
import com.jayrush.springmvcrest.iso8583.IsoMessage;
import com.jayrush.springmvcrest.iso8583.MessageFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import javax.el.BeanNameResolver;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.Date;

import static com.jayrush.springmvcrest.Nibss.processor.IsoProcessor.printIsoFields;


@PropertySource("classpath:application.properties")
public class ClientHandler extends Thread {
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    private ISOPackager packager = new PostBridgePackager();
    private ISOSocket isoSocket = new ISOSocket();
    private String exchangeKey;
    private BeanNameResolver context;
    @Value("${nibss-socket-ipAddress}")
    String ipAddress;
    @Value("${nibss-socket-port}")
    int port;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TerminalRepository terminalRepository;

    @Autowired
    ModelMapper modelMapper;

    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;

    }

    @Override
    public void run() {
//        while (true) {
            try {
                System.out.println("AWaiting data------------------------------------------");
                final byte[] lenBytes = new byte[2];
                dis.readFully(lenBytes);
                final int contentLength = DataUtil.bytesToShort(lenBytes);
                final byte[] resp = new byte[contentLength];
                dis.readFully(resp);
                String messagesent = bytesToHex(resp);
                System.out.println("From POS-----> "+messagesent);
                //to log the request message
                final TerminalTransactions request = parseRequest(resp);
                Terminals terminals =  terminalRepository.findByterminalID(request.getTerminalID());
                if (request.getAmount()!=null && !terminals.getTerminalID().isEmpty()){
                    request.setInstitutionID(terminals.getInstitution().getInstitutionID());
                    transactionRepository.save(request);
                }

                byte[] ReceivedResponse = SendTransactionToProcess(resp);
                dos.write(ReceivedResponse);
                dos.flush();
                System.out.println("************Response Sent*********");
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//    }

    private byte[] SendTransactionToProcess(byte[] MessagePayload){
        ISO8583TransactionResponse response = null;
        ChannelSocketRequestManager socketRequester = null;
        String hextoSend = null;
        TerminalTransactions transactions = new TerminalTransactions();
        try {
            //nibss connection
            socketRequester = new ChannelSocketRequestManager(ipAddress, port);

            //send transaction to nibss
            Response responseObj = socketRequester.toNibss(MessagePayload);


            modelMapper.map(responseObj.getResponseMsg(),transactions);
            if (transactions.getAmount()!=null){
                Date date = new Date();
                transactions.setDateCreated(date.toString());
                TerminalTransactions transactionRequest = transactionRepository.findByrrn(transactions.getRrn());
                transactionRequest.setResponseCode(transactions.getResponseCode());
                transactionRequest.setResponseDesc(transactions.getResponseDesc());
                transactionRequest.setMti(transactions.getMti());
                transactionRequest.setDateCreated(date.toString());
                if (transactions.getResponseCode().equals("00")){
                    transactionRequest.setStatus("Success");
                }
                else{
                    transactionRequest.setStatus("Failed");
                }
                transactionRepository.save(transactionRequest);
            }
            return responseObj.getResponseByte();
        } catch (IOException e) {
            response.setResponseCodeField39("-1");
            System.out.println("Failed to get Transaction response due to IO exception" + (Throwable) e);
            return null;
        } catch (CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException | KeyManagementException | ParseException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (socketRequester != null) {
                try {
                    socketRequester.disconnect();
                } catch (IOException ex) {
                    System.out.println("Failed to disconnect socket ");
                }
            }
        }
    }

    private StringBuilder readInputAsByte() throws IOException {
        //                char dataType = dis.readChar();

        int length = dis.readInt()+4;

        System.out.println("length of the input stream " + length+2);
        byte[] messageByte = new byte[5*1024];
        boolean end = false;
        StringBuilder dataString = new StringBuilder(length);
        int totalBytesRead = 0;
        while (!end) {
            int currentBytesRead = dis.read(messageByte);
            totalBytesRead = currentBytesRead + totalBytesRead;
            if (totalBytesRead <= length) {
                dataString
                        .append(new String(messageByte, 0, currentBytesRead, StandardCharsets.UTF_8));
            } else {
                dataString
                        .append(new String(messageByte, 0, length - totalBytesRead + currentBytesRead,
                                StandardCharsets.UTF_8));
            }
            if (totalBytesRead==currentBytesRead){
                end=true;
            }
            if (dataString.length() >= length) {
                end = true;
            }
        }
        return dataString;
    }

    private String readInputAsObject() {
        // create a DataInputStream so we can read data from it.
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(dis);


            // read the list of messages from the socket
            Object listOfMessages = null;

            listOfMessages = objectInputStream.readObject();
            System.out.println("Received [" + listOfMessages + "] messages from: " + s);
            // print out the text of every message
            System.out.println("All messages:");
            return listOfMessages.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void readByteToISO(byte[] messageByte) {
        System.out.println("Message: " + new String(messageByte));
        ISOMsg isoMsg = new ISOMsg();
        try {
            packager.unpack(isoMsg, messageByte);
        } catch (ISOException e) {
            System.out.println("Could not unpack response iso bytes"+ e);
        }
    }

    private void logISOMesssage(ISOMsg msg) {
        System.out.println("----ISO MESSAGE-----");
        try {
            System.out.println("    MTI :  " + msg.getMTI());
            for (int i = 1; i <= msg.getMaxField(); i++) {
                if (msg.hasField(i)) {
                    System.out.println("    Field-" + i + " : " + msg.getString(i));
                }
            }
        } catch (ISOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("--------------------");
        }
    }

    public static void KeyManagement(String TerminalID) throws IOException, ClassNotFoundException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        String terminalId;
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
        dataStore1.putString(ThamesStoreKeys.THAMES_STRING_CONFIG_COMMUNICATION_HOST_ID, "196.6.103.18");
        dataStore1.putString(ThamesStoreKeys.THAMES_STRING_CONFIG_COMMUNICATION_PORT_DETAILS,"5009");
        //  dataStore1.putString(ThamesStoreKeys.THAMES_STRING_TERMINAL_IDENTIFICATION,"2044GD95");

        NibssRequestsFactory factory = new NibssRequestsFactory(dataStore1, "2101CX81");
        OfflineCTMK offlineCTMK = new OfflineCTMK();
        //TODO load this from terminalConfig.json
        offlineCTMK.setComponentOne("386758793DE364F88319EA0D4C7091EF");
        offlineCTMK.setComponentTwo("67A78CB3D9C1FE38C1DAB6F154D634D6");


        try {
            if (!factory.getMasterKey(offlineCTMK)) {
                System.out.println("Failed to download Master Key");
                return;
            }

            System.out.println("Master Key Downloaded");

            if (!factory.getSessionKey()) {
                System.out.println("Failed to download Session Key");
                return;
            }

            if (!factory.getPinKey()) {
                System.out.println("Failed to download Pin Key");
                return;
            }

            if (!factory.getParameters()) {
                System.out.println("Failed to download Parameters");
                return;
            }
            //SaveKeys(TerminalID, Keys.eClearMasterKey,Keys.eClearSessionKey,Keys.eClearPinKey);

        } catch (Exception ex) {
            System.out.println("Failed to fetch all keys");
            return;
        }

    }

    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public ISOMsg stringToISOMessage(String isoData){

        String messageLenHex = setMessageLengthHexed(isoData);
        System.out.println("Message Len Hex {} "+ messageLenHex);
        byte[] request = ISOUtil.hex2byte(isoData.getBytes(), 0, isoData.length() / 2);
        System.out.println("The unpacked Byte {} "+ request);
        ISOMsg isoMsg = unpackISOMessage(request);

        return isoMsg;
    }

    private ISOMsg unpackISOMessage(byte[] message){
        ISOMsg isoMsg = new ISOMsg();
        try {
            //Setting the Post packager
            isoMsg.setPackager(packager);
            isoMsg.unpack(message);
            System.out.println("The Unpacked Message {} "+ isoMsg);
//            logISOMsg(isoMsg);
        } catch (ISOException e) {
            e.printStackTrace();
        }
        return isoMsg;
    }

    private static String setMessageLengthHexed(String messageHex) {
        // Determine the length of message
        int messageLength = messageHex.length() / 2;
        //Determination of the header in the length of two bytes according to ARKSYS v2.2
        String messageLengthHexed = Integer.toString(messageLength, 16);
        while (messageLengthHexed.length() < 4)
            messageLengthHexed = "0" + messageLengthHexed;

        return messageLengthHexed.toUpperCase() + messageHex;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private TerminalTransactions parseRequest(final byte[] message) throws IOException {
        final TerminalTransactions response = new TerminalTransactions();
        final IsoMessage isoMessage = null;
        final MessageFactory<IsoMessage> responseMessageFactory = (MessageFactory<IsoMessage>)new MessageFactory();
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
            printIsoFields(responseMessage, "Response ====> ");
        }
        catch (Exception e2) {
            return response;
        }

        if (responseMessage != null && responseMessage.hasField(4)) {
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
            response.setResponseCode("Pending Response");
            response.setResponseDesc("");
        }
        System.out.println("Response: {}"+ (Object)response.getResponseCode());
        return response;

    }

}
