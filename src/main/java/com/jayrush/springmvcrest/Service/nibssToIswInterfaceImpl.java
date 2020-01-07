package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.slf4j.Logger;
import com.jayrush.springmvcrest.slf4j.LoggerFactory;
import com.jayrush.springmvcrest.utility.CryptoException;
import com.jayrush.springmvcrest.utility.EncryptionUtil;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author JoshuaO
 */

@Service
public class nibssToIswInterfaceImpl implements nibssToIswInterface {
    @Value("${tms.key}")
    private String tmsKey = "467F5EA176C84FECEC1CF8CE26314654";
    @Value("${switch.key}")
    private String switchExchangeKey = "467F5EA176C84FECEC1CF8CE26314654";
    private static final Logger logger = LoggerFactory.getLogger(nibssToIswInterfaceImpl.class);




    @Override
    public String decryptPinBlock(String pinBlock) throws CryptoException {
        try {
            byte[] tmsKeyBytes = Hex.decodeHex(tmsKey.toCharArray());
            byte[] pinBlockBytes = Hex.decodeHex(pinBlock.toCharArray());

            byte[] clearPinBlockBytes = EncryptionUtil.tdesDecryptECB(pinBlockBytes, tmsKeyBytes);

            return new String(Hex.encodeHex(clearPinBlockBytes));
        } catch (DecoderException e) {
            throw new CryptoException("Could not decode hex key", e);
        }
    }

    public String decryptPinBlock(String pinBlock, String key) throws CryptoException {
        try {
            byte[] tmsKeyBytes = Hex.decodeHex(key.toCharArray());
            byte[] pinBlockBytes = Hex.decodeHex(pinBlock.toCharArray());

            byte[] clearPinBlockBytes = EncryptionUtil.tdesDecryptECB(pinBlockBytes, tmsKeyBytes);

            return new String(Hex.encodeHex(clearPinBlockBytes));
        } catch (DecoderException e) {
            throw new CryptoException("Could not decode hex key", e);
        }
    }

    public String encryptPinBlock(String pinBlock) throws CryptoException {
        logger.info("The pin block bytes {} ", pinBlock);
        if (StringUtils.isEmpty(pinBlock)) {
            return pinBlock;
        }
        byte[] clearPinBlockBytes;
        byte[] zpk;
        try {
            clearPinBlockBytes = Hex.decodeHex(pinBlock.toCharArray());
            logger.info("The clear pin block bytes {} ", clearPinBlockBytes);
            zpk = Hex.decodeHex(switchExchangeKey.toCharArray());
            logger.info("The clear zpk {} ", switchExchangeKey.toCharArray());
        } catch (DecoderException e) {
            throw new CryptoException("Could not decode pin block for Threeline", e);
        }

        byte[] encryptedPinBlockBytes = EncryptionUtil.tdesEncryptECB(clearPinBlockBytes, zpk);

        return new String(Hex.encodeHex(encryptedPinBlockBytes));

    }

    public String encryptPinBlock(String pinBlock, String key) throws CryptoException {
        logger.info("The pin block bytes {} ", pinBlock);
        if (StringUtils.isEmpty(pinBlock)) {
            return pinBlock;
        }
        byte[] clearPinBlockBytes;
        byte[] zpk;
        try {
            clearPinBlockBytes = Hex.decodeHex(pinBlock.toCharArray());
            logger.info("The clear pin block bytes {} ", clearPinBlockBytes);
            zpk = Hex.decodeHex(key.toCharArray());
            logger.info("The clear zpk {} ", key.toCharArray());
        } catch (DecoderException e) {
            throw new CryptoException("Could not decode pin block for Threeline", e);
        }

        byte[] encryptedPinBlockBytes = EncryptionUtil.tdesEncryptECB(clearPinBlockBytes, zpk);

        return new String(Hex.encodeHex(encryptedPinBlockBytes));

    }

}


