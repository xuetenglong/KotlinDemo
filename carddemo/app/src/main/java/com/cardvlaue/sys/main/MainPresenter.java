package com.cardvlaue.sys.main;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.UserInfoNewResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.util.DeviceUtil;
import com.cardvlaue.sys.util.ReadUtil;
import com.cardvlaue.sys.util.RxBus2;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Listens to user actions from the UI ({@link MainFragment}), retrieves the data and updates the UI
 * as required.
 */
final class MainPresenter implements MainContract.Presenter {

    private final TasksRepository mTasksRepository;
    private final MainContract.View mMainView;
    private MainActivity mContext;

    @Inject
    MainPresenter(Context context, TasksRepository tasksRepository, MainContract.View mainView) {
        mContext = (MainActivity) context;
        mTasksRepository = tasksRepository;
        mMainView = mainView;
    }

    @Inject
    void setupListeners() {
        mMainView.setPresenter(this);
    }

    @Override
    public void loadUserInfo() {
        LoginResponse loginResponse = mTasksRepository.getLogin();
        String idStr = loginResponse.objectId;
        String tokenStr = loginResponse.accessToken;
        Timber.e("loadUserInfo:%s||%s", idStr, tokenStr);
        if (!TextUtils.isEmpty(tokenStr) && !TextUtils.isEmpty(idStr)) {
            mTasksRepository.getUserInfo(idStr, tokenStr)
                .compose(mContext.bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Timber.e("getUserInfo:%s", JSON.toJSONString(s));
                    switch (s.responseSuccess(mContext)) {
                        case 0:
                            mTasksRepository.saveUserInfo(s);
                            RxBus2.Companion.get().send(MainFragment.BUS_LOAD_USER_END);
                            break;
                        default:
                            break;
                    }
                }, throwable -> {
                    Timber.e("getUserInfoError:%s", throwable.getMessage());
                });
        }
    }


    @Override
    public void uploadContract() {
        String phone = mTasksRepository.getMobilePhone();
        if (!TextUtils.isEmpty(phone)) {
            new Thread(() -> {
                Timber.e("Upload Contract Start!");
                //获取联系人信息的Uri
                Uri uri = ContactsContract.Contacts.CONTENT_URI;
                //获取ContentResolver
                ContentResolver contentResolver = mContext.getContentResolver();
                //查询数据，返回Cursor
                Cursor cursor = contentResolver.query(uri, null, null, null, null);
                List<Map<String, Object>> list = new ArrayList<>();
                while (cursor != null && cursor.moveToNext()) {
                    StringBuilder sb = new StringBuilder();
                    //获取联系人的ID
                    String contactId = cursor
                        .getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    //获取联系人的姓名
                    String name = cursor
                        .getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    //构造联系人信息
                    sb.append("contactId=").append(contactId).append(",Name=").append(name);
                    String id = cursor
                        .getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));//联系人ID
                    //查询电话类型的数据操作
                    Cursor phones = contentResolver
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                            null, null);
                    while (phones != null && phones.moveToNext()) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        String phoneNumber = phones
                            .getString(
                                phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //添加Phone的信息
                        sb.append(",Phone=").append(phoneNumber);
                        map.put("mobile", phoneNumber);
                        ReadUtil.putEmail(contentResolver, map, sb, contactId);
                        ReadUtil.putAdress(contentResolver, map, sb, contactId);
                        ReadUtil.putCompany(contentResolver, map, sb, contactId, id);
                        list.add(map);
                    }
                    if (phones != null) {
                        phones.close();
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }

                // 提交通讯录
                JSONObject cData = new JSONObject();
                cData.put("userMobile", phone);
                cData.put("addressBookList", list);
                Timber.e("提交通讯录:%s==6666666666666==%s", JSON.toJSONString(cData),
                    DeviceUtil.getImei(mContext));
                mTasksRepository.uploadAddAddressBook(DeviceUtil.getImei(mContext), cData)
                    .compose(mContext.bindToLifecycle())
                    .subscribe(imei -> Timber.e("uploadContract Response:%s", JSON.toJSONString(imei)),
                        throwable -> Timber.e("uploadContract Error:%s", throwable.getMessage()));
            }).start();
        }
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
    }
}
