package com.cardvlaue.sys.applyinfo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.TextView;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.main.MainActivity;

/**
 * <>申请已提交</>
 */
public class AppSubmitActivity extends BaseActivity {

    private Toolbar mToolbarView;
    private TextView mTitleTextView;
    private TextView mTitleTextRightView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_submit);
        initView();
    }

    public void initView() {
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mTitleTextRightView = (TextView) findViewById(R.id.title_default_right);
        mToolbarView.setBackgroundResource(R.color.white);
        mTitleTextView.setTextColor(Color.parseColor("#343434"));
        mTitleTextRightView.setTextColor(Color.parseColor("#359DF5"));
        mTitleTextView.setText(getString(R.string.apply_submitted));
        mTitleTextRightView.setText(getString(R.string.tv_complete));
        mTitleTextRightView.setOnClickListener(v -> {
            Intent intent = new Intent(AppSubmitActivity.this, MainActivity.class);
            intent.putExtra("apply", "011111");
            startActivity(intent);
            AppSubmitActivity.this.finish();
          /*  BusIndustrySelect select = new BusIndustrySelect(ApplyFragment.BUS_APPLY_CODE);
            select.setTypeId("apply");
            RxBus.getDefaultBus().send(select);*/
        });
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
