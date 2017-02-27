package com.cardvlaue.sys.feedback;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.source.TasksDataSource;
import com.cardvlaue.sys.registerverify.RegisterOkDialog;
import com.cardvlaue.sys.util.CheckUtil;
import com.cardvlaue.sys.util.DeviceUtil;
import com.cardvlaue.sys.util.MyGridViewUtil;
import com.cardvlaue.sys.util.ReadUtil;
import com.cardvlaue.sys.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

/**
 * 意见反馈 <p> Created by Administrator on 2016/8/22.
 */
public class FeedBackOneActivity extends BaseActivity implements OnItemClickListener {

    private static final int PERMISSIONS_REQUEST_CAMERA = 0x1211;
    private static final String PROG_DIALOG_TAG = "tag";
    public EditText ed_feekback;      // 反馈内容
    public EditText mContract;               // 手机号
    public Button submit;             // 提交按钮
    private TasksDataSource mTasksRepository;
    private Disposable mDisposable;
    private ArrayList<Bitmap> aList;      // 图片的存放的集合
    private FeedbackAdapter adapter;
    private Context context;
    private String picturePath;
    private Bitmap bmp;
    private List<String> urlLists;          // 存放文件列表
    private boolean mShowDialog = false;
    private RegisterOkDialog registerOkDialog;
    private String content = "请先进入系统设置->应用->小企额->权限,开启所有权限后,再进行上传操作!";
    private String dialogButton = "我知道了，马上开启权限";

    public static byte[] fileBtmip(String fileName) {
        File file = new File(fileName);
        FileInputStream inputFile = null;
        byte[] buffer = {};
        byte[] bf = {};
        byte[] byteArray = {};
        try {
            inputFile = new FileInputStream(file);
            buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;
            Bitmap bitmap2 = BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
            ByteArrayOutputStream streams = new ByteArrayOutputStream();
            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, streams);
            byteArray = streams.toByteArray();
            bitmap2.recycle();
            bf = Base64.encode(byteArray, Base64.NO_WRAP);
            inputFile.close();
            return bf;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "".getBytes();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_feed_back);
        mTasksRepository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();

        String str = DecimalFormat.getNumberInstance().format(1245600000);

        String currecy = NumberFormat.getCurrencyInstance().format(1245600000);

        System.out.println("转换成Currency格式：" + currecy);

        System.out.println("转换成带千分位的格式：" + str);

        //DecimalFormat df = new DecimalFormat("0.00");

        Timber.e(currecy + "========currecy");
        Timber.e(str + "========str");

        Timber.e(fmtMicrometer("0.8888") + "========str1");
        Timber.e(fmtMicrometer("我们杰") + "========str2");
        Timber.e(fmtMicrometer("1000.32") + "========str1");
        Timber.e(fmtMicrometer("10000.32") + "========str2");
        Timber.e(fmtMicrometer("100000.32") + "========str1");
        Timber.e(fmtMicrometer("100000000000.32") + "========str2");

