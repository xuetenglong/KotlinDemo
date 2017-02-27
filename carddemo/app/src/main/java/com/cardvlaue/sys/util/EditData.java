package com.cardvlaue.sys.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.source.TasksDataSource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

//com.cardvalue.sys.widget.EditData
public class EditData extends EditText {

    private String mpageName;//basiclimit
    private String minputName;//requestAmout
    private String minputLabel;
    private int startdata;
    private int enddata;
    private int str1;
    private int str2;
    private Context context;
    private ArrayMap<String, Object> datas = new ArrayMap<>();
    private boolean textChang = false;
    private Date d;
    private Date d1;
    private String mobilePhone = "";

    public EditData(Context context) {
        super(context);
    }

    public EditData(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EditData(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TasksDataSource mTasksRepository = ((CVApplication) context.getApplicationContext())
            .getTasksRepositoryComponent().getTasksRepository();
        if (mTasksRepository != null) {
            mobilePhone = mTasksRepository.getMobilePhone();
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextData);
        mpageName = typedArray.getString(R.styleable.TextData_pageName);
        minputName = typedArray.getString(R.styleable.TextData_inputName);
        minputLabel = typedArray.getString(R.styleable.TextData_inputLabel);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                str2 = s.length();
                Timber.e("str2" + str2 + "====str1==" + str1);
                if (s.length() != str1) {
                    textChang = true;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * 焦点改变
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused) {
            startdata = (int) (new Date().getTime()) / 1000;
            Timber.e("startdata:" + startdata + "获得焦点");
            Date date = new Date();
            Long time = date.getTime();
            d = new Date(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            str1 = EditData.this.getText().toString().trim().length();
            Timber.e(str1 + "==str1===");
        } else if (!focused) {
            enddata = (int) (new Date().getTime()) / 1000;
            Timber.e("enddata离开焦点:" + enddata + "textChang：");
            Date date1 = new Date();
            Long time1 = date1.getTime();
            d1 = new Date(time1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (textChang) {
                datas.clear();
                datas.put("pageName", mpageName);//页面名字   登录
                datas.put("inputName", minputName);//控件名字 mobilePhone
                datas.put("inputLabel", minputLabel);//便签  手机号码
                datas.put("onFocusTime", sdf.format(d));
                datas.put("onBlurTime", sdf.format(d1));
                datas.put("userMobile", mobilePhone);
                textChang = false;
            }

            if (datas != null && datas.size() > 0) {
                addRecord(datas);
            }
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    public void addRecord(Map<String, Object> map) {
        List<Map<String, Object>> list;
        if (PrefUtil.getInfo(context, "finals").size() > 0) {
            list = PrefUtil.getInfo(context, "finals");
        } else {
            list = new ArrayList<>();
        }
        list.add(map);
        Timber.e("list行为数据分析:%s", list.toString());
        if (list.size() > 0) {
            PrefUtil.saveInfo(context, "finals", list);
        }
      /*  list.add(map);
        PrefUtil.saveInfo(context,"finals",list);
        Log.e("list:",list.toString());*/
    }

    public void setMinputName(String minputName) {
        this.minputName = minputName;
    }

}
