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
 * Created by cardvalue on 2016/6/13.  一级
 */
public class NewFileListsAdapter extends RecyclerView.Adapter<NewFileListsAdapter.ViewHolder> {

    private List<NewFileListsItemBO> data = new ArrayList<>();
    private OnItemClickListener itemAddClickListener;
    private OnItemDeleteListener itemDeleteClickListener;
    private OnItemPreviewListener itemPreviewClickListener;
    private OnItemReasonListener itemReasonClickListener;//补件
    private OnItemUploadListener itemUploadClickListener;//显示全部的图片
    private int tag;

    public NewFileListsAdapter(int tag) {
        this.tag = tag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_financing_upload_file_lists, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Timber.e("tagtagtagtagtagtagtagtagtagtag--data--filesCount-" + JSON.toJSONString(data));
        List<NewFileListsImgBO> fileListsImgBOs = data.get(position).getFiles();

        if (fileListsImgBOs.size() > 0) {
            if (!TextUtils
                .isEmpty(fileListsImgBOs.get(fileListsImgBOs.size() - 1).getThumbnail())) {
                holder.imageView
                    .setImageURI(
                        Uri.parse(fileListsImgBOs.get(fileListsImgBOs.size() - 1).getThumbnail()));
            }
        } else {
            if (!TextUtils.isEmpty(data.get(position).getDemo())) {
                holder.imageView.setImageURI(Uri.parse(data.get(position).getDemo()));
            }
        }

        int Descript = 0;
        for (NewFileListsImgBO tp : fileListsImgBOs) {
            if (!TextUtils.isEmpty(tp.getDescript())) {
                Descript++;
            }
        }
        Timber.e("=Descript==" + Descript + "====position=" + position);
        if (fileListsImgBOs.size() == 0) {
            holder.itemUpload.setVisibility(View.GONE);
        } else {
            holder.itemUpload.setVisibility(View.VISIBLE);
            holder.itemUpload
                .setText(fileListsImgBOs.size() != 0 ? fileListsImgBOs.size() - Descript + "" : "");

        }

        holder.titleView
            .setText(data.get(position).getTitle() != null ? data.get(position).getTitle() : "");
        holder.imageView.setOnClickListener(v -> {
            if (itemAddClickListener != null) {
                itemAddClickListener.onItemClick(holder.getAdapterPosition());
            }
        });

        Timber.e("tag" + tag);
        if (tag == 1) {//说明是补件
            holder.delView.setVisibility(View.VISIBLE);
        } else {
            holder.delView.setVisibility(View.GONE);
        }
        holder.delView.setOnClickListener(v -> {
            if (itemReasonClickListener != null) {
                Timber.e("点击事件补件的原因");
                itemReasonClickListener.onItemReasonClick(holder.getAdapterPosition());
            }
        });
        holder.itemUpload.setOnClickListener(v -> {
            itemUploadClickListener.onItemUploadClick(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemUploadClickListener(OnItemUploadListener itemUploadClickListener) {
        this.itemUploadClickListener = itemUploadClickListener;
    }

    public void setItemReasonClickListener(OnItemReasonListener itemReasonClickListener) {
        this.itemReasonClickListener = itemReasonClickListener;
    }

    public void setOnItemAddClickListener(OnItemClickListener listener) {
        itemAddClickListener = listener;
    }

    public void setOnItemDeleteClickListener(OnItemDeleteListener listener) {
        itemDeleteClickListener = listener;
    }

    public void setOnItemPreviewClickListener(OnItemPreviewListener listener) {
        itemPreviewClickListener = listener;
    }

    public void updateData(List<NewFileListsItemBO> files) {
        data.clear();
        data.addAll(files);
        notifyDataSetChanged();
    }

    interface OnItemUploadListener {

        void onItemUploadClick(int position);
    }

    interface OnItemReasonListener {

        void onItemReasonClick(int position);
    }

    interface OnItemDeleteListener {

        void fileId(String fileId);
    }

    interface OnItemPreviewListener {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView imageView;
        TextView titleView, itemUpload;
        ImageView delView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (SimpleDraweeView) itemView.findViewById(R.id.iv_financing_upload_item_img);
            titleView = (TextView) itemView.findViewById(R.id.tv_upload_item_title_text);
            delView = (ImageView) itemView
                .findViewById(R.id.iv_financing_upload_item_dialog);//点击显示补件的原因
            itemUpload = (TextView) itemView
                .findViewById(R.id.iv_left_upload_item_dialog);//点击显示全部图片
        }
    }
}

