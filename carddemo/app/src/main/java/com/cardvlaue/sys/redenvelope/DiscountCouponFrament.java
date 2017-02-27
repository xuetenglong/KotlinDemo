package com.cardvlaue.sys.redenvelope;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * <p>优惠券<p/>
 */
public class DiscountCouponFrament extends BaseFragment {

    public CouponCashAdapter mCouponCashAdapter;
    public ListView mListView;
    private String phone, objectId, token;
    private IFinancingRest rest;
    private List<CouponBO> couponBOs = new ArrayList<>();

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.frament_discount_coupon, container, false);
        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mListView = (ListView) view.findViewById(R.id.message);
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        mCouponCashAdapter = new CouponCashAdapter(getActivity());
        mListView.setDivider(null);
        mListView.setAdapter(mCouponCashAdapter);
        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            ToastUtil.showFailure(getActivity(), "请在提交申请的时候使用"); //失败
        });

        //type 0 优惠券    1 现金券   status  0 没有使用   1 已经使用
        JSONObject couponQuery = new JSONObject();
        couponQuery.put("type", "0");
        couponQuery.put("status", "0");
        queryCoupons(couponQuery);
    }


    public void queryCoupons(JSONObject query) {
        TasksRepository repository = ((CVApplication) getActivity().getApplication())
            .getTasksRepositoryComponent().getTasksRepository();
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
                    view.findViewById(R.id.ly_img_none).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.scrollView).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.ly_img_none).setVisibility(View.GONE);
                    view.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                }
                mCouponCashAdapter.update(couponBOs);

            }, throwable -> Timber
                .e(JSON.toJSONString(throwable) + "-----" + throwable.getMessage()));
    }
}
