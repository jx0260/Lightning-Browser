package acr.browser.lightning.version.view;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;

import acr.browser.lightning.version.entity.VersionEntity;
import acr.browser.lightning.version.presenter.VersionPresenter;
import acr.browser.lightning.version.vo.VersionCheckerVO;

/**
 * Created by MartinKent on 2017/6/5.
 */

public class VersionChecker implements IVersionView {
    private final Context context;
    private final VersionPresenter presenter;
    private OnCheckResultListener mOnCheckResultListener;

    public static VersionChecker create(Context context) {
        return new VersionChecker(context);
    }

    private VersionChecker(Context context) {
        this.context = context;
        this.presenter = new VersionPresenter(context, this);
    }

    public void check(OnCheckResultListener listener) {
        this.mOnCheckResultListener = listener;
        // 类型7 表示远程控制客户端
        presenter.findMaxVersion(7);
    }

    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
    }

    @Override
    public void onFindMaxVersionSuccess(VersionCheckerVO vo) {
        mOnCheckResultListener.onSuccess(vo.getObject());
    }

    @Override
    public void onFindMaxVersionFailed(Throwable e) {
        mOnCheckResultListener.onFailure(e);
    }

    public interface OnCheckResultListener {

        void onSuccess(VersionEntity entity);

        void onFailure(Throwable e);
    }
}
