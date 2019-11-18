// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.network;

import com.jayrush.springmvcrest.Nibss.processor.IsoProcessor;
import com.jayrush.springmvcrest.Nibss.utils.DataUtil;

import com.jayrush.springmvcrest.domain.TerminalTransactions;
import com.jayrush.springmvcrest.domain.domainDTO.Response;
import com.jayrush.springmvcrest.domain.nibssresponse;
import com.jayrush.springmvcrest.iso8583.IsoMessage;
import com.jayrush.springmvcrest.iso8583.MessageFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;

import static com.jayrush.springmvcrest.Nibss.processor.IsoProcessor.printIsoFields;

@Component
public class ChannelSocketRequestManager
{
    private SSLSocket socket;

    public ChannelSocketRequestManager() {

    }

    public ChannelSocketRequestManager(final String endpoint, final int port) throws IOException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException {
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

    public byte[] sendAndRecieveData(final byte[] data) throws IOException {
        if (this.socket.isConnected()) {
            final DataOutputStream os = new DataOutputStream(this.socket.getOutputStream());
            final DataInputStream is = new DataInputStream(this.socket.getInputStream());
            final short length = (short)data.length;
            final byte[] headerBytes = DataUtil.shortToBytes(length);
            final byte[] messagePayload = concat(headerBytes, data);
            os.write(messagePayload);
            os.flush();
            final byte[] lenBytes = new byte[2];
            is.readFully(lenBytes);
            final int contentLength = DataUtil.bytesToShort(lenBytes);
            final byte[] resp = new byte[contentLength];
            is.readFully(resp);
            String s = new String(resp);
            System.out.println("The response from server for the yeye hardcoded request is "+s);
            System.out.println(s);
            return resp;
        }
        throw new IOException("Socket not connected");
    }

    public Response toNibss(final byte[] Message) throws IOException, ParseException {
        if (this.socket.isConnected()) {
            Response responseObj = new Response();
            final DataOutputStream Out = new DataOutputStream(this.socket.getOutputStream());
            final DataInputStream In = new DataInputStream(this.socket.getInputStream());
            final short length = (short)Message.length;
            final byte[] headerBytes = DataUtil.shortToBytes(length);
            final byte[] messagePayload = concat(headerBytes, Message);
            Out.write(messagePayload);
            Out.flush();
            final byte[] lenBytes = new byte[2];
            In.readFully(lenBytes);
            final int contentLength = DataUtil.bytesToShort(lenBytes);
            final byte[] resp = new byte[contentLength];
            In.readFully(resp);
            final String s = new String(resp);
            System.out.println("To POS---> "+s);
            final short len = (short)resp.length;
            final byte[] headBytes = DataUtil.shortToBytes(len);
            final byte[] response = concat(headBytes, resp);

            //to log the response message
            final TerminalTransactions msg = parseResponse(resp);


            responseObj.setResponseByte(response);
            responseObj.setResponseMsg(msg);
            return responseObj;
        }
        throw new IOException("Socket not connected");
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

    private static IsoMessage ToISo(final byte[] message) throws IOException, ParseException {
        final MessageFactory<IsoMessage> responseMessageFactory = (MessageFactory<IsoMessage>)new MessageFactory();
        final IsoMessage ismsg = new IsoMessage();
        responseMessageFactory.addMessageTemplate(ismsg);
        responseMessageFactory.setAssignDate(true);
        responseMessageFactory.setUseBinaryBitmap(false);
        responseMessageFactory.setUseBinaryMessages(false);
        responseMessageFactory.setEtx(-1);
        responseMessageFactory.setIgnoreLastMissingField(false);
        responseMessageFactory.setConfigPath(IsoProcessor.CONFIG_FILE);
        final IsoMessage responseMessage = responseMessageFactory.parseMessage(message, 0);
        for (int i = 0; i < 128; ++i) {
            if (responseMessage.hasField(i)) {
                System.out.println("<Field " + i + " = " + responseMessage.getField(i));
            }
        }
        return responseMessage;
    }

//    public TerminalTransactions parseRequest(final byte[] message) throws IOException, ParseException {
//        final TerminalTransactions response = new TerminalTransactions();
//        nibssresponse nibssresponse = new nibssresponse();
//        final IsoMessage isoMessage = null;
//        final MessageFactory<IsoMessage> responseMessageFactory = (MessageFactory<IsoMessage>)new MessageFactory();
//        responseMessageFactory.addMessageTemplate(isoMessage);
//        responseMessageFactory.setAssignDate(true);
//        responseMessageFactory.setUseBinaryBitmap(false);
//        responseMessageFactory.setUseBinaryMessages(false);
//        responseMessageFactory.setEtx(-1);
//        responseMessageFactory.setIgnoreLastMissingField(false);
//        responseMessageFactory.setConfigPath(IsoProcessor.CONFIG_FILE);
//        IsoMessage responseMessage = null;
//        try {
//            responseMessage = responseMessageFactory.parseMessage(message, 0);
//            printIsoFields(responseMessage, "Response ====> ");
//        }
//        catch (Exception e2) {
//            return response;
//        }
//        response.setResponseCode("Pending Response");
//        response.setResponseDesc("");
//        if (responseMessage != null && responseMessage.hasField(4)) {
//            response.setMti("0200");
//            if (responseMessage.hasField(2)) {
//                response.setPan(responseMessage.getObjectValue(2).toString());
//            }
//            if (responseMessage.hasField(4)) {
//                response.setAmount(responseMessage.getObjectValue(4).toString());
//            }
//            if (responseMessage.hasField(7)) {
//                response.setDateTime(responseMessage.getObjectValue(7).toString());
//            }
//            if (responseMessage.hasField(11)) {
//                response.setStan(responseMessage.getObjectValue(11).toString());
//            }
//            if (responseMessage.hasField(12)) {
//                response.setTime(responseMessage.getObjectValue(12).toString());
//            }
//            if (responseMessage.hasField(13)) {
//                response.setDate(responseMessage.getObjectValue(13).toString());
//            }
//            if (responseMessage.hasField(41)) {
//                response.setTerminalID(responseMessage.getObjectValue(41).toString());
//            }
//            if (responseMessage.hasField(42)) {
//                response.setAgentLocation(responseMessage.getObjectValue(42).toString());
//            }
//        }
//        System.out.println("Response: {}"+ (Object)response.getResponseCode());
//        return response;
//
//    }

    private TerminalTransactions parseResponse(final byte[] message) throws IOException, ParseException {
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
            printIsoFields(responseMessage, "Response ====> ");
        }
        catch (Exception e2) {
            return response;
        }
        response.setResponseCode(responseMessage.getObjectValue(39).toString());
        if (responseMessage != null && responseMessage.hasField(4)) {
            response.setMti("0210");
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
                response.setResponseDesc(nibssresponse.ResponseCodeMap(response.getResponseCode()));
            }
            if (responseMessage.hasField(41)) {
                response.setTerminalID(responseMessage.getObjectValue(41).toString());
            }
            if (responseMessage.hasField(42)) {
                response.setAgentLocation(responseMessage.getObjectValue(42).toString());
            }
        }
        System.out.println("Response Code: {}"+ (Object)response.getResponseCode());
        System.out.println("Response Description: {}"+ (Object)response.getResponseDesc());
        return response;

    }


}
