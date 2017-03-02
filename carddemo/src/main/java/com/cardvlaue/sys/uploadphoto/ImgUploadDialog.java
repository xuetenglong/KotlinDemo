package com.cardvlaue.sys.uploadphoto;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.cardvlaue.sys.R;

/**
 * 上传图片对话框
 * <p/>
 * Created by cardvalue on 2016/6/14.
 */
public class ImgUploadDialog extends DialogFragment {

    public static final String TAG = "ImgUploadDialog";
    public static final String TYPE = "type";
    public static final String SIZE = "size";  // 选择张数
    private static final int REQUEST_CODE = 200;
    private OnClickTakePhoto takePhotoListener;
    private OnSelectPhoto selectPhotoListener;

    public static ImgUploadDialog newInstance() {
        ImgUploadDialog fragment = new ImgUploadDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    public static ImgUploadDialog newInstance(String type, int size) {
        ImgUploadDialog fragment = new ImgUploadDialog();
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        args.putInt(SIZE, size);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_img_upload, container, false);
//        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    public void initView(View view) {
        view.findViewById(R.id.view_photo).setOnClickListener(v -> dismiss());
        //拍照
        view.findViewById(R.id.tv_img_upload_camera).setOnClickListener(v -> {
            dismiss();
            if (takePhotoListener != null) {
                takePhotoListener.takePhoto();
            }
        });
        //从手机相册选择
        view.findViewById(R.id.tv_img_upload_album).setOnClickListener(v -> {
            dismiss();
            if (selectPhotoListener != null) {
                selectPhotoListener.selectPhoto();
            }
        });
        view.findViewById(R.id.tv_img_upload_dismiss).setOnClickListener(v -> dismiss());
    }

    private void toast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 拍照
     */
    public void setTakePhotoListener(OnClickTakePhoto takePhotoListener) {
        this.takePhotoListener = takePhotoListener;
    }

    /**
     * 选择相册
     */
    public void setSelectPhotoListener(OnSelectPhoto selectPhotoListener) {
        this.selectPhotoListener = selectPhotoListener;
    }

    public interface OnClickTakePhoto {

        void takePhoto();
    }

    public interface OnSelectPhoto {

        void selectPhoto();
    }
}
