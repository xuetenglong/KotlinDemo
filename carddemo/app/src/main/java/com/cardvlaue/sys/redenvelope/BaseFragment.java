package com.cardvlaue.sys.redenvelope;

/**
 * Created by Administrator on 2016/7/19.
 */


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class BaseFragment extends Fragment {

    public View view;
    public Context ct;
    //  public  CustomHandler handler;                        // 消息传递
    // public UserServices userService;                            // 与网络请相关的用户service
    // public BusinessServices businessService;                    // 与网络请求相关的业务service
    public ProgressDialog dialog;
    // protected LocalCache cache;                                 // 存放缓存数据

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        initData(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        ct = getActivity();

    }

    /**
     * setContentView;
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        view = initView(inflater, null);

        return view;
    }

    public View getRootView() {
        return view;
    }

    /**
     * 初始化view
     */
    public abstract View initView(LayoutInflater inflater, ViewGroup container);

    /**
     * 初始化数据
     */
    public abstract void initData(Bundle savedInstanceState);

}
