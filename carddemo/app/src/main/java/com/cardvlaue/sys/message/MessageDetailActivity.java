package com.cardvlaue.sys.message;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MessageDetailActivity extends BaseActivity {

    private TextView push_tile;        //标题
    private TextView push_text;        //类容
    private TextView pusp_laiyuan;    //来源
    private TextView push_times;    //时间
    private boolean isFlag = false;
    private String type, readTime, id, title, content, createTime;
    private Toolbar mToolbarView;
    private TextView mBackView;
    private TextView mTitleTextView;
    private MessageRest messageRest;
    private String phone, objectId, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        initData();
    }

    public void initData() {
        type = getIntent().getStringExtra("type");
        id = getIntent().getStringExtra("id");

        messageRest = HttpConfig.getClient().create(MessageRest.class);
        TasksRepository repository = ((CVApplication) getApplication())
            .getTasksRepositoryComponent()
            .getTasksRepository();
        phone = repository.getMobilePhone();

        LoginResponse loginResponse = repository.getLogin();
        objectId = loginResponse.objectId;
        token = loginResponse.accessToken;

        messageRest.getMessageDetail(objectId, token, Integer.parseInt(type), Integer.parseInt(id))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                Timber.e("获取消息详情---" + s);
                Messages messages = JSON.parseObject(s, Messages.class);
                title = messages.getTitle();
                content = messages.getContent();
                createTime = messages.getCreateTime();
                Timber.e("获取消息详情title---" + title + content + createTime);
                initView();
            }, throwable -> throwable.printStackTrace());

    }


    public void initView() {
        mToolbarView = (Toolbar) findViewById(R.id.title_default_toolbar);
        mBackView = (TextView) findViewById(R.id.title_default_left);
        mTitleTextView = (TextView) findViewById(R.id.title_default_middle);
        mBackView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.icon_back, 0, 0, 0);
        mTitleTextView.setText(title);
        mBackView.setOnClickListener(v -> finish());

        push_tile = (TextView) findViewById(R.id.push_tile);
        push_text = (TextView) findViewById(R.id.push_text);
        pusp_laiyuan = (TextView) findViewById(R.id.pusp_laiyuan);
        push_times = (TextView) findViewById(R.id.push_times);
        push_tile.setText(title);
        push_text.setText(content);
        pusp_laiyuan.setText("小企额");
        push_times.setText(createTime);
    }

}
