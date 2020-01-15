package com.jayrush.springmvcrest.utility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ... on 26/11/2018.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "IccRequest")
public class IccRequest {

    @XmlElement(name = "Bitmap", required = true)
    private String bitmap;
    @XmlElement(name = "AmountAuthorized", required = true)
    private String amountAuthorized;
    @XmlElement(name = "AmountOther")
    private String amountOther;
    @XmlElement(name = "ApplicationIdentifier")
    private String applicationIdentifier;
    @XmlElement(name = "ApplicationInterchangeProfile", required = true)
    private String applicationInterchangeProfile;
    @XmlElement(name = "ApplicationTransactionCounter", required = true)
    private String applicationTransactionCounter;
    @XmlElement(name = "ApplicationUsageControl")
    private String applicationUsageControl;
    @XmlElement(name = "AuthorizationResponseCode")
    private String authorizationResponseCode;
    @XmlElement(name = "CardAuthenticationReliabilityIndicator")
    private String cardAuthenticationReliabilityIndicator;
    @XmlElement(name = "CardAuthenticationResultsCode")
    private String cardAuthenticationResultsCode;
    @XmlElement(name = "ChipConditionCode")
    private String chipConditionCode;
    @XmlElement(name = "Cryptogram", required = true)
    private String cryptogram;
    @XmlElement(name = "CryptogramInformationData", required = true)
    private String cryptogramInformationData;
    @XmlElement(name = "CmvList")
    private String cvmList;
    @XmlElement(name = "CvmResults")
    private String cvmResults;
    @XmlElement(name = "InterfaceDeviceSerialNumber")
    private String interfaceDeviceSerialNumber;
    @XmlElement(name = "IssuerActionCode")
    private String issuerActionCode;
    @XmlElement(name = "IssuerApplicationData", required = true)
    private String issuerApplicationData;
    @XmlElement(name = "IssuerScriptResults")
    private String issuerScriptResults;
    @XmlElement(name = "TerminalApplicationVersionNumber")
    private String terminalApplicationVersionNumber;
    @XmlElement(name = "TerminalCapabilities")
    private String terminalCapabilities;
    @XmlElement(name = "TerminalCountryCode", required = true)
    private String terminalCountryCode;
    @XmlElement(name = "TerminalType")
    private String terminalType;
    @XmlElement(name = "TerminalVerificationResult", required = true)
    private String terminalVerificationResult;
    @XmlElement(name = "TransactionCategoryCode")
    private String transactionCategoryCode;
    @XmlElement(name = "TransactionCurrencyCode", required = true)
    private String transactionCurrencyCode;
    @XmlElement(name = "TransactionDate", required = true)
    private String transactionDate;
    @XmlElement(name = "TransactionSequenceCounter")
    private String transactionSequenceCounter;
    @XmlElement(name = "TransactionType", required = true)
    private String transactionType;
    @XmlElement(name = "UnpredictableNumber", required = true)
    private String unpredictableNumber;

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public String getAmountAuthorized() {
        return amountAuthorized;
    }

    public void setAmountAuthorized(String amountAuthorized) {
        this.amountAuthorized = amountAuthorized;
    }

    public String getAmountOther() {
        return amountOther;
    }

    public void setAmountOther(String amountOther) {
        this.amountOther = amountOther;
    }

    public String getApplicationIdentifier() {
        return applicationIdentifier;
    }

    public void setApplicationIdentifier(String applicationIdentifier) {
        this.applicationIdentifier = applicationIdentifier;
    }

    public String getApplicationInterchangeProfile() {
        return applicationInterchangeProfile;
    }

    public void setApplicationInterchangeProfile(String applicationInterchangeProfile) {
        this.applicationInterchangeProfile = applicationInterchangeProfile;
    }

    public String getApplicationTransactionCounter() {
        return applicationTransactionCounter;
    }

    public void setApplicationTransactionCounter(String applicationTransactionCounter) {
        this.applicationTransactionCounter = applicationTransactionCounter;
    }

    public String getApplicationUsageControl() {
        return applicationUsageControl;
    }

    public void setApplicationUsageControl(String applicationUsageControl) {
        this.applicationUsageControl = applicationUsageControl;
    }

    public String getAuthorizationResponseCode() {
        return authorizationResponseCode;
    }

