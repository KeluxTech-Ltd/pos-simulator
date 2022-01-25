package com.jayrush.springmvcrest.domain;

import java.util.HashMap;
import java.util.Map;

public class nibssresponse {
    public static String ResponseCodeMap(String responseCode) {
        Map<String, String> ResponseCodes = new HashMap<>();
        ResponseCodes.put("00", "Approved");
        ResponseCodes.put("01",	"Refer to card Issuer");
        ResponseCodes.put("02",	"Refer to card Issuer Special Condition");
        ResponseCodes.put("03",	"Invalid Merchant");
        ResponseCodes.put("04",	"Pick-up card");
        ResponseCodes.put("05",	"Do Not Honor");
        ResponseCodes.put("06",	"Error");
        ResponseCodes.put("07",	"Pick-Up Card Special Condition");
        ResponseCodes.put("08",	"Honor with Identification");
        ResponseCodes.put("09",	"Request in Progress");
        ResponseCodes.put("10",	"Approved Partial");
        ResponseCodes.put("11",	"Approved VIP");
        ResponseCodes.put("12",	"Invalid Transaction");
        ResponseCodes.put("13",	"Invalid Amount");
        ResponseCodes.put("14",	"Invalid Card Number");
        ResponseCodes.put("15",	"No Such Issuer");
        ResponseCodes.put("16",	"Approved  Update Track 3");
        ResponseCodes.put("17",	"Customer Cancellation");
        ResponseCodes.put("18",	"Customer Dispute");
        ResponseCodes.put("19",	"Re-enter Transaction");
        ResponseCodes.put("20",	"Invalid Response");
        ResponseCodes.put("21",	"No Action Taken");
        ResponseCodes.put("22",	"Suspected Malfunction");
        ResponseCodes.put("23",	"Unacceptable Transaction Fee");
        ResponseCodes.put("24",	"File Update not Supported");
        ResponseCodes.put("25",	"Unable to Locate Record");
        ResponseCodes.put("26",	"Duplicate Record");
        ResponseCodes.put("27",	"File Update Field Edit Error");
        ResponseCodes.put("28",	"File Update File Locked");
        ResponseCodes.put("29",	"File Update Failed");
        ResponseCodes.put("30",	"Format Error");
        ResponseCodes.put("31",	"Bank Not Supported");
        ResponseCodes.put("32",	"Completed Partially");
        ResponseCodes.put("33",	"Expired Card Pick-Up");
        ResponseCodes.put("34",	"Suspected Fraud Pick-Up");
        ResponseCodes.put("35",	"Contact Acquirer Pick-Up");
        ResponseCodes.put("36",	"Restricted Card Pick-Up");
        ResponseCodes.put("37",	"Call Acquirer Security Pick-Up");
        ResponseCodes.put("38",	"PIN Tries Exceeded Pick-Up");
        ResponseCodes.put("39",	"No Credit Account");
        ResponseCodes.put("40",	"Function not Supported");
        ResponseCodes.put("41",	"Lost Card Pick-Up");
        ResponseCodes.put("42",	"No Universal Account");
        ResponseCodes.put("43",	"Stolen Card Pick-Up Stolen Card Pick-Up");
        ResponseCodes.put("44",	"No Investment Account");
        ResponseCodes.put("45",	"Account Closed");
        ResponseCodes.put("46",	"Wrong login details on payment page attempting to login to QT");
        ResponseCodes.put("51",	"Insufficient Funds");
        ResponseCodes.put("52",	"No Check Account");
        ResponseCodes.put("53",	"No Savings Account");
        ResponseCodes.put("54",	"Expired Card");
        ResponseCodes.put("55",	"Incorrect PIN");
        ResponseCodes.put("56",	"No Card Record");
        ResponseCodes.put("57",	"Transaction not Permitted to Cardholder");
        ResponseCodes.put("58",	"Transaction not Permitted on Terminal");
        ResponseCodes.put("59",	"Suspected Fraud");
        ResponseCodes.put("60",	"Contact Acquirer");
        ResponseCodes.put("61",	"Exceeds Withdrawal Limit");
        ResponseCodes.put("62",	"Restricted Card");
        ResponseCodes.put("63",	"Security Violation");
        ResponseCodes.put("64",	"Original Amount Incorrect");
        ResponseCodes.put("65",	"Exceeds withdrawal frequency");
        ResponseCodes.put("66",	"Call Acquirer Security");
        ResponseCodes.put("67",	"Hard Capture");
        ResponseCodes.put("68",	"Response Received Too Late");
        ResponseCodes.put("75",	"PIN tries exceeded");
        ResponseCodes.put("76",	"Reserved for Future Postilion Use");
        ResponseCodes.put("77",	"Intervene Bank Approval Required");
        ResponseCodes.put("78",	"Intervene Bank Approval Required for Partial Amount");
        ResponseCodes.put("90",	"Cut-off in Progress");
        ResponseCodes.put("91",	"Issuer or Switch Inoperative");
        ResponseCodes.put("92",	"Routing Error.");
        ResponseCodes.put("93",	"Violation of law");
        ResponseCodes.put("94",	"Duplicate Transaction");
        ResponseCodes.put("95",	"Reconcile Error");
        ResponseCodes.put("96",	"System Malfunction");
        ResponseCodes.put("98",	"Exceeds Cash Limit");

        return ResponseCodes.get(responseCode);

    }
}
