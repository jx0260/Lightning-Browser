package acr.browser.lightning.app;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.anthonycr.bonsai.Schedulers;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.smtt.sdk.QbSdk;

import net.chinaedu.aedu.utils.LogUtils;
import net.chinaedu.screenrecorder.AppContext;
import net.chinaedu.screenrecorder.LogcatFileManager;
import net.chinaedu.screenrecorder.service.RecorderService;
import net.chinaedu.screenrecorder.utils.AdbUtils;
import net.chinaedu.screenrecorder.utils.PreferenceService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.inject.Inject;

import acr.browser.lightning.BuildConfig;
import acr.browser.lightning.database.HistoryItem;
import acr.browser.lightning.database.bookmark.BookmarkExporter;
import acr.browser.lightning.database.bookmark.BookmarkModel;
import acr.browser.lightning.database.bookmark.legacy.LegacyBookmarkManager;
import acr.browser.lightning.preference.PreferenceManager;
import acr.browser.lightning.utils.FileUtils;
import acr.browser.lightning.utils.MemoryLeakUtils;
import acr.browser.lightning.utils.Preconditions;
import retrofit2.CrystalRetrofitFactory;

public class BrowserApp extends Application {

    private PreferenceService mPreferenceService;

    private static final String TAG = "BrowserApp";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT);
    }

    @Nullable private static AppComponent sAppComponent;

    @Inject PreferenceManager mPreferenceManager;
    @Inject BookmarkModel mBookmarkModel;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        }*/

        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, @NonNull Throwable ex) {

                if (BuildConfig.DEBUG) {
                    FileUtils.writeCrashToStorage(ex);
                }

                if (defaultHandler != null) {
                    defaultHandler.uncaughtException(thread, ex);
                } else {
                    System.exit(2);
                }
            }
        });

        sAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        sAppComponent.inject(this);

        Schedulers.worker().execute(new Runnable() {
            @Override
            public void run() {
                List<HistoryItem> oldBookmarks = LegacyBookmarkManager.destructiveGetBookmarks(BrowserApp.this);

                if (!oldBookmarks.isEmpty()) {
                    // If there are old bookmarks, import them
                    mBookmarkModel.addBookmarkList(oldBookmarks).subscribeOn(Schedulers.io()).subscribe();
                } else if (mBookmarkModel.count() == 0) {
                    // If the database is empty, fill it from the assets list
                    List<HistoryItem> assetsBookmarks = BookmarkExporter.importBookmarksFromAssets(BrowserApp.this);
                    mBookmarkModel.addBookmarkList(assetsBookmarks).subscribeOn(Schedulers.io()).subscribe();
                }
            }
        });

        if (mPreferenceManager.getUseLeakCanary() && !isRelease()) {
            LeakCanary.install(this);
        }
       /* if (!isRelease() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }*/

        registerActivityLifecycleCallbacks(new MemoryLeakUtils.LifecycleAdapter() {
            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d(TAG, "Cleaning up after the Android framework");
                MemoryLeakUtils.clearNextServedView(activity, BrowserApp.this);
            }
        });

        // =========  以下为新代码   =================
        CrystalRetrofitFactory.init(this);

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtils.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);

        // ========   远程控制      =====================
        startService(new Intent(this, RecorderService.class));

        // 全局日志管理
        LogcatFileManager logcatFileManager = LogcatFileManager.getInstance();
        logcatFileManager.startLogcatManager(this);

//        Thread.setDefaultUncaughtExceptionHandler(new ScreenRecorderExceptionHandler(this, null));

        initData();
    }

    private void initData(){
        mPreferenceService = new PreferenceService(this);
        int width = mPreferenceService.getInt("width", 360);
        int height = mPreferenceService.getInt("height", 642);
        int bitRate = mPreferenceService.getInt("bit_rate", 1250000);
        int frame = mPreferenceService.getInt("frame", 30);

        AppContext.getInstance().setWidth(width);
        AppContext.getInstance().setHeight(height);
        AppContext.getInstance().setBitRate(bitRate);
        AppContext.getInstance().setFrame(frame);

//        try {
//            execCommand("setprop service.adb.tcp.port 5555");
//            execCommand("stop adbd");
//            execCommand("start adbd");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        AdbUtils.adbStart(this);
    }

    public void execCommand(String command) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);
        try {
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = in.readLine()) != null) {
                stringBuffer.append(line+" ");
            }
            System.out.println(stringBuffer.toString());

        } catch (InterruptedException e) {
            System.err.println(e);
        }finally{
            try {
                proc.destroy();
            } catch (Exception e2) {
            }
        }
    }

    @NonNull
    public static AppComponent getAppComponent() {
        Preconditions.checkNonNull(sAppComponent);
        return sAppComponent;
    }

    /**
     * Determines whether this is a release build.
     *
     * @return true if this is a release build, false otherwise.
     */
    public static boolean isRelease() {
        return !BuildConfig.DEBUG || BuildConfig.BUILD_TYPE.toLowerCase().equals("release");
    }

    public static void copyToClipboard(@NonNull Context context, @NonNull String string) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("URL", string);
        clipboard.setPrimaryClip(clip);
    }

}
