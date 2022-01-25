// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.network;

import com.jayrush.springmvcrest.Nibss.processor.IsoProcessor;
import com.jayrush.springmvcrest.Nibss.utils.DataUtil;
import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.domainDTO.Response;
import com.jayrush.springmvcrest.domain.domainDTO.host;
import com.jayrush.springmvcrest.domain.nibssresponse;
import com.jayrush.springmvcrest.fep.ISWprocessor;
import com.jayrush.springmvcrest.fep.RequestProcessingException;
import com.jayrush.springmvcrest.iso8583.IsoMessage;
import com.jayrush.springmvcrest.iso8583.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Scanner;

import static com.jayrush.springmvcrest.Nibss.processor.IsoProcessor.printIsoFields;

@Component
public class ChannelSslSocketRequestManager
{
    private static Logger logger = LoggerFactory.getLogger(ChannelSslSocketRequestManager.class);

    private SSLSocket socket;

    public ChannelSslSocketRequestManager() {

    }


    public ChannelSslSocketRequestManager(String endpoint, int port) throws IOException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final TrustManager[] trustAllCerts = { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                final X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                return myTrustedAnchors;
            }

            public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
            }

            public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
            }
        } };
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        this.socket = (SSLSocket)sc.getSocketFactory().createSocket(endpoint, port);
    }

    public void disconnect() throws IOException {
        if (this.socket.isConnected()) {
            this.socket.close();
        }
    }

    public byte[] sendAndRecieveData(final byte[] data) throws IOException, ParseException {
        if (this.socket.isConnected()) {
            logger.info("socket is connected{}", socket.getInetAddress());
            Response responseObj = new Response();
            DataInputStream dis = new DataInputStream(this.socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());
            short length = (short)data.length;
            byte[] headerBytes = DataUtil.shortToBytes(length);
            byte[] messagePayload = concat(headerBytes, data);
            dos.write(messagePayload);
            logger.info("message sent to taj medusa");
            dos.flush();
            byte[] lenBytes = new byte[2];
            dis.readFully(lenBytes);
            int contentLength = DataUtil.bytesToShort(lenBytes);
            byte[] resp = new byte[contentLength];
            dis.readFully(resp);
            String mti = this.asciiResponseMessage(resp);
            short len = (short)resp.length;
            byte[] headBytes = DataUtil.shortToBytes(len);
            byte[] response = concat(headBytes, resp);
            TerminalTransactions msg = this.parseResponse(resp, mti);
            responseObj.setResponseByte(response);
            responseObj.setResponseMsg(msg);
            return resp;
        }
        throw new IOException("Socket not connected");
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

    private String asciiResponseMessage(byte[] resp) {
        final String s = new String(resp);
        String mti = s.substring(0,4);
        switch (mti) {
            case "0810":
                logger.info("ISO Network Management ( 0810 )---> {}", s);
                return mti;
            case "0210":
                logger.info("Transaction Message ( 0210 )---> {}", s);
                return mti;
            case "0110":
                logger.info("Authorization Message ( 0110 )---> {}", s);
                return mti;
            case "0430":
                logger.info("Reversal Response Message ( 0430 )---> {}", s);
                return mti;
            default:
                logger.info("Rersponse ---->{}", s);
                return mti;
        }
    }


    public boolean sendData(final byte[] data) throws IOException {
        if (this.socket.isConnected()) {
            final DataOutputStream os = new DataOutputStream(this.socket.getOutputStream());
            os.write(data);
            os.flush();
        }
        return true;
    }

    public byte[] getData() throws IOException {
        final byte[] buffer = new byte[1024];
        int count = 0;
        final DataInputStream is = new DataInputStream(this.socket.getInputStream());
        final int avail = is.available();
        while (is.available() > 0) {
            buffer[count++] = is.readByte();
            if (count >= buffer.length - 1) {
                this.resize(buffer);
            }
        }
        final byte[] returnbuffer = new byte[count - 2];
        System.arraycopy(buffer, 2, returnbuffer, 0, count - 2);
        return returnbuffer;
    }

    public byte[] _getData() throws IOException {
        final byte[] buffer = new byte[1024];
        final DataInputStream is = new DataInputStream(this.socket.getInputStream());
        final int available = is.available();
        if (available > 0) {
            is.read(buffer);
        }
        return buffer;
    }

    private void resize(byte[] buffer) {
        final int m_Size = 2 * buffer.length;
        final int presentsize = buffer.length;
        final byte[] temp = buffer;
        buffer = new byte[m_Size];
        for (int i = 0; i <= presentsize - 1; ++i) {
            buffer[i] = temp[i];
        }
    }

    private static byte[] concat(final byte[] A, final byte[] B) {
        final int aLen = A.length;
        final int bLen = B.length;
        final byte[] C = new byte[aLen + bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }


    private TerminalTransactions parseResponse(final byte[] message, String mti) throws IOException, ParseException {
        final TerminalTransactions response = new TerminalTransactions();
        nibssresponse nibssresponse = new nibssresponse();
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
            printIsoFields(responseMessage, "ISO MESSAGE ====> ");
        }
        catch (Exception e2) {
            return response;
        }
        response.setResponseCode(responseMessage.getObjectValue(39).toString());

        if (responseMessage != null && responseMessage.hasField(4)) {
            response.setMti(mti);
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
            if (responseMessage.hasField(39)) {
                response.setResponseCode(responseMessage.getObjectValue(39).toString());

            }
            if (responseMessage.hasField(41)) {
                response.setTerminalID(responseMessage.getObjectValue(41).toString());
            }
            if (responseMessage.hasField(42)) {
                response.setAgentLocation(responseMessage.getObjectValue(42).toString());
            }
        }
        response.setResponseDesc(nibssresponse.ResponseCodeMap(response.getResponseCode()));
        logger.info("Response Code: {}", response.getResponseCode());
        logger.info("Response Description: {}", response.getResponseDesc());
        return response;

    }


}
