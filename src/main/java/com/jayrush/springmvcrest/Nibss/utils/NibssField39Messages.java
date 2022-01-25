// 
// Decompiled by Procyon v0.5.36
// 

package com.jayrush.springmvcrest.Nibss.utils;

import java.util.HashMap;
import java.util.Map;

public class NibssField39Messages
{
    private static Map<String, String> responseMap;
    
    public static String getResponseMessageFor(final String code) {
        final String message = NibssField39Messages.responseMap.get(code);
        if (message == null) {
            return "";
        }
        return message;
    }
    
    static {
        (NibssField39Messages.responseMap = new HashMap<String, String>()).put("00", "Approved");
        NibssField39Messages.responseMap.put("01", "Refer to card Issuer");
        NibssField39Messages.responseMap.put("02", "Refer to card Issuer");
        NibssField39Messages.responseMap.put("03", "Invalid Merchant");
        NibssField39Messages.responseMap.put("04", "Pick-up card");
        NibssField39Messages.responseMap.put("05", "Do not honor");
        NibssField39Messages.responseMap.put("06", "Error");
        NibssField39Messages.responseMap.put("07", "Pick-up Card");
        NibssField39Messages.responseMap.put("08", "Honor with identification");
        NibssField39Messages.responseMap.put("09", "Request in progress");
        NibssField39Messages.responseMap.put("10", "Partially Approved");
        NibssField39Messages.responseMap.put("11", "Approved, VIP");
        NibssField39Messages.responseMap.put("12", "Invalid Transaction");
        NibssField39Messages.responseMap.put("13", "Invalid Amount");
        NibssField39Messages.responseMap.put("14", "Invalid card number");
        NibssField39Messages.responseMap.put("15", "No such issuer");
        NibssField39Messages.responseMap.put("16", "Approved, update track 3");
        NibssField39Messages.responseMap.put("17", "Customer cancellation");
        NibssField39Messages.responseMap.put("18", "Customer Dispute");
        NibssField39Messages.responseMap.put("19", "Re-enter transaction");
        NibssField39Messages.responseMap.put("20", "Invalid response");
        NibssField39Messages.responseMap.put("21", "No action taken");
        NibssField39Messages.responseMap.put("22", "Suspected malfunction");
        NibssField39Messages.responseMap.put("23", "Unacceptable transaction fee");
        NibssField39Messages.responseMap.put("24", "File update not supported");
        NibssField39Messages.responseMap.put("25", "Unable to locate record");
        NibssField39Messages.responseMap.put("26", "Duplicate record");
        NibssField39Messages.responseMap.put("27", "File update edit error");
        NibssField39Messages.responseMap.put("28", "File update file locked");
        NibssField39Messages.responseMap.put("29", "File update failed");
        NibssField39Messages.responseMap.put("30", "Format error");
        NibssField39Messages.responseMap.put("31", "Bank not supported");
        NibssField39Messages.responseMap.put("32", "Completed, partially");
        NibssField39Messages.responseMap.put("33", "Expired card");
        NibssField39Messages.responseMap.put("34", "Suspected Fraud");
        NibssField39Messages.responseMap.put("35", "Contact acquirer");
        NibssField39Messages.responseMap.put("36", "Restricted card");
        NibssField39Messages.responseMap.put("37", "Call acquirer security");
        NibssField39Messages.responseMap.put("38", "PIN tries exceeded");
        NibssField39Messages.responseMap.put("39", "No credit account");
        NibssField39Messages.responseMap.put("40", "Function not supported");
        NibssField39Messages.responseMap.put("41", "Lost card");
        NibssField39Messages.responseMap.put("42", "No universal account");
        NibssField39Messages.responseMap.put("43", "Stolen card");
        NibssField39Messages.responseMap.put("44", "No investment account");
        NibssField39Messages.responseMap.put("51", "Insufficient Funds");
        NibssField39Messages.responseMap.put("52", "No check account");
        NibssField39Messages.responseMap.put("53", "No savings account");
        NibssField39Messages.responseMap.put("54", "Expired card");
        NibssField39Messages.responseMap.put("55", "Incorrect pin");
        NibssField39Messages.responseMap.put("56", "No card record");
        NibssField39Messages.responseMap.put("57", "Transaction not permitted to cardholder");
        NibssField39Messages.responseMap.put("58", "Transaction not permitted on terminal");
        NibssField39Messages.responseMap.put("59", "Suspected fraud");
        NibssField39Messages.responseMap.put("60", "Contact acquirer");
        NibssField39Messages.responseMap.put("61", "Exceeds withdrawal limit");
        NibssField39Messages.responseMap.put("62", "Restricted card");
        NibssField39Messages.responseMap.put("63", "Security violation");
        NibssField39Messages.responseMap.put("64", "Original amount incorrect");
        NibssField39Messages.responseMap.put("65", "Exceeds withdrawal frequency");
        NibssField39Messages.responseMap.put("66", "Call acquirer security");
        NibssField39Messages.responseMap.put("67", "Hard capture");
        NibssField39Messages.responseMap.put("68", "Response received too late");
        NibssField39Messages.responseMap.put("75", "Pin tries exceeded");
        NibssField39Messages.responseMap.put("77", "Bank approval required");
        NibssField39Messages.responseMap.put("78", "Bank approval required for parital amount.");
        NibssField39Messages.responseMap.put("90", "Cut-off in progress");
        NibssField39Messages.responseMap.put("91", "Issuer or switch inoperative");
        NibssField39Messages.responseMap.put("92", "Routing error");
        NibssField39Messages.responseMap.put("93", "Violation of law");
        NibssField39Messages.responseMap.put("94", "Duplicate transaction");
        NibssField39Messages.responseMap.put("95", "Reconcile error");
        NibssField39Messages.responseMap.put("96", "System malfunction");
    }
}
