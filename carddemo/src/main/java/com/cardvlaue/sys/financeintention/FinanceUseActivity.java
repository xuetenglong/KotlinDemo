package com.cardvlaue.sys.financeintention;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.IndustrySelectResponse;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.shopadd.IndustrySelectAdapter;
import com.cardvlaue.sys.util.RxBus2;
import java.util.ArrayList;
import java.util.List;

/**
 * 融资用途
 */
public class FinanceUseActivity extends BaseActivity {

    public static final String BUS_FINANCE_USE = "ContractSelectActivity_BUS_FINANCE_USE";

    /*
    private Disposable mDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_white);

        FrameLayout view = (FrameLayout) findViewById(R.id.contentFrame);
        setContainer(view);
        renderPage(WXFileUtils.loadAsset("finance-use.js", this));
    }

    @Override
    public void onStart() {
        super.onStart();
        mDisposable = RxBus2.Companion.get().toObservable().subscribe(o -> {
            // 融资用途
            if (o instanceof BusIndustrySelect) {
                BusIndustrySelect busIndustrySelect = (BusIndustrySelect) o;
                if (FinanceUseActivity.BUS_FINANCE_USE.equals(busIndustrySelect.getBus())) {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
    */

    /*
     * 标题
     */
    @BindView(R.id.tv_white_back)
    TextView mNameView;

    /*
     * 列表
     */
    @BindView(R.id.rv_industry_select)
    RecyclerView mListsView;

    private List<IndustrySelectResponse> mData = new ArrayList<>();

    private String[] mUses = {"开立分店", "店铺装修", "添置设备", "增加库存", "工资账单",
        "广告宣传", "个人消费", "资金周转", "其它"};

    @OnClick(R.id.ibtn_white_back)
    void clickBack() {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industry_select);
        ButterKnife.bind(this);

        mNameView.setText(getString(R.string.finance_use));

        for (String one : mUses) {
            IndustrySelectResponse selectResponse = new IndustrySelectResponse();
            selectResponse.setTitle(one);
            mData.add(selectResponse);
        }
        IndustrySelectAdapter selectAdapter = new IndustrySelectAdapter();
        selectAdapter.updateData(mData);
        selectAdapter.setOnItemClickListener(position -> {
            BusIndustrySelect select = new BusIndustrySelect(BUS_FINANCE_USE);
            select.setTitle(mData.get(position).getTitle());
            RxBus2.Companion.get().send(select);
            finish();
        });

        mListsView.setLayoutManager(new LinearLayoutManager(this));
        mListsView.setAdapter(selectAdapter);
    }

}
