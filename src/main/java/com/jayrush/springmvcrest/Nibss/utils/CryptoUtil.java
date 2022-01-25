// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.utils;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;

public class CryptoUtil
{
    public static byte[] encrypt3DESCBC(final byte[] keyBytes, final byte[] ivBytes, final byte[] dataBytes) {
        try {
            final AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            final SecretKeySpec newKey = new SecretKeySpec(keyBytes, "DESede");
            final Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
            cipher.init(1, newKey, ivSpec);
            return cipher.doFinal(dataBytes);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[] decrypt3DESCBC(final byte[] keyBytes, final byte[] ivBytes, final byte[] dataBytes) {
        try {
            final AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            final SecretKeySpec newKey = new SecretKeySpec(keyBytes, "DESede");
            final Cipher cipher = Cipher.getInstance("DESede/CBC/NoPadding");
            cipher.init(2, newKey, ivSpec);
            return cipher.doFinal(dataBytes);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[] decrypt3DESECB(final byte[] keyBytes, final byte[] dataBytes) {
        try {
            final SecretKeySpec newKey = new SecretKeySpec(keyBytes, "DESede");
            final Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            cipher.init(2, newKey);
            return cipher.doFinal(dataBytes);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[] encrypt3DESECB(final byte[] keyBytes, final byte[] dataBytes) {
        try {
            final SecretKeySpec newKey = new SecretKeySpec(keyBytes, "DESede");
            final Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            cipher.init(1, newKey);
            return cipher.doFinal(dataBytes);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[] calculateSHA256(final byte[] src, final int srcOffset, final int len) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(src, srcOffset, len);
            return md.digest();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[] calculateSHA256(final ByteBuffer buffer) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(buffer);
            return md.digest();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static int crcccit16(int crc, final int b) {
        crc = ((crc >> 8 & 0xFF) | (crc << 8 & 0xFFFF));
        crc ^= (b & 0xFF);
        crc ^= (crc & 0xFF) >> 4;
        crc ^= (crc << 8 << 4 & 0xFFFF);
        crc ^= (crc & 0xFF) << 4 << 1;
        return crc & 0xFFFF;
    }
    
    public static int crcccitt16(final int initial, final byte[] buf, final int offset, final int length) {
        int crc = initial;
        for (int i = 0; i < length; ++i) {
            crc = crcccit16(crc, buf[offset + i] & 0xFF);
        }
        return crc;
    }
    
    public static int crcccitt16(final int initial, final byte[] buf) {
        return crcccitt16(initial, buf, 0, buf.length);
    }
    
    private static void memxor(final byte[] output, final int outPos, final byte[] a, final int aPos, final byte[] b, final int bPos, final int len) {
        for (int i = 0; i < len; ++i) {
            output[outPos + i] = (byte)((a[aPos + i] & 0xFF) ^ (b[bPos + i] & 0xFF));
        }
    }
    
    private static void memcpy(final byte[] dst, final int dstOffset, final byte[] src, final int srcOffset, final int length) {
        System.arraycopy(src, srcOffset, dst, dstOffset, length);
    }
    
    private static void memset(final byte[] dst, final int dstOffset, final int value, final int length) {
        for (int i = 0; i < length; ++i) {
            dst[dstOffset + i] = (byte)value;
        }
    }
    
    public static final void encryptDES(final byte[] output, final int outputOffset, final byte[] input, final int inputOffset, final int length, final byte[] desKey, final int desKeyOffset) {
        try {
            final SecretKey key = new SecretKeySpec(desKey, desKeyOffset, 8, "DES");
            final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
            final Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
            cipher.init(1, key, iv);
            cipher.doFinal(input, inputOffset, length, output, outputOffset);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static final void encrypt3DESECB(final byte[] output, final int outputOffset, final byte[] input, final int inputOffset, final int length, final byte[] desKey, final int desKeyOffset) {
        final byte[] keyValue = new byte[24];
        System.arraycopy(desKey, desKeyOffset, keyValue, 0, 16);
        System.arraycopy(desKey, desKeyOffset, keyValue, 16, 8);
        try {
            final SecretKey key = new SecretKeySpec(keyValue, "DESede");
            final Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            cipher.init(1, key);
            cipher.doFinal(input, inputOffset, length, output, outputOffset);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static byte[] calculateDerivedKey(final byte[] ksn, final byte[] ipek) {
        final byte[] r8 = new byte[8];
        final byte[] r8a = new byte[8];
        final byte[] r8b = new byte[8];
        final byte[] key = new byte[16];
        memcpy(key, 0, ipek, 0, 16);
        memcpy(r8, 0, ksn, 2, 6);
        final byte[] array = r8;
        final int n = 5;
        array[n] &= 0xFFFFFFE0;
        final int ec = (ksn[ksn.length - 3] & 0x1F) << 16 | (ksn[ksn.length - 2] & 0xFF) << 8 | (ksn[ksn.length - 1] & 0xFF);
        int sr = 1048576;
        final byte[] pattern = { -64, -64, -64, -64, 0, 0, 0, 0, -64, -64, -64, -64, 0, 0, 0, 0 };
        while (sr != 0) {
            if ((sr & ec) != 0x0) {
                final byte[] array2 = r8;
                final int n2 = 5;
                array2[n2] |= (byte)(sr >> 16);
                final byte[] array3 = r8;
                final int n3 = 6;
                array3[n3] |= (byte)(sr >> 8);
                final byte[] array4 = r8;
                final int n4 = 7;
                array4[n4] |= (byte)sr;
                memxor(r8a, 0, key, 8, r8, 0, 8);
                encryptDES(r8a, 0, r8a, 0, 8, key, 0);
                memxor(r8a, 0, r8a, 0, key, 8, 8);
                memxor(key, 0, key, 0, pattern, 0, 16);
                memxor(r8b, 0, key, 8, r8, 0, 8);
                encryptDES(r8b, 0, r8b, 0, 8, key, 0);
                memxor(r8b, 0, r8b, 0, key, 8, 8);
                memcpy(key, 8, r8a, 0, 8);
                memcpy(key, 0, r8b, 0, 8);
            }
            sr >>= 1;
        }
        memset(r8, 0, 0, r8.length);
        memset(r8a, 0, 0, r8a.length);
        memset(r8b, 0, 0, r8b.length);
        return key;
    }
    
    public static byte[] calculateDataKey(final byte[] ksn, final byte[] ipek) {
        final byte[] calculateDerivedKey;
        final byte[] dataKey = calculateDerivedKey = calculateDerivedKey(ksn, ipek);
        final int n = 5;
        calculateDerivedKey[n] ^= (byte)255;
        final byte[] array = dataKey;
        final int n2 = 13;
        array[n2] ^= (byte)255;
        encrypt3DESECB(dataKey, 0, dataKey, 0, dataKey.length, dataKey, 0);
        return dataKey;
    }
    
    public static byte[] decryptRSABlock(final byte[] modBytes, final byte[] expBytes, final byte[] data) {
        try {
            final BigInteger modulus = new BigInteger(1, modBytes);
            final BigInteger exponent = new BigInteger(1, expBytes);
            final KeyFactory factory = KeyFactory.getInstance("RSA");
            final Cipher cipher = Cipher.getInstance("RSA");
            final RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(modulus, exponent);
            final PrivateKey privateKey = factory.generatePrivate(privateSpec);
            cipher.init(2, privateKey);
            return cipher.doFinal(data);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[] decryptAESCBC(final byte[] keyBytes, final byte[] ivBytes, final byte[] dataBytes) {
        try {
            final AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            final SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
            final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(2, newKey, ivSpec);
            return cipher.doFinal(dataBytes);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[] decryptAESCBC(final byte[] keyBytes, final byte[] dataBytes) {
        return decryptAESCBC(keyBytes, new byte[16], dataBytes);
    }
    
    public static byte[] encrypt(final byte[] plainText, final byte[] key, final String algorithm, final String transformation) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] keyRef = key;
        if (keyRef.length == 16) {
            keyRef = new byte[24];
            System.arraycopy(key, 0, keyRef, 0, 16);
            System.arraycopy(key, 0, keyRef, 16, 8);
        }
        byte[] ivBytes = null;
        if (transformation != null && transformation.indexOf("CBC") != -1) {
            ivBytes = new byte[8];
            for (int i = 0; i < ivBytes.length; ++i) {
                ivBytes[i] = 0;
            }
        }
        final KeySpec ks = new DESedeKeySpec(keyRef);
        final SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);
        final Cipher c = Cipher.getInstance(transformation);
        final SecretKey sKey = skf.generateSecret(ks);
        if (ivBytes != null) {
            final IvParameterSpec iv = new IvParameterSpec(ivBytes);
            c.init(1, sKey, iv);
        }
        else {
            c.init(1, sKey);
        }
        final byte[] cipherText = c.doFinal(plainText);
        return cipherText;
    }
    
    public static byte[] decrypt(final byte[] cipherText, final byte[] key, final String algorithm, final String transformation) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] keyRef = key;
        if (keyRef.length == 16) {
            keyRef = new byte[24];
            System.arraycopy(key, 0, keyRef, 0, 16);
            System.arraycopy(key, 0, keyRef, 16, 8);
        }
        byte[] ivBytes = null;
        if (transformation != null && transformation.indexOf("CBC") != -1) {
            ivBytes = new byte[8];
            for (int i = 0; i < ivBytes.length; ++i) {
                ivBytes[i] = 0;
            }
        }
        final KeySpec ks = new DESedeKeySpec(keyRef);
        final SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);
        final Cipher c = Cipher.getInstance(transformation);
        final SecretKey sKey = skf.generateSecret(ks);
        if (ivBytes != null) {
            final IvParameterSpec iv = new IvParameterSpec(ivBytes);
            c.init(2, sKey, iv);
        }
        else {
            c.init(2, sKey);
        }
        final byte[] plainText = c.doFinal(cipherText);
        return plainText;
    }
}
