package com.cardvlaue.sys.redenvelope;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
 * <p>现金券<p/>
 */
public class CouponFrament extends BaseFragment {

    public static Handler handler;
    public CouponCashAdapter mCouponCashAdapter;
    public ListView mListView;
    public ProgressDialog dialog;
    private String phone, objectId, token;
    private IFinancingRest rest;
    private List<CouponBO> couponBOs = new ArrayList<>();
    private Dialog dlg;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.frament_coupon, container, false);
        dialog = new ProgressDialog(getActivity());
        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x1017) {
                    JSONObject couponQuery = new JSONObject();
                    couponQuery.put("type", "1");
                    couponQuery.put("status", "0");
                    queryCoupons(couponQuery);
                }
            }
        };
        mListView = (ListView) view.findViewById(R.id.message);
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        mCouponCashAdapter = new CouponCashAdapter(getActivity());
        mListView.setDivider(null);
        mListView.setAdapter(mCouponCashAdapter);
        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            CouponBO listData = couponBOs.get(position);
            if (listData.getType().toString().equals("1") &&
                listData.getStatus().equals("0")) {
                dlg = CouponUtil.showCoupon(getActivity(), listData.getId().toString(), dialog);
            } else if (listData.getType().toString().equals("1") &&
                listData.getStatus().equals("1")) {
                ToastUtil.showFailure(getActivity(), "您所点击的现金券已经提现");
            } else {
                return;
            }
        });

        //type 0 优惠券    1 现金券   status  0 没有使用   1 已经使用
        JSONObject couponQuery = new JSONObject();
        couponQuery.put("type", "1");
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
                Timber.e("红包查询现金券" + s);
                couponBOs = JSON.parseArray(s, CouponBO.class);
                if (couponBOs.size() == 0) {
                    view.findViewById(R.id.ly_img_none).setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.ly_img_none).setVisibility(View.GONE);
                    view.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                }
                mCouponCashAdapter.update(couponBOs);
            }, throwable -> throwable.printStackTrace());
    }
}
