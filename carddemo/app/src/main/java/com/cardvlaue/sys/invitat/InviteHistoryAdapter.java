package com.cardvlaue.sys.invitat;

/**
 * Created by Administrator on 2016/7/23.
 */

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
 * 获取邀请记录adapter
 *
 * @author cardvalue
 */
public class InviteHistoryAdapter extends BaseAdapter {

    public List<InvitatItem> listData = new ArrayList<>();
    public Context context;
    public LayoutInflater inflater;
    public int count = 0;
    private boolean isflag = true;

    public InviteHistoryAdapter(List<InvitatItem> listData,
        Context context) {//,CustomHandler handler
        this.listData = listData;
        this.context = context;
        //this.handler = handler;
        inflater = LayoutInflater.from(context);
    }

    public InviteHistoryAdapter(Context context) {//,CustomHandler handler
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public Object getItem(int arg0) {
        return listData == null ? null : listData.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View view, ViewGroup groups) {

        if (view == null) {
            view = inflater.inflate(R.layout.item_invitehistory, groups, false);
        }
        TextView Name = (TextView) view.findViewById(R.id.name);
        Name.setText(listData.get(position).getMobilePhone().toString());

        TextView Time = (TextView) view.findViewById(R.id.tv_time);
        String str = listData.get(position).getRegisterTime().toString();
        int index2 = str.indexOf(" ");
        str = index2 != -1 ? str.substring(0, index2) : str;
        Time.setText(str + "注册");

        return view;
    }

    public void update(List<InvitatItem> list) {
        listData.clear();
        listData.addAll(list);
        notifyDataSetChanged();
    }
}

