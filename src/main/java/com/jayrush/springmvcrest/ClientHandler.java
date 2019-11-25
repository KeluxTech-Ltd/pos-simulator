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
import java.util.Date;

import static com.jayrush.springmvcrest.Nibss.processor.IsoProcessor.printIsoFields;


@PropertySource("classpath:application.properties")
public class ClientHandler extends Thread {
    private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);
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
    ModelMapper modelMapper;

    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;

    }


    @Override
    public void run() {
        try {
            logger.info("AWaiting data------------------------------------------");
            final byte[] lenBytes = new byte[2];
            dis.readFully(lenBytes);
            final int contentLength = DataUtil.bytesToShort(lenBytes);
            final byte[] resp = new byte[contentLength];
            dis.readFully(resp);
            String messagesent = bytesToHex(resp);
            messageType(messagesent);

            //to log the request message
            final TerminalTransactions request = parseRequest(resp);

            Terminals terminals =  terminalRepository.findByterminalID(request.getTerminalID());
            if (request.getAmount()!=null && !terminals.getTerminalID().isEmpty()){
                request.setInstitutionID(terminals.getInstitution().getInstitutionID());
                transactionRepository.save(request);
            }
            byte[] receivedResponse = sendTransactionToProcess(resp);
            assert receivedResponse != null;
            dos.write(receivedResponse);
            dos.flush();
            logger.info("************Response Sent*********");
            dos.close();

        } catch (IOException e) {
            logger.info(e.getMessage());

        } finally {
            try {
                dos.close();
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
        }
    }

    private void messageType(String messagesent) {
        String asciiMessage = hexToAscii(messagesent);

        if (asciiMessage.startsWith("0800")){
            logger.info("ISO Network Management ( 0800 )--->{}",asciiMessage);
        }
        else if (asciiMessage.startsWith("0200")){
            logger.info("Transaction Message ( 200 )--->{}",asciiMessage);
        }
        else if (asciiMessage.startsWith("0100")){
            logger.info("Authorization Message ( 0100 )---> {}",asciiMessage);
        }
    }


    private byte[] sendTransactionToProcess(byte[] messagePayload){
        ISO8583TransactionResponse response = null;
        ChannelSocketRequestManager socketRequester = null;
        TerminalTransactions transactions = new TerminalTransactions();
        try {
            //nibss connection
            socketRequester = new ChannelSocketRequestManager(ipAddress, port);

            //send transaction to nibss
            Response responseObj = socketRequester.toNibss(messagePayload);


            modelMapper.map(responseObj.getResponseMsg(),transactions);
            transaction(transactions);
            return responseObj.getResponseByte();
        } catch (IOException e) {
            response.setResponseCodeField39("-1");
            logger.info("Failed to get Transaction response due to IO exception " , e);
            return null;
        } catch (CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException | KeyManagementException | ParseException e) {
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
    }

    public static void keyManagement(String terminalID) {

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

        NibssRequestsFactory factory = new NibssRequestsFactory(dataStore1, "2101CX81");
        OfflineCTMK offlineCTMK = new OfflineCTMK();
        offlineCTMK.setComponentOne("386758793DE364F88319EA0D4C7091EF");
        offlineCTMK.setComponentTwo("67A78CB3D9C1FE38C1DAB6F154D634D6");


        try {
            getKeys_Params(factory, offlineCTMK);

        } catch (Exception ex) {
            logger.info("Failed to fetch all keys ",ex);

        }

    }

    private static void getKeys_Params(NibssRequestsFactory factory, OfflineCTMK offlineCTMK) {
        if (!factory.getMasterKey(offlineCTMK)) {
            logger.info("Failed to download Master Key");
        }

        logger.info("Master Key Downloaded");

        if (!factory.getSessionKey()) {
            logger.info("Failed to download Session Key");

        }

        if (!factory.getPinKey()) {
            logger.info("Failed to download Pin Key");

        }

        if (!factory.getParameters()) {
            logger.info("Failed to download Parameters");
        }
    }

    private static String hexToAscii(String hexStr) {
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
            printIsoFields(responseMessage, "ISO message ====> ");
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

        }
        response.setResponseCode("Pending Response");
        response.setResponseDesc("");
        logger.info("Response: {}", response.getResponseCode());
        return response;

    }

}
