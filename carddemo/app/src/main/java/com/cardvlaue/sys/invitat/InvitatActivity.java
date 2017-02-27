package com.cardvlaue.sys.invitat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.data.source.remote.UrlConstants;
import com.cardvlaue.sys.util.ToastUtil;
import com.cardvlaue.sys.webshow.WebShowActivity;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * <p>邀请人  分享的<p/>
 */
public class InvitatActivity extends BaseActivity implements UMShareListener {

    final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN,
        SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
        SHARE_MEDIA.SINA};//,SHARE_MEDIA.TENCENT
    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView;
    private TextView mTitleTextRightView;
    private IFinancingRest rest;//获取邀请记录
    private TasksRepository repository;
    private InvitatRest invitatRest;
    private String objectId, token;
    private InviteHistoryAdapter mAdapter;
    private ListView listview;
    private String Phone;
    private String title;
    private String describe;
    private String link;
    private String imgUrl;
    private String showImgUrl;
    private UMImage image;
    private UMShareAPI mShareAPI;
    private Context context = InvitatActivity.this;
    private Button mShareBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitat);
        initView();
    }


    public void initView() {
        repository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        rest = HttpConfig.getClient().create(IFinancingRest.class);

        invitatRest = HttpConfig.getClientWeiXin().create(InvitatRest.class);
        invitatRest.getLatestShare(repository.getMobilePhone(), "weixin")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(shareLatest -> {
                switch (shareLatest.responseSuccess(InvitatActivity.this)) {
                    case 0:
                        if (shareLatest.getResultCode().equals("1")) {
                            ShareLatestItem item = JSON
                                .parseObject(shareLatest.getResultData(), ShareLatestItem.class);
                            title = item.getTitle();
                            describe = item.getDescribe();
                            link = item.getLink();
                            imgUrl = item.getImgUrl();
                            showImgUrl = item.getShowImgUrl();
                            image = new UMImage(InvitatActivity.this, imgUrl);//image  showImgUrl
                        }
                        Timber.e(JSON.toJSONString(shareLatest) + "=分享==");
                        break;
                    case -1:
                        ToastUtil.showFailure(this, shareLatest.getError());
                        break;
                }
            }, throwable -> {
                Timber.e("queryApplyState:%s", throwable.getMessage());
            });
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("授权中");
        dialog.setMessage("正在加载···");
        Config.dialog = dialog;

        mShareAPI = UMShareAPI.get(this);
        mShareBt = (Button) findViewById(R.id.btn_shread);
        Timber.e("showImgUrl:" + showImgUrl);
        mShareBt.setOnClickListener(v -> {
            new ShareAction(InvitatActivity.this).setDisplayList(displaylist)
                .withText(title)
                // app内容
                //.withTitle(title)
                .withTargetUrl(link)// 标题加 链接
                .withMedia(image).setCallback((UMShareListener) InvitatActivity.this)
                .open();
        });
        listview = (ListView) findViewById(R.id.listview);
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mBackView = (TextView) findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mTitleTextRightView = (TextView) findViewById(R.id.title_default_right);
        setSupportActionBar(mToolbarView);
        mTitleTextView.setTextColor(Color.parseColor("#ffffff"));
        mTitleTextRightView.setTextColor(Color.parseColor("#ffffff"));
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_back, 0, 0, 0);
        mTitleTextView.setText(getString(R.string.invited_record));
        mTitleTextRightView.setText(getString(R.string.rule));

        mBackView.setOnClickListener(v -> finish());

        mTitleTextRightView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra(WebShowActivity.EXTRA_TITLE, getString(R.string.envelope_rules));
            intent.putExtra(WebShowActivity.EXTRA_URL, UrlConstants.CREDIT_RULE);
            intent.setClass(InvitatActivity.this, WebShowActivity.class);
            startActivity(intent);
        });
        mAdapter = new InviteHistoryAdapter(InvitatActivity.this);
        listview.setAdapter(mAdapter);

        if (!TextUtils.isEmpty(repository.getMobilePhone())) {
            //获取邀请记录
            getInviteHistory();
        }

    }


    public void getInviteHistory() {
        LoginResponse loginResponse = repository.getLogin();
        objectId = loginResponse.objectId;
        token = loginResponse.accessToken;

        rest.getInviteHistory(objectId, token, objectId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(invitat -> {
                switch (invitat.responseSuccess(InvitatActivity.this)) {
                    case 0:
                        Timber.e(JSON.toJSONString(invitat) + "===");
                        ((TextView) findViewById(R.id.yuan)).setText(
                            invitat.getAmount().toString().equals("") ? "0.00"
                                : invitat.getAmount().toString());
                        ((TextView) findViewById(R.id.inviteCount))
                            .setText("成功邀请：" + invitat.getInviteCount().toString() + "人");
                        Timber.e(JSON.toJSONString(invitat) + "==1111=" + invitat.getInvitees()
                            .size());
                        if (invitat.getInvitees().size() == 0) {
                            ((RelativeLayout) findViewById(R.id.ry_person))
                                .setVisibility(View.VISIBLE);
                            listview.setVisibility(View.GONE);
                            return;
                        } else {
                            ((RelativeLayout) findViewById(R.id.ry_person))
                                .setVisibility(View.GONE);
                            listview.setVisibility(View.VISIBLE);
                            mAdapter.update(invitat.getInvitees());
                        }
                        break;
                }
            }, throwable -> {
                throwable.printStackTrace();
                ToastUtil.showFailure(InvitatActivity.this, "请稍后再试");
            });
    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {
        Toast.makeText(this, "onSucced 成功", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        Toast.makeText(this, "onError 失败", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {
        Toast.makeText(this, "onCancel 取消", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
