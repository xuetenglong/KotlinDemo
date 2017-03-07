package com.example.xuetenglong.kotlindemo;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private EditText edit;
    private LinearLayout linearLayout;
    String str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text1);
        edit = (EditText) findViewById(R.id.edit);
        linearLayout = (LinearLayout) findViewById(R.id.activity_main);

        linearLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                linearLayout.setFocusable(true);
                linearLayout.setFocusableInTouchMode(true);
                linearLayout.requestFocus();
                return false;
            }
        });

        getExternalCacheDir();
        getCacheDir();





//        edit.setEllipsize(TextUtils.TruncateAt.END);
//        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus){
//                    edit.setText(str);
//                }else{
//                    str = edit.getText()!=null?edit.getText().toString():"";
//                    if(getCharacterWidth(str,edit.getTextSize())>edit.getWidth()){
//                        edit.setText(getEllipsisStr(str));
//                    }
//                }
//            }
//        });


        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edit.setEllipsize(null);
                }else{
                    edit.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });




    }

    public int getCharacterWidth(String text, float size) {
        if (null == text || "".equals(text)){
            return 0;
        }
        Paint paint = new Paint();
        paint.setTextSize(size);
        int text_width = (int) paint.measureText(text);// 得到总体长度
        return text_width;
    }


    public String getEllipsisStr(String text){
        String total = "";
        for(int i=0;i<text.length();i++){
            total =  text.substring(0,i);
            float px = Px2Dpi.convertDpToPixel(this,15);
            if(getCharacterWidth(total, px)>edit.getWidth()){
                break;
            }
        }
        total = total.substring(0,total.length()-3)+"...";
        return total;

    }



    public void onClick(View view){
         startActivity(new Intent(this,Main2Activity.class));
    }
}
