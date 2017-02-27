package com.cardvlaue.sys.uploadphoto;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.cardvlaue.sys.CVApplication;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.UploadImagePager.ImagePagerActivity;
import com.cardvlaue.sys.amount.IFinancingRest;
import com.cardvlaue.sys.apply.HttpConfig;
import com.cardvlaue.sys.data.LoginResponse;
import com.cardvlaue.sys.data.source.TasksRepository;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.util.RxBus;
import com.cardvlaue.sys.util.ToastUtil;
import java.util.Arrays;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Administrator on 2016/10/18.
 */
public class UploadPhotoDailog extends DialogFragment {

    public static final String TITILE = "TIP_MSG";
    public static final String CONTENT = "content";
    private ItemDialogListsAdapter dialogListsAdapter;
    private RecyclerView picRecyclerView;
    private Animator anim2;
    private Animator anim1;
    private List<NewFileListsImgBO> dialogPic;
    private IFinancingRest rest;//更新申请
    private TasksRepository repository;
    private Animation mEndAnimation;

    public static UploadPhotoDailog newInstance(String title, String content) {
        UploadPhotoDailog fragment = new UploadPhotoDailog();
        Bundle args = new Bundle();
        args.putString(TITILE, title);
        args.putString(CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rest = HttpConfig.getClient().create(IFinancingRest.class);
        repository = ((CVApplication) getActivity().getApplication()).getTasksRepositoryComponent()
            .getTasksRepository();
        //系统自带的全屏 dialog
        //setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setDimAmount(0);
        View view = inflater.inflate(R.layout.dialog_upload, container, false);
       /* getDialog().getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        getDialog(). getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams p =  getDialog().getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.height = ViewGroup.LayoutParams.MATCH_PARENT;;//高度自己设定
        getDialog().getWindow().setAttributes(p);
        getDialog().getWindow().setLayout(p.width,p.height );*/
        this.getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                anim1.start();
                view.findViewById(R.id.rl_mobile).startAnimation(mEndAnimation);
                anim1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        BusIndustrySelect select = new BusIndustrySelect(
                            UploadPhotoActivity.BUS_UPLOADPHOTO_CODE);
                        select.setTypeId("cancel");
                        RxBus.getDefaultBus().send(select);
                        dismiss();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
            return false;
        });

        LinearLayout image = (LinearLayout) view.findViewById(R.id.layout);

        PropertyValuesHolder valueHolder_1 = PropertyValuesHolder.ofFloat(
            "scaleX", 1f, 0f);
        PropertyValuesHolder valuesHolder_2 = PropertyValuesHolder.ofFloat(
            "scaleY", 1f, 0f);
        anim1 = ObjectAnimator.ofPropertyValuesHolder(image, valueHolder_1,
            valuesHolder_2);
        anim1.setDuration(250);
        anim1.setInterpolator(new LinearInterpolator());

        PropertyValuesHolder valueHolder_3 = PropertyValuesHolder.ofFloat(
            "scaleX", 0.1f, 1f);
        PropertyValuesHolder valuesHolder_4 = PropertyValuesHolder.ofFloat(
            "scaleY", 0.1f, 1f);
        anim2 = ObjectAnimator.ofPropertyValuesHolder(image, valueHolder_3,
            valuesHolder_4);
        anim2.setDuration(300);
        anim2.setInterpolator(new LinearInterpolator());
        anim2.start();

        mEndAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        mEndAnimation.setFillAfter(true);
        mEndAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.findViewById(R.id.tv_titile).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.findViewById(R.id.rl_mobile).setVisibility(View.GONE);
                view.findViewById(R.id.rl_mobile).clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        ((TextView) view.findViewById(R.id.tv_titile)).setText(getArguments().getString(TITILE));
        picRecyclerView = (RecyclerView) view.findViewById(R.id.rv_pic);
        picRecyclerView.getBackground().setAlpha(210);
        String content = getArguments().getString(CONTENT);
        Timber.e("删除的图片dialog：" + content);
        dialogListsAdapter = new ItemDialogListsAdapter(1);

        picRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        });

        picRecyclerView.setAdapter(dialogListsAdapter);
        if (content != null) {
            // List<NewFileListsItemBO> dialogPic = JSON.parseArray(content,NewFileListsItemBO.class);
            dialogPic = JSON.parseArray(content, NewFileListsImgBO.class);
           /* Gson gson = new Gson();
            //NewFileListsItemBO  List<NewFileListsImgBO>
            List<NewFileListsItemBO> dialogPic = gson.fromJson(content, List<NewFileListsItemBO>);
            Timber.e("dialogPic"+ JSON.toJSONString(dialogPic));*/
            dialogListsAdapter.updateData(dialogPic);
        }

        dialogListsAdapter.setOnItemAddClickListener(position -> {
            Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
            String[] list = new String[dialogPic.size()];
            String[] listTxt = new String[dialogPic.size()];
            int index = 0;
            int indexTxt = 0;
            for (NewFileListsImgBO p : dialogPic) {
                list[index++] = p.getUrl().toString();
                if (!TextUtils.isEmpty(p.getDescript())) {
                    listTxt[indexTxt++] = p.getDescript().toString();
                }
            }
            intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS,
                Arrays.copyOf(list, dialogPic.size()));
            intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
            intent
                .putExtra(ImagePagerActivity.EXTRA_DEMO, Arrays.copyOf(listTxt, dialogPic.size()));
            startActivity(intent);
        });

        dialogListsAdapter.setItemReasonClickListener(position -> {
            newDeletelists(dialogPic.get(position).getFileId(), position);
        });

        view.findViewById(R.id.rl_mobile).setOnClickListener(v -> {
                anim1.start();
                view.findViewById(R.id.rl_mobile).startAnimation(mEndAnimation);
                anim1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        BusIndustrySelect select = new BusIndustrySelect(
                            UploadPhotoActivity.BUS_UPLOADPHOTO_CODE);
                        select.setTypeId("cancel");
                        RxBus.getDefaultBus().send(select);
                        dismiss();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

            }
        );
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
      /*  Dialog d = getDialog();
        if (d!=null){
            d.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        }*/

        //设置透明度
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams winLayoutParams = window.getAttributes();
        winLayoutParams.alpha = 1f;
        window.setAttributes(winLayoutParams);
    }

/*
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams  attributes = window.getAttributes();

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      *//*  if (needFullScreen)
        {*//*
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        *//*}*//*
    }*/


    /**
     * 删除图片
     */
    public void newDeletelists(String fileId, int position) {
        LoginResponse loginResponse = repository.getLogin();
        String objectId = loginResponse.objectId;
        String token = loginResponse.accessToken;

        if (!TextUtils.isEmpty(repository.getMerchantId())) {
            objectId = repository.getMerchantId();
        }
        Timber.e("补件的：" + objectId + "token:" + token);
        rest.newDeletelists(objectId, token, objectId, fileId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(newFileListsBO -> {
                Timber.e("删除图片：" + JSON.toJSONString(newFileListsBO));
                if (newFileListsBO.responseSuccess(getActivity()) == 0) {
                    if (newFileListsBO.getResults() != null) {
                        ToastUtil.showFailure(getActivity(), "删除成功");
                    } else if (newFileListsBO.getError() != null) {
                        ToastUtil.showFailure(getActivity(), newFileListsBO.getError());
                    } else {
                        dialogPic.remove(dialogPic.get(position));
                        dialogListsAdapter.updateData(dialogPic);
                        ToastUtil.showFailure(getActivity(), "删除成功");
                        if (dialogPic.size() == 0) {
                            dismiss();
                            BusIndustrySelect select1 = new BusIndustrySelect(
                                UploadPhotoActivity.BUS_UPLOADPHOTO_CODE);
                            select1.setTypeId("cancel");
                            RxBus.getDefaultBus().send(select1);
                        }
                        BusIndustrySelect select = new BusIndustrySelect(
                            UploadPhotoActivity.BUS_UPLOADPHOTO_CODE);
                        select.setTypeId("delete");
                        RxBus.getDefaultBus().send(select);

                    }
                } else if (newFileListsBO.responseSuccess(getActivity()) == -1) {
                    ToastUtil.showFailure(getActivity(), newFileListsBO.getError());
                }
            }, throwable -> {
                Timber
                    .e("throwable删除图片失败newFileListsBO" + JSON.toJSONString(throwable) + "===="
                        + throwable
                        .getMessage());
                ToastUtil.showFailure(getActivity(), "删除图片失败，请稍后再试");
            });
    }


}
