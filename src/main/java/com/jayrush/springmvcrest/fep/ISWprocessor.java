package com.jayrush.springmvcrest.fep;

import com.globasure.nibss.tms.client.lib.utils.StringUtils;
import com.jayrush.springmvcrest.Nibss.processor.IsoProcessor;
import com.jayrush.springmvcrest.Nibss.utils.DataUtil;
import com.jayrush.springmvcrest.PostBridgePackager;
import com.jayrush.springmvcrest.Service.nibssToIswInterface;
import com.jayrush.springmvcrest.exceptions.EmvProcessingException;
import com.jayrush.springmvcrest.iso8583.IsoMessage;
import com.jayrush.springmvcrest.iso8583.IsoType;
import com.jayrush.springmvcrest.iso8583.IsoValue;
import com.jayrush.springmvcrest.iso8583.MessageFactory;
import org.jpos.iso.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import static com.jayrush.springmvcrest.utility.MainConverter.hexify;
import static com.jayrush.springmvcrest.utility.Utils.maskPanForReceipt;
import static com.jayrush.springmvcrest.utility.emvUtil.extractKeyValuePairs;
import static org.jpos.iso.ISOUtil.concat;

/**
 * @author JoshuaO
 */

@Component
public class ISWprocessor {
    @Autowired
    nibssToIswInterface nibssToIswInterface;

    private static final Logger logger = LoggerFactory.getLogger(ISWprocessor.class);

    public byte[] toFEP(byte[]fromPOS) throws IOException, ParseException, RequestProcessingException, ISOException {
        StanCounter stanCounter = new StanCounter();
        ISOPackager packager = new PostBridgePackager();
        ISOMsg isoMsg = new ISOMsg();
        Date now = new Date();
        final IsoMessage isoMessage = null;
        final MessageFactory<IsoMessage> responseMessageFactory = (MessageFactory<IsoMessage>) new MessageFactory();
        responseMessageFactory.addMessageTemplate(isoMessage);
        responseMessageFactory.setAssignDate(true);
        responseMessageFactory.setUseBinaryBitmap(false);
        responseMessageFactory.setUseBinaryMessages(false);
        responseMessageFactory.setEtx(-1);
        responseMessageFactory.setIgnoreLastMissingField(false);
        responseMessageFactory.setConfigPath(IsoProcessor.CONFIG_FILE);
        IsoMessage responseMessage = responseMessageFactory.parseMessage(fromPOS, 0);
        try {
            for (int i = 0; i<128; i++){
                if (responseMessage.hasField(i)){
                    switch (i){
                        case 7:
                            isoMsg.set(7, ISODate.getDateTime(now));
                            break;
                        case 12:
                            isoMsg.set(12, ISODate.getTime(now));
                            break;
                        case 13:
                            isoMsg.set(13, ISODate.getDate(now));
                            break;
                        case 15:
                            isoMsg.set(15,ISODate.getDate(now));
                            break;
                        case 52:
                            String pinblock = responseMessage.getObjectValue(52).toString();
                            isoMsg.set(52, ISOUtil.hex2byte (pinblock));
                            break;
                        default:
                            isoMsg.set(i,responseMessage.getObjectValue(i).toString());
                            break;
                    }
                }
            }
            String fromPOSmessage = hexify(fromPOS);
            String asciiMessage = toAscii(fromPOSmessage);
            String mti = asciiMessage.substring(0,4);
            isoMsg.setMTI(mti);
            switch (mti){
                case "0200":
                    sanitizeCashoutISO(isoMsg, now, responseMessage);
                    break;
                case "0420":
                    sanitizeReversal(isoMsg, now, responseMessage);
                    break;
                default:
                    break;
            }
            logger.info("Interswitch ISO request");
            for (int j = 0;j<isoMsg.getMaxField(); j++){
                if (isoMsg.hasField(j)){
                    if (j==2){
                        String pan = maskPanForReceipt(isoMsg.getString(j));
                        logger.info("<field {}> = {}",j,pan);
                    }else {
                        logger.info("<field {}> = {}",j,isoMsg.getString(j));
                    }

                }
            }

        } catch (ISOException | EmvProcessingException /*| EmvProcessingException*/ e) {
            throw new RequestProcessingException("Could not set request mti", e);
        }


        byte[] message;
        try {
            isoMsg.setPackager(packager);
            message = isoMsg.pack();
        } catch (ISOException e) {
            throw new RequestProcessingException("Could not pack iso message", e);
        }
        return prependLenBytes(message);
    }

