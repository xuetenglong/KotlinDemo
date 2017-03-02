package com.cardvlaue.sys.shopadd;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.AddressSearchItemResponse;
import com.cardvlaue.sys.view.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;

class AddressSearchAdapter extends RecyclerView.Adapter<AddressSearchAdapter.ViewHolder> {

    private List<AddressSearchItemResponse> mData = new ArrayList<>();

    private OnItemClickListener mItemClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String cityStr = mData.get(position).city + mData.get(position).district;
        holder.cityView.setText(cityStr);
        holder.detailView.setText(mData.get(position).name);

        holder.contentView.setOnClickListener(view -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(List<AddressSearchItemResponse> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    void setOnItemClickLister(OnItemClickListener lister) {
        mItemClickListener = lister;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_search_item_detail)
        TextView detailView;

        @BindView(R.id.tv_search_item_city)
        TextView cityView;

        @BindView(R.id.ll_search_content)
        LinearLayout contentView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
