// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.models.transaction;

//import com.globasure.nibss.tms.client.lib.utils.CryptoUtil;
//import com.globasure.nibss.tms.client.lib.utils.StringUtils;
import com.jayrush.springmvcrest.Nibss.utils.*;

public class GetSessionKeyResponse
{
    private byte[] clearSessionKey;
    private String encryptedSessionKey;
    
    public String getEncryptedSessionKey() {
        return this.encryptedSessionKey;
    }
    
    public void setEncryptedSessionKey(final String encryptedSessionKey) {
        this.encryptedSessionKey = encryptedSessionKey;
    }
    
    public byte[] getClearSessionKey() {
        return this.clearSessionKey;
    }
    
    public void decryptSessionKey(final byte[] tmk) throws Exception {
        final byte[] sessionKeyBytes = StringUtils.hexStringToByteArray(this.encryptedSessionKey);
        final byte[] clearSessionKeyBytes = CryptoUtil.decrypt(sessionKeyBytes, tmk, "DESede", "DESede/ECB/NoPadding");
        this.clearSessionKey = clearSessionKeyBytes;
    }
}
