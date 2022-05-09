// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.processor;

import com.jayrush.springmvcrest.Nibss.constants.Globals;
import com.jayrush.springmvcrest.Nibss.constants.TransactionErrorCode;
import com.jayrush.springmvcrest.Nibss.models.transaction.*;
import com.jayrush.springmvcrest.Nibss.network.ChannelSocketRequestManager;
import com.jayrush.springmvcrest.domain.nibssresponse;
import com.jayrush.springmvcrest.iso8583.IsoMessage;
import com.jayrush.springmvcrest.iso8583.IsoType;
import com.jayrush.springmvcrest.iso8583.IsoValue;
import com.jayrush.springmvcrest.iso8583.MessageFactory;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.ParseException;

import static com.jayrush.springmvcrest.Nibss.utils.DataUtil.bytesToHex;
import static com.jayrush.springmvcrest.utility.Utils.maskPanForReceipt;

//import com.solab.iso8583.*;

public class IsoProcessor
{
   // static Logger logger;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(IsoProcessor.class);
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
            final IsoValue<String> field3 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, request.getProcessingCode(), 6);
            final IsoValue<String> field4 = (IsoValue<String>)new IsoValue(IsoType.DATE10, request.getTransmissionDateAndTime(), 10);
            final IsoValue<String> field5 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, request.getSystemTraceAuditNumber(), 6);
            final IsoValue<String> field6 = (IsoValue<String>)new IsoValue(IsoType.TIME, request.getTimeLocalTransaction(), 6);
            final IsoValue<String> field7 = (IsoValue<String>)new IsoValue(IsoType.DATE4, request.getDateLocalTransaction(), 4);
            final IsoValue<String> field8 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getCardAcceptorTerminalId(), 8);
            final IsoValue<String> field9 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, "0100820390018");
            ismsg.setField(3, field3);
            ismsg.setField(7, field4);
            ismsg.setField(11, field5);
            ismsg.setField(12, field6);
            ismsg.setField(13, field7);
            ismsg.setField(41, field8);
            ismsg.setField(62, field9);
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
            System.out.println("Get masterkey response: {}"+ response);
            logger.info("Get masterkey response: {}"+ response);
        }
        catch (IOException e) {
            response = new GetMasterKeyResponse();
            response.setField39("-1");
            System.out.println("Failed to get master key due to IO exception"+ e);
            logger.info("Failed to get master key due to IO exception"+ e);
        }
        catch (Exception e2) {
            response = new GetMasterKeyResponse();
            response.setField39("-1");
            System.out.println("Failed to get pin key"+ e2);
            logger.info("Failed to get pin key"+ e2);
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
            final IsoValue<String> field3 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, request.getProcessingCode(), 6);
            final IsoValue<String> field4 = (IsoValue<String>)new IsoValue(IsoType.DATE10, request.getTransmissionDateAndTime(), 10);
            final IsoValue<String> field5 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, request.getSystemTraceAuditNumber(), 6);
            final IsoValue<String> field6 = (IsoValue<String>)new IsoValue(IsoType.TIME, request.getTimeLocalTransaction(), 6);
            final IsoValue<String> field7 = (IsoValue<String>)new IsoValue(IsoType.DATE4, request.getDateLocalTransaction(), 4);
            final IsoValue<String> field8 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getCardAcceptorTerminalId(), 8);
            final IsoValue<String> field9 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, "0100820390018");
            ismsg.setField(3, field3);
            ismsg.setField(7, field4);
            ismsg.setField(11, field5);
            ismsg.setField(12, field6);
            ismsg.setField(13, field7);
            ismsg.setField(41, field8);
            ismsg.setField(62, field9);
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
            final IsoMessage responseMessage = responseMessageFactory.parseMessage(responseBytes, 0);
            if (responseMessage != null) {
                response = new GetSessionKeyResponse();
                if (responseMessage.hasField(53)) {
                    response.setEncryptedSessionKey(responseMessage.getObjectValue(53).toString());
                }
            }
        }
        catch (IOException e) {
            response = new GetSessionKeyResponse();
            System.out.println("Failed to get session key due to IO exception"+ e);
        }
        catch (Exception e2) {
            response = new GetSessionKeyResponse();
            System.out.println("Failed to get session key"+ e2);
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
            final IsoValue<String> field3 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, request.getProcessingCode(), 6);
            final IsoValue<String> field4 = (IsoValue<String>)new IsoValue(IsoType.DATE10, request.getTransmissionDateAndTime(), 10);
            final IsoValue<String> field5 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, request.getSystemTraceAuditNumber(), 6);
            final IsoValue<String> field6 = (IsoValue<String>)new IsoValue(IsoType.TIME, request.getTimeLocalTransaction(), 6);
            final IsoValue<String> field7 = (IsoValue<String>)new IsoValue(IsoType.DATE4, request.getDateLocalTransaction(), 4);
            final IsoValue<String> field8 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getCardAcceptorTerminalId(), 8);
            final IsoValue<String> field9 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, "0100820390018");
            ismsg.setField(3, field3);
            ismsg.setField(7, field4);
            ismsg.setField(11, field5);
            ismsg.setField(12, field6);
            ismsg.setField(13, field7);
            ismsg.setField(41, field8);
            ismsg.setField(62, field9);
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
            final IsoMessage responseMessage = responseMessageFactory.parseMessage(responseBytes, 0);
            if (responseMessage != null) {
                response = new GetPinKeyResponse();
                if (responseMessage.hasField(53)) {
                    response.setEncryptedPinKey(responseMessage.getObjectValue(53).toString());
                }
            }
        }
        catch (IOException e) {
            response = new GetPinKeyResponse();
            System.out.println("Failed to get pin key due to IO exception"+ e);
        }
        catch (Exception e2) {
            response = new GetPinKeyResponse();
            System.out.println("Failed to get pin key"+ e2);
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
            final IsoValue<String> field3 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, request.getProcessingCode(), 6);
            final IsoValue<String> field4 = (IsoValue<String>)new IsoValue(IsoType.DATE10, request.getTransmissionDateAndTime(), 10);
            final IsoValue<String> field5 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, request.getSystemTraceAuditNumber(), 6);
            final IsoValue<String> field6 = (IsoValue<String>)new IsoValue(IsoType.TIME, request.getTimeLocalTransaction(), 6);
            final IsoValue<String> field7 = (IsoValue<String>)new IsoValue(IsoType.DATE4, request.getDateLocalTransaction(), 4);
            final IsoValue<String> field8 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getCardAcceptorTerminalId(), 8);
            final IsoValue<String> field9 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, "0100820390018");
            final IsoValue<String> field10 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, new String(new byte[] { 0 }), 64);
            ismsg.setField(3, field3);
            ismsg.setField(7, field4);
            ismsg.setField(11, field5);
            ismsg.setField(12, field6);
            ismsg.setField(13, field7);
            ismsg.setField(41, field8);
            ismsg.setField(62, field9);
            ismsg.setField(64, field10);
            final byte[] bites = ismsg.writeData();
            System.out.println("Get Params bytes {}"+ new String(bites));
            final int length = bites.length;
            final byte[] temp = new byte[length - 64];
            if (length >= 64) {
                System.arraycopy(bites, 0, temp, 0, length - 64);
            }
            final String hashHex = generateHash256Value(temp, sessionKey);
            ismsg.setField(64, new IsoValue(IsoType.ALPHA, hashHex, 64));
            final byte[] messagepayload = ismsg.writeData();
            socketRequester = new ChannelSocketRequestManager(IsoProcessor.NIBSS_IP, IsoProcessor.NIBSS_PORT);
            final byte[] responseBytes = socketRequester.sendAndRecieveData(messagepayload);
            System.out.println("Get params response bytes {}"+ new String(responseBytes));
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
            System.out.println("Failed to get master key"+ e);
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
        final IsoValue<String> field2 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, request.getPanField2());
        final IsoValue<String> field3 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, request.getProcessingCodeField3(), 6);
        final IsoValue<String> field4 = (IsoValue<String>)new IsoValue(IsoType.AMOUNT, request.getTransactionAmountField4(), 12);
        final IsoValue<String> field5 = (IsoValue<String>)new IsoValue(IsoType.DATE10, request.getTransmissionDateTimeField7(), 10);
        final IsoValue<String> field6 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, request.getStanField11(), 6);
        final IsoValue<String> field7 = (IsoValue<String>)new IsoValue(IsoType.TIME, request.getLocalTransactionTimeField12(), 6);
        final IsoValue<String> field8 = (IsoValue<String>)new IsoValue(IsoType.DATE4, request.getLocalTransactionDateField13(), 4);
        final IsoValue<String> field9 = (IsoValue<String>)new IsoValue(IsoType.DATE4, request.getCardExpirationDateField14());
        final IsoValue<String> field10 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getMerchantTypeField18(), 4);
        final IsoValue<String> field11 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getPosEntryModeField22(), 3);
        final IsoValue<String> field12 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, request.getCardSequenceNumberField23(), 3);
        final IsoValue<String> field13 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getPosConditionCodeField25(), 2);
        final IsoValue<String> field14 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, request.getPosPinCaptureCodeField26(), 2);
        final IsoValue<String> field15 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getTransactionFeeAmountField28(), 9);
        final IsoValue<String> field16 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, request.getAcquiringInstitutionIdCodeField32());
        final IsoValue<String> field17 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, request.getTrack2DataField35());
        final IsoValue<String> field18 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getRetrievalReferenceNumberField37(), 12);
        final IsoValue<String> field19 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, request.getServiceRestrictionCodeField40(), 3);
        final IsoValue<String> field20 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getTerminalIdField41(), 8);
        final IsoValue<String> field21 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getCardAcceptorIdCodeField42(), 15);
        final IsoValue<String> field22 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getCardAcceptorNameOrLocationField43(), 40);
        final IsoValue<String> field23 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, request.getTransactionCurrencyCodeField49(), 3);
        final IsoValue<String> field24 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, request.getiCCDataField55());
        final IsoValue<String> field25 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, request.getPaymentInformationField60());
        final IsoValue<String> field26 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, request.getPOSDataCodeField123());
        final IsoValue<String> field27 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, new String(new byte[] { 0 }), 64);
        isoMessage.setField(2, field2);
        isoMessage.setField(3, field3);
        isoMessage.setField(4, field4);
        isoMessage.setField(7, field5);
        isoMessage.setField(11, field6);
        isoMessage.setField(12, field7);
        isoMessage.setField(13, field8);
        isoMessage.setField(14, field9);
        isoMessage.setField(18, field10);
        isoMessage.setField(22, field11);
        isoMessage.setField(23, field12);
        isoMessage.setField(25, field13);
        isoMessage.setField(26, field14);
        isoMessage.setField(28, field15);
        isoMessage.setField(32, field16);
        isoMessage.setField(35, field17);
        isoMessage.setField(37, field18);
        isoMessage.setField(40, field19);
        isoMessage.setField(41, field20);
        isoMessage.setField(42, field21);
        isoMessage.setField(43, field22);
        isoMessage.setField(49, field23);
        isoMessage.setField(60, field25);
        isoMessage.setField(55, field24);
        isoMessage.setField(123, field26);
        isoMessage.setField(128, field27);
        if (request.getAuthoriationCode38() != null) {
            final IsoValue<String> field28 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getAuthoriationCode38(), 6);
            isoMessage.setField(38, field28);
        }
        if (request.getPinDataField52() != null) {
            final IsoValue<String> field29 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getPinDataField52(), 16);
            isoMessage.setField(52, field29);
        }
        if (request.getAdditionalAmountsField54() != null) {
            final IsoValue<String> field30 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, request.getAdditionalAmountsField54());
            isoMessage.setField(54, field30);
        }
        if (request.getMessageReasonCodeField56() != null) {
            final IsoValue<String> field31 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, request.getMessageReasonCodeField56());
            isoMessage.setField(56, field31);
        }
        if (request.getTransportDataField59() != null) {
            final IsoValue<String> field32 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, request.getTransportDataField59());
            isoMessage.setField(59, field32);
        }
        if (request.getOriginalDataElementsField90() != null) {
            final IsoValue<String> field33 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getOriginalDataElementsField90(), 42);
            isoMessage.setField(90, field33);
        }
        if (request.getReplacementAmountsField95() != null) {
            final IsoValue<String> field34 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, request.getReplacementAmountsField95(), 42);
            isoMessage.setField(95, field34);
        }
        return isoMessage;
    }

    public static void main(String...args) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyManagementException, KeyStoreException, ParseException {
        ChannelSocketRequestManager socketRequester = null;
//        ChannelSslSocketRequestManager socketRequester = null;
//  String withdrawalMethod = "0200F23E46D129E082000000000000000021165199110778515301001000000000010000030711351800143311351803072411030754110510000012D0000000006519911335199110778515301D24112210025087770255281131172212101KI58FBP2060604626493LINECARD1 LANG                         5662309F26083E0230E279347FEA9F2701809F10120110A50003020000000000000000000000FF9F3704B0CD798A9F36020006950504800008009A032203079C01009F02060000000100009F03060000000000005F2A020566820239009F3303E0F8C85F3401009F3501229F34034103029F1A0205660155101115113441013881C426D3770C4D5B404AFF41C18662848F677CC441975EF8B30C223D1D7D6B";
  String withdrawalMethod = "0200F23E46D129E092000000000000000021164192270019648970001000000000010000040519013500154319013504052411040554110510000012D0000000006419227374192270019648970D241122625057820000009514355201392262101KI58FBP2060604626493LINECARD1 LANG                         566F0B0E74BE6EDC33C2089F2608BE5CC18A373E246D9F2701809F100706020A03A0A8009F370414EA9A319F36020019950508800408009A032204059C01009F02060000000100009F03060000000000005F2A02056682023C009F3303E0F8C85F3401009F3501229F34034203009F1A020566015510111511344101765DCCB7DBADAD3A4101903D779CF304FA8E8B56754C69F9C329E674CC7D4398";//
        //        String pinkey = "080022380000008000009G00000611115633005891115633061123021001";
//        String pin_change = "0600F23846C028E01A0000000000000000211950616601001322113319210000000000000000423151249000154151249042354110510000004365061660100132211331D22116010099104682619187169332101KI58TAJAGENCYBANKIN161918716933@2101KI58TAJ_AGENT        NGDA932B3A7E9EED4100F8E28B216576673D0000000000000000000000000000004269F0106A000000000019F02060000000000009F03060000000000009F090200019F10200FA501A103C0000000000000000000000F0100000000000000000000000000009F150200019F26083564CA4A7F8E4FA29F2701809F3303E0F8C89F34034103009F3501229F360200DC9F37048C49901B9F4104000000019F1A0205669F1E083030303030303031950502000408009A032104239C01005F24032211305F2A0205665F340100820258008407A00000037100015A0A5061660100132211331F57125061660100132211331D2211601009910468015010101511044001BC129F775F0B4E7C012AB5A643E26E8FD0BCF62DB703A20A70758535BA612B20";
        final IsoMessage isoMessages = null;
        final MessageFactory<IsoMessage> responseMessageFactorys = (MessageFactory<IsoMessage>)new MessageFactory();
        responseMessageFactorys.addMessageTemplate(isoMessages);
        responseMessageFactorys.setAssignDate(true);
        responseMessageFactorys.setUseBinaryBitmap(false);
        responseMessageFactorys.setUseBinaryMessages(false);
        responseMessageFactorys.setEtx(-1);
        responseMessageFactorys.setIgnoreLastMissingField(false);
        responseMessageFactorys.setConfigPath(IsoProcessor.CONFIG_FILE);
        IsoMessage responseMessages = null;
        try {
            responseMessages = responseMessageFactorys.parseMessage(withdrawalMethod.getBytes(), 0);
            printIsoFields(responseMessages, "ISO REQUEST MESSAGE ====> ");
        }
        catch (Exception e2) {
//            response.setResponseCodeField39(TransactionErrorCode.FAILED_TO_READ_RESPONSE);
        }

//        ChannelSslSocketRequestManager socketRequester = null;
//        socketRequester = new ChannelSocketRequestManager("test.3lineng.com", 2001);
//        socketRequester = new ChannelSocketRequestManager("10.9.8.64", 8080);
//        socketRequester = new ChannelSocketRequestManager("tms.3lineng.com", 6001);
//        socketRequester = new ChannelSslSocketRequestManager("41.203.107.82", 8888);//2101KI58
//        socketRequester = new ChannelSocketRequestManager("196.13.161.97", 8888);
//        socketRequester = new ChannelSocketRequestManager("54.171.62.223", 9999);
//        socketRequester = new ChannelSocketRequestManager("127.0.0.1", 8080);
//        socketRequester = new ChannelSocketRequestManager("127.0.0.1", 2000);
//        socketRequester = new ChannelSocketRequestManager("nibss.medusang.com", 9090);
        socketRequester = new ChannelSocketRequestManager("core.medusang.com", 8080);
//        socketRequester = new ChannelSocketRequestManager("102.135.213.34", 8888);
//        socketRequester = new ChannelSslSocketRequestManager("41.223.147.221", 8888);


//        socketRequester = new ChannelSocketRequestManager("41.219.149.51", 5336);
//  String withdrawalMethod = "0200F23C46D129E09230000000000000002116539983921671360100000000000000020012221347160000211347161222230654110510010004D0000000003044345399839216713601D2306221001315452018439468589822120442R112302BA000009611CW BY 9PSB_AGENT @ 43, ADEOLA ODEKU S NG566463DB51F4B84428B3349F01061234567890009F02060000000002009F03060000000000009F090200029F10120110A040002A0000000000000000000000FF9F150212349F26086DBABC4EBF43BF229F2701809F3303E0F8C89F34034203009F3501229F360201359F3704A1A9B4BD9F4104000001009F1A0205669F1E083132333435363738950500202480009A031222009C01005F24032306305F2A0205665F340101820239008407A0000000041010006010101017Payment from mpos015511101512344101DBA38C48CD708443D41A58E939BF8254FDE0F72D9B5BBDA1C877625DE558463D";
        //        String withdrawalMethod = "0200F23C46D129E08230000000000000002116418745102695510600100000000660000004131202390000021202390413210154110510020004044 06111130324187451026955106D2101226195532491618311759962262101KI58YOU VERIFY161831175996@2101KI58YOU_VERIFY NG566000006010101017Payment from mpos015511101512344101C8BC8B2D49D732442D072FCE23F6D4E0AC512D4658751BA23D4697AFD1251259";
//        String balance = "0100F23C46D129E08200000000000000002116539983921671360131100000000000000003120055170000170055170312230654110510010012D0000000006539983345399839216713601D2306221001315452000000000001722120442R112302SO000056406Visum Solution LANG                     5662309F26088F026D3E2599E0339F2701809F10120110A50003020000000000000000000000FF9F3704F53491339F36020128950504800008009A032103129C01319F02060000000000009F03060000000000005F2A020566820239009F3303E0F8C85F3401019F3501229F34034103029F1A02056601551011151134410176B828634D7F433712D693BDBCD9329D5A1E74CDCC4D07C80DAB2434474E31B5";

//        String withdrawalMethod = "0200F23C46D129E08200000000000000002116532732010452386200000000000001000004221534450013441534450422220954110510010012D0000000006532732345327320104523862D220922100154277881001344742032212TLPC2A62302SO000056452Tellerpoint.                        LANG5662309F26084557BD1F5DCE10D59F2701809F10120110A50003020400000000000000000000FF9F3704E3C0F1CB9F360205D0950504800008009A032104229C01009F02060000000100009F03060000000000005F2A020566820239009F3303E0F8C85F3401019F3501229F34034103029F1A020566015510101511344101ACF15CBD56EB28102DB8E6D29B96642825EBC73D4CFE971625823DDB63B692ED";


        for(int i = 0; i<306;i++){
            final byte[] responseBytes = socketRequester.sendAndRecieveData(withdrawalMethod.getBytes());
            System.out.println(responseBytes);
            final IsoMessage isoMessage = null;
            final ISO8583TransactionResponse response = new ISO8583TransactionResponse();
            if (responseBytes != null && responseBytes.length > 0) {
                System.out.println("Response receive {}"+ new String(responseBytes));
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
            System.out.println("Response: {}"+ response.getResponseCodeField39());
            System.out.println("Response: {}"+ nibssresponse.ResponseCodeMap(response.getResponseCodeField39()));
        }

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
                final IsoValue<String> field128update = (IsoValue<String>)new IsoValue(IsoType.ALPHA, hashHex, 64);
                isoMessage.setField(128, field128update);
                System.out.println("Message was hashed");
            }
            else {
                System.out.println("Message not hashed");
            }
            //printIsoFields(isoMessage, String.format("%04x request", request.getMessageType()));

            System.out.println("Message to send {}"+ new String(Message));
            socketRequester = new ChannelSocketRequestManager("196.6.103.18", 5009);
            //todo This is where the message coming from the pos will be sent
            //final byte[] responseBytes = socketRequester.sendAndRecieveData(toSend);
//            final byte[] responseBytes = socketRequester.toNibss(Message);
            final byte[] responseBytes = null;
            if (responseBytes != null && responseBytes.length > 0) {
                System.out.println("Response receive {}"+ new String(responseBytes));
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
            System.out.println("Response: {}"+ response.getResponseCodeField39());
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
            final IsoValue<String> field2 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, reversalRequest.getPanField2());
            final IsoValue<String> field3 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, reversalRequest.getProcessingCodeField3(), 6);
            final IsoValue<String> field4 = (IsoValue<String>)new IsoValue(IsoType.AMOUNT, reversalRequest.getTransactionAmountField4(), 12);
            final IsoValue<String> field5 = (IsoValue<String>)new IsoValue(IsoType.DATE10, reversalRequest.getTransmissionDateTimeField7(), 10);
            final IsoValue<String> field6 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, reversalRequest.getStanField11(), 6);
            final IsoValue<String> field7 = (IsoValue<String>)new IsoValue(IsoType.TIME, reversalRequest.getLocalTransactionTimeField12(), 6);
            final IsoValue<String> field8 = (IsoValue<String>)new IsoValue(IsoType.DATE4, reversalRequest.getLocalTransactionDateField13(), 4);
            final IsoValue<String> field9 = (IsoValue<String>)new IsoValue(IsoType.DATE4, reversalRequest.getCardExpirationDateField14());
            final IsoValue<String> field10 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, reversalRequest.getMerchantTypeField18(), 4);
            final IsoValue<String> field11 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, reversalRequest.getPosEntryModeField22(), 3);
            final IsoValue<String> field12 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, reversalRequest.getCardSequenceNumberField23(), 3);
            final IsoValue<String> field13 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, reversalRequest.getPosConditionCodeField25(), 2);
            final IsoValue<String> field14 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, reversalRequest.getPosPinCaptureCodeField26(), 2);
            final IsoValue<String> field15 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, reversalRequest.getTransactionFeeAmountField28(), 9);
            final IsoValue<String> field16 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, reversalRequest.getAcquiringInstitutionIdCodeField32());
            final IsoValue<String> field17 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, reversalRequest.getTrack2DataField35());
            final IsoValue<String> field18 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, reversalRequest.getRetrievalReferenceNumberField37(), 12);
            final IsoValue<String> field19 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, reversalRequest.getServiceRestrictionCodeField40(), 3);
            final IsoValue<String> field20 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, reversalRequest.getTerminalIdField41(), 8);
            final IsoValue<String> field21 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, reversalRequest.getCardAcceptorIdCodeField42(), 15);
            final IsoValue<String> field22 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, reversalRequest.getCardAcceptorNameOrLocationField43(), 40);
            final IsoValue<String> field23 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, reversalRequest.getTransactionCurrencyCodeField49(), 3);
            final IsoValue<String> field24 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, reversalRequest.getPinDataField52(), 16);
            final IsoValue<String> field25 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, reversalRequest.getAdditionalAmountsField54());
            final IsoValue<String> field26 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, reversalRequest.getMessageReasonCodeField56());
            final IsoValue<String> field27 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, reversalRequest.getTransportDataField59());
