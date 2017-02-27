package com.cardvlaue.sys.posadd;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.view.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;

class PosAddVerifyAdapter extends RecyclerView.Adapter<PosAddVerifyAdapter.ViewHolder> {

    private List<String> mData = new ArrayList<>();

    private OnItemClickListener mListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_pos_add_dialog_verify, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.answerView.setText(mData.get(position));

        if (mListener != null) {
            holder.answerView.setOnClickListener(v -> mListener.onItemClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void updateData(List<String> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_pos_add_dialog_verify_answer)
        TextView answerView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
