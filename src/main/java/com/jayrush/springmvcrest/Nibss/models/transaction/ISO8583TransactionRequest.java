// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.models.transaction;

public abstract class ISO8583TransactionRequest
{
    private String panField2;
    private String processingCodeField3;
    private String transactionAmountField4;
    private String transmissionDateTimeField7;
    private String stanField11;
    private String localTransactionTimeField12;
    private String localTransactionDateField13;
    private String cardExpirationDateField14;
    private String merchantTypeField18;
    private String posEntryModeField22;
    private String cardSequenceNumberField23;
    private String posConditionCodeField25;
    private String posPinCaptureCodeField26;
    private String transactionFeeAmountField28;
    private String acquiringInstitutionIdCodeField32;
    private String track2DataField35;
    private String retrievalReferenceNumberField37;
    private String authoriationCode38;
    private String serviceRestrictionCodeField40;
    private String terminalIdField41;
    private String cardAcceptorIdCodeField42;
    private String cardAcceptorNameOrLocationField43;
    private String transactionCurrencyCodeField49;
    private String pinDataField52;
    private String securityRelatedControlInfoField53;
    private String additionalAmountsField54;
    private String iCCDataField55;
    private String messageReasonCodeField56;
    private String transportDataField59;
    private String paymentInformationField60;
    private String managementData1Field62;
    private String originalDataElementsField90;
    private String replacementAmountsField95;
    private String POSDataCodeField123;
    private String NFCDataField124;
    private String secondaryMessageHashValueField128;
    private String forwardingInstitutionIdCodeField33;
    
    public static int[] getMandatoryFields() {
        return new int[] { 2, 3, 4, 7, 11, 12, 13, 14, 18, 22, 25, 28, 32, 37, 41, 42, 43, 49, 123, 128 };
    }
    
    public abstract boolean hashMessage();
    
    public String getPanField2() {
        return this.panField2;
    }
    
    public void setPanField2(final String panField2) {
        this.panField2 = panField2;
    }
    
    public String getProcessingCodeField3() {
        return this.processingCodeField3;
    }
    
    public void setProcessingCodeField3(final String processingCodeField3) {
        this.processingCodeField3 = processingCodeField3;
    }
    
    public String getTransactionAmountField4() {
        return this.transactionAmountField4;
    }
    
    public void setTransactionAmountField4(final String transactionAmountField4) {
        this.transactionAmountField4 = transactionAmountField4;
    }
    
    public String getTransmissionDateTimeField7() {
        return this.transmissionDateTimeField7;
    }
    
    public void setTransmissionDateTimeField7(final String transmissionDateTimeField7) {
        this.transmissionDateTimeField7 = transmissionDateTimeField7;
    }
    
    public String getStanField11() {
        return this.stanField11;
    }
    
    public void setStanField11(final String stanField11) {
        this.stanField11 = stanField11;
    }
    
    public String getLocalTransactionTimeField12() {
        return this.localTransactionTimeField12;
    }
    
    public void setLocalTransactionTimeField12(final String localTransactionTimeField12) {
        this.localTransactionTimeField12 = localTransactionTimeField12;
    }
    
    public String getLocalTransactionDateField13() {
        return this.localTransactionDateField13;
    }
    
    public void setLocalTransactionDateField13(final String localTransactionDateField13) {
        this.localTransactionDateField13 = localTransactionDateField13;
    }
    
    public String getCardExpirationDateField14() {
        return this.cardExpirationDateField14;
    }
    
    public void setCardExpirationDateField14(final String cardExpirationDateField14) {
        this.cardExpirationDateField14 = cardExpirationDateField14;
    }
    
    public String getMerchantTypeField18() {
        return this.merchantTypeField18;
    }
    
    public void setMerchantTypeField18(final String merchantTypeField18) {
        this.merchantTypeField18 = merchantTypeField18;
    }
    
    public String getPosEntryModeField22() {
        return this.posEntryModeField22;
    }
    
    public void setPosEntryModeField22(final String posEntryModeField22) {
        this.posEntryModeField22 = posEntryModeField22;
    }
    
    public String getCardSequenceNumberField23() {
        return this.cardSequenceNumberField23;
    }
    
    public void setCardSequenceNumberField23(final String cardSequenceNumberField23) {
        this.cardSequenceNumberField23 = cardSequenceNumberField23;
    }
    
    public String getPosConditionCodeField25() {
        return this.posConditionCodeField25;
    }
    
    public void setPosConditionCodeField25(final String posConditionCodeField25) {
        this.posConditionCodeField25 = posConditionCodeField25;
    }
    
    public String getPosPinCaptureCodeField26() {
        return this.posPinCaptureCodeField26;
    }
    
    public void setPosPinCaptureCodeField26(final String posPinCaptureCodeField26) {
        this.posPinCaptureCodeField26 = posPinCaptureCodeField26;
    }
    
    public String getTransactionFeeAmountField28() {
        return this.transactionFeeAmountField28;
    }
    
