package com.jayrush.springmvcrest.fep;

import com.jayrush.springmvcrest.Nibss.processor.IsoProcessor;
import com.jayrush.springmvcrest.PostBridgePackager;
import com.jayrush.springmvcrest.Service.nibssToIswInterface;
import com.jayrush.springmvcrest.iso8583.IsoMessage;
import com.jayrush.springmvcrest.iso8583.MessageFactory;
import com.jayrush.springmvcrest.utility.CryptoException;
import org.jpos.iso.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.jayrush.springmvcrest.Nibss.processor.IsoProcessor.printIsoFields;
import static com.jayrush.springmvcrest.utility.MainConverter.hexify;

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
                        case 11:
                            isoMsg.set(11, stanCounter.getStan());
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
                            //decrypt pinblock from pos and encrypt for interswitch
                            String pinblock = responseMessage.getObjectValue(52).toString();
                            String posPinblock = nibssToIswInterface.decryptPinBlock(pinblock);
                            String toIswPinblock = nibssToIswInterface.encryptPinBlock(posPinblock);
                            isoMsg.set(52,toIswPinblock);
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
            if ("0200".equals(isoMsg.getMTI())) {
                isoMsg.set(98, "5965350022|WDL|45:45:10:5");
                isoMsg.set(100, "628009");
                isoMsg.set(103, "1360876053");
                isoMsg.set(111, "x6JER8Y");
                isoMsg.set(113, "");
            }
            logger.info("Interswitch ISO request");
            for (int j = 0;j<isoMsg.getMaxField(); j++){
                if (isoMsg.hasField(j)){
                    logger.info("<field {}> = {}",j,isoMsg.getString(j));
                }
            }

        } catch (ISOException | CryptoException e) {
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

    private static String toAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }


    private static byte[] prependLenBytes(byte[] data) {
        short len = (short) data.length;
        byte[] newBytes = new byte[len + 2];
        newBytes[0] = (byte) (len / 256);
        newBytes[1] = (byte) (len & 255);
        System.arraycopy(data, 0, newBytes, 2, len);
        return newBytes;
    }
}
