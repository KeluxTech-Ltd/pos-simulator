// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.processor;

import com.jayrush.springmvcrest.Nibss.constants.Globals;
import com.jayrush.springmvcrest.Nibss.constants.TransactionErrorCode;
import com.jayrush.springmvcrest.Nibss.models.transaction.*;
import com.jayrush.springmvcrest.Nibss.network.ChannelSocketRequestManager;
import com.jayrush.springmvcrest.Nibss.utils.DataUtil;
import com.jayrush.springmvcrest.Service.TerminalInterfaceImpl;
import com.jayrush.springmvcrest.iso8583.IsoMessage;
import com.jayrush.springmvcrest.iso8583.IsoType;
import com.jayrush.springmvcrest.iso8583.IsoValue;
import com.jayrush.springmvcrest.iso8583.MessageFactory;
import com.jayrush.springmvcrest.slf4j.*;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.jayrush.springmvcrest.Nibss.utils.DataUtil.bytesToHex;
import static com.jayrush.springmvcrest.utility.Utils.maskPanForReceipt;

//import com.solab.iso8583.*;

public class IsoProcessor
{
   // static Logger logger;
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(IsoProcessor.class);
    private static String NIBSS_IP;
    private static int NIBSS_PORT;
    public static String CONFIG_FILE;
    
    public static void setConnectionParameters(final String ipAddress, final int portNumber) {
        IsoProcessor.NIBSS_IP = ipAddress;
        IsoProcessor.NIBSS_PORT = portNumber;
    }


