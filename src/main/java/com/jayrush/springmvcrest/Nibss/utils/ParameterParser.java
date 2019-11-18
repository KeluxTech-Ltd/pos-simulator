// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.utils;

import java.util.HashMap;
import java.util.Map;

public class ParameterParser
{
    public static Map<String, String> parseParameters(String parameters) {
        int length = parameters.length();
        final Map<String, String> decodedValues = new HashMap<String, String>();
        try {
            while (length > 0) {
                final String key = parameters.substring(0, 2);
                final int valueLen = Integer.parseInt(parameters.substring(2, 5)) + 5;
                decodedValues.put(key, parameters.substring(5, valueLen));
                parameters = parameters.substring(valueLen);
                length = parameters.length();
            }
        }
        catch (Exception ex) {}
        return decodedValues;
    }
}