//            final IsoValue<String> field28 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)reversalRequest.getPaymentInformationField60());
            final IsoValue<String> field29 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, reversalRequest.getOriginalDataElementsField90(), 42);
            final IsoValue<String> field30 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, reversalRequest.getReplacementAmountsField95(), 42);
            final IsoValue<String> field31 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, reversalRequest.getPOSDataCodeField123());
            final IsoValue<String> field32 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, new String(new byte[] { 0 }), 64);
            isoMessage.setField(2, field2);
            isoMessage.setField(3, field3);
            isoMessage.setField(4, field4);
            isoMessage.setField(7, field5);
            isoMessage.setField(11, field6);
            isoMessage.setField(12, field7);
            isoMessage.setField(13, field8);
            isoMessage.setField(14, field9);
            isoMessage.setField(18, field10);
            isoMessage.setField(22, field11);
            isoMessage.setField(23, field12);
            isoMessage.setField(25, field13);
            isoMessage.setField(26, field14);
            isoMessage.setField(28, field15);
            isoMessage.setField(32, field16);
            isoMessage.setField(35, field17);
            isoMessage.setField(37, field18);
            isoMessage.setField(40, field19);
            isoMessage.setField(41, field20);
            isoMessage.setField(42, field21);
            isoMessage.setField(43, field22);
            isoMessage.setField(49, field23);
            if (reversalRequest.getPinDataField52() != null) {
                isoMessage.setField(52, field24);
            }
            if (reversalRequest.getAdditionalAmountsField54() != null) {
                isoMessage.setField(54, field25);
            }
            if (reversalRequest.getMessageReasonCodeField56() != null) {
                isoMessage.setField(56, field26);
            }
            if (reversalRequest.getTransportDataField59() != null) {
                isoMessage.setField(59, field27);
            }
//            isoMessage.setField(60, (IsoValue)field28);
            isoMessage.setField(90, field29);
            isoMessage.setField(95, field30);
            isoMessage.setField(123, field31);
            isoMessage.setField(128, field32);
            final byte[] bites = isoMessage.writeData();
            final int length = bites.length;
            final byte[] temp = new byte[length - 64];
            if (length >= 64) {
                System.arraycopy(bites, 0, temp, 0, length - 64);
            }
            final String hashHex = generateHash256Value(temp, sessionKey);
            final IsoValue<String> field128update = (IsoValue<String>)new IsoValue(IsoType.ALPHA, hashHex, 64);
            isoMessage.setField(128, field128update);
            printIsoFields(isoMessage, "reversal request");
            final byte[] toSend = isoMessage.writeData();
            System.out.println("Message to send {}"+ new String(toSend));
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
            System.out.println("Could not complete reversal"+ e);
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
            System.out.println("Failed to calculate hash"+ e);
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
            String temp = hashText;
            for (int i = 0; i < numberOfZeroes; ++i) {
                zeroes += "0";
            }
            temp = zeroes + temp;
            System.out.println("Utility :: generateHash256Value :: HashValue with zeroes: {}"+ temp);
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
