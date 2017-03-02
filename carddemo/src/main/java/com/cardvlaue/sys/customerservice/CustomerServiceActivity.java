package com.cardvlaue.sys.customerservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.cardvlaue.sys.BaseActivity;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import java.util.HashMap;

public class CustomerServiceActivity extends BaseActivity {

    private TasksRepository repository;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);
        MQConfig.init(this, "d226ece3e8a9a4f0604e3850c8d7a38e", new OnInitCallback() {
            @Override
            public void onSuccess(String clientId) {
                Toast.makeText(CustomerServiceActivity.this, "init success", Toast.LENGTH_SHORT)
                    .show();
            }

            @Override
            public void onFailure(int code, String message) {
                Toast.makeText(CustomerServiceActivity.this, "init failure", Toast.LENGTH_SHORT)
                    .show();
            }
        });
        repository = ((CVApplication) getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();

        name = repository.getUserInfo().ownerName;

        HashMap<String, String> clientInfo = new HashMap<>();
        clientInfo.put("name", name);
        //clientInfo.put("tel", repository.queryGpsAddress());
        Intent intent = new MQIntentBuilder(this)
            .setCustomizedId(repository.getMobilePhone())//自己设置的用户id
            .setClientInfo(clientInfo)
            .build();
        startActivity(intent);
        finish();
    }
}
