/*
 * Copyright 2014 A.C.R. Development
 */
package acr.browser.lightning.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.BuildConfig;
import android.widget.Toast;

import acr.browser.lightning.R;
import acr.browser.lightning.utils.DownloadNotification;
import acr.browser.lightning.utils.Installer;
import acr.browser.lightning.version.entity.VersionEntity;
import acr.browser.lightning.version.view.VersionChecker;
import acr.browser.lightning.widget.VersionCheckerDialog;

/**
 * 检查更新fragment
 */
public class CheckUpdateFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private Activity mActivity;

    private static final String CHECK_UPDATE = "check_update";

    private Preference checkUpdate;

    private DownloadNotification mNotification;

    private boolean isVersionCheckCompleted = false;
    public VersionEntity mVersionEntity = null;
    private boolean isDownloadSuccess = false;
    private String mDownloadedFile = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_checkupdate);

        mActivity = getActivity();
        mNotification = new DownloadNotification(mActivity);

        checkUpdate = findPreference(CHECK_UPDATE);
        checkUpdate.setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        VersionChecker.create(mActivity).check(new VersionChecker.OnCheckResultListener() {
            @Override
            public void onSuccess(VersionEntity entity) {
                mVersionEntity = entity;
                if (null == entity || entity.getVersionCode() <= BuildConfig.VERSION_CODE) {
                    checkUpdate.setTitle("当前已是最新版本");
                    return;
                }
                checkUpdate.setTitle("检查到新版本");
                isVersionCheckCompleted = true;

                VersionCheckerDialog dialog = new VersionCheckerDialog(mActivity, mVersionEntity);
                dialog.setOnClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (0 == which) {
                            getPresenter().startDownload(mVersionEntity);
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

            @Override
            public void onFailure(Throwable e) {
                mVersionEntity = null;
                isVersionCheckCompleted = true;
            }
        });
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case CHECK_UPDATE:
                if (isVersionCheckCompleted && null != mVersionEntity && mVersionEntity.getVersionCode() > BuildConfig.VERSION_CODE) {
                    VersionCheckerDialog dialog = new VersionCheckerDialog(mActivity, mVersionEntity);
                    dialog.setOnClickListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (0 == which) {
                                getPresenter().startDownload(mVersionEntity);
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                return true;
        }
        return false;
    }

    private ICheckUpdatePresenter getPresenter(){
        return new CheckUpdatePresenter(mActivity, this);
    }

    public void onStartDownload() {
        mDownloadedFile = null;
        mNotification.show(new DownloadNotification.Listener() {
            @Override
            public void onCanceled(DownloadNotification notificationManager) {
                getPresenter().stopDownload();
            }

            @Override
            public void onClicked(DownloadNotification notificationManager) {
                if (isDownloadSuccess && null != mDownloadedFile) {
                    Installer.install(mActivity, mDownloadedFile);
                }
            }
        });
    }

    public void onDownloadProgress(int progress) {
        mNotification.update(progress);
    }

    public void onDownloadSucess(String file) {
        this.mDownloadedFile = file;
        isDownloadSuccess = true;
        Installer.install(mActivity, mDownloadedFile);
        mNotification.cancel();
    }

    public void onDownloadError(Throwable e) {
        Toast.makeText(mActivity, "更新下载失败", Toast.LENGTH_SHORT).show();
        mNotification.cancel();
    }

}
