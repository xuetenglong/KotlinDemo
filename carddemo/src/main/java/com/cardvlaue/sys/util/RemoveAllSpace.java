package com.cardvlaue.sys.util;

/**
 * Created by Administrator on 2017/2/21.
 */

public class RemoveAllSpace {

    public static String removeAllSpace(String str){
        String  mtpstr=str.replace(" ", "");
        return mtpstr;
    }


    public static String allSpace(String s) {
        String newStr = s.toString();
        newStr = newStr.replace(" ", "");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < newStr.length(); i += 4) {
            if (i > 0) {
                sb.append(" ");
            }
            if (i + 4 <= newStr.length()) {
                sb.append(newStr.substring(i, i + 4));
            } else {
                sb.append(newStr.substring(i, newStr.length()));
            }
        }
        return sb.toString();
    }
}
