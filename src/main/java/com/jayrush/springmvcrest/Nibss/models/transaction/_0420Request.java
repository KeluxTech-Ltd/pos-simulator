// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.models.transaction;

public class _0420Request extends ISO8583TransactionRequest
{
    @Override
    public boolean hashMessage() {
        return true;
    }
    
    @Override
    public int getMessageType() {
        return 1056;
    }
    
    @Override
    public int getRepeatMessageType() {
        return 1057;
    }
}
