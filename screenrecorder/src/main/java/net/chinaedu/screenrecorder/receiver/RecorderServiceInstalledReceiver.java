package net.chinaedu.screenrecorder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.chinaedu.screenrecorder.service.RecorderService;

/**
 * Created by Administrator on 2017/5/18.
 */

public class RecorderServiceInstalledReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY")) {
                        /* 服务开机自启动 */
            context.startService(new Intent(context, RecorderService.class));
        }
    }
}
