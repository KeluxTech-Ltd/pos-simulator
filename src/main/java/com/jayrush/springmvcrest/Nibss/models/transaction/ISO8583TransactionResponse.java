// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.models.transaction;

import lombok.Data;

@Data
public class ISO8583TransactionResponse
{
    private String panField2;
    private String processingCodeField3;
    private String transactionAmountField4;
    private String transactionSettlementAmountField7;
    private String stanField11;
    private String localTransactionTimeField12;
    private String localTransactionDateField13;
    private String cardExpirationDateField14;
    private String settlementDateField15;
    private String merchantTypeField18;
    private String posEntryModeField22;
    private String cardSequenceNumberField23;
    private String posConditionCodeField25;
    private String transactionFeeAmountField28;
    private String transactionProcessingFeeAmountField30;
    private String acquiringInstitutionIdCodeField32;
    private String forwardingInstitutionIdCodeField33;
    private String track2DataField35;
    private String retrievalReferenceNumberField37;
    private String authorizationIdResponseField38;
    private String responseCodeField39;
    private String serviceRestrictionCodeField40;
    private String terminalIdField41;
    private String cardAcceptorIdCodeField42;
    private String cardAcceptorNameOrLocationField43;
    private String transactionCurrencyCodeField49;
    private String additionalAmountsField54;
    private String iCCDataField55;
    private String messageReasonCodeField56;
    private String transportDataField59;
    private String originalDataElementsField90;
    private String replacementAmountsField95;
    private String accountIdentification1Field102;
    private String accountIdentification2Field103;
    private String POSDataCodeField123;
    private String NFCDataField124;
    private String secondaryMessageHashValueField128;

    public void setiCCDataField55(String toString) {
    }
}
