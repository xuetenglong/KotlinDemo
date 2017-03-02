package com.cardvlaue.sys.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import timber.log.Timber;

/**
 * Created by cardvalue on 2016/4/5.
 */
public class SafeUtil {

    /**
     * 生成 MD5
     */
    public synchronized static String createMD5(String s) {
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            digester.update(s.getBytes());
            byte[] digests = digester.digest();
            StringBuilder sb = new StringBuilder();
            for (byte digest : digests) {
                if (Integer.toHexString(0xff & digest).length() == 1) {
                    sb.append("0").append(Integer.toHexString(0xff & digest));
                } else {
                    sb.append(Integer.toHexString(0xff & digest));
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Timber.e("createMD5EEE:%s", e.getLocalizedMessage());
        }
        throw new SecurityException();
    }


    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e',
            'f'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
