package com.cardvlaue.sys.posadd;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.cardvlaue.sys.R;
import com.cardvlaue.sys.data.PosBus;
import com.cardvlaue.sys.shopadd.BusIndustrySelect;
import com.cardvlaue.sys.util.RxBus;
import java.util.ArrayList;
import timber.log.Timber;

public class PosAddVerifyDialog extends AppCompatDialogFragment {

    public static final String ARGUMENT_EVENT = "event";
    public static final String ARGUMENT_VERIFY_ID = "verifyId";
    public static final String ARGUMENT_QUESTION = "question";
    public static final String ARGUMENT_LISTS = "lists";
    public static final String TAG = "PosAddVerifyDialog_tag";
    public static final String BUS_CLOSE = "PosAddVerifyDialog_BUS_CLOSE";

    @BindView(R.id.ll_pos_add_dialog_content)
    LinearLayout mContentView;

    @BindView(R.id.rv_pos_add_dialog_verify)
    RecyclerView mListsView;

    @BindView(R.id.tv_pos_add_dialog_question)
    TextView mQuestionView;

    //tag 1为添加商编   2 商编管理
    public static PosAddVerifyDialog newInstance(String event, String verifyId, String question,
        ArrayList<String> data, String tag) {
        Bundle args = new Bundle();
        args.putString(ARGUMENT_EVENT, event);
        args.putString(ARGUMENT_VERIFY_ID, verifyId);
        args.putString(ARGUMENT_QUESTION, question);
        args.putStringArrayList(ARGUMENT_LISTS, data);
        args.putString(TAG, tag);
        PosAddVerifyDialog dialog = new PosAddVerifyDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @OnClick(R.id.iv_pos_add_dialog_close)
    void clickClose() {
        if ("1".equals(getArguments().getString(TAG))) {
            dismiss();
            BusIndustrySelect select = new BusIndustrySelect(BUS_CLOSE);
            select.setTypeId("PosAddVerifyDialog_BUS_CLOSE");//PosAddVerifyDialog
            RxBus.getDefaultBus().send(select);
        } else {
            dismiss();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.dialog_pos_add_verify, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mQuestionView.setText(getArguments().getString(ARGUMENT_QUESTION));

        PosAddVerifyAdapter mAdapter = new PosAddVerifyAdapter();
        mListsView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mListsView.setAdapter(mAdapter);

        PropertyValuesHolder valueHolder_3 = PropertyValuesHolder.ofFloat("scaleX", 0.1f, 1f);
        PropertyValuesHolder valuesHolder_4 = PropertyValuesHolder.ofFloat("scaleY", 0.1f, 1f);
        Animator anim2 = ObjectAnimator
            .ofPropertyValuesHolder(mContentView, valueHolder_3, valuesHolder_4);
        anim2.setDuration(300);
        anim2.setInterpolator(new LinearInterpolator());
        anim2.start();

        ArrayList<String> data = getArguments().getStringArrayList(ARGUMENT_LISTS);
        if (data != null) {
            mAdapter.updateData(data);
            mAdapter.setOnItemClickListener(position -> {
                dismiss();
                String eventStr = getArguments().getString(ARGUMENT_EVENT);
                String verifyStr = getArguments().getString(ARGUMENT_VERIFY_ID);
                String idStr = data.get(position);
                Timber.e("商编：%s||%s||%s", eventStr, verifyStr, idStr);
                RxBus.getDefaultBus().send(new PosBus(eventStr, verifyStr, idStr));
            });
        }
    }

}
