package com.cardvlaue.sys.confirm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.cardverify.CardPrivateActivity;
import com.cardvlaue.sys.cardverify.CardPublicActivity;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import java.util.ArrayList;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * 获取银行列表
 */
public class ConfirmListActivity extends BaseActivity {

    public static Handler handler;
    List<ConfirmListItem> list = new ArrayList<>();
    private AutoCompleteTextView ed_search;
    private RecyclerView mRecyclerView;
    private ConfirmRest confirmRest;
    private String objectId, token, phone;
    private TasksRepository repository;
    private String type;
    private String tagName;
    private ConfirmAdapter mConfirmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_list);
        initView();
        initData();
    }

    //接受传过来的值
    public void initData() {
        String text = getIntent().getStringExtra("title");
        type = getIntent().getStringExtra("type");
        tagName = getIntent().getStringExtra("tagName");
        ed_search.setText(text);
    }

    public void initView() {
        confirmRest = HttpConfig.getClient().create(ConfirmRest.class);
        repository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        phone = repository.getMobilePhone();

        LoginResponse loginResponse = repository.getLogin();
        objectId = loginResponse.objectId;
        token = loginResponse.accessToken;

        ed_search = (AutoCompleteTextView) findViewById(R.id.ed_search);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mConfirmAdapter = new ConfirmAdapter(ConfirmListActivity.this, "4");
        //  mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mConfirmAdapter);
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() != 0) {
                    Timber.e("请求的文字======" + s.toString());
                    queryCashList(s.toString());
                }
            }
        });

        //点击事件
        mConfirmAdapter.setOnItemClickListenter((v, position) -> {
            ConfirmListItem item = list.get(position);
            Message ms = new Message();
            Bundle bundle = new Bundle();
            if (tagName.equals("directDebitBankName")) {
                ms.what = CardPrivateActivity.CALLBACK_MSG_GETBANK_PRIVATE;
                bundle.putString("title", item.getTitle().toString());
                bundle.putString("id", item.getId() + "");
                Timber.e("选择银行的标题和id===" + item.getTitle().toString() + "====id-=" + item.getId()
                    + "");
                ms.setData(bundle);
                CardPrivateActivity.handler.sendMessage(ms);
            } else if (tagName.equals("secondaryBankName")) {
                ms.what = CardPrivateActivity.CALLBACK_MSG_GETBANK_PRIVATE;
                bundle.putString("title", item.getTitle().toString());
                bundle.putString("id", item.getId() + "");
                Timber.e("选择银行的标题和id===" + item.getTitle().toString() + "====id-=" + item.getId()
                    + "");
                ms.setData(bundle);
                CardPublicActivity.handler.sendMessage(ms);
            } else if (tagName.equals("secondaryBankName1")) {
                ms.what = CardPublicActivity.CALLBACK_MSG_GETBANK_PUBLIC;
                bundle.putString("title", item.getTitle().toString());
                bundle.putString("id", item.getId() + "");
                Timber.e("选择银行的标题和id===" + item.getTitle().toString() + "====id-=" + item.getId()
                    + "");
                ms.setData(bundle);
                CardPublicActivity.handler.sendMessage(ms);
            }
            finish();
        });
        findViewById(R.id.iv_left).setOnClickListener(v -> finish());
    }

    public void queryCashList(String s) {
        JSONObject param = new JSONObject();
        JSONObject temp = new JSONObject();
        param.put("type", type);
        temp.put("$regex", s.toString());
        param.put("bankName", temp);
        Timber.e("参数==" + param.toString());
        confirmRest.queryConfirmList(objectId, token, param.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s1 -> {
                Timber.e(s1 + "申请信息");
                ConfirmList confirmList = JSON.parseObject(s1, ConfirmList.class);
                Timber.i("获取银行======" + JSON.toJSONString(confirmList.getResults()));
                list = confirmList.getResults();
                mConfirmAdapter.update(list);
            }, throwable -> Timber
                .e(throwable.getMessage() + "获取银行列表的throwable==" + JSON.toJSONString(throwable)));
    }
}
