package com.cardvlaue.sys.qrcode;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.facebook.drawee.view.SimpleDraweeView;

public class QRCodeActivity extends BaseActivity {

    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView;
    private SimpleDraweeView mQrView;
    private String imgUrl;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        initWindow();
    }

    private void initWindow() {
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mBackView = (TextView) findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mQrView = (SimpleDraweeView) findViewById(R.id.sdv_qr_image);
        setSupportActionBar(mToolbarView);
        mBackView.setOnClickListener(v -> finish());

        TasksRepository repository = ((CVApplication) getApplication())
            .getTasksRepositoryComponent()
            .getTasksRepository();
        phone = repository.getMobilePhone();

        imgUrl = repository.getUserInfo().qrcodeUrl;

        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_back, 0, 0, 0);
        mTitleTextView.setText(getString(R.string.account_qr_name));
        if (!TextUtils.isEmpty(imgUrl)) {
            mQrView.setImageURI(Uri.parse(imgUrl));
        }
    }
}
