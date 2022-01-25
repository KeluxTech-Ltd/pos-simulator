// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583;

public interface CustomBinaryField<T> extends CustomField<T>
{
    T decodeBinaryField(final byte[] p0, final int p1, final int p2);
    
    byte[] encodeBinaryField(final T p0);
}