    public static GetMasterKeyResponse process(final GetMasterKeyRequest request) {
        GetMasterKeyResponse response = null;
        ChannelSocketRequestManager socketRequester = null;
        try {
            final IsoMessage ismsg = new IsoMessage();
            ismsg.setType(2048);
            final IsoValue<String> field3 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)request.getProcessingCode(), 6);
            final IsoValue<String> field4 = (IsoValue<String>)new IsoValue(IsoType.DATE10, (Object)request.getTransmissionDateAndTime(), 10);
            final IsoValue<String> field5 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)request.getSystemTraceAuditNumber(), 6);
            final IsoValue<String> field6 = (IsoValue<String>)new IsoValue(IsoType.TIME, (Object)request.getTimeLocalTransaction(), 6);
            final IsoValue<String> field7 = (IsoValue<String>)new IsoValue(IsoType.DATE4, (Object)request.getDateLocalTransaction(), 4);
            final IsoValue<String> field8 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getCardAcceptorTerminalId(), 8);
            final IsoValue<String> field9 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)"0100820390018");
            ismsg.setField(3, (IsoValue)field3);
            ismsg.setField(7, (IsoValue)field4);
            ismsg.setField(11, (IsoValue)field5);
            ismsg.setField(12, (IsoValue)field6);
            ismsg.setField(13, (IsoValue)field7);
            ismsg.setField(41, (IsoValue)field8);
            ismsg.setField(62, (IsoValue)field9);
            final byte[] messagepayload = ismsg.writeData();
            socketRequester = new ChannelSocketRequestManager(IsoProcessor.NIBSS_IP, IsoProcessor.NIBSS_PORT);

            String hextoSend = bytesToHex(messagepayload);
            //todo view the hex to be sent
            //System.out.println( Hex.encodeHexString( bytes ) );
            final byte[] responseBytes = socketRequester.sendAndRecieveData(messagepayload);
            //System.out.println("Response Bytes Gotten"+responseBytes);
            final MessageFactory<IsoMessage> responseMessageFactory = (MessageFactory<IsoMessage>)new MessageFactory();
            responseMessageFactory.addMessageTemplate(ismsg);
            responseMessageFactory.setAssignDate(true);
            responseMessageFactory.setUseBinaryBitmap(false);
            responseMessageFactory.setUseBinaryMessages(false);
            responseMessageFactory.setEtx(-1);
            responseMessageFactory.setIgnoreLastMissingField(false);
            responseMessageFactory.setConfigPath(IsoProcessor.CONFIG_FILE);
            final IsoMessage responseMessage = responseMessageFactory.parseMessage(responseBytes, 0);
            if (responseMessage != null) {
                response = new GetMasterKeyResponse();
                if (responseMessage.hasField(39)) {
                    response.setField39(responseMessage.getObjectValue(39).toString());
                }
                if (responseMessage.hasField(53)) {
                    response.setEncryptedMasterKey(responseMessage.getObjectValue(53).toString());
                }
            }
            System.out.println("Get masterkey response: {}"+ (Object)response);
            logger.info("Get masterkey response: {}"+ (Object)response);
        }
        catch (IOException e) {
            response = new GetMasterKeyResponse();
            response.setField39("-1");
            System.out.println("Failed to get master key due to IO exception"+ (Throwable)e);
            logger.info("Failed to get master key due to IO exception"+ (Throwable)e);
        }
        catch (Exception e2) {
            response = new GetMasterKeyResponse();
            response.setField39("-1");
            System.out.println("Failed to get pin key"+ (Throwable)e2);
            logger.info("Failed to get pin key"+ (Throwable)e2);
        }
        finally {
            if (socketRequester != null) {
                try {
                    socketRequester.disconnect();
                }
                catch (IOException ex) {
                    System.out.println("Failed to disconnect socket ");
                    logger.info("Failed to disconnect socket ");
                }
            }
        }
        return response;
    }
    
    public static GetSessionKeyResponse process(final GetSessionKeyRequest request) {
        GetSessionKeyResponse response = null;
        ChannelSocketRequestManager socketRequester = null;
        try {
            final IsoMessage ismsg = new IsoMessage();
            ismsg.setType(2048);
            final IsoValue<String> field3 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)request.getProcessingCode(), 6);
            final IsoValue<String> field4 = (IsoValue<String>)new IsoValue(IsoType.DATE10, (Object)request.getTransmissionDateAndTime(), 10);
            final IsoValue<String> field5 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)request.getSystemTraceAuditNumber(), 6);
            final IsoValue<String> field6 = (IsoValue<String>)new IsoValue(IsoType.TIME, (Object)request.getTimeLocalTransaction(), 6);
            final IsoValue<String> field7 = (IsoValue<String>)new IsoValue(IsoType.DATE4, (Object)request.getDateLocalTransaction(), 4);
            final IsoValue<String> field8 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getCardAcceptorTerminalId(), 8);
            final IsoValue<String> field9 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)"0100820390018");
            ismsg.setField(3, (IsoValue)field3);
            ismsg.setField(7, (IsoValue)field4);
            ismsg.setField(11, (IsoValue)field5);
            ismsg.setField(12, (IsoValue)field6);
            ismsg.setField(13, (IsoValue)field7);
            ismsg.setField(41, (IsoValue)field8);
            ismsg.setField(62, (IsoValue)field9);
            final byte[] messagepayload = ismsg.writeData();
            socketRequester = new ChannelSocketRequestManager(IsoProcessor.NIBSS_IP, IsoProcessor.NIBSS_PORT);
            final byte[] responseBytes = socketRequester.sendAndRecieveData(messagepayload);
            final MessageFactory responseMessageFactory = new MessageFactory();
            responseMessageFactory.addMessageTemplate(ismsg);
            responseMessageFactory.setAssignDate(true);
            responseMessageFactory.setUseBinaryBitmap(false);
            responseMessageFactory.setUseBinaryMessages(false);
            responseMessageFactory.setEtx(-1);
            responseMessageFactory.setIgnoreLastMissingField(false);
            responseMessageFactory.setConfigPath(IsoProcessor.CONFIG_FILE);
            final IsoMessage responseMessage = (IsoMessage) responseMessageFactory.parseMessage(responseBytes, 0);
            if (responseMessage != null) {
                response = new GetSessionKeyResponse();
                if (responseMessage.hasField(53)) {
                    response.setEncryptedSessionKey(responseMessage.getObjectValue(53).toString());
                }
            }
        }
        catch (IOException e) {
            response = new GetSessionKeyResponse();
            System.out.println("Failed to get session key due to IO exception"+ (Throwable)e);
        }
        catch (Exception e2) {
            response = new GetSessionKeyResponse();
            System.out.println("Failed to get session key"+ (Throwable)e2);
        }
        finally {
            if (socketRequester != null) {
                try {
                    socketRequester.disconnect();
                }
                catch (IOException ex) {
                    System.out.println("Failed to disconnect socket" );
                }
            }
        }
        return response;
    }
    
    public static GetPinKeyResponse process(final GetPinKeyRequest request) {
        GetPinKeyResponse response = null;
        ChannelSocketRequestManager socketRequester = null;
        try {
            final IsoMessage ismsg = new IsoMessage();
            ismsg.setType(2048);
            final IsoValue<String> field3 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)request.getProcessingCode(), 6);
            final IsoValue<String> field4 = (IsoValue<String>)new IsoValue(IsoType.DATE10, (Object)request.getTransmissionDateAndTime(), 10);
            final IsoValue<String> field5 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)request.getSystemTraceAuditNumber(), 6);
            final IsoValue<String> field6 = (IsoValue<String>)new IsoValue(IsoType.TIME, (Object)request.getTimeLocalTransaction(), 6);
            final IsoValue<String> field7 = (IsoValue<String>)new IsoValue(IsoType.DATE4, (Object)request.getDateLocalTransaction(), 4);
            final IsoValue<String> field8 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getCardAcceptorTerminalId(), 8);
            final IsoValue<String> field9 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)"0100820390018");
            ismsg.setField(3, (IsoValue)field3);
            ismsg.setField(7, (IsoValue)field4);
            ismsg.setField(11, (IsoValue)field5);
            ismsg.setField(12, (IsoValue)field6);
            ismsg.setField(13, (IsoValue)field7);
            ismsg.setField(41, (IsoValue)field8);
            ismsg.setField(62, (IsoValue)field9);
            final byte[] messagepayload = ismsg.writeData();
            socketRequester = new ChannelSocketRequestManager(IsoProcessor.NIBSS_IP, IsoProcessor.NIBSS_PORT);
            final byte[] responseBytes = socketRequester.sendAndRecieveData(messagepayload);
            final MessageFactory responseMessageFactory = new MessageFactory();
            responseMessageFactory.addMessageTemplate(ismsg);
            responseMessageFactory.setAssignDate(true);
            responseMessageFactory.setUseBinaryBitmap(false);
            responseMessageFactory.setUseBinaryMessages(false);
            responseMessageFactory.setEtx(-1);
            responseMessageFactory.setIgnoreLastMissingField(false);
            responseMessageFactory.setConfigPath(IsoProcessor.CONFIG_FILE);
            final IsoMessage responseMessage = (IsoMessage) responseMessageFactory.parseMessage(responseBytes, 0);
            if (responseMessage != null) {
                response = new GetPinKeyResponse();
                if (responseMessage.hasField(53)) {
                    response.setEncryptedPinKey(responseMessage.getObjectValue(53).toString());
                }
            }
        }
        catch (IOException e) {
            response = new GetPinKeyResponse();
            System.out.println("Failed to get pin key due to IO exception"+ (Throwable)e);
        }
        catch (Exception e2) {
            response = new GetPinKeyResponse();
            System.out.println("Failed to get pin key"+ (Throwable)e2);
        }
        finally {
            if (socketRequester != null) {
                try {
                    socketRequester.disconnect();
                }
                catch (IOException ex) {
                    System.out.println("Failed to disconnect socket" );
                }
            }
        }
        return response;
    }
    
    public static GetParameterResponse process(final GetParameterRequest request, final byte[] sessionKey) {
        GetParameterResponse response = null;
        ChannelSocketRequestManager socketRequester = null;
        try {
            final IsoMessage ismsg = new IsoMessage();
            ismsg.setType(2048);
            final IsoValue<String> field3 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)request.getProcessingCode(), 6);
            final IsoValue<String> field4 = (IsoValue<String>)new IsoValue(IsoType.DATE10, (Object)request.getTransmissionDateAndTime(), 10);
            final IsoValue<String> field5 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)request.getSystemTraceAuditNumber(), 6);
            final IsoValue<String> field6 = (IsoValue<String>)new IsoValue(IsoType.TIME, (Object)request.getTimeLocalTransaction(), 6);
            final IsoValue<String> field7 = (IsoValue<String>)new IsoValue(IsoType.DATE4, (Object)request.getDateLocalTransaction(), 4);
            final IsoValue<String> field8 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getCardAcceptorTerminalId(), 8);
            final IsoValue<String> field9 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)"0100820390018");
            final IsoValue<String> field10 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)new String(new byte[] { 0 }), 64);
            ismsg.setField(3, (IsoValue)field3);
            ismsg.setField(7, (IsoValue)field4);
            ismsg.setField(11, (IsoValue)field5);
            ismsg.setField(12, (IsoValue)field6);
            ismsg.setField(13, (IsoValue)field7);
            ismsg.setField(41, (IsoValue)field8);
            ismsg.setField(62, (IsoValue)field9);
            ismsg.setField(64, (IsoValue)field10);
            final byte[] bites = ismsg.writeData();
            System.out.println("Get Params bytes {}"+ (Object)new String(bites));
            final int length = bites.length;
            final byte[] temp = new byte[length - 64];
            if (length >= 64) {
                System.arraycopy(bites, 0, temp, 0, length - 64);
            }
            final String hashHex = generateHash256Value(temp, sessionKey);
            ismsg.setField(64, new IsoValue(IsoType.ALPHA, (Object)hashHex, 64));
            final byte[] messagepayload = ismsg.writeData();
            socketRequester = new ChannelSocketRequestManager(IsoProcessor.NIBSS_IP, IsoProcessor.NIBSS_PORT);
            final byte[] responseBytes = socketRequester.sendAndRecieveData(messagepayload);
            System.out.println("Get params response bytes {}"+ (Object)new String(responseBytes));
            final MessageFactory<IsoMessage> responseMessageFactory = (MessageFactory<IsoMessage>)new MessageFactory();
            responseMessageFactory.addMessageTemplate(ismsg);
            responseMessageFactory.setAssignDate(true);
            responseMessageFactory.setUseBinaryBitmap(false);
            responseMessageFactory.setUseBinaryMessages(false);
            responseMessageFactory.setEtx(-1);
            responseMessageFactory.setIgnoreLastMissingField(false);
            responseMessageFactory.setConfigPath(IsoProcessor.CONFIG_FILE);
            final IsoMessage responseMessage = responseMessageFactory.parseMessage(responseBytes, 0);
            if (responseMessage != null) {
                response = new GetParameterResponse();
                if (responseMessage.hasField(39)) {
                    response.setField39(responseMessage.getObjectValue(39).toString());
                }
                if (responseMessage.hasField(62)) {
                    response.setField62(responseMessage.getObjectValue(62).toString());
                }
            }
        }
        catch (Exception e) {
            System.out.println("Failed to get master key"+ (Throwable)e);
            if (socketRequester != null) {
                try {
                    socketRequester.disconnect();
                }
                catch (IOException ex) {
                    System.out.println("Failed to disconnect socket" );
                }
            }
        }
        finally {
            if (socketRequester != null) {
                try {
                    socketRequester.disconnect();
                }
                catch (IOException ex2) {
                    System.out.println("Failed to disconnect socket"+ 2);
                }
            }
        }
        return response;
    }
    
    private static IsoMessage generateISOMessage(final ISO8583TransactionRequest request) {
        final IsoMessage isoMessage = new IsoMessage();
        isoMessage.setType(request.getMessageType());
        final IsoValue<String> field2 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, (Object)request.getPanField2());
        final IsoValue<String> field3 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)request.getProcessingCodeField3(), 6);
        final IsoValue<String> field4 = (IsoValue<String>)new IsoValue(IsoType.AMOUNT, (Object)request.getTransactionAmountField4(), 12);
        final IsoValue<String> field5 = (IsoValue<String>)new IsoValue(IsoType.DATE10, (Object)request.getTransmissionDateTimeField7(), 10);
        final IsoValue<String> field6 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)request.getStanField11(), 6);
        final IsoValue<String> field7 = (IsoValue<String>)new IsoValue(IsoType.TIME, (Object)request.getLocalTransactionTimeField12(), 6);
        final IsoValue<String> field8 = (IsoValue<String>)new IsoValue(IsoType.DATE4, (Object)request.getLocalTransactionDateField13(), 4);
        final IsoValue<String> field9 = (IsoValue<String>)new IsoValue(IsoType.DATE4, (Object)request.getCardExpirationDateField14());
        final IsoValue<String> field10 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getMerchantTypeField18(), 4);
        final IsoValue<String> field11 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getPosEntryModeField22(), 3);
        final IsoValue<String> field12 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)request.getCardSequenceNumberField23(), 3);
        final IsoValue<String> field13 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getPosConditionCodeField25(), 2);
        final IsoValue<String> field14 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)request.getPosPinCaptureCodeField26(), 2);
        final IsoValue<String> field15 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getTransactionFeeAmountField28(), 9);
        final IsoValue<String> field16 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, (Object)request.getAcquiringInstitutionIdCodeField32());
        final IsoValue<String> field17 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, (Object)request.getTrack2DataField35());
        final IsoValue<String> field18 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getRetrievalReferenceNumberField37(), 12);
        final IsoValue<String> field19 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)request.getServiceRestrictionCodeField40(), 3);
        final IsoValue<String> field20 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getTerminalIdField41(), 8);
        final IsoValue<String> field21 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getCardAcceptorIdCodeField42(), 15);
        final IsoValue<String> field22 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getCardAcceptorNameOrLocationField43(), 40);
        final IsoValue<String> field23 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)request.getTransactionCurrencyCodeField49(), 3);
        final IsoValue<String> field24 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)request.getiCCDataField55());
        final IsoValue<String> field25 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)request.getPaymentInformationField60());
        final IsoValue<String> field26 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)request.getPOSDataCodeField123());
        final IsoValue<String> field27 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)new String(new byte[] { 0 }), 64);
        isoMessage.setField(2, (IsoValue)field2);
        isoMessage.setField(3, (IsoValue)field3);
        isoMessage.setField(4, (IsoValue)field4);
        isoMessage.setField(7, (IsoValue)field5);
        isoMessage.setField(11, (IsoValue)field6);
        isoMessage.setField(12, (IsoValue)field7);
        isoMessage.setField(13, (IsoValue)field8);
        isoMessage.setField(14, (IsoValue)field9);
        isoMessage.setField(18, (IsoValue)field10);
        isoMessage.setField(22, (IsoValue)field11);
        isoMessage.setField(23, (IsoValue)field12);
        isoMessage.setField(25, (IsoValue)field13);
        isoMessage.setField(26, (IsoValue)field14);
        isoMessage.setField(28, (IsoValue)field15);
        isoMessage.setField(32, (IsoValue)field16);
        isoMessage.setField(35, (IsoValue)field17);
        isoMessage.setField(37, (IsoValue)field18);
        isoMessage.setField(40, (IsoValue)field19);
        isoMessage.setField(41, (IsoValue)field20);
        isoMessage.setField(42, (IsoValue)field21);
        isoMessage.setField(43, (IsoValue)field22);
        isoMessage.setField(49, (IsoValue)field23);
        isoMessage.setField(60, (IsoValue)field25);
        isoMessage.setField(55, (IsoValue)field24);
        isoMessage.setField(123, (IsoValue)field26);
        isoMessage.setField(128, (IsoValue)field27);
        if (request.getAuthoriationCode38() != null) {
            final IsoValue<String> field28 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getAuthoriationCode38(), 6);
            isoMessage.setField(38, (IsoValue)field28);
        }
        if (request.getPinDataField52() != null) {
            final IsoValue<String> field29 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getPinDataField52(), 16);
            isoMessage.setField(52, (IsoValue)field29);
        }
        if (request.getAdditionalAmountsField54() != null) {
            final IsoValue<String> field30 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)request.getAdditionalAmountsField54());
            isoMessage.setField(54, (IsoValue)field30);
        }
        if (request.getMessageReasonCodeField56() != null) {
            final IsoValue<String> field31 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)request.getMessageReasonCodeField56());
            isoMessage.setField(56, (IsoValue)field31);
        }
        if (request.getTransportDataField59() != null) {
            final IsoValue<String> field32 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)request.getTransportDataField59());
            isoMessage.setField(59, (IsoValue)field32);
        }
        if (request.getOriginalDataElementsField90() != null) {
            final IsoValue<String> field33 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getOriginalDataElementsField90(), 42);
            isoMessage.setField(90, (IsoValue)field33);
        }
        if (request.getReplacementAmountsField95() != null) {
            final IsoValue<String> field34 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)request.getReplacementAmountsField95(), 42);
            isoMessage.setField(95, (IsoValue)field34);
        }
        return isoMessage;
    }
    
    public static ISO8583TransactionResponse processISO8583Transaction(final ISO8583TransactionRequest request, final byte[] sessionKey, final byte[] Message) {
        final ISO8583TransactionResponse response = new ISO8583TransactionResponse();
        ChannelSocketRequestManager socketRequester = null;
        try {
            final IsoMessage isoMessage = null;
            final byte[] bites = null;
            final int length = bites.length;
            final byte[] temp = new byte[length - 64];
            if (length >= 64) {
                System.arraycopy(bites, 0, temp, 0, length - 64);
            }
            if (request.hashMessage()) {
                final String hashHex = generateHash256Value(temp, sessionKey);
                final IsoValue<String> field128update = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)hashHex, 64);
                isoMessage.setField(128, (IsoValue)field128update);
                System.out.println("Message was hashed");
            }
            else {
                System.out.println("Message not hashed");
            }
            //printIsoFields(isoMessage, String.format("%04x request", request.getMessageType()));

            System.out.println("Message to send {}"+ (Object)new String(Message));
            socketRequester = new ChannelSocketRequestManager("196.6.103.18", 5009);
            //todo This is where the message coming from the pos will be sent
            //final byte[] responseBytes = socketRequester.sendAndRecieveData(toSend);
