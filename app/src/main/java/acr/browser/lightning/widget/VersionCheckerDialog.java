package acr.browser.lightning.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import acr.browser.lightning.R;
import acr.browser.lightning.version.entity.VersionEntity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MartinKent on 2017/6/5.
 */

public class VersionCheckerDialog extends Dialog {
    @BindView(R.id.tv_dialog_confirm_title)
    TextView mVersionCheckerTitleTv;
    @BindView(R.id.tv_dialog_version_checker_latest_version)
    TextView mVersionCheckerLatestVersionTv;
    @BindView(R.id.tv_dialog_version_checker_latest_version_size)
    TextView mVersionCheckerLatestVersionSizeTv;
    @BindView(R.id.tv_dialog_confirm_left_button)
    TextView mVersionCheckerLeftButtonTv;
    @BindView(R.id.tv_dialog_confirm_right_button)
    TextView mVersionCheckerRightButtonTv;

    private VersionEntity mVersionEntity;
    private OnClickListener mOnClickListener;

    public VersionCheckerDialog(@NonNull Context context, VersionEntity entity) {
        super(context, R.style.dialog_common_style);
        init(context, entity);
    }

    private void init(Context context, VersionEntity entity) {
        super.setContentView(LayoutInflater.from(context).inflate(R.layout.layout_dialog_version_checker, null));
        ButterKnife.bind(this);
        this.mVersionEntity = entity;

        mVersionCheckerTitleTv.setText(entity.getTitle());
        mVersionCheckerLatestVersionTv.setText(entity.getMobileVersion());
        String msize = String.format(Locale.getDefault(), "%.02fM", 1.0f * entity.getSize() / 1024);
        mVersionCheckerLatestVersionSizeTv.setText(msize);

        if (1 != entity.getMustUpdate()) {
            setLeftButton("取消");
        }
        setRightButton("立即更新");
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        if (null != title) {
            mVersionCheckerTitleTv.setText(title);
            mVersionCheckerTitleTv.setVisibility(View.VISIBLE);
        } else {
            mVersionCheckerTitleTv.setVisibility(View.GONE);
        }
    }

    @Deprecated
    @Override
    public void setContentView(@LayoutRes int layoutResID) {

    }

    @Deprecated
    @Override
    public void setContentView(@NonNull View view) {

    }

    @Deprecated
    @Override
    public void setContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {

    }

    private void setLeftButton(String msg) {
        if (null != msg) {
            mVersionCheckerLeftButtonTv.setText(msg);
            mVersionCheckerLeftButtonTv.setVisibility(View.VISIBLE);
        } else {
            mVersionCheckerLeftButtonTv.setVisibility(View.GONE);
        }
    }

    private void setRightButton(String msg) {
        if (null != msg) {
            mVersionCheckerRightButtonTv.setText(msg);
            mVersionCheckerRightButtonTv.setVisibility(View.VISIBLE);
        } else {
            mVersionCheckerRightButtonTv.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.tv_dialog_confirm_left_button, R.id.tv_dialog_confirm_right_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_dialog_confirm_left_button:
                if (null != mOnClickListener) {
                    mOnClickListener.onClick(this, 1);
                }
                break;
            case R.id.tv_dialog_confirm_right_button:
                if (null != mOnClickListener) {
                    mOnClickListener.onClick(this, 0);
                }
                break;
        }
    }
}
