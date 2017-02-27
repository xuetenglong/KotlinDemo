package com.cardvlaue.sys.redenvelope;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.cardvlaue.sys.R;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * Created by Administrator on 2016/7/19.
 */
public class CouponCashAdapter extends BaseAdapter {

    public List<CouponBO> listData = new ArrayList<>();
    public Context context;
    public LayoutInflater inflater;
    public int count = 0;
    private boolean isflag = true;

    public CouponCashAdapter(List<CouponBO> listData, Context context) {
        this.listData = listData;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public CouponCashAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        //return listData.size();
        return listData == null ? 0 : listData.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return listData == null ? null : listData.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View view, ViewGroup groups) {

        if (view == null) {
            view = inflater.inflate(R.layout.item_cash, groups, false);
        }
        final com.cardvlaue.sys.redenvelope.TransRelativeLayout TransRelativeLayout = (com.cardvlaue.sys.redenvelope.TransRelativeLayout) view
            .findViewById(R.id.TransRelativeLayout);
        RelativeLayout youhuiquan = (RelativeLayout) view.findViewById(R.id.ry_youhuiquan);
        TextView tv_use = (TextView) view.findViewById(R.id.tv_use);//优惠券有没有使用
        TextView amount = (TextView) view.findViewById(R.id.amount);
        amount.setText("￥" + listData.get(position).getAmount().toString());
        TextView memo = (TextView) view.findViewById(R.id.memo);
        View view1 = (View) view.findViewById(R.id.view);
        memo.setText(listData.get(position).getMemo().toString() + "");
        Timber.e(listData.get(position) + "****" + listData.size() + position);
        if (position == 0) {
            view1.setVisibility(View.VISIBLE);
        } else {
            view1.setVisibility(View.GONE);
        }
        if (listData.get(position).getType().toString().equals("0")) {//优惠券  type=0：1？优惠券   现金券
            if (listData.get(position).getStatus().toString()
                .equals("0")) {  // status  0  没有使用    1已经使用
                youhuiquan.setBackgroundResource(R.mipmap.youhuiquan);
                tv_use.setText("未使用");
                //amount.setTextColor(Color.parseColor("#fc5031"));
                amount.setTextColor(Color.RED);
                tv_use.setTextColor(Color.RED);
            } else {

                youhuiquan.setBackgroundResource(R.mipmap.ic_youhuiquan_no);
                tv_use.setText("已使用");
                amount.setTextColor(Color.RED);
                amount.setTextColor(Color.parseColor("#818181"));
            }


        } else {//现金券
            if (listData.get(position).getStatus().toString()
                .equals("0")) {  // status  0  没有使用    1已经使用
                youhuiquan.setBackgroundResource(R.mipmap.ic_money_red);
                tv_use.setText("未使用");
                amount.setTextColor(Color.parseColor("#fc5031"));
            } else {
                youhuiquan.setBackgroundResource(R.mipmap.ic_money_bg);
                tv_use.setText("已使用");
                amount.setTextColor(Color.RED);
                amount.setTextColor(Color.parseColor("#818181"));
            }

        }
        return view;
    }

    public void update(List<CouponBO> list) {
        listData.clear();
        listData.addAll(list);
        notifyDataSetChanged();
    }
}