//            final byte[] responseBytes = socketRequester.toNibss(Message);
            final byte[] responseBytes = null;
            if (responseBytes != null && responseBytes.length > 0) {
                System.out.println("Response receive {}"+ (Object)new String(responseBytes));
            }
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
                responseMessage = responseMessageFactory.parseMessage(responseBytes, 0);
                printIsoFields(responseMessage, "ISO MESSAGE ====> ");
            }
            catch (Exception e2) {
                response.setResponseCodeField39(TransactionErrorCode.FAILED_TO_READ_RESPONSE);
                return response;
            }
            if (responseMessage != null) {
                if (responseMessage.hasField(2)) {
                    response.setPanField2(responseMessage.getObjectValue(2).toString());
                }
                if (responseMessage.hasField(3)) {
                    response.setProcessingCodeField3(responseMessage.getObjectValue(3).toString());
                }
                if (responseMessage.hasField(4)) {
                    response.setTransactionAmountField4(responseMessage.getObjectValue(4).toString());
                }
                if (responseMessage.hasField(7)) {
                    response.setTransactionSettlementAmountField7(responseMessage.getObjectValue(7).toString());
                }
                if (responseMessage.hasField(11)) {
                    response.setStanField11(responseMessage.getObjectValue(11).toString());
                }
                if (responseMessage.hasField(12)) {
                    response.setLocalTransactionTimeField12(responseMessage.getObjectValue(12).toString());
                }
                if (responseMessage.hasField(13)) {
                    response.setLocalTransactionDateField13(responseMessage.getObjectValue(13).toString());
                }
                if (responseMessage.hasField(14)) {
                    response.setCardExpirationDateField14(responseMessage.getObjectValue(14).toString());
                }
                if (responseMessage.hasField(15)) {
                    response.setSettlementDateField15(responseMessage.getObjectValue(15).toString());
                }
                if (responseMessage.hasField(18)) {
                    response.setMerchantTypeField18(responseMessage.getObjectValue(18).toString());
                }
                if (responseMessage.hasField(22)) {
                    response.setPosEntryModeField22(responseMessage.getObjectValue(22).toString());
                }
                if (responseMessage.hasField(23)) {
                    response.setCardSequenceNumberField23(responseMessage.getObjectValue(23).toString());
                }
                if (responseMessage.hasField(25)) {
                    response.setPosConditionCodeField25(responseMessage.getObjectValue(25).toString());
                }
                if (responseMessage.hasField(28)) {
                    response.setTransactionFeeAmountField28(responseMessage.getObjectValue(28).toString());
                }
                if (responseMessage.hasField(30)) {
                    response.setTransactionProcessingFeeAmountField30(responseMessage.getObjectValue(30).toString());
                }
                if (responseMessage.hasField(32)) {
                    response.setAcquiringInstitutionIdCodeField32(responseMessage.getObjectValue(32).toString());
                }
                if (responseMessage.hasField(33)) {
                    response.setForwardingInstitutionIdCodeField33(responseMessage.getObjectValue(33).toString());
                }
                if (responseMessage.hasField(35)) {
                    response.setTrack2DataField35(responseMessage.getObjectValue(35).toString());
                }
                if (responseMessage.hasField(37)) {
                    response.setRetrievalReferenceNumberField37(responseMessage.getObjectValue(37).toString());
                }
                if (responseMessage.hasField(38)) {
                    response.setAuthorizationIdResponseField38(responseMessage.getObjectValue(38).toString());
                }
                if (responseMessage.hasField(39)) {
                    response.setResponseCodeField39(responseMessage.getObjectValue(39).toString());
                }
                if (responseMessage.hasField(40)) {
                    response.setServiceRestrictionCodeField40(responseMessage.getObjectValue(40).toString());
                }
                if (responseMessage.hasField(41)) {
                    response.setTerminalIdField41(responseMessage.getObjectValue(41).toString());
                }
                if (responseMessage.hasField(42)) {
                    response.setCardAcceptorIdCodeField42(responseMessage.getObjectValue(42).toString());
                }
                if (responseMessage.hasField(43)) {
                    response.setCardAcceptorNameOrLocationField43(responseMessage.getObjectValue(43).toString());
                }
                if (responseMessage.hasField(49)) {
                    response.setTransactionCurrencyCodeField49(responseMessage.getObjectValue(49).toString());
                }
                if (responseMessage.hasField(54)) {
                    response.setAdditionalAmountsField54(responseMessage.getObjectValue(54).toString());
                }
                if (responseMessage.hasField(55)) {
                    response.setiCCDataField55(responseMessage.getObjectValue(55).toString());
                }
                if (responseMessage.hasField(56)) {
                    response.setMessageReasonCodeField56(responseMessage.getObjectValue(56).toString());
                }
                if (responseMessage.hasField(59)) {
                    response.setTransportDataField59(responseMessage.getObjectValue(59).toString());
                }
                if (responseMessage.hasField(90)) {
                    response.setOriginalDataElementsField90(responseMessage.getObjectValue(90).toString());
                }
                if (responseMessage.hasField(95)) {
                    response.setReplacementAmountsField95(responseMessage.getObjectValue(95).toString());
                }
                if (responseMessage.hasField(102)) {
                    response.setAccountIdentification1Field102(responseMessage.getObjectValue(102).toString());
                }
                if (responseMessage.hasField(103)) {
                    response.setAccountIdentification2Field103(responseMessage.getObjectValue(103).toString());
                }
                if (responseMessage.hasField(123)) {
                    response.setPOSDataCodeField123(responseMessage.getObjectValue(123).toString());
                }
                if (responseMessage.hasField(124)) {
                    response.setNFCDataField124(responseMessage.getObjectValue(124).toString());
                }
                if (responseMessage.hasField(128)) {
                    response.setSecondaryMessageHashValueField128(responseMessage.getObjectValue(128).toString());
                }
            }
            System.out.println("Response: {}"+ (Object)response.getResponseCodeField39());
        }
        catch (EOFException ex2) {
            response.setResponseCodeField39(TransactionErrorCode.FAILED_TO_READ_RESPONSE);
            return response;
        }
        catch (Exception e) {
            System.out.println("Could not complete financial transaction");
        }
        finally {
            if (socketRequester != null) {
                try {
                    socketRequester.disconnect();
                }
                catch (IOException ex) {
                    System.out.println("Failed to disconnect socket requester" );
                }
            }
        }
        return response;
    }
    
    public static ReversalResponse process(final ReversalRequest reversalRequest, final byte[] sessionKey, final int reversalCount) {
        ReversalResponse response = new ReversalResponse();
        ChannelSocketRequestManager socketRequester = null;
        try {
            final IsoMessage isoMessage = new IsoMessage();
            if (reversalCount > 1) {
                isoMessage.setType(1057);
            }
            else {
                isoMessage.setType(1056);
            }
            final IsoValue<String> field2 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, (Object)reversalRequest.getPanField2());
            final IsoValue<String> field3 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)reversalRequest.getProcessingCodeField3(), 6);
            final IsoValue<String> field4 = (IsoValue<String>)new IsoValue(IsoType.AMOUNT, (Object)reversalRequest.getTransactionAmountField4(), 12);
            final IsoValue<String> field5 = (IsoValue<String>)new IsoValue(IsoType.DATE10, (Object)reversalRequest.getTransmissionDateTimeField7(), 10);
            final IsoValue<String> field6 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)reversalRequest.getStanField11(), 6);
            final IsoValue<String> field7 = (IsoValue<String>)new IsoValue(IsoType.TIME, (Object)reversalRequest.getLocalTransactionTimeField12(), 6);
            final IsoValue<String> field8 = (IsoValue<String>)new IsoValue(IsoType.DATE4, (Object)reversalRequest.getLocalTransactionDateField13(), 4);
            final IsoValue<String> field9 = (IsoValue<String>)new IsoValue(IsoType.DATE4, (Object)reversalRequest.getCardExpirationDateField14());
            final IsoValue<String> field10 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)reversalRequest.getMerchantTypeField18(), 4);
            final IsoValue<String> field11 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)reversalRequest.getPosEntryModeField22(), 3);
            final IsoValue<String> field12 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)reversalRequest.getCardSequenceNumberField23(), 3);
            final IsoValue<String> field13 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)reversalRequest.getPosConditionCodeField25(), 2);
            final IsoValue<String> field14 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)reversalRequest.getPosPinCaptureCodeField26(), 2);
            final IsoValue<String> field15 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)reversalRequest.getTransactionFeeAmountField28(), 9);
            final IsoValue<String> field16 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, (Object)reversalRequest.getAcquiringInstitutionIdCodeField32());
            final IsoValue<String> field17 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, (Object)reversalRequest.getTrack2DataField35());
            final IsoValue<String> field18 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)reversalRequest.getRetrievalReferenceNumberField37(), 12);
            final IsoValue<String> field19 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)reversalRequest.getServiceRestrictionCodeField40(), 3);
            final IsoValue<String> field20 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)reversalRequest.getTerminalIdField41(), 8);
            final IsoValue<String> field21 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)reversalRequest.getCardAcceptorIdCodeField42(), 15);
            final IsoValue<String> field22 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)reversalRequest.getCardAcceptorNameOrLocationField43(), 40);
            final IsoValue<String> field23 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)reversalRequest.getTransactionCurrencyCodeField49(), 3);
            final IsoValue<String> field24 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)reversalRequest.getPinDataField52(), 16);
            final IsoValue<String> field25 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)reversalRequest.getAdditionalAmountsField54());
            final IsoValue<String> field26 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)reversalRequest.getMessageReasonCodeField56());
            final IsoValue<String> field27 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)reversalRequest.getTransportDataField59());
            final IsoValue<String> field28 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)reversalRequest.getPaymentInformationField60());
            final IsoValue<String> field29 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)reversalRequest.getOriginalDataElementsField90(), 42);
            final IsoValue<String> field30 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)reversalRequest.getReplacementAmountsField95(), 42);
            final IsoValue<String> field31 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)reversalRequest.getPOSDataCodeField123());
            final IsoValue<String> field32 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)new String(new byte[] { 0 }), 64);
            isoMessage.setField(2, (IsoValue)field2);
            isoMessage.setField(3, (IsoValue)field3);
            isoMessage.setField(4, (IsoValue)field4);
            isoMessage.setField(7, (IsoValue)field5);
            isoMessage.setField(11, (IsoValue)field6);
            isoMessage.setField(12, (IsoValue)field7);
            isoMessage.setField(13, (IsoValue)field8);
            isoMessage.setField(14, (IsoValue)field9);
            isoMessage.setField(18, (IsoValue)field10);
            isoMessage.setField(22, (IsoValue)field11);
            isoMessage.setField(23, (IsoValue)field12);
            isoMessage.setField(25, (IsoValue)field13);
            isoMessage.setField(26, (IsoValue)field14);
            isoMessage.setField(28, (IsoValue)field15);
            isoMessage.setField(32, (IsoValue)field16);
            isoMessage.setField(35, (IsoValue)field17);
            isoMessage.setField(37, (IsoValue)field18);
            isoMessage.setField(40, (IsoValue)field19);
            isoMessage.setField(41, (IsoValue)field20);
            isoMessage.setField(42, (IsoValue)field21);
            isoMessage.setField(43, (IsoValue)field22);
            isoMessage.setField(49, (IsoValue)field23);
            if (reversalRequest.getPinDataField52() != null) {
                isoMessage.setField(52, (IsoValue)field24);
            }
            if (reversalRequest.getAdditionalAmountsField54() != null) {
                isoMessage.setField(54, (IsoValue)field25);
            }
            if (reversalRequest.getMessageReasonCodeField56() != null) {
                isoMessage.setField(56, (IsoValue)field26);
            }
            if (reversalRequest.getTransportDataField59() != null) {
                isoMessage.setField(59, (IsoValue)field27);
            }
            isoMessage.setField(60, (IsoValue)field28);
            isoMessage.setField(90, (IsoValue)field29);
            isoMessage.setField(95, (IsoValue)field30);
            isoMessage.setField(123, (IsoValue)field31);
            isoMessage.setField(128, (IsoValue)field32);
            final byte[] bites = isoMessage.writeData();
            final int length = bites.length;
            final byte[] temp = new byte[length - 64];
            if (length >= 64) {
                System.arraycopy(bites, 0, temp, 0, length - 64);
            }
            final String hashHex = generateHash256Value(temp, sessionKey);
            final IsoValue<String> field128update = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)hashHex, 64);
            isoMessage.setField(128, (IsoValue)field128update);
            printIsoFields(isoMessage, "reversal request");
            final byte[] toSend = isoMessage.writeData();
            System.out.println("Message to send {}"+ (Object)new String(toSend));
            socketRequester = new ChannelSocketRequestManager(IsoProcessor.NIBSS_IP, IsoProcessor.NIBSS_PORT);
            final byte[] responseBytes = socketRequester.sendAndRecieveData(toSend);
            final MessageFactory<IsoMessage> responseMessageFactory = (MessageFactory<IsoMessage>)new MessageFactory();
            responseMessageFactory.addMessageTemplate(isoMessage);
            responseMessageFactory.setAssignDate(true);
            responseMessageFactory.setUseBinaryBitmap(false);
            responseMessageFactory.setUseBinaryMessages(false);
            responseMessageFactory.setEtx(-1);
            responseMessageFactory.setIgnoreLastMissingField(false);
            responseMessageFactory.setConfigPath(IsoProcessor.CONFIG_FILE);
            final IsoMessage responseMessage = responseMessageFactory.parseMessage(responseBytes, 0);
            printIsoFields(responseMessage, "Reversal Response ====> ");
            if (responseMessage != null) {
                response = new ReversalResponse();
                if (responseMessage.hasField(2)) {
                    response.setPanField2(responseMessage.getObjectValue(2).toString());
                }
                if (responseMessage.hasField(3)) {
                    response.setProcessingCodeField3(responseMessage.getObjectValue(3).toString());
                }
                if (responseMessage.hasField(4)) {
                    response.setTransactionAmountField4(responseMessage.getObjectValue(4).toString());
                }
                if (responseMessage.hasField(11)) {
                    response.setStanField11(responseMessage.getObjectValue(11).toString());
                }
                if (responseMessage.hasField(12)) {
                    response.setLocalTransactionTimeField12(responseMessage.getObjectValue(12).toString());
                }
                if (responseMessage.hasField(13)) {
                    response.setLocalTransactionDateField13(responseMessage.getObjectValue(13).toString());
                }
                if (responseMessage.hasField(14)) {
                    response.setCardExpirationDateField14(responseMessage.getObjectValue(14).toString());
                }
                if (responseMessage.hasField(15)) {
                    response.setSettlementDateField15(responseMessage.getObjectValue(15).toString());
                }
                if (responseMessage.hasField(18)) {
                    response.setMerchantTypeField18(responseMessage.getObjectValue(18).toString());
                }
                if (responseMessage.hasField(22)) {
                    response.setPosEntryModeField22(responseMessage.getObjectValue(22).toString());
                }
                if (responseMessage.hasField(23)) {
                    response.setCardSequenceNumberField23(responseMessage.getObjectValue(23).toString());
                }
                if (responseMessage.hasField(25)) {
                    response.setPosConditionCodeField25(responseMessage.getObjectValue(25).toString());
                }
                if (responseMessage.hasField(28)) {
                    response.setTransactionFeeAmountField28(responseMessage.getObjectValue(28).toString());
                }
                if (responseMessage.hasField(30)) {
                    response.setTransactionProcessingFeeAmountField30(responseMessage.getObjectValue(30).toString());
                }
                if (responseMessage.hasField(32)) {
                    response.setAcquiringInstitutionIdCodeField32(responseMessage.getObjectValue(32).toString());
                }
                if (responseMessage.hasField(33)) {
                    response.setForwardingInstitutionIdCodeField33(responseMessage.getObjectValue(33).toString());
                }
                if (responseMessage.hasField(35)) {
                    response.setTrack2DataField35(responseMessage.getObjectValue(35).toString());
                }
                if (responseMessage.hasField(37)) {
                    response.setRetrievalReferenceNumberField37(responseMessage.getObjectValue(37).toString());
                }
                if (responseMessage.hasField(38)) {
                    response.setAuthorizationIdResponseField38(responseMessage.getObjectValue(38).toString());
                }
                if (responseMessage.hasField(39)) {
                    response.setResponseCodeField39(responseMessage.getObjectValue(39).toString());
                }
                if (responseMessage.hasField(40)) {
                    response.setServiceRestrictionCodeField40(responseMessage.getObjectValue(40).toString());
                }
                if (responseMessage.hasField(41)) {
                    response.setTerminalIdField41(responseMessage.getObjectValue(41).toString());
                }
                if (responseMessage.hasField(42)) {
                    response.setCardAcceptorIdCodeField42(responseMessage.getObjectValue(42).toString());
                }
                if (responseMessage.hasField(43)) {
                    response.setCardAcceptorNameOrLocationField43(responseMessage.getObjectValue(43).toString());
                }
                if (responseMessage.hasField(49)) {
                    response.setTransactionCurrencyCodeField49(responseMessage.getObjectValue(49).toString());
                }
                if (responseMessage.hasField(54)) {
                    response.setAdditionalAmountsField54(responseMessage.getObjectValue(54).toString());
                }
                if (responseMessage.hasField(56)) {
                    response.setMessageReasonCodeField56(responseMessage.getObjectValue(56).toString());
                }
                if (responseMessage.hasField(59)) {
                    response.setTransportDataField59(responseMessage.getObjectValue(59).toString());
                }
                if (responseMessage.hasField(90)) {
                    response.setOriginalDataElementsField90(responseMessage.getObjectValue(90).toString());
                }
                if (responseMessage.hasField(95)) {
                    response.setReplacementAmountsField95(responseMessage.getObjectValue(95).toString());
                }
                if (responseMessage.hasField(102)) {
                    response.setAccountIdentification1Field102(responseMessage.getObjectValue(102).toString());
                }
                if (responseMessage.hasField(103)) {
                    response.setAccountIdentification2Field103(responseMessage.getObjectValue(103).toString());
                }
                if (responseMessage.hasField(123)) {
                    response.setPOSDataCodeField123(responseMessage.getObjectValue(123).toString());
                }
                if (responseMessage.hasField(128)) {
                    response.setSecondaryMessageHashValueField128(responseMessage.getObjectValue(128).toString());
                }
            }
        }
        catch (Exception e) {
            System.out.println("Could not complete reversal"+ (Throwable)e);
            if (socketRequester != null) {
                try {
                    socketRequester.disconnect();
                }
                catch (IOException ex) {
                    System.out.println("Failed to disconnect socket");
                }
            }
        }
        finally {
            if (socketRequester != null) {
                try {
                    socketRequester.disconnect();
                }
                catch (IOException ex2) {
                    System.out.println("Failed to disconnect socket"+ 2);
                }
            }
        }
        return response;
    }
    
    private static byte[] calculateHash(final byte[] sessionKey, final byte[] iso) {
        try {
            final MessageDigest sha256Context = MessageDigest.getInstance("SHA-256");
            sha256Context.reset();
            sha256Context.update(sessionKey, 0, sessionKey.length);
            sha256Context.update(iso, 0, iso.length);
            return sha256Context.digest();
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println("Failed to calculate hash"+ (Throwable)e);
            return null;
        }
    }
    
    public static String generateHash256Value(final byte[] iso, final byte[] key) {
        String hashText = null;
        try {
            final MessageDigest m = MessageDigest.getInstance("SHA-256");
            m.update(key, 0, key.length);
            m.update(iso, 0, iso.length);
            hashText = bytesToHex(m.digest());
            hashText = hashText.replace(" ", "");
        }
        catch (NoSuchAlgorithmException ex) {
            System.out.println("Hashing " );
        }
        if (hashText.length() < 64) {
            final int numberOfZeroes = 64 - hashText.length();
            String zeroes = "";
            String temp = hashText.toString();
            for (int i = 0; i < numberOfZeroes; ++i) {
                zeroes += "0";
            }
            temp = zeroes + temp;
            System.out.println("Utility :: generateHash256Value :: HashValue with zeroes: {}"+ (Object)temp);
            return temp;
        }
        return hashText;
    }
    
    private static byte[] concat(final byte[] A, final byte[] B) {
        final int aLen = A.length;
        final int bLen = B.length;
        final byte[] C = new byte[aLen + bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }
    
    public static void printIsoFields(final IsoMessage isoMessage, final String type) {
        if (isoMessage == null) {
            return;
        }
        logger.info("==================================================");
        logger.info(type);
        for (int index = 1; index <= 128; ++index) {
            if (isoMessage.hasField(index)) {
                if (index == 2){
                    String pan = maskPanForReceipt(isoMessage.getAt(index).getValue().toString());
                    logger.info("<field {}> = {}",index,pan);
                }else {
                    logger.info("field {} : {}" , index , isoMessage.getAt(index).getValue());
                }
            }
        }
    }
    
    static {
        IsoProcessor.NIBSS_IP = null;
        IsoProcessor.NIBSS_PORT = 0;
        IsoProcessor.CONFIG_FILE = Globals.ISO_CONFIG_FILE;
    }
}
