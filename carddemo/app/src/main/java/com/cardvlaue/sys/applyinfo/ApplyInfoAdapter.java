package com.cardvlaue.sys.applyinfo;

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
public class ApplyInfoAdapter extends RecyclerView.Adapter {

    public OnItemClickListenter onItemClickListenter;
    private List<Item> mlist = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    private String tag;

    public ApplyInfoAdapter(Context context, String tag) {
        this.tag = tag;
        this.context = context;
        this.inflater = LayoutInflater.from(context);

    }

    public ApplyInfoAdapter(Context context, List<Item> mlist) {
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
            .inflate(R.layout.item_apply_info_lists, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 设置数据
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
           /* if (tag.equals("1") && (position == 2 || position == 8)) {
                viewHolder.asd.setVisibility(View.VISIBLE);
            }*/
            Item selHasLeaseContract = mlist.get(position);
            Timber.e("onBindViewHolder" + position + "==" + (mlist.size() - 1));
            if (tag.equals("4") && position == mlist.size() - 1) {
                viewHolder.view_bottom.setVisibility(View.VISIBLE);
            } else {
                viewHolder.view_bottom.setVisibility(View.GONE);
            }
            if (ReadUtil.isContain(selHasLeaseContract.getValue())) {
                viewHolder.value.setText(ReadUtil.fmtMicrometer(selHasLeaseContract.getValue()));
            } else {
                viewHolder.value.setText(selHasLeaseContract.getValue());
            }
            viewHolder.titile.setText(selHasLeaseContract.getTitle());
            Timber.e(selHasLeaseContract.getValue() + selHasLeaseContract.getTitle() + "设置数据");
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

    public void update(List<Item> list) {
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

        TextView titile, value;
        View asd, view_bottom;

        //构造函数
        public ViewHolder(final View itemView) {
            super(itemView);
            //初始化方法
            inint(itemView);
            //点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListenter != null) {
                        onItemClickListenter.OnItemClick(itemView, getLayoutPosition());
                    }
                }
            });
        }

        public void inint(View view) {
            titile = (TextView) view.findViewById(R.id.title);
            value = (TextView) view.findViewById(R.id.value);
            asd = view.findViewById(R.id.and);
            view_bottom = view.findViewById(R.id.view_bottom);
        }
    }
}
