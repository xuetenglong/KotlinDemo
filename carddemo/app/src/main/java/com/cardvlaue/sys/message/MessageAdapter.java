package com.cardvlaue.sys.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.cardvlaue.sys.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/9.
 */
public class MessageAdapter extends BaseAdapter {

    public List<Messages> listData = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    /**
     * @param //1 = 系统消息， 0 = 用户消息
     */
    public MessageAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_message, parent, false);
        }
        if (!(listData.get(position).getReadTime() == null || listData.get(position).getReadTime()
            .equals(""))) {
            convertView.findViewById(R.id.tv_red).setVisibility(View.GONE);
        } else {
            convertView.findViewById(R.id.tv_red).setVisibility(View.VISIBLE);
        }

        TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
        TextView tv_time = (TextView) convertView.findViewById(R.id.tv_time);
        TextView content = (TextView) convertView.findViewById(R.id.content);

        tv_title.setText(listData.get(position).getTitle().toString());
        tv_time.setText(listData.get(position).getCreateTime().toString());
        content.setText(listData.get(position).getContent().toString());

        return convertView;
    }


    public void update(List<Messages> list) {
        listData.clear();
        listData.addAll(list);
        notifyDataSetChanged();
    }
}
