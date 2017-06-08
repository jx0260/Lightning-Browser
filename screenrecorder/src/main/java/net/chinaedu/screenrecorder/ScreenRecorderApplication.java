package net.chinaedu.screenrecorder;

import android.app.Application;
import android.content.Intent;

import net.chinaedu.screenrecorder.service.RecorderService;
import net.chinaedu.screenrecorder.utils.AdbUtils;
import net.chinaedu.screenrecorder.utils.PreferenceService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/5/17.
 */

public class ScreenRecorderApplication extends Application {

    private PreferenceService mPreferenceService;

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, RecorderService.class));

        // 全局日志管理
        LogcatFileManager logcatFileManager = LogcatFileManager.getInstance();
        logcatFileManager.startLogcatManager(this);

        Thread.setDefaultUncaughtExceptionHandler(new ScreenRecorderExceptionHandler(this, null));

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
}
