package com.cardvlaue.sys.financeintention;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ScreenUtil;

/**
 * 加号
 */
public class AddView extends View {

    private Paint mPaint;

    public AddView(Context context) {
        super(context);
        initData();
    }

    public AddView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public AddView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AddView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initData();
    }

    private void initData() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.app_blue));
        mPaint.setStrokeWidth(ScreenUtil.dp2px(getContext(), 1.0f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), mPaint);
        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, mPaint);
    }

}
