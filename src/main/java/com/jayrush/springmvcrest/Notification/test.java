package com.jayrush.springmvcrest.Notification;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author JoshuaO
 */
public class test {

    public static String Decrypt(String data, String tmskey) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException, DecoderException {
        byte [] key = Hex.decodeHex(tmskey.toCharArray());

        System.out.println(Arrays.toString(key));
        Cipher cipher = Cipher.getInstance("AEdecryptMasterKeyS");
//        System.out.println(tmskey.toCharArray()[0]);

        SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES");

        cipher.init(Cipher.DECRYPT_MODE, originalKey);
        return new String(cipher.doFinal(Base64.decodeBase64(data)), "UTF-8");

    }

    public static void main(String...args) throws NoSuchPaddingException, DecoderException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        String data = "asHWFAgWZn/bysvrN5Q7YSFQZ1H7Xznyt/uY5ICDGpbPtJZ7wnd8Sba983jKJ+w9bifQ0r3Qr0IymqBCtrbKLZDdZFS9a70R1v+1L6es/f7sNhq4h95NPpZleio4VUCja53Knx5ebjLp16rcNvdDyylZhQLjg67c4fT4nsUtpwNBbQsxrtgpQa/Iw2ejIsHSznaydN6j/NUeTa8q60v/H4TepwrVBDWlrHD+cKWPmi1G5aCqpyNa52k80iW+UGLBe9b8bvsOXdpcKooaCnKgJCB/swmSqrVLlJZFgqzUKSa1riEGn6hD37H2xcC8sPgq6SsP3p/RXwH2zd+NRIEGMw==";
        String key = "2d7b477e13bff3803b09af3f9a158139";
        String value = Decrypt(data, key);
        System.out.println(value);
    }

}

