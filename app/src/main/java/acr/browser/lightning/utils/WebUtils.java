package acr.browser.lightning.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewDatabase;

import com.anthonycr.bonsai.Schedulers;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebIconDatabase;
import com.tencent.smtt.sdk.WebStorage;

import acr.browser.lightning.database.history.HistoryModel;

/**
 * Copyright 8/4/2015 Anthony Restaino
 */
public class WebUtils {

    public static void clearCookies(@NonNull Context context) {
        CookieManager c = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            c.removeAllCookies(null);
        } else {
            //noinspection deprecation
            CookieSyncManager.createInstance(context);
            //noinspection deprecation
            c.removeAllCookie();
        }
    }

    public static void clearWebStorage() {
        WebStorage.getInstance().deleteAllData();
    }

    public static void clearHistory(@NonNull Context context) {
        HistoryModel.deleteHistory()
                .subscribeOn(Schedulers.io())
                .subscribe();
        WebViewDatabase m = WebViewDatabase.getInstance(context);
        m.clearFormData();
        m.clearHttpAuthUsernamePassword();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //noinspection deprecation
            m.clearUsernamePassword();
            //noinspection deprecation
            WebIconDatabase.getInstance().removeAllIcons();
        }
        Utils.trimCache(context);
    }

    public static void clearCache(@Nullable WebView view) {
        if (view == null) return;
        view.clearCache(true);
    }

}