    private void sanitizeCashoutISO(ISOMsg isoMsg, Date now, IsoMessage responseMessage) throws ISOException, EmvProcessingException {
        isoMsg.unset(4);
        String amount = responseMessage.getObjectValue(4).toString();
        String formatedAmount = amount.replace(".","");
        String finalAmount = padLeftZeros(formatedAmount,12);
        System.out.println("Transaction Amount is "+finalAmount);
        isoMsg.set(4,finalAmount);
        isoMsg.unset(59);
        isoMsg.unset(60);
        isoMsg.unset(128);

        isoMsg.set(15, ISODate.getDate(now));
        isoMsg.set(18,"6010");
        isoMsg.set(33,"013622");
        isoMsg.set(98, "0000000000|WDL|45:45:10:5");//identifying the payee (recipient) of a payment transaction //can be dummy data
//                isoMsg.set(100, "506146");//A code identifying the financial institution that should receive a request or advice
//                isoMsg.set(103, "6900326912");//When used in payment transactions, this field specifies the bank account number of the payee
//                isoMsg.set(111, "42VWY37");//custom
        isoMsg.set(113, "");//custom
        isoMsg.set("127.0", "");
        isoMsg.set("127.2", "0200:000663:0124165012:067250612");//switch key : The switch key field uniquely identifies a transaction.
//        isoMsg.set("127.3", "FCMBLmpSrc  FCMBFinSnk  180316180316FCMBGroup   ");//routing info//add if it fails
        isoMsg.set("127.13", "000000 566       ");//pos geographic data
        isoMsg.set("127.33", "6007");//pos geographic data
        isoMsg.set(55,field55Generator(isoMsg, responseMessage));
//        isoMsg.unset(55);

//                isoMsg.set("127.25", emvUtil.emvStringToXmlString(responseMessage.getObjectValue(55).toString()));
        isoMsg.set("127.33", "6007");//Extended transaction type
    }

    private void sanitizeReversal(ISOMsg isoMsg, Date now, IsoMessage responseMessage) throws ISOException, EmvProcessingException {
        isoMsg.unset(4);
        String amount = responseMessage.getObjectValue(4).toString();
        String formatedAmount = amount.replace(".","");
        String finalAmount = padLeftZeros(formatedAmount,12);
        System.out.println("Transaction Amount is "+finalAmount);
        isoMsg.set(4,finalAmount);
        isoMsg.unset(59);
        isoMsg.unset(60);
        isoMsg.unset(128);
        isoMsg.set(15, ISODate.getDate(now));
        isoMsg.set(18,"6010");
        isoMsg.set(33,"013622");
        isoMsg.set(98, "0000000000|WDL|45:45:10:5");//identifying the payee (recipient) of a payment transaction //can be dummy data
//                isoMsg.set(100, "506146");//A code identifying the financial institution that should receive a request or advice
//                isoMsg.set(103, "6900326912");//When used in payment transactions, this field specifies the bank account number of the payee
//                isoMsg.set(111, "42VWY37");//custom
        isoMsg.set(113, "");//custom
        isoMsg.set("127.0", "");
        isoMsg.set("127.2", "0200:000663:0124165012:067250612");//switch key : The switch key field uniquely identifies a transaction.
        isoMsg.set("127.3", "FCMBLmpSrc  FCMBFinSnk  180316180316FCMBGroup   ");//routing info
        isoMsg.set("127.13", "000000 566       ");//pos geographic data
        isoMsg.set("127.33", "6007");//pos geographic data

//                isoMsg.set("127.25", emvUtil.emvStringToXmlString(responseMessage.getObjectValue(55).toString()));
        isoMsg.set("127.33", "6007");//Extended transaction type
    }

    private String field55Generator(ISOMsg isoMsg, IsoMessage responseMessage) throws EmvProcessingException {
        Map<String, String> field55 = extractKeyValuePairs(responseMessage.getObjectValue(55).toString());
//                String tlvAmount = field55.get("9F02");
        String tlvAmount = field55.get("9F02");
        String OriginalField55 = isoMsg.getString(55);
//        isoMsg.unset(55);
        //check the padding value
        byte[]byteAmount = ISOUtil.hex2byte(tlvAmount);
        String HexAmount = amountToHexHelper(byteAmount).toUpperCase();
        String NewField55 = org.apache.commons.lang3.StringUtils.replacePattern(OriginalField55,"9F0206"+"[a-z,0-9,A-Z]*"+"9F03","9F0206"
                +HexAmount+"9F03");
        return NewField55;
    }

    private static String toAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    private byte[] prependLenBytes(byte[] data) {
        short len = (short) data.length;
        byte[] newBytes = new byte[len + 2];
        newBytes[0] = (byte) (len / 256);
        newBytes[1] = (byte) (len & 255);
        System.arraycopy(data, 0, newBytes, 2, len);
        return newBytes;
    }