    public void setAuthorizationResponseCode(String authorizationResponseCode) {
        this.authorizationResponseCode = authorizationResponseCode;
    }

    public String getCardAuthenticationReliabilityIndicator() {
        return cardAuthenticationReliabilityIndicator;
    }

    public void setCardAuthenticationReliabilityIndicator(String cardAuthenticationReliabilityIndicator) {
        this.cardAuthenticationReliabilityIndicator = cardAuthenticationReliabilityIndicator;
    }

    public String getCardAuthenticationResultsCode() {
        return cardAuthenticationResultsCode;
    }

    public void setCardAuthenticationResultsCode(String cardAuthenticationResultsCode) {
        this.cardAuthenticationResultsCode = cardAuthenticationResultsCode;
    }

    public String getChipConditionCode() {
        return chipConditionCode;
    }

    public void setChipConditionCode(String chipConditionCode) {
        this.chipConditionCode = chipConditionCode;
    }

    public String getCryptogram() {
        return cryptogram;
    }

    public void setCryptogram(String cryptogram) {
        this.cryptogram = cryptogram;
    }

    public String getCryptogramInformationData() {
        return cryptogramInformationData;
    }

    public void setCryptogramInformationData(String cryptogramInformationData) {
        this.cryptogramInformationData = cryptogramInformationData;
    }

    public String getCvmList() {
        return cvmList;
    }

    public void setCvmList(String cvmList) {
        this.cvmList = cvmList;
    }

    public String getCvmResults() {
        return cvmResults;
    }

    public void setCvmResults(String cvmResults) {
        this.cvmResults = cvmResults;
    }

    public String getInterfaceDeviceSerialNumber() {
        return interfaceDeviceSerialNumber;
    }

    public void setInterfaceDeviceSerialNumber(String interfaceDeviceSerialNumber) {
        this.interfaceDeviceSerialNumber = interfaceDeviceSerialNumber;
    }

    public String getIssuerActionCode() {
        return issuerActionCode;
    }

    public void setIssuerActionCode(String issuerActionCode) {
        this.issuerActionCode = issuerActionCode;
    }

    public String getIssuerApplicationData() {
        return issuerApplicationData;
    }

    public void setIssuerApplicationData(String issuerApplicationData) {
        this.issuerApplicationData = issuerApplicationData;
    }

    public String getIssuerScriptResults() {
        return issuerScriptResults;
    }

    public void setIssuerScriptResults(String issuerScriptResults) {
        this.issuerScriptResults = issuerScriptResults;
    }

    public String getTerminalApplicationVersionNumber() {
        return terminalApplicationVersionNumber;
    }

    public void setTerminalApplicationVersionNumber(String terminalApplicationVersionNumber) {
        this.terminalApplicationVersionNumber = terminalApplicationVersionNumber;
    }

    public String getTerminalCapabilities() {
        return terminalCapabilities;
    }

    public void setTerminalCapabilities(String terminalCapabilities) {
        this.terminalCapabilities = terminalCapabilities;
    }

    public String getTerminalCountryCode() {
        return terminalCountryCode;
    }

    public void setTerminalCountryCode(String terminalCountryCode) {
        this.terminalCountryCode = terminalCountryCode;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getTerminalVerificationResult() {
        return terminalVerificationResult;
    }

    public void setTerminalVerificationResult(String terminalVerificationResult) {
        this.terminalVerificationResult = terminalVerificationResult;
    }

    public String getTransactionCategoryCode() {
        return transactionCategoryCode;
    }

    public void setTransactionCategoryCode(String transactionCategoryCode) {
        this.transactionCategoryCode = transactionCategoryCode;
    }

    public String getTransactionCurrencyCode() {
        return transactionCurrencyCode;
    }

    public void setTransactionCurrencyCode(String transactionCurrencyCode) {
        this.transactionCurrencyCode = transactionCurrencyCode;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionSequenceCounter() {
        return transactionSequenceCounter;
    }

    public void setTransactionSequenceCounter(String transactionSequenceCounter) {
        this.transactionSequenceCounter = transactionSequenceCounter;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getUnpredictableNumber() {
        return unpredictableNumber;
    }

    public void setUnpredictableNumber(String unpredictableNumber) {
        this.unpredictableNumber = unpredictableNumber;
    }
}
