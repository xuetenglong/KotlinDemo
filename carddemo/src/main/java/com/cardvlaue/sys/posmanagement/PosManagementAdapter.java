package com.cardvlaue.sys.posmanagement;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.MidsItemResponse;
import com.cardvlaue.sys.view.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;

class PosManagementAdapter extends RecyclerView.Adapter<PosManagementAdapter.ViewHolder> {

    private List<MidsItemResponse> mData = new ArrayList<>();

    private OnItemClickListener mListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_pos_management_lists, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.id.setText(mData.get(position).getMid());
        holder.status.setText(mData.get(position).getStatusInfo());

        holder.content.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(position);
            }
        });

        if ("U".equals(mData.get(position).getStatus())) {
            holder.moreView.setVisibility(View.VISIBLE);
        } else {
            holder.moreView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(List<MidsItemResponse> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_pos_management_item_id)
        TextView id;

        @BindView(R.id.tv_pos_management_item_status)
        TextView status;

        @BindView(R.id.ll_pos_management_content)
        LinearLayout content;

        @BindView(R.id.iv_pos_management_item_can_click)
        ImageView moreView;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

}
