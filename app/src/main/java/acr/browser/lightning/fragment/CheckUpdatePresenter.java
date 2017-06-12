package acr.browser.lightning.fragment;

import android.content.Context;

import net.chinaedu.aedu.utils.LogUtils;

import java.io.File;

import acr.browser.lightning.utils.DownloadManager;
import acr.browser.lightning.version.entity.VersionEntity;
import io.reactivex.disposables.Disposable;
import zlc.season.rxdownload2.entity.DownloadStatus;

/**
 * Created by Jin Liang on 2017/6/10.
 */

public class CheckUpdatePresenter implements ICheckUpdatePresenter {

    private DownloadManager mDownloadManager;
    private VersionEntity mVersionEntity;

    private Context mContext;
    private CheckUpdateFragment mView;

    public CheckUpdatePresenter(Context context, CheckUpdateFragment view) {
        mContext = context;
        mView = view;
        mDownloadManager = new DownloadManager(context);
    }

    @Override
    public void startDownload(VersionEntity entity) {
        this.mVersionEntity = entity;
        if (null == entity) {
            return;
        }
        mDownloadManager.startDownload(mContext, entity.getMobileVersionUrl(), new DownloadManager.DownloadListener() {
            @Override
            public void onStart(Disposable d) {
                getView().onStartDownload();
            }

            @Override
            public void onProgress(DownloadStatus status) {
                LogUtils.d("getDownloadSize=" + status.getDownloadSize());
                int progress = (int) (1.0f * 100 * status.getDownloadSize() / status.getTotalSize());
                LogUtils.d("currentProgress=" + progress);
                getView().onDownloadProgress(progress);
            }

            @Override
            public void onSuccess(File dest) {
                getView().onDownloadSucess(dest.getAbsolutePath());
                mVersionEntity = null;
            }

            @Override
            public void onFailure(Throwable e) {
                LogUtils.d("onFailure=" + e.getMessage());
                getView().onDownloadError(e);
                mVersionEntity = null;
            }
        });
    }

    @Override
    public void stopDownload() {
        if (null != mVersionEntity) {
            mDownloadManager.stopDownload(mVersionEntity.getMobileVersionUrl());
        }
    }

    private CheckUpdateFragment getView(){
        return mView;
    }
}
