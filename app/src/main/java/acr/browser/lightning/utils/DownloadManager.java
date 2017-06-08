package acr.browser.lightning.utils;

import android.Manifest;
import android.content.Context;
import android.os.AsyncTask;

import com.tbruyelle.rxpermissions2.RxPermissions;


import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import zlc.season.rxdownload2.RxDownload;
import zlc.season.rxdownload2.entity.DownloadStatus;

/**
 * Created by MartinKent on 2017/6/6.
 */

public class DownloadManager {
    private final Context context;
    private AsyncTask<String, Integer, Long> getContentLengthTask;
    private RxDownload rxDownload;

    private Map<String, Disposable> disposableMap = new ConcurrentHashMap<>();

    public DownloadManager(Context context) {
        this.context = context;
    }

    public void stopDownload(String url) {
        if (disposableMap.containsKey(url)) {
            if (null != getContentLengthTask && !getContentLengthTask.isCancelled()) {
                getContentLengthTask.cancel(true);
                getContentLengthTask = null;
            }
            Disposable d = disposableMap.get(url);
            if (!d.isDisposed()) {
                d.dispose();
            }
            rxDownload.deleteServiceDownload(url, true);
            disposableMap.remove(url);
        }
    }

    public void startDownload(final Context context, final String url, final DownloadListener listener) {
        getContentLengthTask = new AsyncTask<String, Integer, Long>() {
            @Override
            protected Long doInBackground(String... params) {
                try {
                    URLConnection urlcon = new URL(url).openConnection();
                    return (long) urlcon.getContentLength();
                } catch (Exception e) {
                    return 0L;
                }
            }

            @Override
            protected void onPostExecute(Long size) {
                super.onPostExecute(size);
                if (0 == size) {
                    if (null != listener) {
                        listener.onFailure(new Throwable("content length is 0"));
                    }
                    return;
                }

                File shareDir = FileUtil.getShareDirectoryForSize(context, size);
                if (null == shareDir) {
                    if (null != listener) {
                        listener.onFailure(new Throwable("存储空间不足"));
                    }
                    return;
                }
                final File savedFile = new File(shareDir, new SimpleDateFormat("yyyyMMddhhmmss_", Locale.getDefault()).format(new Date()) + url.substring(url.lastIndexOf("/") + 1));

                rxDownload = RxDownload.getInstance(context);
                RxPermissions.getInstance(context)
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .doOnNext(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (!aBoolean) {
                                    if (null != listener) {
                                        listener.onFailure(new Throwable("no permission for [" + Manifest.permission.WRITE_EXTERNAL_STORAGE + "]"));
                                    }
                                }
                            }
                        })
                        .compose(rxDownload.transform(url, savedFile.getName(), savedFile.getParent()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<DownloadStatus>() {
                            private Throwable mError = null;

                            @Override
                            public void onSubscribe(Disposable d) {
                                disposableMap.put(url, d);
                                if (null != listener) {
                                    listener.onStart(d);
                                }
                            }

                            @Override
                            public void onNext(DownloadStatus status) {
                                if (null != listener) {
                                    listener.onProgress(status);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                mError = e;
                            }

                            @Override
                            public void onComplete() {
                                disposableMap.remove(url);
                                getContentLengthTask = null;
                                if (null != listener) {
                                    if (null == mError) {
                                        listener.onSuccess(savedFile);
                                    } else {
                                        listener.onFailure(mError);
                                    }
                                }
                            }
                        });
            }
        };
        getContentLengthTask.execute();
    }

    public interface DownloadListener {

        void onStart(Disposable d);

        void onProgress(DownloadStatus event);

        void onSuccess(File dest);

        void onFailure(Throwable e);
    }
}
