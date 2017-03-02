package com.cardvlaue.sys.changepassword;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.cardverify.CheckingForm;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.login.LoginActivity;
import com.cardvlaue.sys.util.SafeUtil;
import com.cardvlaue.sys.util.ToastUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * <p>修改密码<p/>
 */
public class ChangePasswordActivity extends BaseActivity {

    private IFinancingRest rest;
    private TasksRepository repository;
    private String objectId, token;

    private EditText mCurPwd;//旧密码
    private EditText mNewPwd;//新密码
    private EditText mNewPwd1;//新确认密码

    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
    }

    public void initView() {
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mBackView = (TextView) findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        setSupportActionBar(mToolbarView);
        mTitleTextView.setTextColor(Color.parseColor("#ffffff"));
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_back, 0, 0, 0);
        mTitleTextView.setText(getString(R.string.user_pwd));
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        repository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        mCurPwd = (EditText) findViewById(R.id.curPwd);
        mNewPwd = (EditText) findViewById(R.id.newPwd);
        mNewPwd1 = (EditText) findViewById(R.id.newPwd1);
        mBackView.setOnClickListener(v -> ChangePasswordActivity.this.finish());

        LoginResponse loginResponse = repository.getLogin();
        objectId = loginResponse.objectId;
        token = loginResponse.accessToken;

        findViewById(R.id.btn_chang_pwd_commit).setOnClickListener(v -> {
            String pwdnew = mNewPwd.getText().toString().trim();    //新密码
            String confirmpwd = mNewPwd1.getText().toString();        //新密码的确认密码
            if (pwdnew.length() < 6 || confirmpwd.length() < 6) {
                ToastUtil.showFailure(ChangePasswordActivity.this, "不能少于6位字符");
                return;
            }
            if (!CheckingForm
                .checkForChangepwdForm(mCurPwd.getText().toString(),
                    mNewPwd.getText().toString().trim(),
                    mNewPwd1.getText().toString())) {
                ToastUtil.showFailure(ChangePasswordActivity.this, CheckingForm.LastError);
                return;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("oldPassword", SafeUtil.MD5(mCurPwd.getText().toString().trim()));//旧密码
            jsonObject.put("newPassword", SafeUtil.MD5(mNewPwd1.getText().toString().trim()));//新密码
            rest.changPwd(objectId, token, objectId, jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(errorResponse -> {
                    if (TextUtils.isEmpty(errorResponse.getError())) {
                        ToastUtil.showSuccess(ChangePasswordActivity.this, "修改成功");
                        startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                    } else {
                        ToastUtil
                            .showFailure(ChangePasswordActivity.this, errorResponse.getError());
                    }
                }, throwable ->
                    Timber.e("修改密码E" + throwable.getMessage() + "==throwable=="));
        });

    }
}
