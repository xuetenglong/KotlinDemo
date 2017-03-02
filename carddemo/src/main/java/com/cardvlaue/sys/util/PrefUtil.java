package com.cardvlaue.sys.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.cardvlaue.sys.CVApplication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>配置文件<p/> Created by cardvalue on 2016/4/8.
 */
public class PrefUtil {

    private static void putString(Context context, String fileName, String keyName,
        String valueName) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(keyName, valueName);
        editor.apply();
    }

    private static String getString(Context context, String fileName, String keyName) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getString(keyName, "");
    }

    public static void putString(Context context, String keyName, String valueName) {
        putString(context, CVApplication.APP_FILE_NAME, keyName, valueName);
    }

    public static String getString(Context context, String keyName) {
        return getString(context, CVApplication.APP_FILE_NAME, keyName);
    }


    /**
     * 保存List<Map<String, String>>数据
     */
    public static void saveInfo(Context context, String key, List<Map<String, Object>> datas) {
        JSONArray mJsonArray = new JSONArray();
        for (int i = 0; i < datas.size(); i++) {
            Map<String, Object> itemMap = datas.get(i);
            Iterator<Map.Entry<String, Object>> iterator = itemMap.entrySet().iterator();
            JSONObject object = new JSONObject();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                try {
                    object.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                }
            }
            mJsonArray.put(object);
        }
        SharedPreferences sp = context.getSharedPreferences("finals", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, mJsonArray.toString());
        editor.commit();
    }


    public static List<Map<String, Object>> getInfo(Context context, String key) {
        List<Map<String, Object>> datas = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences("finals", Context.MODE_PRIVATE);
        String result = sp.getString(key, "");
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                Map<String, Object> itemMap = new HashMap<>();
                JSONArray names = itemObject.names();
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        String value = itemObject.getString(name);
                        itemMap.put(name, value);
                    }
                }
                datas.add(itemMap);
            }
        } catch (JSONException e) {
        }
        return datas;
    }

}