    public void setTransactionFeeAmountField28(final String transactionFeeAmountField28) {
        this.transactionFeeAmountField28 = transactionFeeAmountField28;
    }
    
    public String getAcquiringInstitutionIdCodeField32() {
        return this.acquiringInstitutionIdCodeField32;
    }
    
    public void setAcquiringInstitutionIdCodeField32(final String acquiringInstitutionIdCodeField32) {
        this.acquiringInstitutionIdCodeField32 = acquiringInstitutionIdCodeField32;
    }
    
    public String getTrack2DataField35() {
        return this.track2DataField35;
    }
    
    public void setTrack2DataField35(final String track2DataField35) {
        this.track2DataField35 = track2DataField35;
    }
    
    public String getRetrievalReferenceNumberField37() {
        return this.retrievalReferenceNumberField37;
    }
    
    public void setRetrievalReferenceNumberField37(final String retrievalReferenceNumberField37) {
        this.retrievalReferenceNumberField37 = retrievalReferenceNumberField37;
    }
    
    public String getServiceRestrictionCodeField40() {
        return this.serviceRestrictionCodeField40;
    }
    
    public void setServiceRestrictionCodeField40(final String serviceRestrictionCodeField40) {
        this.serviceRestrictionCodeField40 = serviceRestrictionCodeField40;
    }
    
    public String getTerminalIdField41() {
        return this.terminalIdField41;
    }
    
    public void setTerminalIdField41(final String terminalIdField41) {
        this.terminalIdField41 = terminalIdField41;
    }
    
    public String getCardAcceptorIdCodeField42() {
        return this.cardAcceptorIdCodeField42;
    }
    
    public void setCardAcceptorIdCodeField42(final String cardAcceptorIdCodeField42) {
        this.cardAcceptorIdCodeField42 = cardAcceptorIdCodeField42;
    }
    
    public String getCardAcceptorNameOrLocationField43() {
        return this.cardAcceptorNameOrLocationField43;
    }
    
    public void setCardAcceptorNameOrLocationField43(final String cardAcceptorNameOrLocationField43) {
        this.cardAcceptorNameOrLocationField43 = cardAcceptorNameOrLocationField43;
    }
    
    public String getTransactionCurrencyCodeField49() {
        return this.transactionCurrencyCodeField49;
    }
    
    public void setTransactionCurrencyCodeField49(final String transactionCurrencyCodeField49) {
        this.transactionCurrencyCodeField49 = transactionCurrencyCodeField49;
    }
    
    public String getPinDataField52() {
        return this.pinDataField52;
    }
    
    public void setPinDataField52(final String pinDataField52) {
        this.pinDataField52 = pinDataField52;
    }
    
    public String getSecurityRelatedControlInfoField53() {
        return this.securityRelatedControlInfoField53;
    }
    
    public void setSecurityRelatedControlInfoField53(final String securityRelatedControlInfoField53) {
        this.securityRelatedControlInfoField53 = securityRelatedControlInfoField53;
    }
    
    public String getAdditionalAmountsField54() {
        return this.additionalAmountsField54;
    }
    
    public void setAdditionalAmountsField54(final String additionalAmountsField54) {
        this.additionalAmountsField54 = additionalAmountsField54;
    }
    
    public String getiCCDataField55() {
        return this.iCCDataField55;
    }
    
    public void setiCCDataField55(final String iCCDataField55) {
        this.iCCDataField55 = iCCDataField55;
    }
    
    public String getMessageReasonCodeField56() {
        return this.messageReasonCodeField56;
    }
    
    public void setMessageReasonCodeField56(final String messageReasonCodeField56) {
        this.messageReasonCodeField56 = messageReasonCodeField56;
    }
    
    public String getTransportDataField59() {
        return this.transportDataField59;
    }
    
    public void setTransportDataField59(final String transportDataField59) {
        this.transportDataField59 = transportDataField59;
    }
    
    public String getPaymentInformationField60() {
        return this.paymentInformationField60;
    }
    
    public void setPaymentInformationField60(final String paymentInformationField60) {
        this.paymentInformationField60 = paymentInformationField60;
    }
    
    public String getManagementData1Field62() {
        return this.managementData1Field62;
    }
    
    public void setManagementData1Field62(final String managementData1Field62) {
        this.managementData1Field62 = managementData1Field62;
    }
    
    public String getPOSDataCodeField123() {
        return this.POSDataCodeField123;
    }
    
    public void setPOSDataCodeField123(final String POSDataCodeField123) {
        this.POSDataCodeField123 = POSDataCodeField123;
    }
    
    public String getNFCDataField124() {
        return this.NFCDataField124;
    }
    
    public void setNFCDataField124(final String NFCDataField124) {
        this.NFCDataField124 = NFCDataField124;
    }
    
    public String getSecondaryMessageHashValueField128() {
        return this.secondaryMessageHashValueField128;
    }
    
