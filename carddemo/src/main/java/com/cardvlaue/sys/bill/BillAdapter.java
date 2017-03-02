package com.cardvlaue.sys.bill;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.util.ReadUtil;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * Created by Administrator on 2016/6/27.
 */
public class BillAdapter extends RecyclerView.Adapter {

    public OnItemClickListenter onItemClickListenter;
    private List<CashListItem> mlist = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    private String tag;

    public BillAdapter(Context context, String tag) {
        this.tag = tag;
        this.context = context;
        this.inflater = LayoutInflater.from(context);

    }


    public BillAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);

    }

    public BillAdapter(Context context, List<CashListItem> mlist) {
        this.context = context;
        this.mlist = mlist;
        this.inflater = LayoutInflater.from(context);

    }

    /**
     * 设置view,只做布局解析
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view=View.inflate(context,R.layout.list_textview,null);
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_bill, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 设置数据
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CashListItem selHasLeaseContract = mlist.get(position);
            viewHolder.tv_shouldReturnDate.setText(selHasLeaseContract.getShouldReturnDate());
            viewHolder.tv_shouldReturnMoney
                .setText(
                    ReadUtil.fmtMicrometer(selHasLeaseContract.getShouldReturnMoney() + "") + "元");
            viewHolder.tv_receiveDate.setText(selHasLeaseContract.getReceiveDate());
            viewHolder.tv_receiveMoney
                .setText(ReadUtil.fmtMicrometer(selHasLeaseContract.getReceiveMoney() + "") + "元");
            Timber.e(selHasLeaseContract.getShouldReturnDate() + "==" + selHasLeaseContract
                .getShouldReturnMoney() + "设置数据" + selHasLeaseContract.getReceiveDate() + "=="
                + selHasLeaseContract.getReceiveMoney());
        }
    }

    /**
     * 设置adapter的条数
     */
    @Override
    public int getItemCount() {
        return mlist != null ? mlist.size() : 0;
    }

    public void setOnItemClickListenter(OnItemClickListenter onItemClickListenter) {
        this.onItemClickListenter = onItemClickListenter;
    }

    public void update(List<CashListItem> list) {
        mlist.clear();
        mlist.addAll(list);
        notifyDataSetChanged();
    }


    /**
     * 对外暴露的点击事件接口
     */

    public interface OnItemClickListenter {

        void OnItemClick(View v, int position);
    }

    /**
     * viewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_shouldReturnDate;
        TextView tv_shouldReturnMoney;
        TextView tv_receiveDate;
        TextView tv_receiveMoney;

        //构造函数
        public ViewHolder(final View itemView) {
            super(itemView);
            //初始化方法
            inint(itemView);
            //点击事件
            itemView.setOnClickListener(v -> {
                if (onItemClickListenter != null) {
                    onItemClickListenter.OnItemClick(itemView, getLayoutPosition());
                }
            });
        }

        public void inint(View view) {
            tv_shouldReturnDate = (TextView) view.findViewById(R.id.tv_shouldReturnDate);
            tv_shouldReturnMoney = (TextView) view.findViewById(R.id.tv_shouldReturnMoney);
            tv_receiveDate = (TextView) view.findViewById(R.id.tv_receiveDate);
            tv_receiveMoney = (TextView) view.findViewById(R.id.tv_receiveMoney);
        }
    }
}
