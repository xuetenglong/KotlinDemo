package com.cardvlaue.sys.uploadphoto;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.R;
import com.facebook.drawee.view.SimpleDraweeView;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * Created by Administrator on 2016/10/18.
 */

public class ItemDialogListsAdapter extends
    RecyclerView.Adapter<ItemDialogListsAdapter.ViewHolder> {

    private List<NewFileListsImgBO> data = new ArrayList<>();
    private OnItemClickListener itemAddClickListener;
    private NewFileListsAdapter.OnItemDeleteListener itemDeleteClickListener;
    private NewFileListsAdapter.OnItemPreviewListener itemPreviewClickListener;
    private NewFileListsAdapter.OnItemReasonListener itemReasonClickListener;//补件
    private NewFileListsAdapter.OnItemUploadListener itemUploadClickListener;//显示全部的图片
    private int tag;

    public ItemDialogListsAdapter(int tag) {
        this.tag = tag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_upload_dialog_lists, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int a = position + 1;
        Timber.e("tagtagtagtagtagtagtagtagtagtag--data---" + JSON.toJSONString(data));
        if (!TextUtils.isEmpty(data.get(position).getUrl())) {
            holder.imageView.setImageURI(Uri.parse(data.get(position).getUrl()));
        }
        if (!TextUtils.isEmpty(data.get(position).getDescript())) {
            holder.tv_demo.setVisibility(View.VISIBLE);
            holder.tv_demo.setText("标准样张" + a);
        } else {
            holder.tv_demo.setVisibility(View.GONE);
        }

        holder.imageView.setOnClickListener(v -> {
            if (itemAddClickListener != null) {
                itemAddClickListener.onItemClick(holder.getAdapterPosition());
            }
        });

        holder.viewBg.getBackground().setAlpha(210);
        holder.delView.setOnClickListener(v -> {
            if (itemReasonClickListener != null) {
                Timber.e("删除图片");
                itemReasonClickListener.onItemReasonClick(holder.getAdapterPosition());
            }
        });

      /*  holder.imageView.setOnClickListener(v -> {
            itemUploadClickListener.onItemUploadClick(holder.getAdapterPosition());
        });*/
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemUploadClickListener(
        NewFileListsAdapter.OnItemUploadListener itemUploadClickListener) {
        this.itemUploadClickListener = itemUploadClickListener;
    }

    public void setItemReasonClickListener(
        NewFileListsAdapter.OnItemReasonListener itemReasonClickListener) {
        this.itemReasonClickListener = itemReasonClickListener;
    }

    public void setOnItemAddClickListener(OnItemClickListener listener) {
        itemAddClickListener = listener;
    }

    public void setOnItemDeleteClickListener(NewFileListsAdapter.OnItemDeleteListener listener) {
        itemDeleteClickListener = listener;
    }

    public void setOnItemPreviewClickListener(NewFileListsAdapter.OnItemPreviewListener listener) {
        itemPreviewClickListener = listener;
    }

    public void updateData(List<NewFileListsImgBO> files) {
        data.clear();
        data.addAll(files);
        notifyDataSetChanged();
    }

    public interface OnItemUploadListener {

        void onItemUploadClick(int position);
    }

    public interface OnItemReasonListener {

        void onItemReasonClick(int position);
    }

    public interface OnItemDeleteListener {

        void fileId(String fileId);
    }

    public interface OnItemPreviewListener {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView imageView;
        TextView viewBg, tv_demo;
        ImageView delView;

        public ViewHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(this, itemView);
            imageView = (SimpleDraweeView) itemView.findViewById(R.id.iv_financing_upload_item_img);
            // titleView = (TextView) itemView.findViewById(R.id.tv_upload_item_titile_text);
            viewBg = (TextView) itemView.findViewById(R.id.tv_bg_img);
            //delView=(ImageView) itemView.findViewById(R.id.iv_financing_upload_item_dialog);//点击显示补件的原因
            delView = (ImageView) itemView.findViewById(R.id.iv_left_upload_item_dialog);//删除图片
            tv_demo = (TextView) itemView.findViewById(R.id.tv_demo);//demo的文字

        }
    }


}