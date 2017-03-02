package com.cardvlaue.sys.apply;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.applyinfo.ApplyInfoActivity;
import com.cardvlaue.sys.baolinotice.BaoliNoticeActivity;
import com.cardvlaue.sys.bill.BillActivity;
import com.cardvlaue.sys.cardverify.CardPrivateActivity;
import com.cardvlaue.sys.cardverify.CardPublicActivity;
import com.cardvlaue.sys.cardverify.UpdateApplyBO;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.dialog.ContentLoadingDialog;
import com.cardvlaue.sys.face.FaceActivity;
import com.cardvlaue.sys.face.FaceVerifyBean;
import com.cardvlaue.sys.financeconfirm.FinanceConfirmActivity;
import com.cardvlaue.sys.financeintention.FinanceIntentionActivity;
import com.cardvlaue.sys.main.MainActivity;
import com.cardvlaue.sys.uploadphoto.UploadPhotoActivity;
import com.cardvlaue.sys.util.CacheUtil;
import com.cardvlaue.sys.util.ReadUtil;
import com.cardvlaue.sys.util.ToastUtil;
import java.util.ArrayList;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * 查询申请的adapter Created by Administrator on 2016/6/27.
 */
public class ApplyAdapter extends RecyclerView.Adapter {

    public OnItemClickListenter onItemClickListenter;
    private List<QueryApplyItemBO> mlist = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    private OnItemStatusClickListener OnItemStatusClickListener;  //申请的状态
    private OnItemRenewClickListener OnItemRenewClickListener;//续贷
    private TasksRepository repository;
    private String planFundTerm;
    private ContentLoadingDialog mLoadingDialog;
    private IFinancingRest rest;

