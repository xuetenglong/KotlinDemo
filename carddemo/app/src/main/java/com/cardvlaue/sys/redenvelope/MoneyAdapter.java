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

/**
 * Created by Administrator on 2016/7/25.
 */
public class MoneyAdapter extends BaseAdapter {

    public List<CouponBO> listData = new ArrayList<>();
    public Context context;
    public LayoutInflater inflater;
    public int count = 0;
    private String loanAmount;

    public MoneyAdapter(List<CouponBO> listData, Context context, String loanAmount) {
        this.listData = listData;
        this.context = context;

        this.loanAmount = loanAmount;
        inflater = LayoutInflater.from(context);
    }

    public MoneyAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return listData == null ? 0 : listData.size();
    }

    public int getPositionById(String id) {
        int position = 0;
        for (CouponBO temp : listData) {
            if (temp.getId().toString().equals(id)) {
                return position;
            } else {
                position++;
            }
        }
        return -1;
    }

    public CouponBO getItemById(String id) {
        int position = 0;
        for (CouponBO temp : listData) {
            if (temp.getId().toString().equals(id)) {
                return temp;
            }
        }
        return null;
    }

    @Override
    public Object getItem(int arg0) {
        return listData == null ? null : listData.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View view, ViewGroup groups) {
        if (view == null) {
            view = inflater.inflate(R.layout.item_money, groups, false);
        }
        final int position1 = position;
        com.cardvlaue.sys.redenvelope.TransRelativeLayout TransRelativeLayout = (com.cardvlaue.sys.redenvelope.TransRelativeLayout) view
            .findViewById(R.id.TransRelativeLayout);
        RelativeLayout youhuiquan = (RelativeLayout) view.findViewById(R.id.ry_youhuiquan);

        TextView tv_use = (TextView) view.findViewById(R.id.tv_use);//优惠券是否使用
        View view1 = (View) view.findViewById(R.id.view1);
        if (position == 0) {
            view1.setVisibility(View.VISIBLE);
        } else {
            view1.setVisibility(View.GONE);
        }
        if (listData.get(position1).isSelected()) {
            youhuiquan.setBackgroundResource(R.mipmap.ic_mon_yes);
            TransRelativeLayout.setVisibility(View.VISIBLE);
        } else {
            youhuiquan.setBackgroundResource(R.mipmap.youhuiquan);
            TransRelativeLayout.setVisibility(View.GONE);

        }

        TextView qian = (TextView) view.findViewById(R.id.qian);
        TextView qian_s = (TextView) view.findViewById(R.id.qian_s);
        if (listData.get(position).getStatus().toString().equals("0")) {  //status=0?没有使用：已经使用了
            tv_use.setText("未使用");
            qian.setTextColor(Color.parseColor("#fc5031"));
        } else {
            youhuiquan.setVisibility(View.GONE);
            youhuiquan.setBackgroundResource(R.mipmap.ic_youhuiquan_no);
            tv_use.setText("已使用");
            qian.setTextColor(Color.parseColor("#818181"));
        }
        qian.setText("￥" + listData.get(position).getAmount().toString());
        qian_s.setText(listData.get(position).getMemo().toString());
        return view;
    }


    public void update(List<CouponBO> list) {
        listData.clear();
        listData.addAll(list);
        notifyDataSetChanged();
    }
}


