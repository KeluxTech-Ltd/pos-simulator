package com.jayrush.springmvcrest.utility;

import com.jayrush.springmvcrest.exceptions.EmvProcessingException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.util.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author JoshuaO
 */
public final class emvUtil {
    private static JAXBContext jaxbContext;
    private static Marshaller marshaller;

    private emvUtil() {

    }
    private static Marshaller getMarshaller() throws JAXBException {
        if (jaxbContext == null) {
            jaxbContext = JAXBContext.newInstance(IccData.class);
        }
        if (marshaller == null) {
            marshaller = jaxbContext.createMarshaller();
        }

        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        return marshaller;
    }
    public static String emvStringToXmlString(String emvString) throws EmvProcessingException {
        if (StringUtils.isEmpty(emvString)) {
            return null;
        }
        IccData iccData = new IccData();
        IccRequest iccRequest = new IccRequest();

        Map<String, String> emvPairs = extractKeyValuePairs(emvString);

        if (emvPairs.size() == 0) {
            return null;
        }

        iccRequest.setApplicationInterchangeProfile(emvPairs.get("82"));
        iccRequest.setAuthorizationResponseCode(emvPairs.get("8A"));
        iccRequest.setApplicationTransactionCounter(emvPairs.get("9F36"));
        iccRequest.setTransactionSequenceCounter(emvPairs.get("9F41"));
        iccRequest.setCryptogramInformationData(emvPairs.get("9F27"));
        iccRequest.setCvmResults(emvPairs.get("9F34"));
        iccRequest.setIssuerApplicationData(emvPairs.get("9F10"));
        iccRequest.setTerminalCapabilities(emvPairs.get("9F33"));
        iccRequest.setTerminalType(emvPairs.get("9F35"));
        iccRequest.setTerminalVerificationResult(emvPairs.get("95"));
        iccRequest.setUnpredictableNumber(emvPairs.get("9F37"));
        iccRequest.setApplicationIdentifier(emvPairs.get("9F01"));
        iccRequest.setTransactionType(emvPairs.get("9C"));
        if (!StringUtils.isEmpty(emvPairs.get("9F02"))) {
            iccRequest.setAmountAuthorized(String.format("%012d", extractHexAmount(emvPairs.get("9F02"))));
            //iccRequest.setAmountAuthorized(org.apache.commons.lang3.StringUtils.leftPad(emvPairs.get("9F02"), 12, '0'));
        }
        if (!StringUtils.isEmpty(emvPairs.get("9F03"))) {
            iccRequest.setAmountOther(String.format("%012d", extractHexAmount(emvPairs.get("9F03"))));
            //iccRequest.setAmountOther(org.apache.commons.lang3.StringUtils.leftPad(emvPairs.get("9F03"), 12, '0'));
        }
        if (!StringUtils.isEmpty(emvPairs.get("9F1A"))) {
            String value9FIA = emvPairs.get("9F1A");
            iccRequest.setTerminalCountryCode(value9FIA.substring(1,value9FIA.length()));
        }
        if (!StringUtils.isEmpty(emvPairs.get("5F2A"))) {
            String value5F2A = emvPairs.get("5F2A");
            iccRequest.setTransactionCurrencyCode(value5F2A.substring(1,value5F2A.length()));
        }
//        iccRequest.setTerminalCountryCode(emvPairs.get("9F1A").replace("0", ""));
//        iccRequest.setTransactionCurrencyCode(emvPairs.get("5F2A").replace("0", ""));
        iccRequest.setTransactionDate(emvPairs.get("9A"));
        iccRequest.setCryptogram(emvPairs.get("9F26"));

        iccData.setIccRequest(iccRequest);

        StringWriter xmlString = new StringWriter();
        xmlString.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
        try {
            getMarshaller().marshal(iccData, xmlString);
        } catch (JAXBException e) {
            throw new EmvProcessingException("Could not marshall icc data to xml string", e);
        }
        return xmlString.toString();

    }

    public static Map<String, String> extractKeyValuePairs(String emvString) throws EmvProcessingException {
        int fieldIndex = 0;
        Map<String, String> emvPairs = new HashMap<>();
        while (fieldIndex < emvString.length()) {
            byte[] firstByte;
            String key;
            String value;
            int valueLength;
            try {
                firstByte = Hex.decodeHex(emvString.substring(fieldIndex, fieldIndex + 2).toCharArray());
            } catch (DecoderException e) {
                throw new EmvProcessingException("An error occurred while reading emv data ", e);
            }

            if ((firstByte[0] & 0x1F) == 0x1F) {
                key = emvString.substring(fieldIndex, fieldIndex + 4).toUpperCase();
                fieldIndex = fieldIndex + 4;
            } else {
                key = emvString.substring(fieldIndex, fieldIndex + 2).toUpperCase();
                fieldIndex = fieldIndex + 2;
            }

            try {
                valueLength = Hex.decodeHex(emvString.substring(fieldIndex, fieldIndex + 2).toCharArray())[0];
                valueLength = valueLength * 2;
                fieldIndex += 2;
            } catch (DecoderException e) {
                throw new EmvProcessingException("An error occurred while reading emv data ", e);
            }
            try {
                value = emvString.substring(fieldIndex, fieldIndex + valueLength);
                System.out.println("Value: " + value);
            } catch (IndexOutOfBoundsException e) {
                throw new EmvProcessingException("An error occurred while reading emv data", e);
            }
            fieldIndex += valueLength;

            emvPairs.put(key, value.toUpperCase());
        }
        return emvPairs;
    }

    private static Long extractHexAmount(String value) throws EmvProcessingException {
        try {
            System.out.println("Value: " + value);
            return Long.decode("0x" + value);
        } catch (NumberFormatException e) {
            throw new EmvProcessingException("Error parsing value for emv data", e);
        }
    }


}
