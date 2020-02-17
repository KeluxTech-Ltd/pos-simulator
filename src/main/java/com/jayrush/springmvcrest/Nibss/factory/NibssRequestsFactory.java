// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.factory;

import com.jayrush.springmvcrest.Nibss.constants.Globals;
import com.jayrush.springmvcrest.Nibss.constants.TransactionErrorCode;
import com.jayrush.springmvcrest.Nibss.models.store.OfflineCTMK;
import com.jayrush.springmvcrest.Nibss.models.transaction.*;
import com.jayrush.springmvcrest.Nibss.processor.IsoProcessor;
import com.jayrush.springmvcrest.Nibss.repository.DataStore;
import com.jayrush.springmvcrest.Nibss.utils.DataUtil;
import com.jayrush.springmvcrest.Nibss.utils.ISOUtil;
import com.jayrush.springmvcrest.Nibss.utils.ParameterParser;
import com.jayrush.springmvcrest.Nibss.utils.StringUtils;
import com.jayrush.springmvcrest.Repositories.terminalKeysRepo;
import com.jayrush.springmvcrest.domain.domainDTO.host;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class NibssRequestsFactory
{

    terminalKeysRepo terminalKeysRepo;

    private static final String TAG;
    private static final String acquirerID = "111130";
   // static Logger logger;
    private DataStore dataStore;
    private String terminalId;
    String TerminalSessionKey = "";
    
    public NibssRequestsFactory(final DataStore dataStore, final String terminalId, final terminalKeysRepo terminalKeysRepo ) {
        this.dataStore = dataStore;
        this.terminalId = terminalId;
        this.terminalKeysRepo = terminalKeysRepo;
    }
    
    public String getMasterKey(final host host, String ctmkString) {
        try {
//            terminalKeyManagement terminalKeyManagement = terminalKeysRepo.findByTerminalID(this.terminalId);
//            if (Objects.isNull(terminalKeyManagement)){
//                terminalKeyManagement = new terminalKeyManagement();
//            }
            final GetMasterKeyRequest rk = new GetMasterKeyRequest();
            rk.setCardAcceptorTerminalId(this.terminalId);
            rk.setProcessingCode("9A0000");
            rk.setDateLocalTransaction(DataUtil.dateLocalTransaction(new Date()));
            rk.setTimeLocalTransaction(DataUtil.timeLocalTransaction(new Date()));
            rk.setTransmissionDateAndTime(DataUtil.transmissionDateAndTime(new Date()));
            final int counter = this.dataStore.getInt(Globals.PREF_MASTER_KEY_STAN) + 1;
            this.dataStore.putInt(Globals.PREF_MASTER_KEY_STAN, counter);
            rk.setSystemTraceAuditNumber(DataUtil.leftZeroPad(counter));
            final String nibssIpPAddress = host.getHostIp();
            final int nibssPort = host.getHostPort();
            IsoProcessor.setConnectionParameters(nibssIpPAddress, nibssPort);
            System.out.println(rk.toString());
            final GetMasterKeyResponse rep = IsoProcessor.process(rk);
            this.dataStore.putString(Globals.PREF_TMK_ENC, rep.getEncryptedMasterKey());
//            final String ctmkString = ISOUtil.hexor(offlineCTMKManager.getComponentOne(), offlineCTMKManager.getComponentTwo());
            final byte[] ctmk = StringUtils.hexStringToByteArray(ctmkString);
            rep.decryptMasterKey(ctmk);
            //this holds the clear masterKey
            Globals.TMK = rep.getClearMasterKey();
            this.dataStore.putString(Globals.PREF_TMK, StringUtils.bytesToHex(rep.getClearMasterKey()));
//            Keys.eClearMasterKey = rep.getEncryptedMasterKey();
//            terminalKeyManagement.setTerminalID(terminalId);
//            terminalKeyManagement.setMasterKey(StringUtils.bytesToHex(rep.getClearMasterKey()));
//            terminalKeysRepo.save(terminalKeyManagement);
//            return true;
            return StringUtils.bytesToHex(rep.getClearMasterKey());
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getSessionKey(final host host) {
        try {
            final GetSessionKeyRequest sk = new GetSessionKeyRequest();
            sk.setCardAcceptorTerminalId(this.terminalId);
            sk.setProcessingCode("9B0000");
            sk.setDateLocalTransaction(DataUtil.dateLocalTransaction(new Date()));
            sk.setTimeLocalTransaction(DataUtil.timeLocalTransaction(new Date()));
            sk.setTransmissionDateAndTime(DataUtil.transmissionDateAndTime(new Date()));
            final int counter = this.dataStore.getInt(Globals.PREF_SESSION_KEY_STAN) + 1;
            this.dataStore.putInt(Globals.PREF_SESSION_KEY_STAN, counter);
            sk.setSystemTraceAuditNumber(DataUtil.leftZeroPad(counter));
            final String nibssIpPAddress = host.getHostIp();
            final int nibssPort = host.getHostPort();
            IsoProcessor.setConnectionParameters(nibssIpPAddress, nibssPort);
            final GetSessionKeyResponse skResponse = IsoProcessor.process(sk);
            this.dataStore.putString(Globals.PREF_TSK_ENC, skResponse.getEncryptedSessionKey());
            skResponse.decryptSessionKey(Globals.TMK);
           // NibssRequestsFactory.logger.info(String.format("Session Key key -> %s", StringUtils.bytesToHex(skResponse.getClearSessionKey())));
            System.out.println(String.format("Session Key key -> %s", StringUtils.bytesToHex(skResponse.getClearSessionKey())));
            TerminalSessionKey = StringUtils.bytesToHex(skResponse.getClearSessionKey());
            this.dataStore.putString(Globals.PREF_TSK, StringUtils.bytesToHex(skResponse.getClearSessionKey()));
//            Keys.eClearSessionKey = skResponse.getEncryptedSessionKey();
//            terminalKeyManagement terminalKeyManagement = terminalKeysRepo.findByTerminalID(sk.getCardAcceptorTerminalId());
//            terminalKeyManagement.setSessionKey(TerminalSessionKey);
//            terminalKeysRepo.save(terminalKeyManagement);
            return TerminalSessionKey;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getPinKey(host host) {
        try {
            final GetPinKeyRequest pk = new GetPinKeyRequest();
            pk.setCardAcceptorTerminalId(this.terminalId);
            pk.setProcessingCode("9G0000");
            pk.setDateLocalTransaction(DataUtil.dateLocalTransaction(new Date()));
            pk.setTimeLocalTransaction(DataUtil.timeLocalTransaction(new Date()));
            pk.setTransmissionDateAndTime(DataUtil.transmissionDateAndTime(new Date()));
            final int counter = this.dataStore.getInt(Globals.PREF_PIN_KEY_STAN) + 1;
            this.dataStore.putInt(Globals.PREF_PIN_KEY_STAN, counter);
            pk.setSystemTraceAuditNumber(DataUtil.leftZeroPad(counter));
            final String nibssIpPAddress = host.getHostIp();
            final int nibssPort = host.getHostPort();
            IsoProcessor.setConnectionParameters(nibssIpPAddress, nibssPort);
            final GetPinKeyResponse pkResponse = IsoProcessor.process(pk);
            this.dataStore.putString(Globals.PREF_TPK_ENC, pkResponse.getEncryptedPinKey());
            pkResponse.descryptPinKey(Globals.TMK);
            System.out.println(String.format("Pin key -> %s", StringUtils.bytesToHex(pkResponse.getClearPinKey())));
            this.dataStore.putString(Globals.PREF_TPK, StringUtils.bytesToHex(pkResponse.getClearPinKey()));
//            terminalKeyManagement terminalKeyManagement = terminalKeysRepo.findByTerminalID(pk.getCardAcceptorTerminalId());
//            terminalKeyManagement.setPinKey(StringUtils.bytesToHex(pkResponse.getClearPinKey()));
//            terminalKeysRepo.save(terminalKeyManagement);
//            Keys.eClearPinKey = pkResponse.getEncryptedPinKey();
//            System.out.println("Jayrush Clear PinKey "+Keys.eClearPinKey);
            return StringUtils.bytesToHex(pkResponse.getClearPinKey());
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getParameters(host host) {
        try {
            final GetParameterRequest parameterRequest = new GetParameterRequest();
            parameterRequest.setCardAcceptorTerminalId(this.terminalId);
            parameterRequest.setProcessingCode("9C0000");
            parameterRequest.setDateLocalTransaction(DataUtil.dateLocalTransaction(new Date()));
            parameterRequest.setTimeLocalTransaction(DataUtil.timeLocalTransaction(new Date()));
            parameterRequest.setTransmissionDateAndTime(DataUtil.transmissionDateAndTime(new Date()));
            final int counter = this.dataStore.getInt(Globals.PREF_GET_PARAMETER_STAN) + 1;
            this.dataStore.putInt(Globals.PREF_GET_PARAMETER_STAN, counter);
            parameterRequest.setSystemTraceAuditNumber(DataUtil.leftZeroPad(counter));
            final String nibssIpPAddress = host.getHostIp();
            final int nibssPort = host.getHostPort();
            IsoProcessor.setConnectionParameters(nibssIpPAddress, nibssPort);

            final String tskString = TerminalSessionKey;
            final GetParameterResponse getParameterResponse = IsoProcessor.process(parameterRequest, StringUtils.hexStringToByteArray(tskString));
            if (getParameterResponse != null) {
                System.out.println("Parameters Loaded");
                final Map<String, String> decodedParameters = ParameterParser.parseParameters(getParameterResponse.getField62());
                this.dataStore.putString(Globals.PREF_CARD_ACCEPTOR_ID, decodedParameters.get("03"));
                this.dataStore.putString(Globals.PREF_CARD_ACCEPTOR_LOC, decodedParameters.get("52"));
                this.dataStore.putString(Globals.PREF_CURRENCY_CODE, decodedParameters.get("05"));
                this.dataStore.putString(Globals.PREF_MERCHANT_TYPE, decodedParameters.get("08"));
//                terminalKeyManagement terminalKeyManagement = terminalKeysRepo.findByTerminalID(parameterRequest.getCardAcceptorTerminalId());
//                terminalKeyManagement.setParameterDownloaded("Success");
//                terminalKeysRepo.save(terminalKeyManagement);
                return "Parameters downloaded successfully";
            }
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    
    public ISO8583TransactionRequest setCommonFields(final ISO8583TransactionRequest request) {
        request.setTransmissionDateTimeField7(DataUtil.transmissionDateAndTime(new Date()));
        final int counter = this.dataStore.getInt(Globals.PREF_TRXN_STAN) + 1;
        this.dataStore.putInt(Globals.PREF_TRXN_STAN, counter);
        request.setStanField11(String.format("%06d", counter));
        request.setLocalTransactionTimeField12(DataUtil.timeLocalTransaction(new Date()));
        request.setLocalTransactionDateField13(DataUtil.dateLocalTransaction(new Date()));
        request.setMerchantTypeField18(this.dataStore.getString(Globals.PREF_MERCHANT_TYPE));
        request.setPosEntryModeField22("051");
        request.setPosConditionCodeField25("00");
        request.setPosPinCaptureCodeField26("04");
        request.setTransactionFeeAmountField28("C00000000");
        request.setAcquiringInstitutionIdCodeField32("111130");
        final String retrievalRefNumber = String.format("%012d", counter);
        request.setRetrievalReferenceNumberField37(retrievalRefNumber);
        request.setTerminalIdField41(this.terminalId);
        request.setCardAcceptorIdCodeField42(this.dataStore.getString(Globals.PREF_CARD_ACCEPTOR_ID));
        request.setCardAcceptorNameOrLocationField43(this.dataStore.getString(Globals.PREF_CARD_ACCEPTOR_LOC));
        request.setTransactionCurrencyCodeField49(this.dataStore.getString(Globals.PREF_CURRENCY_CODE));
        request.setTransportDataField59("010101");
        request.setPaymentInformationField60("Payment from mpos");
        request.setPOSDataCodeField123("511101512344101");
        return request;
    }
    //todo remove the byte array message from function. it was done when trying to move the pos message
    public ISO8583TransactionResponse makeISO8583Transaction(final ISO8583TransactionRequest transactionRequest, byte[] Message) {
        final byte[] sessionKey = StringUtils.hexStringToByteArray(this.dataStore.getString(Globals.PREF_TSK));
        final String nibssIpPAddress = "196.6.103.18";
        final int nibssPort = 5334;
        IsoProcessor.setConnectionParameters(nibssIpPAddress, nibssPort);
        System.out.println("NIBSS Request " + transactionRequest);
        final ISO8583TransactionResponse transactionResponse = IsoProcessor.processISO8583Transaction(transactionRequest, sessionKey, Message);
        final ISO8583TransactionRequest requestObjectForReversal = transactionRequest;
        final ISO8583TransactionResponse responseObjectForReversal = transactionResponse;
        if (reversalShouldBeSent(transactionResponse)) {
            new Thread(new Runnable() {
                public void run() {
                    NibssRequestsFactory.this.sendReversal(requestObjectForReversal);
                }
            }).start();
        }
        return transactionResponse;
    }
    public boolean SendISO8583Transaction(final ISO8583TransactionRequest transactionRequest, final byte[] Message) {
        //final byte[] sessionKey = StringUtils.hexStringToByteArray(this.dataStore.getString(Globals.PREF_TSK));
        IsoProcessor.setConnectionParameters("196.6.103.18", 5009);
        System.out.println("NIBSS Request " + transactionRequest);
        final ISO8583TransactionResponse transactionResponse = IsoProcessor.processISO8583Transaction(transactionRequest, null, Message);
        final ISO8583TransactionRequest requestObjectForReversal = transactionRequest;
        final ISO8583TransactionResponse responseObjectForReversal = transactionResponse;
        if (reversalShouldBeSent(transactionResponse)) {
            new Thread(new Runnable() {
                public void run() {
                    NibssRequestsFactory.this.sendReversal(requestObjectForReversal);
                }
            }).start();
        }
        return true;
    }

    public ISO8583TransactionResponse sendReversal(final ISO8583TransactionRequest transactionRequest) {
        try {
            final ISO8583TransactionRequest reversalRequest = new ReversalRequest();
            reversalRequest.setPanField2(transactionRequest.getPanField2());
            reversalRequest.setProcessingCodeField3(transactionRequest.getProcessingCodeField3());
            reversalRequest.setTransactionAmountField4(transactionRequest.getTransactionAmountField4());
            reversalRequest.setTransmissionDateTimeField7(DataUtil.transmissionDateAndTime(new Date()));
            final int counter = this.dataStore.getInt(Globals.PREF_REVERSAL_STAN) + 1;
            this.dataStore.putInt(Globals.PREF_REVERSAL_STAN, counter);
            reversalRequest.setStanField11(String.format("%06d", counter));
            reversalRequest.setLocalTransactionTimeField12(DataUtil.timeLocalTransaction(new Date()));
            reversalRequest.setLocalTransactionDateField13(DataUtil.dateLocalTransaction(new Date()));
            reversalRequest.setCardExpirationDateField14(transactionRequest.getCardExpirationDateField14());
            reversalRequest.setMerchantTypeField18(this.dataStore.getString(Globals.PREF_MERCHANT_TYPE));
            reversalRequest.setPosEntryModeField22(transactionRequest.getPosEntryModeField22());
            reversalRequest.setCardSequenceNumberField23(transactionRequest.getCardSequenceNumberField23());
            reversalRequest.setPosConditionCodeField25(transactionRequest.getPosConditionCodeField25());
            reversalRequest.setPosPinCaptureCodeField26(transactionRequest.getPosPinCaptureCodeField26());
            reversalRequest.setTransactionFeeAmountField28(transactionRequest.getTransactionFeeAmountField28());
            reversalRequest.setAcquiringInstitutionIdCodeField32("111130");
            reversalRequest.setTrack2DataField35(transactionRequest.getTrack2DataField35());
            final String retrievalRefNumber = String.format("%012d", counter);
            reversalRequest.setRetrievalReferenceNumberField37(retrievalRefNumber);
            reversalRequest.setServiceRestrictionCodeField40(transactionRequest.getServiceRestrictionCodeField40());
            reversalRequest.setTerminalIdField41(transactionRequest.getTerminalIdField41());
            reversalRequest.setCardAcceptorIdCodeField42(transactionRequest.getCardAcceptorIdCodeField42());
            reversalRequest.setCardAcceptorNameOrLocationField43(transactionRequest.getCardAcceptorNameOrLocationField43());
            reversalRequest.setTransactionCurrencyCodeField49(transactionRequest.getTransactionCurrencyCodeField49());
            reversalRequest.setPinDataField52(transactionRequest.getPinDataField52());
            reversalRequest.setAdditionalAmountsField54(transactionRequest.getAdditionalAmountsField54());
            reversalRequest.setMessageReasonCodeField56(transactionRequest.getMessageReasonCodeField56());
            reversalRequest.setTransportDataField59(transactionRequest.getTransportDataField59());
            reversalRequest.setPaymentInformationField60(transactionRequest.getPaymentInformationField60());
            final String originalDataElements = "0200" + transactionRequest.getStanField11() + transactionRequest.getTransmissionDateTimeField7() + StringUtils.padRight(transactionRequest.getAcquiringInstitutionIdCodeField32(), 11, '0') + StringUtils.padRight(transactionRequest.getForwardingInstitutionIdCodeField33(), 11, '0');
            reversalRequest.setOriginalDataElementsField90(originalDataElements);
            final String replacementAmounts = DataUtil.formatAmount(transactionRequest.getTransactionAmountField4()) + DataUtil.formatAmount("0") + "C00000000C00000000";
            reversalRequest.setReplacementAmountsField95(replacementAmounts);
            reversalRequest.setPOSDataCodeField123(transactionRequest.getPOSDataCodeField123());
            final byte[] sessionKey = StringUtils.hexStringToByteArray(this.dataStore.getString(Globals.PREF_TSK));
            final String nibssIpPAddress = this.dataStore.getString(Globals.PREF_NIBSS_IP);
            final int nibssPort = Integer.parseInt(this.dataStore.getString(Globals.PREF_NIBSS_PORT));
            IsoProcessor.setConnectionParameters(nibssIpPAddress, nibssPort);
            ISO8583TransactionResponse reversalResponse = IsoProcessor.process((ReversalRequest)reversalRequest, sessionKey, 1);
            for (int index = 2; index <= 3; ++index) {
                reversalResponse = IsoProcessor.process((ReversalRequest)reversalRequest, sessionKey, index);
                if (reversalResponse.getResponseCodeField39() != null && reversalResponse.getResponseCodeField39().equals("00")) {
                    return reversalResponse;
                }
            }
            return reversalResponse;
        }
        catch (Exception ex) {
            return new ReversalResponse();
        }
    }
    
    public ReversalResponse sendReversal(final ReversalRequest reversalRequest, final _0200Response financialResponse) {
        try {
            reversalRequest.setProcessingCodeField3(financialResponse.getProcessingCodeField3());
            reversalRequest.setTransmissionDateTimeField7(DataUtil.transmissionDateAndTime(new Date()));
            final int counter = this.dataStore.getInt(Globals.PREF_TRXN_STAN) + 1;
            this.dataStore.putInt(Globals.PREF_TRXN_STAN, counter);
            reversalRequest.setStanField11(String.format("%06d", counter));
            reversalRequest.setLocalTransactionTimeField12(DataUtil.timeLocalTransaction(new Date()));
            reversalRequest.setLocalTransactionDateField13(DataUtil.dateLocalTransaction(new Date()));
            reversalRequest.setMerchantTypeField18(this.dataStore.getString(Globals.PREF_MERCHANT_TYPE));
            reversalRequest.setPosEntryModeField22(financialResponse.getPosEntryModeField22());
            reversalRequest.setPosConditionCodeField25(financialResponse.getPosConditionCodeField25());
            reversalRequest.setAcquiringInstitutionIdCodeField32("111130");
            final String retrievalRefNumber = String.format("%012d", counter);
            reversalRequest.setRetrievalReferenceNumberField37(retrievalRefNumber);
            reversalRequest.setTerminalIdField41(this.terminalId);
            reversalRequest.setCardAcceptorIdCodeField42(this.dataStore.getString(Globals.PREF_CARD_ACCEPTOR_ID));
            reversalRequest.setCardAcceptorNameOrLocationField43(this.dataStore.getString(Globals.PREF_CARD_ACCEPTOR_LOC));
            reversalRequest.setTransactionCurrencyCodeField49(this.dataStore.getString(Globals.PREF_CURRENCY_CODE));
            reversalRequest.setAdditionalAmountsField54(financialResponse.getAdditionalAmountsField54());
            reversalRequest.setMessageReasonCodeField56("4001");
            reversalRequest.setTransportDataField59("010101");
            reversalRequest.setPaymentInformationField60("Reversal via MPOS");
            String originalTransmissionDateTime;
            try {
                originalTransmissionDateTime = DataUtil.transmissionDateAndTime(new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(financialResponse.getTransactionSettlementAmountField7()));
            }
            catch (Exception e) {
                return null;
            }
            final String originalDataElements = "0200" + financialResponse.getStanField11() + originalTransmissionDateTime + StringUtils.padRight(financialResponse.getAcquiringInstitutionIdCodeField32(), 11, '0') + StringUtils.padRight(financialResponse.getForwardingInstitutionIdCodeField33(), 11, '0');
            reversalRequest.setOriginalDataElementsField90(originalDataElements);
            final String replacementAmounts = DataUtil.formatAmount(financialResponse.getTransactionAmountField4()) + DataUtil.formatAmount("0") + "C00000000C00000000";
            reversalRequest.setReplacementAmountsField95(replacementAmounts);
            reversalRequest.setPOSDataCodeField123("511101512344101");
            final byte[] sessionKey = StringUtils.hexStringToByteArray(this.dataStore.getString(Globals.PREF_TSK));
            final String nibssIpPAddress = this.dataStore.getString(Globals.PREF_NIBSS_IP);
            final int nibssPort = Integer.parseInt(this.dataStore.getString(Globals.PREF_NIBSS_PORT));
            IsoProcessor.setConnectionParameters(nibssIpPAddress, nibssPort);
            ReversalResponse reversalResponse = IsoProcessor.process(reversalRequest, sessionKey, 1);
            for (int index = 2; index <= 3; ++index) {
                reversalResponse = IsoProcessor.process(reversalRequest, sessionKey, index);
                if (reversalResponse.getResponseCodeField39() != null && reversalResponse.getResponseCodeField39() == "00") {
                    return reversalResponse;
                }
            }
            return reversalResponse;
        }
        catch (Exception ex) {
            return new ReversalResponse();
        }
    }
    
    public static boolean reversalShouldBeSent(final ISO8583TransactionResponse response) {
        return response == null || response.getResponseCodeField39() == null || response.getResponseCodeField39().equals(TransactionErrorCode.FAILED_TO_READ_RESPONSE);
    }
    
    static {
        TAG = NibssRequestsFactory.class.getSimpleName().toUpperCase();
        //NibssRequestsFactory.logger = (Logger) LoggerFactory.getLogger(NibssRequestsFactory.class.toString());
    }
}