        initView();
        CustomTextWatcher();
    }

    public void CustomTextWatcher() {
        ed_feekback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() > 0) {
                    ed_feekback.setGravity(Gravity.START);
                } else {
                    ed_feekback.setGravity(Gravity.CENTER);
                }
                if (s.length() > 0 && mContract.length() > 0) {
                    submit.setBackgroundResource(R.drawable.selector_button_rectangle);
                } else {
                    submit.setBackgroundResource(R.drawable.selector_disabled_button);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mContract.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() > 0 && ed_feekback.length() > 0) {
                    submit.setBackgroundResource(R.drawable.selector_button_rectangle);
                } else {
                    submit.setBackgroundResource(R.drawable.selector_disabled_button);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    /**
     * GridView布局
     */
    private void initView() {
        TextView mBackView = (TextView) findViewById(R.id.title_default_left);
        TextView mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_back, 0, 0, 0);
        mTitleTextView.setText(getString(R.string.more_feedback));
        mBackView.setOnClickListener(v -> finish());
        // 图片的Gridview 布局
        MyGridViewUtil gridView = (MyGridViewUtil) findViewById(R.id.grid);
        bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.add);
        aList = new ArrayList<>();

        aList.add(bmp);
        adapter = new FeedbackAdapter(this, aList);
        gridView.setAdapter(adapter);
        context = FeedBackOneActivity.this;

        gridView.setOnItemClickListener(this);

        ed_feekback = (EditText) findViewById(R.id.ed_feekback);
        mContract = (EditText) findViewById(R.id.ed_feedback_contract);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(view -> {
            String content = ed_feekback.getText().toString();
            String mobilePhone = mContract.getText().toString();
            if (TextUtils.isEmpty(content)) {
                ToastUtil.showFailure(getApplicationContext(), "反馈的类容不能为空");
                return;
            }
            if (TextUtils.isEmpty(mobilePhone)) {
                ToastUtil.showFailure(getApplicationContext(), "联系方式不能为空");
                return;
            }
            if (!CheckUtil.isMobilePhone(mobilePhone)) {
                ToastUtil.showFailure(getApplicationContext(), "错误的手机号");
                return;
            }
            //上传
            List<Map<String, Object>> img = new ArrayList<>();
            List<String> imgpics = urlLists;
            if (imgpics != null) {
                for (int i = 0; i < imgpics.size(); i++) {
                    JSONObject temp = new JSONObject();
                    temp.put("data", new String(FeedBackOneActivity.fileBtmip(imgpics.get(i))));
                    temp.put("type", "jpg");
                    img.add(temp);
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("connection", mobilePhone);
            jsonObject.put("content", content);
            jsonObject.put("img", img);
            mDisposable = mTasksRepository.postFeedback(jsonObject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Timber.e("意见反馈SSS:" + s);
                    finish();
                }, throwable -> {
                    ToastUtil.showFailure(this, "反馈提交失败");
                    Timber.e("意见反馈=throwable=%s", throwable.getMessage());
                });
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
        long id) {
        if (position == aList.size() - 1) {
            new AlertDialog.Builder(context)
                .setItems(new String[]{"画册添加", "拍照添加"},
                    (dialog, which) -> {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, 1);
                                break;
                            case 1:
                                if (ContextCompat
                                    .checkSelfPermission(this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                    //申请CAMERA权限
                                    requestPermissions(new String[]{
                                        Manifest.permission.CAMERA
                                    }, PERMISSIONS_REQUEST_CAMERA);
                                    return;
                                }
                                Intent intent1 = new Intent();
                                intent1.setAction("android.media.action.IMAGE_CAPTURE");
                                intent1.addCategory("android.intent.category.DEFAULT");
                                // TODO 创建照片存储路径，方便调用
                                picturePath = "/mnt/sdcard/DCIM/pic"
                                    + DateFormat.format("kkmmss",
                                    new Date()).toString()
                                    + ".jpg";
                                File file = new File(picturePath);
                                Uri uri = Uri.fromFile(file);
                                intent1.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                startActivityForResult(intent1, 2);
                                break;
                        }
                    }).create().show();
        }
    }

    // 点击加号后执行
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 0 && resultCode == RESULT_OK && null != data) {
            if (this.urlLists == null) {
                urlLists = new ArrayList<>();
            }
            switch (requestCode) {
                case 1:// 相册
                    Uri selectedImage = data.getData();

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Log.i("info", filePathColumn[0] + "");
                    Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    urlLists.add(picturePath);
                    cursor.close();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(picturePath)) {
            Bitmap addbmp = getBitmapFromFile(picturePath, 500, 600);
            aList.add(addbmp);
            // 先移除用来添加的图标，再添加以保证添加的图片始终在最后
            aList.remove(bmp);
            aList.add(bmp);
        }
        adapter.setDate(aList);
        adapter.notifyDataSetChanged();
        // 刷新后释放，防止手机休眠后自动添加
        picturePath = null;
    }

    public Bitmap getBitmapFromFile(String dst, int width, int height) {
        if (null != dst) {
            BitmapFactory.Options opts = null;
            if (width > 0 && height > 0) {
                opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(dst, opts);
                // 计算图片缩放比例
                final int minSideLength = Math.min(width, height);
                opts.inSampleSize = ReadUtil.computeSampleSize(opts, minSideLength, width * height);
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
            }
            try {
                return BitmapFactory.decodeFile(dst, opts);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                        //请求打开摄像头权限
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_CAMERA);
                        return;
                    }
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        mShowDialog = true;
                    } else {
                        mShowDialog = true;
                    }
                }
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mShowDialog) {
            mShowDialog = false;
            if (getSupportFragmentManager().findFragmentByTag(PROG_DIALOG_TAG) == null) {
                registerOkDialog = RegisterOkDialog.newInstance(content, dialogButton);
                registerOkDialog.show(getSupportFragmentManager(), "tag");
                registerOkDialog
                    .setOnClickOkListener(
                        () -> startActivity(DeviceUtil.getAppDetailSettingIntent(this)));
            }
        }
    }


   /* public static String fmtMicrometer(String text)
    {
        DecimalFormat df = null;
        if(text.indexOf(".") > 0)
        {
            if(text.length() - text.indexOf(".")-1 == 0)
            {
                df = new DecimalFormat("###,##0.");//0.00
            }else if(text.length() - text.indexOf(".")-1 == 1)
            {
                df = new DecimalFormat("###,##0.0");//0.00
            }else
            {
                df = new DecimalFormat("###,##0.00");//0.00
            }
        }else
        {
            df = new DecimalFormat("###,##0");//0.00  ###,##0
        }
        double number = 0.0;
        try {
            number = Double.parseDouble(text);
        } catch (Exception e) {
            number = 0.0;
        }
        return df.format(number);
    }*/
}


