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
                String val = parameters.substring(2, 5);
                System.out.println("parameters.substring(2, 5)"+val);
                final int valueLen = Integer.parseInt(parameters.substring(2, 5)) + 5;
                decodedValues.put(key, parameters.substring(5, valueLen));
                System.out.println(decodedValues);
                parameters = parameters.substring(valueLen);
                length = parameters.length();
            }
        }
        catch (Exception ex) {}
        return decodedValues;
    }

    public static void main(String...args){
//        String parameters = "0810023800000280000506111142320000011142320611002101CX811220201420200611115010030152101LA00000AH4804002600500356606003566070011080045411520403 Line Management LimitLA           LANGe6da10373f7d0705fce4c9671bf4d51de968afe977b3bc464505a221632dd037";
//        String parameters = "0810023800000280000506221522530000011522530622002TAJ00011220201420200611115010030152TAJFC0000023830400260050035660600356607001108004541152040TAJ BANK AGENCY BANKING  2020    TAJBank8ABA156F2D23B411986187B82CAD9DBA476B23D7FF77846535BC6426A21F1CA1";
        String parameters = "0810023800000280000506221606220000011606220622002TAJ00011220201420200611115010030152TAJFC0000023830400260050035660600356607001108004541152040TAJ BANK AGENCY BANKING  2020    TAJBank9C583037C8D54F8F167821AD2F7D9C4736FC280B349B2DDAC2E1DBBFCA45C70C";
        Map<String,String>result = parseParameters(parameters);
        System.out.println(result);
    }
}
