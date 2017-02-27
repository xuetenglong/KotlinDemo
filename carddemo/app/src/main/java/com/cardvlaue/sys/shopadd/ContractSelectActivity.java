package com.cardvlaue.sys.shopadd;

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
import com.cardvlaue.sys.util.RxBus2;
import java.util.ArrayList;
import java.util.List;

/**
 * 无店铺租赁合同原因
 */
public class ContractSelectActivity extends BaseActivity {

    public static final String EXTRA_HAS_CONTRACT = "hasContract";

    public static final String BUS_HAS_CONTRACT = "ContractSelectActivity_BUS_HAS_CONTRACT";

    public static final String BUS_WHY_NO_CONTRACT = "ContractSelectActivity_BUS_WHY_NO_CONTRACT";

    /**
     * 标题文字
     */
    @BindView(R.id.tv_white_back)
    TextView mNameView;

    /**
     * 列表
     */
    @BindView(R.id.rv_industry_select)
    RecyclerView mListsView;

    private List<IndustrySelectResponse> hasContractData = new ArrayList<>(), whyNoData = new ArrayList<>();

    private String[] mHasContracts = {"无", "有"}, mContractLoss = {"自有房产", "无偿使用", "合同丢失"};

    private IndustrySelectAdapter mSelectAdapter;

    @OnClick(R.id.ibtn_white_back)
    void clickBack() {
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industry_select);
        ButterKnife.bind(this);

        int flag = getIntent().getIntExtra(EXTRA_HAS_CONTRACT, -1);

        mNameView.setText(getString(R.string.shop_add_select));

        // 是否有店铺租赁合同
        for (String one : mHasContracts) {
            IndustrySelectResponse selectResponse = new IndustrySelectResponse();
            selectResponse.setTitle(one);
            hasContractData.add(selectResponse);
        }

        // 无店铺租赁合同原因
        for (String one : mContractLoss) {
            IndustrySelectResponse selectResponse = new IndustrySelectResponse();
            selectResponse.setTitle(one);
            whyNoData.add(selectResponse);
        }

        mSelectAdapter = new IndustrySelectAdapter();
        mSelectAdapter.setOnItemClickListener(position -> {
            switch (flag) {
                case 0:
                    BusIndustrySelect hasContractBus = new BusIndustrySelect(BUS_HAS_CONTRACT);
                    hasContractBus.setTitle(hasContractData.get(position).getTitle());
                    RxBus2.Companion.get().send(hasContractBus);
                    break;
                case 1:
                    BusIndustrySelect whyNoContractBus = new BusIndustrySelect(BUS_WHY_NO_CONTRACT);
                    whyNoContractBus.setTitle(whyNoData.get(position).getTitle());
                    RxBus2.Companion.get().send(whyNoContractBus);
                    break;
            }

            finish();
        });
        mListsView.setLayoutManager(new LinearLayoutManager(this));
        mListsView.setAdapter(mSelectAdapter);
        switch (flag) {
            case 0:
                mSelectAdapter.updateData(hasContractData);
                break;
            case 1:
                mSelectAdapter.updateData(whyNoData);
                break;
        }
    }

}
