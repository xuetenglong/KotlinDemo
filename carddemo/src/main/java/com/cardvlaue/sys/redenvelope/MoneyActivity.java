package com.cardvlaue.sys.redenvelope;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.data.source.remote.UrlConstants;
import com.cardvlaue.sys.my.MyFragment;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.ToastUtil;
import com.cardvlaue.sys.webshow.WebShowActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MoneyActivity extends BaseActivity {

    public MoneyAdapter mCouponCashAdapter;
    public ListView mListView;
    private String phone, objectId, token;
    private IFinancingRest rest;
    private List<CouponBO> couponBOs = new ArrayList<>();
    private TextView mBackView;
    private TextView mTitleTextRightView;
    private TextView mContent;//选择红包的个数


    private int count = 0;
    private int count1 = 0;
    private List<String> typeId = new ArrayList<>();
    private List<String> amount = new ArrayList<>();
    private String loanAmount;
    private String upTypeId;
    private String upAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        initView();
        initData();
    }

    public void initData() {
        if (getIntent().getStringExtra("loanAmount") != null) {
            loanAmount = getIntent().getStringExtra("loanAmount");
            int Amount = Integer.parseInt(loanAmount);
            if (Amount >= 5000 && Amount < 20000) {
                count1 = 1;
            } else if (Amount >= 20000 && Amount < 50000) {
                count1 = 2;
            } else if (Amount >= 50000) {
                count1 = 3;
            } else {
                count1 = 0;
            }
            upTypeId = getIntent().getStringExtra("typeId");
            upAmount = getIntent().getStringExtra("amount");
            Timber.e(loanAmount + "loanAmount" + upTypeId + "upTypeId" + upAmount + "upAmount");
        }
    }

    public void initView() {
        mBackView = (TextView) findViewById(R.id.title_default_left);
        mTitleTextRightView = (TextView) findViewById(R.id.title_default_right);
        mTitleTextRightView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_explain, 0);
        mContent = (TextView) findViewById(R.id.title_content);
        mListView = (ListView) findViewById(R.id.message);
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        mCouponCashAdapter = new MoneyAdapter(MoneyActivity.this);
        mListView.setDivider(null);
        mListView.setAdapter(mCouponCashAdapter);
        //返回键
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lableString = "";
                for (int i = 0; i < typeId.size(); i++) {
                    if (i == typeId.size() - 1) {
                        lableString += typeId.get(i);
                    } else {
                        lableString += typeId.get(i) + ",";
                    }
                }

                String monenyString = "";
                for (int i = 0; i < amount.size(); i++) {
                    if (i == amount.size() - 1) {
                        monenyString += amount.get(i);
                    } else {
                        monenyString += amount.get(i) + ",";
                    }
                }

                BusIndustrySelect select = new BusIndustrySelect(MyFragment.BUS_MONEY_CODE);
                select.setAmount(monenyString);
                select.setTypeId(getIds());
                RxBus.getDefaultBus().send(select);
                finish();
            }
        });

        //红包使用详情
        mTitleTextRightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(WebShowActivity.EXTRA_TITLE, getString(R.string.envelope_rules));
                intent.putExtra(WebShowActivity.EXTRA_URL, UrlConstants.CREDIT_RULE);
                intent.setClass(MoneyActivity.this, WebShowActivity.class);
                startActivity(intent);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CouponBO listData = couponBOs.get(i);
                if (!listData.isSelected()) {
                    if (count >= count1) {
                        ToastUtil.showFailure(MoneyActivity.this, "你只能使用" + count + "张优惠券");
                        return;
                    }
                    listData.setSelected(true);
                    count++;
                    String id1 = listData.getId().toString();
                    typeId.add(id1);
                    String amountid = listData.getAmount().toString();
                    int index2 = amountid.indexOf(".");
                    amountid = index2 != -1 ? amountid.substring(0, index2) : amountid;
                    amount.add(amountid);
                    ((MoneyAdapter) mListView.getAdapter()).notifyDataSetChanged();
                } else {
                    listData.setSelected(false);
                    count--;
                    String id1 = listData.getId().toString();
                    typeId.remove(id1);
                    String amountid = listData.getAmount().toString();
                    int index2 = amountid.indexOf(".");
                    amountid = index2 != -1 ? amountid.substring(0, index2) : amountid;
                    amount.remove(amountid);
                    ((MoneyAdapter) mListView.getAdapter()).notifyDataSetChanged();
                }
                mContent.setTextColor(Color.parseColor("#6e6e6e"));
                mContent.setText("完成" + count + "");
            }
        });

        //type 0 优惠券    1 现金券   status  0 没有使用   1 已经使用
        JSONObject couponQuery = new JSONObject();
        couponQuery.put("type", "0");
        couponQuery.put("status", "0");
        queryCoupons(couponQuery);
    }

    public void queryCoupons(JSONObject query) {
        TasksRepository repository = ((CVApplication) getApplication())
            .getTasksRepositoryComponent()
            .getTasksRepository();
        phone = repository.getMobilePhone();
        LoginResponse loginResponse = repository.getLogin();
        objectId = loginResponse.objectId;
        token = loginResponse.accessToken;

        rest.queryCoupons(objectId, token, objectId, query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(s -> {
                Timber.e("红包查询优惠券" + s);
                couponBOs = JSON.parseArray(s, CouponBO.class);
                if (couponBOs.size() == 0) {
                    findViewById(R.id.ly_img_none).setVisibility(View.VISIBLE);
                    findViewById(R.id.scrollView).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.ly_img_none).setVisibility(View.GONE);
                    findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < couponBOs.size(); i++) {
                    couponBOs.get(i).setSelected(false);
                }
                mCouponCashAdapter.update(couponBOs);
                Success();
            }, throwable ->
                Timber.e(JSON.toJSONString(throwable) + "-----" + throwable.getMessage()));
    }


    private String getIds() {
        MoneyAdapter adapter = (MoneyAdapter) mListView.getAdapter();
        StringBuffer sb = new StringBuffer();
        for (CouponBO temp : adapter.listData) {
            if (temp.isSelected()) {
                sb.append(temp.getId()).append(",");
            }
        }
        if (sb.length() != 0) {
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
        return sb.toString();

    }


    public void Success() {
        Timber.e(mListView.getChildCount() + "成功的返回有没有执行3000");
        upTypeId = upTypeId == null ? "" : upTypeId;
        String ids[] = upTypeId.split(",");
        count = ids.length;
        MoneyAdapter ma = (MoneyAdapter) mListView.getAdapter();
        for (String id : ids) {
            for (CouponBO temp1 : couponBOs) {
                if (temp1.getId().toString().equals(id)) {
                    typeId.add(id);
                    CouponBO dt = ma.getItemById(id);
                    String amount1 = dt.getAmount();
                    int index2 = amount1.indexOf(".");
                    amount1 = index2 != -1 ? amount1.substring(0, index2) : amount1;
                    amount.add(amount1);
                    dt.setSelected(true);
                }
            }
        }

        Timber.e("ids===============>" + Arrays.toString(ids) + "===" + ids.length);
        if (Arrays.toString(ids).toString().trim().equals("[]")) {
            Timber.e("执行了=========>>>>>>>>");
            ma.listData.get(0).setSelected(true);
            String id1 = ma.listData.get(0).getId();
            typeId.add(id1);
            String amountid = ma.listData.get(0).getAmount();
            int index2 = amountid.indexOf(".");
            amountid = index2 != -1 ? amountid.substring(0, index2) : amountid;
            amount.add(amountid);
            ((MoneyAdapter) mListView.getAdapter()).notifyDataSetChanged();
        }
        mContent.setTextColor(Color.parseColor("#6e6e6e"));
        mContent.setText("完成" + count);
        ma.notifyDataSetChanged();
    }
}