    private static String amountToHexHelper(byte[] amount) {
        return StringUtils.padLeft(Long.toHexString(Long.parseLong(ISOUtil.hexString(amount))), 12, '0');
    }

    public byte[]toPOS(byte[]fromISW) {
        ISOMsg iswResponse = new ISOMsg();
        PostBridgePackager packager = new PostBridgePackager();
        iswResponse.setPackager(packager);

        try {
            iswResponse.unpack(fromISW);
        } catch (ISOException e) {
            e.printStackTrace();
        }
        logger.info("Response From Int2Switch");
        for (int i = 0; i<iswResponse.getMaxField(); i++){
            if (iswResponse.hasField(i)){
                if (i==2){
                    String pan = maskPanForReceipt(iswResponse.getString(i));
                    logger.info("<field {}> = {}",i,pan);
                }else {
                    logger.info("<field {}> = {}",i,iswResponse.getString(i));
                }
            }
        }

        final IsoMessage isoMessage = new IsoMessage();
        isoMessage.setType(528);
        final IsoValue<String> field2 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, (Object)iswResponse.getString(2));
        final IsoValue<String> field3 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)iswResponse.getString(3), 6);
        final IsoValue<String> field4 = (IsoValue<String>)new IsoValue(IsoType.AMOUNT, (Object)iswResponse.getString(4), 12);
        final IsoValue<String> field7 = (IsoValue<String>)new IsoValue(IsoType.DATE10, (Object)iswResponse.getString(7), 10);
        final IsoValue<String> field11 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)iswResponse.getString(11), 6);
        final IsoValue<String> field12 = (IsoValue<String>)new IsoValue(IsoType.TIME, (Object)iswResponse.getString(12), 6);
        final IsoValue<String> field13 = (IsoValue<String>)new IsoValue(IsoType.DATE4, (Object)iswResponse.getString(13), 4);
        final IsoValue<String> field14 = (IsoValue<String>)new IsoValue(IsoType.DATE4, (Object)iswResponse.getString(14));
        final IsoValue<String> field15 = (IsoValue<String>)new IsoValue(IsoType.DATE4, (Object)iswResponse.getString(15));
        final IsoValue<String> field22 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)iswResponse.getString(22), 3);
        final IsoValue<String> field25 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)iswResponse.getString(25), 2);
        final IsoValue<String> field30 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)"C00000000", 9);
        final IsoValue<String> field33 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, (Object)iswResponse.getString(33));
        final IsoValue<String> field35 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, (Object)iswResponse.getString(35));
        final IsoValue<String> field37 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)iswResponse.getString(37), 12);
        final IsoValue<String> field39 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)iswResponse.getString(39), 2);
        final IsoValue<String> field41 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)iswResponse.getString(41), 8);
        final IsoValue<String> field42 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)iswResponse.getString(42), 15);
        final IsoValue<String> field49 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)iswResponse.getString(49), 3);
        final IsoValue<String> field55 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)"910a3E14388911C5FADE3035");
        final IsoValue<String> field59 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)"010101");
        final IsoValue<String> field123 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)iswResponse.getString(123));
        final IsoValue<String> field124 = (IsoValue<String>)new IsoValue(IsoType.LLLLVAR, (Object)"05004^NST");
        final IsoValue<String> field128 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)"44b603eeee222ddf803c586ded0123a912281dd24c368f552a3b7e803e166464",64);

        isoMessage.setField(2, (IsoValue)field2);
        isoMessage.setField(3, (IsoValue)field3);
        isoMessage.setField(4, (IsoValue)field4);
        isoMessage.setField(7, (IsoValue)field7);
        isoMessage.setField(11, (IsoValue)field11);
        isoMessage.setField(12, (IsoValue)field12);
        isoMessage.setField(13, (IsoValue)field13);
        isoMessage.setField(14, (IsoValue)field14);
        isoMessage.setField(15, (IsoValue)field15);
        isoMessage.setField(22, (IsoValue)field22);
        isoMessage.setField(25, (IsoValue)field25);
        isoMessage.setField(30, (IsoValue)field30);
        isoMessage.setField(33, (IsoValue)field33);
        isoMessage.setField(35, (IsoValue)field35);
        isoMessage.setField(37, (IsoValue)field37);
        isoMessage.setField(39, (IsoValue)field39);
        isoMessage.setField(41, (IsoValue)field41);
        isoMessage.setField(42, (IsoValue)field42);
        isoMessage.setField(49, (IsoValue)field49);
        isoMessage.setField(55, (IsoValue)field55);
        isoMessage.setField(59, (IsoValue)field59);
        isoMessage.setField(123, (IsoValue)field123);
        isoMessage.setField(124, (IsoValue)field124);
        isoMessage.setField(128, (IsoValue)field128);
        return isoMessage.writeData();

    }

    private String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }

    public byte[] mockResponseForAmountLimit(IsoMessage isoMessage)
    {
        isoMessage.setType(528);
        final IsoValue<String> field2 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, (Object)isoMessage.getObjectValue(2));
        final IsoValue<String> field3 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)isoMessage.getObjectValue(3), 6);
        final IsoValue<String> field4 = (IsoValue<String>)new IsoValue(IsoType.AMOUNT, (Object)isoMessage.getObjectValue(4), 12);
        final IsoValue<String> field7 = (IsoValue<String>)new IsoValue(IsoType.DATE10, (Object)isoMessage.getObjectValue(7), 10);
        final IsoValue<String> field11 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)isoMessage.getObjectValue(11), 6);
        final IsoValue<String> field12 = (IsoValue<String>)new IsoValue(IsoType.TIME, (Object)isoMessage.getObjectValue(12), 6);
        final IsoValue<String> field13 = (IsoValue<String>)new IsoValue(IsoType.DATE4, (Object)isoMessage.getObjectValue(13), 4);
        final IsoValue<String> field14 = (IsoValue<String>)new IsoValue(IsoType.DATE4, (Object)isoMessage.getObjectValue(14));
        final IsoValue<String> field15 = (IsoValue<String>)new IsoValue(IsoType.DATE4, (Object)isoMessage.getObjectValue(15));
        final IsoValue<String> field22 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)isoMessage.getObjectValue(22), 3);
        final IsoValue<String> field25 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)isoMessage.getObjectValue(25), 2);
        final IsoValue<String> field30 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)"C00000000", 9);
        final IsoValue<String> field33 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, (Object)"000000");
        final IsoValue<String> field35 = (IsoValue<String>)new IsoValue(IsoType.LLVAR, (Object)isoMessage.getObjectValue(35));
        final IsoValue<String> field37 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)isoMessage.getObjectValue(37), 12);
        final IsoValue<String> field39 = (IsoValue<String>)new IsoValue(IsoType.NUMERIC, (Object)"13", 2);
        final IsoValue<String> field41 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)isoMessage.getObjectValue(41), 8);
        final IsoValue<String> field42 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)isoMessage.getObjectValue(42), 15);
        final IsoValue<String> field49 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)isoMessage.getObjectValue(49), 3);
        final IsoValue<String> field55 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)"910a3E14388911C5FADE3035");
        final IsoValue<String> field59 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)"010101");
        final IsoValue<String> field123 = (IsoValue<String>)new IsoValue(IsoType.LLLVAR, (Object)isoMessage.getObjectValue(123));
        final IsoValue<String> field124 = (IsoValue<String>)new IsoValue(IsoType.LLLLVAR, (Object)"05004^NST");
        final IsoValue<String> field128 = (IsoValue<String>)new IsoValue(IsoType.ALPHA, (Object)"44b603eeee222ddf803c586ded0123a912281dd24c368f552a3b7e803e166464",64);

        isoMessage.setField(2, (IsoValue)field2);
        isoMessage.setField(3, (IsoValue)field3);
        isoMessage.setField(4, (IsoValue)field4);
        isoMessage.setField(7, (IsoValue)field7);
        isoMessage.setField(11, (IsoValue)field11);
        isoMessage.setField(12, (IsoValue)field12);
        isoMessage.setField(13, (IsoValue)field13);
        isoMessage.setField(14, (IsoValue)field14);
        isoMessage.setField(15, (IsoValue)field15);
        isoMessage.setField(22, (IsoValue)field22);
        isoMessage.setField(25, (IsoValue)field25);
        isoMessage.setField(30, (IsoValue)field30);
        isoMessage.setField(33, (IsoValue)field33);
        isoMessage.setField(35, (IsoValue)field35);
        isoMessage.setField(37, (IsoValue)field37);
        isoMessage.setField(39, (IsoValue)field39);
        isoMessage.setField(41, (IsoValue)field41);
        isoMessage.setField(42, (IsoValue)field42);
        isoMessage.setField(49, (IsoValue)field49);
        isoMessage.setField(55, (IsoValue)field55);
        isoMessage.setField(59, (IsoValue)field59);
        isoMessage.setField(123, (IsoValue)field123);
        isoMessage.setField(124, (IsoValue)field124);
        isoMessage.setField(128, (IsoValue)field128);
        byte[] resp = isoMessage.writeData();
        final short len = (short)resp.length;
        final byte[] headBytes = DataUtil.shortToBytes(len);
        final byte[] response = concat(headBytes, resp);
        return response;
    }


}
