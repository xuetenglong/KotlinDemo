package com.cardvlaue.sys.shopselect;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.ShopListsBean;
import com.cardvlaue.sys.view.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;

class ShopSelectListsAdapter extends RecyclerView.Adapter<ShopSelectListsAdapter.ViewHolder> {

    private List<ShopListsBean> mData = new ArrayList<>();

    private OnItemClickListener mItemClickListener, mEditClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_shop_select_lists, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mData.get(position).isShowTip) {
            holder.tipView.setVisibility(View.VISIBLE);
        } else {
            holder.tipView.setVisibility(View.GONE);
        }

        if ("1".equals(mData.get(position).isCurrent)) {
            holder.currentView.setVisibility(View.VISIBLE);
        } else {
            holder.currentView.setVisibility(View.INVISIBLE);
        }

        holder.ownerView.setText(mData.get(position).ownerName);
        holder.regView.setText(mData.get(position).bizRegisterNo);
        holder.shopView.setText(mData.get(position).corporateName);

        if ("1".equals(mData.get(position).isDocumentLocked)) {
            holder.editView.setVisibility(View.GONE);
            holder.lockView.setVisibility(View.VISIBLE);
            holder.currentView.setVisibility(View.INVISIBLE);
        } else if ("0".equals(mData.get(position).isDocumentLocked)) {
            holder.editView.setVisibility(View.VISIBLE);
            holder.lockView.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(view -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(position);
                }
            });
        } else {
            holder.editView.setVisibility(View.GONE);
            holder.lockView.setVisibility(View.GONE);
        }

        holder.editView.setOnClickListener(v -> {
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

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    void setOnEditClickListener(OnItemClickListener listener) {
        mEditClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * 提示文字
         */
        @BindView(R.id.tv_shop_select_item_tip)
        TextView tipView;

        @BindView(R.id.ll_shop_select_item_content)
        LinearLayout itemView;

        /**
         * 当前选中店铺
         */
        @BindView(R.id.iv_shop_select_item_current)
        ImageView currentView;

        /**
         * 法定代表
         */
        @BindView(R.id.tv_shop_select_item_owner)
        TextView ownerView;

        /**
         * 营业注册号
         */
        @BindView(R.id.tv_shop_select_item_reg)
        TextView regView;

        /**
         * 店铺名称
         */
        @BindView(R.id.tv_shop_select_item_shop)
        TextView shopView;

        /**
         * 编辑按钮
         */
        @BindView(R.id.fl_shop_select_item_edit)
        FrameLayout editView;

        /**
         * 锁定
         */
        @BindView(R.id.tv_shop_select_item_lock)
        TextView lockView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