    public ApplyAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        repository = ((CVApplication) context.getApplicationContext()).getTasksRepositoryComponent()
            .getTasksRepository();
        mLoadingDialog = ContentLoadingDialog.newInstance("加载中...");
        rest = HttpConfig.getClient().create(IFinancingRest.class);

    }

    public ApplyAdapter(Context context, List<QueryApplyItemBO> mlist) {
        this.context = context;
        this.mlist = mlist;
        this.inflater = LayoutInflater.from(context);
    }

    /**
     * 设置view,只做布局解析
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_apply, parent, false);
        return new ViewHolder(view);

    }

    /**
     * 设置数据
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            QueryApplyItemBO apply = mlist.get(position);
            if (mlist.size() - 1 == position) {
                viewHolder.view_bottom.setVisibility(View.VISIBLE);
            } else {
                viewHolder.view_bottom.setVisibility(View.GONE);
            }
            viewHolder.tv_time.setText(apply.getCreatedAt());
            viewHolder.tv_shop_name.setText(apply.getCorporateName());
            viewHolder.tv_status.setText(apply.getStatus());
            int index2 = apply.getAmountRequested().indexOf(".");
            String amountRequested = index2 != -1 ? apply.getAmountRequested().substring(0, index2)
                : apply.getAmountRequested();
            viewHolder.tv_amountRequest.setText(ReadUtil.fmtMicrometer(amountRequested) + "元");
            planFundTerm = apply.getPlanFundTerm();
            if (!TextUtils.isEmpty(planFundTerm)) {
                switch (planFundTerm) {
                    case "3":
                        planFundTerm = "91";
                        viewHolder.tv_planFundTerm.setText(planFundTerm + "天");
                        break;
                    case "6":
                        planFundTerm = "180";
                        viewHolder.tv_planFundTerm.setText(planFundTerm + "天");
                        break;
                    case "9":
                        planFundTerm = "273";
                        viewHolder.tv_planFundTerm.setText(planFundTerm + "天");
                        break;
                    default:
                        viewHolder.tv_planFundTerm.setText(apply.getPlanFundTerm() + "天");
                }
            } else {
                viewHolder.tv_planFundTerm.setText(apply.getPlanFundTerm() + "天");
            }

            switch (apply.getStatus().trim()) {
                case "提现中":
                    viewHolder.btn1.setVisibility(View.INVISIBLE);
                    viewHolder.btn2.setVisibility(View.INVISIBLE);
                    viewHolder.btn3.setVisibility(View.INVISIBLE);
                    break;
                case "审核中":
                    //默认为1表示符合，0不符合(重新上传照片)
                    if ("0".equals(apply.getIsUploadComplete())) {
                        viewHolder.btn1.setVisibility(View.VISIBLE);
                        viewHolder.btn2.setVisibility(View.INVISIBLE);
                        viewHolder.btn3.setVisibility(View.INVISIBLE);
                        viewHolder.btn1.setText("重新上传");
                    } else {
                        viewHolder.btn1.setVisibility(View.INVISIBLE);
                        viewHolder.btn2.setVisibility(View.INVISIBLE);
                        viewHolder.btn3.setVisibility(View.INVISIBLE);
                    }
                    break;
                case "补充资料":
                    viewHolder.btn1.setVisibility(View.VISIBLE);
                    viewHolder.btn2.setVisibility(View.INVISIBLE);
                    viewHolder.btn3.setVisibility(View.INVISIBLE);
                    viewHolder.btn1.setText("补全资料");
                    break;
                case "融资确认":
                    viewHolder.btn2.setVisibility(View.INVISIBLE);
                    viewHolder.btn3.setVisibility(View.INVISIBLE);
                    viewHolder.btn1.setVisibility(View.VISIBLE);
                    if (apply.getIsWithdrawConfirm().trim().equals("0")) {
                        //融资方案页面
                        viewHolder.btn1.setText(apply.getStatus());
                    }
                    /*else if (apply.getIsWithdrawConfirm().trim().equals("1") && apply
                        .getIsKalaRecognize().trim().equals("0")) {
                        //人脸识别验证成功一次，传给crm为1      验证达到3次，传给crm为2
                        LoginResponse loginResponse = repository.getLogin();
                        String token = loginResponse.accessToken;
                        UpdateApplyBO updateApplyBO = new UpdateApplyBO();
                        String json = CacheUtil.getFaceFlag(context, apply.getMerchantId());
                        int errorCount = 0;
                        boolean status = false;
                        if (!TextUtils.isEmpty(json)) {
                            FaceVerifyBean fvb = JSON.parseObject(json, FaceVerifyBean.class);
                            errorCount = fvb.count;
                            status = fvb.status;
                            Timber.e("FaceVerifyBean:%s", JSON.toJSONString(fvb));
                            if (status) {
                                viewHolder.btn1.setText("绑定银行卡");
                                updateApplyBO.setIsKalaRecognize("1");
                                updateApply(apply.getMerchantId(), token, apply.getApplicationId(),
                                    updateApplyBO);
                            } else if (errorCount > 2) {
                                viewHolder.btn1.setText("绑定银行卡");
                                updateApplyBO.setIsKalaRecognize("2");
                                Timber
                                    .e("==========22222=======" + apply.getMerchantId() + "=token=="
                                        + token
                                        + "ApplicationId" + apply.getApplicationId() + JSON
                                        .toJSONString(updateApplyBO));
                                updateApply(apply.getMerchantId(), token, apply.getApplicationId(),
                                    updateApplyBO);
                            } else {
                                //viewHolder.btn1.setText("人脸识别");
                            }
                        } else {
                           // viewHolder.btn1.setText("人脸识别");
                        }
                    } */
                    else if (apply.getIsWithdrawConfirm().trim().equals("1") && apply.getIsValidBankCard().equals("0")) {
                        //(apply.getIsKalaRecognize().trim().equals("1") || apply.getIsKalaRecognize().trim().equals("2")) &&
                        viewHolder.btn1.setText("绑定银行卡");
                    } else if (apply.getIsValidBankCard().equals("1")) {
                        viewHolder.btn1.setText("上传确认书");
                    }
                    break;
                case "申请未通过":
                    //如果是重新申请的话就显示
                    if (apply.getIsRenewable().equals("1")) {
                        viewHolder.btn1.setText("重新申请");
                        viewHolder.btn1.setVisibility(View.VISIBLE);
                        viewHolder.btn2.setVisibility(View.INVISIBLE);
                        viewHolder.btn3.setVisibility(View.INVISIBLE);
                    } else {
                        viewHolder.btn1.setVisibility(View.INVISIBLE);
                        viewHolder.btn2.setVisibility(View.INVISIBLE);
                        viewHolder.btn3.setVisibility(View.INVISIBLE);
                    }
                  /*  viewHolder.btn1.setOnClickListener(v->{
                        if (OnItemRenewClickListener != null) {
                            Timber.e("重新申请");
                            OnItemRenewClickListener.OnItemRenewClick(holder.getAdapterPosition());
                        }
                    });*/
                    break;
                case "审核未通过":
                    //如果是重新申请的话就显示
                    if (apply.getIsRenewable().equals("1")) {
                        viewHolder.btn1.setText("重新申请");
                        viewHolder.btn1.setVisibility(View.VISIBLE);
                        viewHolder.btn2.setVisibility(View.INVISIBLE);
                        viewHolder.btn3.setVisibility(View.INVISIBLE);
                    } else {
                        viewHolder.btn1.setVisibility(View.INVISIBLE);
                        viewHolder.btn2.setVisibility(View.INVISIBLE);
                        viewHolder.btn3.setVisibility(View.INVISIBLE);
                    }
                   /* viewHolder.btn1.setOnClickListener(v->{
                        if (OnItemRenewClickListener != null) {
                            Timber.e("审核未通过重新申请");
                            viewHolder.btn2.setVisibility(View.INVISIBLE);
                            viewHolder.btn3.setVisibility(View.INVISIBLE);
                            OnItemRenewClickListener.OnItemRenewClick(holder.getAdapterPosition());
                        }
                    });*/
                    break;
                case "还款中":
                    viewHolder.btn3.setVisibility(View.INVISIBLE);
                    viewHolder.btn1.setText("查看对账单");
                    viewHolder.btn2.setText("保理通知书");
                    viewHolder.btn1.setVisibility(View.VISIBLE);
                    viewHolder.btn2.setVisibility(View.VISIBLE);
                    break;
                case "已完成":
                    viewHolder.btn2.setVisibility(View.INVISIBLE);
                    viewHolder.btn3.setVisibility(View.INVISIBLE);
                    viewHolder.btn1.setVisibility(View.VISIBLE);
                    viewHolder.btn1.setText("续贷");
                    break;
                case "申请中":
                    viewHolder.btn2.setVisibility(View.INVISIBLE);
                    viewHolder.btn3.setVisibility(View.INVISIBLE);
                    viewHolder.btn1.setVisibility(View.VISIBLE);
                    viewHolder.btn1.setText("继续申请");
                    break;
            }

            //btn1的点击事件
            viewHolder.btn1.setOnClickListener(v -> {
                LoginResponse loginResponse = repository.getLogin();
                String token = loginResponse.accessToken;
                MainActivity aa = (MainActivity) context;
                mLoadingDialog.show(aa.getSupportFragmentManager(), "tag");
                repository.getUserInfo(apply.getMerchantId(), token)
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe(s -> {
                        mLoadingDialog.dismiss();
                        if (TextUtils.isEmpty(s.getError())) {
                            repository.saveUserInfo(s);
                            repository.saveSetStep(apply.getSetStep() + "");
                            repository.saveLogin(apply.getMerchantId());//objectId
                            repository.saveMerchantId(apply.getMerchantId());
                            repository.saveApplicationId(apply.getApplicationId());
                            repository.saveCreditId(apply.getCreditId());
                            switch (apply.getStatus().trim()) {
                                case "审核中":
                                    //默认为1表示符合，0不符合(重新上传照片)
                                    if ("0".equals(apply.getIsUploadComplete())) {
                                        Intent intent = new Intent(context,
                                            UploadPhotoActivity.class);
                                        intent.putExtra("again", "again");
                                        context.startActivity(intent);
                                    }
                                    break;
                                case "补充资料":
                                    Intent intent = new Intent(context,
                                        UploadPhotoActivity.class);
                                    intent.putExtra("addFile", "addFile");
                                    context.startActivity(intent);
                                    break;
                                case "融资确认":
                                    if (apply.getIsWithdrawConfirm().trim()
                                        .equals("0")) {
                                        //融资方案页面
                                        Intent intentFinanceConfirm = new Intent(context,
                                            FinanceConfirmActivity.class);
                                        context.startActivity(intentFinanceConfirm);
                                    } /*else if (apply.getIsWithdrawConfirm().trim()
                                        .equals("1") && apply
                                        .getIsKalaRecognize().trim().equals("0")) {
                                        int errorCount = 0;
                                        boolean status = false;
                                        String json = CacheUtil
                                            .getFaceFlag(context, apply.getMerchantId());
                                        if (!TextUtils.isEmpty(json)) {
                                            FaceVerifyBean fvb = JSON
                                                .parseObject(json, FaceVerifyBean.class);
                                            errorCount = fvb.count;
                                            status = fvb.status;
                                            Timber
                                                .e("FaceVerifyBean:%s", JSON.toJSONString(fvb));
                                            if (status || errorCount > 2) {
                                                if (apply.getSecondaryBankAccountType()
                                                    .equals("对私")) {
                                                    //对私
                                                    Intent intentCardPrivate = new Intent(
                                                        context,
                                                        CardPrivateActivity.class);
                                                    context.startActivity(intentCardPrivate);
                                                } else {
                                                    //对公
                                                    Intent intentCardPublic = new Intent(
                                                        context,
                                                        CardPublicActivity.class);
                                                    context.startActivity(intentCardPublic);
                                                }
                                            } else {
                                                Intent intentFaceConfirm = new Intent(context,
                                                    FaceActivity.class);
                                                context.startActivity(intentFaceConfirm);
                                            }
                                        } else {
                                            Intent intentFaceConfirm = new Intent(context,
                                                FaceActivity.class);
                                            context.startActivity(intentFaceConfirm);
                                        }
                                    }*/ else if (apply.getIsWithdrawConfirm().trim().equals("1")  && apply.getIsValidBankCard().equals("0")) {
                                        //&& (apply.getIsKalaRecognize().trim().equals("1")|| apply.getIsKalaRecognize().trim().equals("2"))
                                        if (apply.getSecondaryBankAccountType().equals("对私")) {
                                            //对私
                                            Intent intentCardPrivate = new Intent(context,
                                                CardPrivateActivity.class);
                                            context.startActivity(intentCardPrivate);
                                        } else {
                                            //对公
                                            Intent intentCardPublic = new Intent(context,
                                                CardPublicActivity.class);
                                            context.startActivity(intentCardPublic);
                                        }
                                    } else if (apply.getIsValidBankCard().equals("1")) {
                                        //上传确认书
                                        Intent intentUpload = new Intent(context,
                                            UploadPhotoActivity.class);
                                        intentUpload.putExtra("confirmation", "confirmation");
                                        context.startActivity(intentUpload);
                                    }
                                    break;
                                case "申请未通过":
                                    Timber.e("申请未通过" + apply.getStatus());
                                    //如果是重新申请的话就显示
                                    if (OnItemRenewClickListener != null) {
                                        Timber.e("重新申请");
                                        OnItemRenewClickListener
                                            .OnItemRenewClick(holder.getAdapterPosition());
                                    }
                                    break;
                                case "审核未通过":
                                    Timber.e("申请未通过" + apply.getStatus());
                                    //如果是重新申请的话就显示

                                    if (OnItemRenewClickListener != null) {
                                        Timber.e("审核未通过重新申请");
                                        viewHolder.btn2.setVisibility(View.INVISIBLE);
                                        viewHolder.btn3.setVisibility(View.INVISIBLE);
                                        OnItemRenewClickListener
                                            .OnItemRenewClick(holder.getAdapterPosition());
                                    }
                                    break;
                                case "还款中":
                                    Intent intentBtn1 = new Intent(context, BillActivity.class);
                                    context.startActivity(intentBtn1);
                                    break;
                                case "已完成":
                                    viewHolder.btn1.setText("续贷");
                                    if (OnItemRenewClickListener != null) {
                                        OnItemRenewClickListener
                                            .OnItemRenewClick(holder.getAdapterPosition());
                                    }
                                    break;
                                case "申请中":
                                    Timber.e("apply.getSetStep():" + apply.getSetStep()
                                        + "申请中getMerchantId:"
                                        + repository.getMerchantId() + "apply.getMerchantId():"
                                        + apply
                                        .getMerchantId() + "getApplicationId:" + repository
                                        .getApplicationId());
                                    int step = apply.getSetStep();
                                    if (step != 0) {
                                        step = step + 10;
                                        ((CVApplication) context.getApplicationContext())
                                            .getQueue()
                                            .setPosition(step, context);
                                    } else {
                                        context.startActivity(new Intent(context,
                                            FinanceIntentionActivity.class));
                                    }
                                    //  ((CVApplication) context.getApplicationContext()).getQueue().setPosition(50, context);
                                    break;
                            }
                        }
                    }, throwable -> mLoadingDialog.dismissAllowingStateLoss());
            });

            //btn2的点击事件
            viewHolder.btn2.setOnClickListener(v -> {
                switch (apply.getStatus().trim()) {
                    case "还款中":
                        repository.saveCreditId(apply.getCreditId());
                        repository.saveMerchantId(apply.getMerchantId());
                        repository.saveApplicationId(apply.getApplicationId());
                        Intent intentBtn1 = new Intent(context, BaoliNoticeActivity.class);
                        context.startActivity(intentBtn1);
                        break;
                }
            });

            //状态
            viewHolder.tv_status.setOnClickListener(v -> {
                 /*   if (OnItemStatusClickListener != null) {
                        OnItemStatusClickListener.OnItemStatusClick(holder.getAdapterPosition());
                    }*/

                //只有审核没有通过才不可以点击
                repository.saveApplicationId(apply.getApplicationId());
                repository.saveMerchantId(apply.getMerchantId());
                repository.saveCreditId(apply.getCreditId());
                Intent intent = new Intent(context, ApplyInfoActivity.class);
                intent.putExtra("applyinfo", "tag");
                context.startActivity(intent);
            });
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

    public void setOnItemStatusClickListener(OnItemStatusClickListener OnItemStatusClickListener) {
        this.OnItemStatusClickListener = OnItemStatusClickListener;
    }

    public void update(List<QueryApplyItemBO> list) {
        mlist.clear();
        mlist.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnItemRenewClickListener(OnItemRenewClickListener onItemRenewClickListener) {
        OnItemRenewClickListener = onItemRenewClickListener;
    }

    public void updateApply(String merchantId, String token, String applicationId,
        UpdateApplyBO updateApplyBO) {
        rest.updateApply(merchantId, token, applicationId, updateApplyBO)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(loginBO -> {
                    Timber.e(loginBO.getUpdatedAt() + "==更新申请adapter====" + JSON.toJSONString(loginBO));
                    if (!TextUtils.isEmpty(loginBO.getError())) {
                        ToastUtil.showFailure(context, loginBO.getError());
                    }
                    putStatus(false, -1, merchantId);
                }, throwable -> {
                    Timber.e(throwable.getMessage() + "==throwable==" + JSON.toJSONString(throwable));
                }
            );
    }

    private void putStatus(boolean status, int current, String merchantId) {
        FaceVerifyBean fvb = new FaceVerifyBean(status, current + 1);
        String json = JSON.toJSONString(fvb);
        CacheUtil.putFaceFlag(context, merchantId, json);
    }

    /**
     * 对外暴露的点击事件接口
     */

    public interface OnItemClickListenter {

        void OnItemClick(View v, int position);
    }

    public interface OnItemStatusClickListener {

        void OnItemStatusClick(int position);
    }


    public interface OnItemRenewClickListener {

        void OnItemRenewClick(int position);
    }

    /**
     * viewHolder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        //时间,店铺的名字,状态，融资额度,融资期限
        TextView tv_time, tv_shop_name, tv_status, tv_amountRequest, tv_planFundTerm;
        Button btn1, btn2, btn3;
        View view1, view_bottom;
        LinearLayout ll_item;

        //构造函数
        public ViewHolder(final View itemView) {
            super(itemView);
            //初始化方法
            inint(itemView);
            //点击事件
            itemView.setOnClickListener(v -> {
                if (onItemClickListenter != null) {
                    onItemClickListenter.OnItemClick(itemView, getLayoutPosition());
                }
            });
        }

        void inint(View view) {
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_shop_name = (TextView) view.findViewById(R.id.tv_shop_name);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
            tv_amountRequest = (TextView) view.findViewById(R.id.tv_amountRequest);
            tv_planFundTerm = (TextView) view.findViewById(R.id.tv_planFundTerm);

            view_bottom = view.findViewById(R.id.view_bottom);
            view1 = view.findViewById(R.id.view1);
            btn1 = (Button) view.findViewById(R.id.btn1);
            btn2 = (Button) view.findViewById(R.id.btn2);
            btn3 = (Button) view.findViewById(R.id.btn3);
        }
    }
}
