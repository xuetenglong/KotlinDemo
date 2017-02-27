package com.cardvlaue.sys.newalipayverify;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.cardvlaue.sys.R;
import org.lzh.framework.updatepluginlib.creator.DialogCreator;
import org.lzh.framework.updatepluginlib.model.Update;
import org.lzh.framework.updatepluginlib.util.SafeDialogOper;

/**
 * Created by cardvalue on 2017/1/5.
 */
class AlipayDownloadDialog extends DialogCreator {

    @Override
    public Dialog create(Update update, Activity context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_alipay, null);
        ((TextView) view.findViewById(R.id.message)).setText("您尚未安装支付宝APP, 请安装后重试!");
        ((Button) view.findViewById(R.id.confirm_btn)).setText("立即下载");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);//设置自定义样式布局到对话框
        AlertDialog dialog = builder.create();
        view.findViewById(R.id.confirm_btn).setOnClickListener(view1 -> {
            SafeDialogOper.safeDismissDialog(dialog);
            sendDownloadRequest(update, context);
        });
        return dialog;
    }

}
