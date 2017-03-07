package com.example.xuetenglong.kotlindemo.view;

import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import com.example.xuetenglong.kotlindemo.Px2Dpi;

/**
 * Created by xuetenglong on 2017/3/2.
 */

public class EllipsisEditText extends EditText implements View.OnFocusChangeListener {

    private String oldStr;

    public EllipsisEditText(Context context) {
        super(context);
        this.setOnFocusChangeListener(this);
        oldStr = getText()!=null?getText().toString():"";
    }

    public EllipsisEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnFocusChangeListener(this);
        oldStr = getText()!=null?getText().toString():"";
    }

    public EllipsisEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnFocusChangeListener(this);
        oldStr = getText()!=null?getText().toString():"";
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EllipsisEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            setText(oldStr);
        }else{
            oldStr = getText()!=null?getText().toString():"";
            if(getCharacterWidth(oldStr,getTextSize())>getWidth()){
                setText(getEllipsisStr(oldStr));
            }
        }
    }


    private int getCharacterWidth(String text, float size) {
        if (null == text || "".equals(text)){
            return 0;
        }
        Paint paint = new Paint();
        paint.setTextSize(size);
        int text_width = (int) paint.measureText(text);// 得到总体长度
        return text_width;
    }

    private String getEllipsisStr(String text){
        String total = "";
        for(int i=0;i<text.length();i++){
            total =  text.substring(0,i);
            if(getCharacterWidth(total, getTextSize())>getWidth()){
                break;
            }
        }
        total = total.substring(0,total.length()-3)+"...";
        return total;
    }




}
