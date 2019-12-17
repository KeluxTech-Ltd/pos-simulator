package com.jayrush.springmvcrest.Service;

import com.jayrush.springmvcrest.utility.CryptoException;

/**
 * @author JoshuaO
 */
public interface nibssToIswInterface {
    String decryptPinBlock(String pinBlock) throws CryptoException;
    String encryptPinBlock(String pinBlock) throws CryptoException;

}
