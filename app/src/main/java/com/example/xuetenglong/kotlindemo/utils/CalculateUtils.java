package com.example.xuetenglong.kotlindemo.utils;

/**
 * Created by xuetenglong on 2017/3/6.
 */

public class CalculateUtils {

    static {
        System.loadLibrary("calculate_jni");
    }
    public native String getStringFromNative(String str);//本地方法

}
