package com.cardvlaue.sys.feedback;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.cardvlaue.sys.R;
import java.util.List;

class FeedbackAdapter extends BaseAdapter {

    public LayoutInflater inflater;
    private List<Bitmap> arrayList;

    FeedbackAdapter(Context context, List<Bitmap> bmpList) {
        this.arrayList = bmpList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    //集合适配数据
    void setDate(List<Bitmap> bmpList) {
        this.arrayList = bmpList;
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int mposition = position;
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_feed_back, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv);
            viewHolder.gridview_delete = (ImageView) convertView.findViewById(R.id.gridview_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 判断+号图片上的小点图片影藏
        if (position == arrayList.size() - 1) {
            viewHolder.gridview_delete.setVisibility(View.GONE);
        } else {
            viewHolder.gridview_delete.setVisibility(View.VISIBLE);
        }
        //判断最多上传5张图片
        if (position == 5) {
            viewHolder.gridview_delete.setVisibility(View.GONE);
            viewHolder.imageView.setVisibility(View.GONE);
        }
        viewHolder.imageView.setImageBitmap(arrayList.get(position));
        viewHolder.gridview_delete.setTag(mposition);
        viewHolder.gridview_delete.setOnClickListener(view -> {
            viewHolder.gridview_delete = (ImageView) view;
            int p = (Integer) viewHolder.gridview_delete.getTag();
            arrayList.remove(mposition);

            notifyDataSetChanged();
        });
        viewHolder.imageView.setScaleType(ScaleType.FIT_XY);
        return convertView;
    }

    class ViewHolder {

        ImageView imageView;
        ImageView gridview_delete;
    }

}
