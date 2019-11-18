package com.jayrush.springmvcrest.Sender;

import com.jayrush.springmvcrest.Nibss.constants.TransactionErrorCode;
import com.jayrush.springmvcrest.Nibss.models.transaction.ISO8583TransactionRequest;
import com.jayrush.springmvcrest.Nibss.models.transaction.ISO8583TransactionResponse;
import com.jayrush.springmvcrest.Nibss.network.ChannelSocketRequestManager;
import com.jayrush.springmvcrest.iso8583.IsoMessage;
import com.jayrush.springmvcrest.iso8583.MessageFactory;

import java.io.EOFException;
import java.io.IOException;

public class NibssSender {
    public static ISO8583TransactionResponse processISO8583Transaction(final ISO8583TransactionRequest request, final byte[] sessionKey,final byte[]Message)
    {
        final ISO8583TransactionResponse response = new ISO8583TransactionResponse();
        ChannelSocketRequestManager socketRequester = null;
        try {
            final IsoMessage isoMessage = null;
            /*final byte[] bites = isoMessage.writeData();
            final int length = bites.length;
            final byte[] temp = new byte[length - 64];
            if (length >= 64) {
                System.arraycopy(bites, 0, temp, 0, length - 64);
            }
            if (request.hashMessage()) {
                final String hashHex = generateHash256Value(temp, sessionKey);
                final IsoValue<String> field128update = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)hashHex, 64);
                isoMessage.setField(128, (IsoValue)field128update);
                //IsoProcessor.logger.debug("Message was hashed");
            }
            else {
                //IsoProcessor.logger.debug("Message not hashed");
            }*/


            final byte[] toSend = Message;
            System.out.println("Message to Send");
            System.out.println((Object)new String(toSend));
            socketRequester = new ChannelSocketRequestManager("196.6.103.18", 5009);
            final byte[] responseBytes = socketRequester.sendAndRecieveData(toSend);
            if (responseBytes != null && responseBytes.length > 0) {
                //IsoProcessor.logger.debug("Response receive {}", (Object)new String(responseBytes));
            }
            final MessageFactory<IsoMessage> responseMessageFactory = (MessageFactory<IsoMessage>)new MessageFactory();
            responseMessageFactory.addMessageTemplate(isoMessage);
            responseMessageFactory.setAssignDate(true);
            responseMessageFactory.setUseBinaryBitmap(false);
            responseMessageFactory.setUseBinaryMessages(false);
            responseMessageFactory.setEtx(-1);
            responseMessageFactory.setIgnoreLastMissingField(false);
            //responseMessageFactory.setConfigPath(IsoProcessor.CONFIG_FILE);
            IsoMessage responseMessage = null;
            try {
                responseMessage = responseMessageFactory.parseMessage(responseBytes, 0);
                System.out.println(responseMessage+ "Response ====> ");
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
            System.out.println("Response on field 39");
            System.out.println((Object)response.getResponseCodeField39());
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
                    System.out.println("Failed to disconnect socket requester");
                }
            }
        }
        return response;
    }

}
