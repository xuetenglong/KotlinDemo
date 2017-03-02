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
import com.cardvlaue.sys.data.IndustrySelectResponse;
import com.cardvlaue.sys.view.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 单行 Item 适配器
 */
public class IndustrySelectAdapter extends RecyclerView.Adapter<IndustrySelectAdapter.ViewHolder> {

    private List<IndustrySelectResponse> mData = new ArrayList<>();

    private OnItemClickListener mItemClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_industry_select, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.text.setText(mData.get(position).getTitle());

        holder.group.setOnClickListener(view -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(List<IndustrySelectResponse> strs) {
        mData.clear();
        mData.addAll(strs);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ll_industry_select_group)
        LinearLayout group;

        @BindView(R.id.tv_financing_item_calculate_name)
        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

}
