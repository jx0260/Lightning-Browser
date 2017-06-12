package acr.browser.lightning.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.widget.RemoteViews;

import net.chinaedu.aedu.utils.LogUtils;

import acr.browser.lightning.R;

/**
 * Created by MartinKent on 2017/6/6.
 */

public class DownloadNotification {
    private static final String ACTION_CLICK = DownloadNotification.class.getName() + ".ACTION_CLICK";
    private static final String ACTION_DELETE = DownloadNotification.class.getName() + ".ACTION_DELETE";
    private static final int REQ_DELETE_NOTIFICATION = 0x7001;

    private final Context context;
    private Notification notification;
    private RemoteViews contentView;
    private android.app.NotificationManager notificationManager;

    private NotificationBroadcastReceiver receiver;
    private boolean isReceiverRegistered = false;
    private Listener mListener;

    public DownloadNotification(Context context) {
        this.context = context;
        this.receiver = new NotificationBroadcastReceiver();
    }

    public void show(Listener listener) {
        this.mListener = listener;
        notification = new Notification(
                R.mipmap.ic_launcher,
                context.getResources().getString(R.string.app_name) + "正在下载",
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        contentView = new RemoteViews(context.getPackageName(), R.layout.layout_common_download_notification);
        contentView.setTextViewText(R.id.tv_version_download_notification_title, context.getResources().getString(R.string.app_name) + "正在下载");
        contentView.setTextViewText(R.id.notificationPercent, "0%");
        contentView.setProgressBar(R.id.tv_version_download_notification_progress, 100, 0, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification.bigContentView = contentView;
        }
        notification.contentView = contentView;
        notification.deleteIntent = PendingIntent.getBroadcast(context, REQ_DELETE_NOTIFICATION, new Intent(ACTION_DELETE), PendingIntent.FLAG_CANCEL_CURRENT);
        notification.contentIntent = PendingIntent.getBroadcast(context, REQ_DELETE_NOTIFICATION, new Intent(ACTION_CLICK), PendingIntent.FLAG_CANCEL_CURRENT);

        notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(R.layout.layout_common_download_notification, notification);

        registerReceiver();
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_CLICK);
            filter.addAction(ACTION_DELETE);
            context.registerReceiver(receiver, filter);
            isReceiverRegistered = true;
        }
    }

    private void unregisterReceiver() {
        if (isReceiverRegistered) {
            context.unregisterReceiver(receiver);
            isReceiverRegistered = false;
        }
    }

    private int lastProgress = -1;

    public void update(int progress) {
        if (lastProgress != progress) {
            contentView.setTextViewText(R.id.tv_version_download_notification_title, context.getResources().getString(R.string.app_name) + "正在下载");
            contentView.setTextViewText(R.id.notificationPercent, progress + "%");
            contentView.setProgressBar(R.id.tv_version_download_notification_progress, 100, progress, false);
            notificationManager.notify(R.layout.layout_common_download_notification, notification);
            lastProgress = progress;
        }
    }

    public void cancel() {
        notificationManager.cancel(R.layout.layout_common_download_notification);
    }

    public class NotificationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d("action=" + intent.getAction());
            if (ACTION_DELETE.equals(intent.getAction())) {
                cancel();
                unregisterReceiver();
                if (null != mListener) {
                    mListener.onCanceled(DownloadNotification.this);
                }
            } else if (ACTION_CLICK.equals(intent.getAction())) {
                if (null != mListener) {
                    mListener.onClicked(DownloadNotification.this);
                }
            }
        }
    }

    public interface Listener {
        void onCanceled(DownloadNotification notificationManager);

        void onClicked(DownloadNotification notificationManager);
    }
}
