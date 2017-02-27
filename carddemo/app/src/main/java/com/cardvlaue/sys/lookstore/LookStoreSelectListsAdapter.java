package com.cardvlaue.sys.lookstore;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.ShopListsBean;
import com.cardvlaue.sys.view.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/16.
 */

public class LookStoreSelectListsAdapter extends
    RecyclerView.Adapter<LookStoreSelectListsAdapter.ViewHolder> {

    private List<ShopListsBean> mData = new ArrayList<>();

    private OnItemClickListener mEditClickListener;

    @Override
    public LookStoreSelectListsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
        int viewType) {
        return new LookStoreSelectListsAdapter.ViewHolder(
            LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_look_store, parent, false));
    }

    @Override
    public void onBindViewHolder(LookStoreSelectListsAdapter.ViewHolder holder, int position) {
        holder.ownerView.setText(mData.get(position).ownerName);
        holder.regView.setText(mData.get(position).bizRegisterNo);
        holder.shopView.setText(mData.get(position).corporateName);

        holder.itemView.setOnClickListener(view -> {
            if (mEditClickListener != null) {
                mEditClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(List<ShopListsBean> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    void setOnEditClickListener(OnItemClickListener listener) {
        mEditClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.ll_shop_select_item_content)
        LinearLayout itemView;

        /**
         * 法定代表
         */
        @BindView(R.id.tv_shop_select_item_owner)//
            TextView ownerView;

        /**
         * 营业注册号
         */
        @BindView(R.id.tv_shop_select_item_reg)//
            TextView regView;

        /**
         * 店铺名称
         */
        @BindView(R.id.tv_shop_select_item_shop)//
            TextView shopView;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
