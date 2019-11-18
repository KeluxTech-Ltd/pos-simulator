// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.models.transaction;

//import com.globasure.nibss.tms.client.lib.utils.CryptoUtil;
//import com.globasure.nibss.tms.client.lib.utils.StringUtils;
import com.jayrush.springmvcrest.Nibss.utils.*;
import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class GetMasterKeyResponse
{
    private String field39;
    private String encryptedMasterKey;
    private byte[] clearMasterKey;
    
    public byte[] getClearMasterKey() {
        return this.clearMasterKey;
    }
    
    public void decryptMasterKey(final byte[] transportKey) throws Exception {
        final byte[] ctmkBytes = transportKey;
        final byte[] encryptedBytes = StringUtils.hexStringToByteArray(this.encryptedMasterKey);
        final byte[] clearTMKBytes = CryptoUtil.decrypt(encryptedBytes, ctmkBytes, "DESede", "DESede/ECB/NoPadding");
        this.clearMasterKey = clearTMKBytes;
    }
    
    public void _decryptMasterKey(final byte[] transportKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
        final Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        final KeySpec spec = new DESedeKeySpec(transportKey);
        final SecretKeyFactory factory = SecretKeyFactory.getInstance("DESede");
        final SecretKey key = factory.generateSecret(spec);
        cipher.init(2, key);
        this.clearMasterKey = cipher.doFinal(this.encryptedMasterKey.getBytes());
    }
    
    public String getEncryptedMasterKey() {
        return this.encryptedMasterKey;
    }
    
    public void setEncryptedMasterKey(final String encryptedMasterKey) {
        this.encryptedMasterKey = encryptedMasterKey;
    }
    
    public String getField39() {
        return this.field39;
    }
    
    public void setField39(final String field39) {
        this.field39 = field39;
    }
    
    @Override
    public String toString() {
        return "GetMasterKeyResponse{encryptedMasterKey='" + this.encryptedMasterKey + '\'' + ", field39='" + this.field39 + '\'' + '}';
    }
}