    public void setSecondaryMessageHashValueField128(final String secondaryMessageHashValueField128) {
        this.secondaryMessageHashValueField128 = secondaryMessageHashValueField128;
    }
    
    public abstract int getMessageType();
    
    public int getRepeatMessageType() {
        return 0;
    }
    
    public String getOriginalDataElementsField90() {
        return this.originalDataElementsField90;
    }
    
    public void setOriginalDataElementsField90(final String originalDataElementsField90) {
        this.originalDataElementsField90 = originalDataElementsField90;
    }
    
    public String getAuthoriationCode38() {
        return this.authoriationCode38;
    }
    
    public void setAuthoriationCode38(final String authoriationCode38) {
        this.authoriationCode38 = authoriationCode38;
    }
    
    public String getReplacementAmountsField95() {
        return this.replacementAmountsField95;
    }
    
    public void setReplacementAmountsField95(final String replacementAmountsField95) {
        this.replacementAmountsField95 = replacementAmountsField95;
    }
    
    public String getForwardingInstitutionIdCodeField33() {
        return this.forwardingInstitutionIdCodeField33;
    }
    
    public void setForwardingInstitutionIdCodeField33(final String forwardingInstitutionIdCodeField33) {
        this.forwardingInstitutionIdCodeField33 = forwardingInstitutionIdCodeField33;
    }
    
    @Override
    public String toString() {
        return "ISO8583TransactionRequest{panField2='" + this.panField2 + '\'' + '\n' + ", processingCodeField3='" + this.processingCodeField3 + '\'' + '\n' + ", transactionAmountField4='" + this.transactionAmountField4 + '\'' + '\n' + ", transmissionDateTimeField7='" + this.transmissionDateTimeField7 + '\'' + '\n' + ", stanField11='" + this.stanField11 + '\'' + '\n' + ", localTransactionTimeField12='" + this.localTransactionTimeField12 + '\'' + '\n' + ", localTransactionDateField13='" + this.localTransactionDateField13 + '\'' + '\n' + ", cardExpirationDateField14='" + this.cardExpirationDateField14 + '\'' + '\n' + ", merchantTypeField18='" + this.merchantTypeField18 + '\'' + '\n' + ", posEntryModeField22='" + this.posEntryModeField22 + '\'' + '\n' + ", cardSequenceNumberField23='" + this.cardSequenceNumberField23 + '\'' + '\n' + ", posConditionCodeField25='" + this.posConditionCodeField25 + '\'' + '\n' + ", posPinCaptureCodeField26='" + this.posPinCaptureCodeField26 + '\'' + '\n' + ", transactionFeeAmountField28='" + this.transactionFeeAmountField28 + '\'' + '\n' + ", acquiringInstitutionIdCodeField32='" + this.acquiringInstitutionIdCodeField32 + '\'' + '\n' + ", track2DataField35='" + this.track2DataField35 + '\'' + '\n' + ", retrievalReferenceNumberField37='" + this.retrievalReferenceNumberField37 + '\'' + '\n' + ", authoriationCode38='" + this.authoriationCode38 + '\'' + '\n' + ", serviceRestrictionCodeField40='" + this.serviceRestrictionCodeField40 + '\'' + '\n' + ", terminalIdField41='" + this.terminalIdField41 + '\'' + '\n' + ", cardAcceptorIdCodeField42='" + this.cardAcceptorIdCodeField42 + '\'' + '\n' + ", cardAcceptorNameOrLocationField43='" + this.cardAcceptorNameOrLocationField43 + '\'' + '\n' + ", transactionCurrencyCodeField49='" + this.transactionCurrencyCodeField49 + '\'' + '\n' + ", pinDataField52='" + this.pinDataField52 + '\'' + '\n' + ", securityRelatedControlInfoField53='" + this.securityRelatedControlInfoField53 + '\'' + '\n' + ", additionalAmountsField54='" + this.additionalAmountsField54 + '\'' + '\n' + ", iCCDataField55='" + this.iCCDataField55 + '\'' + '\n' + ", messageReasonCodeField56='" + this.messageReasonCodeField56 + '\'' + '\n' + ", transportDataField59='" + this.transportDataField59 + '\'' + '\n' + ", paymentInformationField60='" + this.paymentInformationField60 + '\'' + '\n' + ", managementData1Field62='" + this.managementData1Field62 + '\'' + '\n' + ", originalDataElementsField90='" + this.originalDataElementsField90 + '\'' + '\n' + ", replacementAmountsField95='" + this.replacementAmountsField95 + '\'' + '\n' + ", POSDataCodeField123='" + this.POSDataCodeField123 + '\'' + '\n' + ", NFCDataField124='" + this.NFCDataField124 + '\'' + '\n' + ", secondaryMessageHashValueField128='" + this.secondaryMessageHashValueField128 + '\'' + '\n' + ", forwardingInstitutionIdCodeField33='" + this.forwardingInstitutionIdCodeField33 + '\'' + '\n' + '}';
    }
}
