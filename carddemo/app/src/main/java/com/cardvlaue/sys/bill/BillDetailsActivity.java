package com.cardvlaue.sys.bill;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.R;

public class BillDetailsActivity extends BaseActivity {

    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);
        initView();
    }

    public void initView() {
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mBackView = (TextView) findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mToolbarView.setBackgroundResource(R.color.white);
        mTitleTextView.setTextColor(Color.parseColor("#343434"));
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_black, 0, 0, 0);
        mTitleTextView.setText("账单详情");
    }
}
