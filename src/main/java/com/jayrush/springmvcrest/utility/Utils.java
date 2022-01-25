package com.jayrush.springmvcrest.utility;

/**
 * @author JoyU
 * @date 11/8/2018
 */
public class Utils {

    public static String removeLeadingZeros(String str) {
        if (str == null) {
            return null;
        }
        char[] chars = str.toCharArray();
        int index = 0;
        for (; index < str.length(); index++) {
            if (chars[index] != '0') {
                break;
            }
        }
        return (index == 0) ? str : str.substring(index);
    }

    public static String maskPanForReceipt(String pan) {
        StringBuilder maskedPan = new StringBuilder();
        int maskingStartIndex = 6;
        int maskingEndIndex = pan.length() - 4;

        for (int index = 0; index < pan.length(); index++) {
            if (index >= maskingStartIndex && index < maskingEndIndex) {
                maskedPan.append("*");
            } else {
                maskedPan.append(pan.charAt(index));
            }
        }
        return maskedPan.toString();
    }







}
