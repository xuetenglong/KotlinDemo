package com.example.xuetenglong.kotlindemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.xuetenglong.kotlindemo.R;


/**
 * Created by shu.xinghu on 2016/3/29.
 */
public class SawtoothView extends View {
    private int size = 36;
    private int width = 20;
    private Paint paint = new Paint();

    public SawtoothView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(getResources().getColor(R.color.colorAccent));
        //消除锯齿
        paint.setAntiAlias(true);
        paint.setStrokeWidth(0);
        paint.clearShadowLayer();
        //设置镂空（方便查看效果）
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int halfHeight = getHeight() >> 1;
        for (int i = 0; i <= getWidth(); i += size) {
            int j = i/size;
            Path path = new Path();
            path.moveTo(j*(size+width), halfHeight);
            path.addArc(new RectF(j*(size+width), (getHeight() - 20) / 2, j*(size+width)+size, (getHeight() + 20) / 2), 0, 180);
            path.moveTo((j+1)*size+j*width, halfHeight);
            path.lineTo((j+1)*(width+size), halfHeight);
            path.lineTo((j+1)*(width+size), 0);
            path.lineTo(j*(width+size), 0);
            path.lineTo(j*(width+size), halfHeight);
            canvas.drawPath(path, paint);
        }

    }
}
