package com.cardvlaue.sys.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import com.cardvlaue.sys.CVApplication;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import net.glxn.qrgen.android.QRCode;
import okio.BufferedSink;
import okio.Okio;
import timber.log.Timber;

/**
 * 图片处理
 * <p/>
 * Created by cardvalue on 2016/6/15.
 */
public class ImgUtil {

    /**
     * 支付宝二维码图片是否存在
     */
    public static boolean isQrImageExists() {
        if (!Environment.isExternalStorageEmulated()) {
            return false;
        } else {
            File appDir = new File(Environment.getExternalStorageDirectory(),
                CVApplication.APP_FILE_NAME + "/cv-qr-code.jpg");
            Timber.i("isQrImageExists:%s", appDir.getAbsolutePath());
            return appDir.exists();
        }
    }

    /**
     * 保存二维码图片
     */
    public static void saveQrImage(Context context, String msg) {
        if (!Environment.isExternalStorageEmulated()) {
            ToastUtil.showFailure(context, "存储卡不可用");
        } else {
            File appDir = new File(Environment.getExternalStorageDirectory(),
                CVApplication.APP_FILE_NAME);
            boolean isDirectoryCreated = appDir.exists();
            if (!isDirectoryCreated) {
                isDirectoryCreated = appDir.mkdir();
            }
            if (isDirectoryCreated) {
                String qrName = "cv-qr-code.jpg";
                File qrDir = new File(appDir, qrName);
                Timber.i("二维码：%s", qrDir);
                Bitmap qrBitmap = QRCode.from(msg).withSize(500, 500).bitmap();
                try {
                    BufferedSink sink = Okio.buffer(Okio.sink(qrDir));
                    qrBitmap.compress(Bitmap.CompressFormat.JPEG, 100, sink.outputStream());
                    sink.flush();
                    sink.close();
                } catch (IOException e) {
                    Timber.e(e.getMessage());
                } finally {
                    qrBitmap.recycle();
                }
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + qrDir.getAbsolutePath())));
            }
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
        int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * 压缩图片生成Bitmap
     */
    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeResource(res, resId, options);
        return BitmapFactory.decodeFile(pathName, options);
    }

    /**
     * 图片转string
     */
    public static String bitmapToString(Bitmap bitmap) {
//        Bitmap bitmap = decodeSampledBitmapFromResource(pathName, 480, 800);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    /**
     * base64字符串转化成图片
     */
    public static boolean GenerateImage(String imgStr, String path) {
        if (imgStr == null) // 图像数据为空
        {
            return false;
        }
        try {
            // Base64解码
            byte[] b = Base64.decode(imgStr, Base64.NO_WRAP);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            // 生成jpeg图片 System.currentTimeMillis()
            OutputStream out = new FileOutputStream(path);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        return Bitmap
            .createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
