package com.cardvlaue.sys.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.ContactsContract;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * <p>写文件和读取文件<p/> Created by Administrator on 2016/8/10.
 */
public class ReadUtil {

    //文件的目录和路径
    private final static String PATH = "/sdcard/digu";
    private final static String FILENAME = "/notes.txt";

    /**
     * 向sdcard写文件 写文件
     */
    public static void write(String str) {
        try {
            //1.判断是否存在sdcard
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                //目录
                File path = new File(PATH);
                //文件
                File f = new File(PATH + FILENAME);
                if (!path.exists()) {
                    //2.创建目录，可以在应用启动的时候创建
                    path.mkdirs();
                }
                if (!f.exists()) {
                    //3.创建文件
                    f.createNewFile();
                } else if (f.exists()) {
                    f.delete();
                    f.createNewFile();
                }
                OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(f));
                //4.写文件，从EditView获得文本值
                osw.write(str);
                osw.close();
            }
        } catch (Exception e) {
        }
    }

    /*
     * 读文件
     */
    public static String read() {
        //文件
        File f = new File(PATH + FILENAME);
        if (!f.exists()) {
            return "";
        }
        FileInputStream is = null;
        try {
            is = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] array = new byte[1024];
            int len;//int len=-1;
            while ((len = is.read(array)) != -1) {
                bos.write(array, 0, len);
                bos.close();
                return bos.toString();
            }
        } catch (IOException e) {
            // e.printStackTrace();
            return "";
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 获取应用市场的名称
     */
    public static String readKey(Context context, String keyName) {
        try {
            return context.getPackageManager().getApplicationInfo(
                context.getPackageName(), PackageManager.GET_META_DATA)
                .metaData.getString(keyName);
        } catch (PackageManager.NameNotFoundException e) {
            return "Android";
        }
    }

    /**
     * 获取Email
     */
    public static void putEmail(ContentResolver contentResolver, Map<String, Object> map,
        StringBuilder sb, String contactId) {
        //查询Email类型的数据操作
        Cursor emails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,
            null, null);
        if (emails == null) {
            return;
        }
        String moveTo = "" + emails.moveToNext();
        if (moveTo.equals("false")) {
            map.put("email", "");
        }
        while (emails.moveToNext()) {
            String emailAddress = emails.getString(emails.getColumnIndex(
                ContactsContract.CommonDataKinds.Email.DATA));

            //添加Email的信息
            map.put("email", emailAddress);

            sb.append(",Email=").append(emailAddress);
        }
        emails.close();
    }

    /**
     * 获取Adress
     */
    public static void putAdress(ContentResolver contentResolver, Map<String, Object> map,
        StringBuilder sb, String contactId) {

        //查询==地址==类型的数据操作.StructuredPostal.TYPE_WORK
        Cursor address = contentResolver
            .query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = " + contactId,
                null, null);
        if (address == null) {
            return;
        }
        String moveToAddress = address.moveToNext() + "";
        if (moveToAddress.equals("false")) {
            map.put("address", "");
        }
        while (address.moveToNext()) {
            String workAddress = address.getString(address.getColumnIndex(
                ContactsContract.CommonDataKinds.StructuredPostal.DATA));
            //添加Email的信息
            sb.append(",address").append(workAddress);
            map.put("address", workAddress);
        }
        address.close();
    }

    /**
     * 获取Company(公司)
     */
    public static void putCompany(ContentResolver contentResolver, Map<String, Object> map,
        StringBuilder sb, String contactId, String id) {
        //查询==公司名字==类型的数据操作.Organization.COMPANY  ContactsContract.Data.CONTENT_URI
        String orgWhere =
            ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE
                + " = ?";
        String[] orgWhereParams = new String[]{id,
            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
        Cursor orgCur = contentResolver.query(ContactsContract.Data.CONTENT_URI,
            null, orgWhere, orgWhereParams, null);
        if (orgCur == null) {
            return;
        }
        String moveToorgCur = orgCur.moveToNext() + "";
        if (moveToorgCur.equals("false")) {
            map.put("company", "");
        }
        if (orgCur.moveToFirst()) {
            //组织名 (公司名字)
            String company = orgCur
                .getString(
                    orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
            //职位
            String title = orgCur
                .getString(
                    orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
            sb.append(",company").append(company);
            sb.append(",title").append(title);
            map.put("company", company);
            //map.put("title", title);
        }
        orgCur.close();
    }

    /**
     * 压缩图片
     */
    public static int computeSampleSize(BitmapFactory.Options options,
        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
            maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
        int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
            .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
            .floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }


    /**
     * 格式化数字为千分位显示 要格式化的数字
     */
    public static String fmtMicrometer(String text) {
        DecimalFormat df = null;
        if (text.indexOf(".") > 0) {
            if (text.length() - text.indexOf(".") - 1 == 0) {
                df = new DecimalFormat("###,##0.00");//0.00
            } else if (text.length() - text.indexOf(".") - 1 == 1) {
                df = new DecimalFormat("###,##0.00");//0.00
            } else {
                df = new DecimalFormat("###,##0.00");//0.00
            }
        } else {
            df = new DecimalFormat("###,##0.00");//0.00  ###,##0
        }
        double number = 0.00;
        try {
            number = Double.parseDouble(text);
        } catch (Exception e) {
            number = 0.00;
        }
        return df.format(number);
    }


    /**
     * 第一个参数s1 中是否包含s2
     */
    public static boolean isContain(String s1) {
        return s1.contains(".00");
    }
}
