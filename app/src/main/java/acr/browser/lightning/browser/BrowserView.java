package acr.browser.lightning.browser;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;

import acr.browser.lightning.version.entity.VersionEntity;

public interface BrowserView {

    void setTabView(@NonNull View view);

    void removeTabView();

    void updateUrl(String url, boolean shortUrl);

    void updateProgress(int progress);

    void updateTabNumber(int number);

    void closeBrowser();

    void closeActivity();

    void showBlockedLocalFileDialog(DialogInterface.OnClickListener listener);

    void showSnackbar(@StringRes int resource);

    void setForwardButtonEnabled(boolean enabled);

    void setBackButtonEnabled(boolean enabled);

    void notifyTabViewRemoved(int position);

    void notifyTabViewAdded();

    void notifyTabViewChanged(int position);

    void notifyTabViewInitialized();

    // =======  检查更新    ==============

    void onCheckVersionSuccess(VersionEntity entity);

    void onStartDownload();

    void onDownloadProgress(int progress);

    void onDownloadSucess(String file);

    void onCheckVersionFailed(Throwable e);

    void onDownloadError(Throwable e);

}
