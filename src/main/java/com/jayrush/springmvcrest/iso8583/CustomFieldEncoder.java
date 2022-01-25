// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.iso8583;

public interface CustomFieldEncoder<DataType>
{
    String encodeField(final DataType p0);
}
