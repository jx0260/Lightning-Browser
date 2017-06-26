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
        context.startService(new Intent(context, RecorderService.class));
    }
}
