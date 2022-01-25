// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.models.transaction;

//import com.globasure.nibss.tms.client.lib.utils.CryptoUtil;
//import com.globasure.nibss.tms.client.lib.utils.StringUtils;
import com.jayrush.springmvcrest.Nibss.utils.*;

public class GetPinKeyResponse
{
    private byte[] clearPinKey;
    private String encryptedPinKey;
    
    public byte[] getClearPinKey() {
        return this.clearPinKey;
    }
    
    public String getEncryptedPinKey() {
        return this.encryptedPinKey;
    }
    
    public void setEncryptedPinKey(final String encryptedPinKey) {
        this.encryptedPinKey = encryptedPinKey;
    }
    
    public void descryptPinKey(final byte[] tmk) throws Exception {
        final byte[] pinKeyBytes = StringUtils.hexStringToByteArray(this.encryptedPinKey.replace(" ", ""));
        this.clearPinKey = CryptoUtil.decrypt(pinKeyBytes, tmk, "DESede", "DESede/ECB/NoPadding");
    }
}
