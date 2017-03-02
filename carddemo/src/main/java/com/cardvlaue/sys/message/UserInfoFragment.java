package com.cardvlaue.sys.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.redenvelope.BaseFragment;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * 用户消息 Created by Administrator on 2016/9/9.
 */
public class UserInfoFragment extends BaseFragment {

    private MessageRest messageRest;
    private MessageAdapter messageAdapter;
    private List<Messages> messagesList = new ArrayList<>();
    private ListView listView;
    private String phone, objectId, token, id;
    private MessagesDailog messagesDailog;
    private ContentLoadingDialog mLoadingDialog;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.frament_message, container, false);
        messageRest = HttpConfig.getClient().create(MessageRest.class);
        mLoadingDialog = ContentLoadingDialog.newInstance("加载中...");
        mLoadingDialog.setCancelable(false);
        TasksRepository repository = ((CVApplication) getActivity().getApplication())
            .getTasksRepositoryComponent().getTasksRepository();
        phone = repository.getMobilePhone();
        LoginResponse loginResponse = repository.getLogin();
        objectId = loginResponse.objectId;
        token = loginResponse.accessToken;
        listView = (ListView) view.findViewById(R.id.message);
        messagesDailog = new MessagesDailog();
        mLoadingDialog.show(getFragmentManager(), "tag");
        getMessage();
        messageAdapter = new MessageAdapter(getActivity());
        listView.setDivider(null);
        listView.setAdapter(messageAdapter);
        listView.setOnItemClickListener((parent, view1, position, id1) -> {
            Messages listData = messagesList.get(position);
            Timber.e("listData+++++" + JSON.toJSONString(listData));
            Intent intent = new Intent(getActivity(), MessageDetailActivity.class);
            intent.putExtra("type", "0");
            intent.putExtra("readTime", listData.getReadTime());
            intent.putExtra("id", listData.getId());
            intent.putExtra("title", listData.getTitle());
            intent.putExtra("content", listData.getContent());
            intent.putExtra("createTime", listData.getCreateTime());
            getActivity().startActivity(intent);
            getMessage();
        });
        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        listView.setOnItemLongClickListener((parent, view1, position, id1) -> {
            messagesDailog.show(getActivity().getFragmentManager(), "删除消息");
            id = messagesList.get(position).getId();
            Timber.e("id的值:" + id);
            return true;
        });

        /**
         * 删除消息
         */
        messagesDailog.setMessageListener(() -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", "0");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            messageRest.deleteMessage(objectId, token, 0, Integer.parseInt(id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Timber.e("删除消息---" + s);
                    getMessage();
                }, throwable -> Timber.e("删除消息throwable---" + throwable.getMessage()));

            messagesDailog.dismiss();
        });
    }

    public void getMessage() {
        //获取系统消息  1 = 系统消息      0 =  用户消息
        /**
         * @Header("X-CRM-Merchant-Id") String objectId,
         @Header("X-CRM-Access-Token") String accessToken,
         @Query("where") JSONObject jsonObject,
         @Query("skip") int skip,
         @Query("limit") int limit
         */
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageRest.attemgetMessage(objectId, token, jsonObject, 0, 10)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<String>() {
                @Override
                public void call(String s) {
                    mLoadingDialog.dismiss();
                    Timber.e("消息的返回" + s);
                    if (s.substring(0, 1).equals("{")) {
                        view.findViewById(R.id.ly_img_none).setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                        return;
                    }
                    List<Messages> message = JSON.parseArray(s, Messages.class);
                    if (message.size() != 0) {
                        view.findViewById(R.id.ly_img_none).setVisibility(View.GONE);
                        view.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
                        listView.setVisibility(View.VISIBLE);
                    }
                    messagesList = message;
                    messageAdapter.update(messagesList);

                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    mLoadingDialog.dismiss();
                    throwable.printStackTrace();
                }
            });
    }

    @Override
    public void onStart() {
        super.onStart();
        getMessage();
    }

}
