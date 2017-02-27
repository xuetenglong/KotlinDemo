package com.cardvlaue.sys.amount;

/**
 * 把带有小数点的数字 转为整 Created by Administrator on 2016/7/11.
 */

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 转换map list中的数字格式
 *
 * @author weiweina
 */

public class Convert {

    //将Map<String,Object> 中的数值转成字符串并去掉多余小数点
    public static Map<String, Object> convertMap(Map<String, Object> param) {
        Map<String, Object> pm = new HashMap<String, Object>();
        if (param == null) {
            return pm;
        }
        for (String key : param.keySet()) {
            if (param.get(key) == null) {
                pm.put(key, "");
            } else if (param.get(key).getClass().getName().equals("java.lang.Integer")) {
                pm.put(key, subZeroAndDot(String.valueOf(param.get(key))));
            } else if (param.get(key).getClass().getName().equals("java.lang.Double")) {
                Double tmp = (Double) param.get(key);
                pm.put(key, subZeroAndDot(String.valueOf(tmp)));
            } else {
                if (isNumber(param.get(key).toString())) {
                    pm.put(key, subZeroAndDot(param.get(key).toString()));
                } else {
                    pm.put(key, param.get(key));
                }
            }
        }
        return pm;
    }

    public static <T> void convertMap(T cls) {
        Field[] fields = cls.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(cls);
                String temp = convertToString(value);
                field.set(cls, temp);

            } catch (IllegalAccessException | IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //同上 ，作用于list
    public static List<Map<String, Object>> convertListObject(List<Map<String, Object>> param) {
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> tp : param) {
            ret.add(convertMap(tp));
        }
        return ret;
    }


    public static List<Object> convertListString(List<Object> param) {
        List<Object> ret = new ArrayList<Object>();
        if (ret == null && ret.size() == 0) {
            return new ArrayList<Object>();
        }
        for (Object tp : param) {
            ret.add(convertToString(tp));
        }
        return ret;
    }

    public static List<String> convertStringToListString(List<Object> param) {
        List<String> ret = new ArrayList<String>();
        if (ret == null && ret.size() == 0) {
            return new ArrayList<String>();
        }
        for (Object tp : param) {
            ret.add(convertToString(tp).toString());
        }
        return ret;
    }


    public static String convertToString(Object str) {
        if (str.getClass().getName().equals("java.lang.Integer")) {
            return subZeroAndDot(String.valueOf(str));
        } else if (str.getClass().getName().equals("java.lang.Double")) {
            Double tmp = (Double) str;
            return subZeroAndDot(String.valueOf(tmp));
        } else {
            return subZeroAndDot(str.toString());
        }
    }

    public static void print(String msg) {
        System.out.println(msg);
    }

    //去掉多余的0
    private static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    /**
     * 判断字符串是否是整数
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是浮点数
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            if (value.contains(".")) {
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是数字
     */
    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value);
    }


}
