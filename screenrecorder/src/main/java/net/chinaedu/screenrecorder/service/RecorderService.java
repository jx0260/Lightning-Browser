package net.chinaedu.screenrecorder.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import net.chinaedu.screenrecorder.AppContext;
import net.chinaedu.screenrecorder.recorder.ScreenRecorder;
import net.chinaedu.screenrecorder.recorder.ScreenRecorderLocal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by qinyun on 2017/5/16.
 */

public class RecorderService extends Service {

    private static final int REQUEST_CODE = 1;
    private ScreenRecorder mRecorder;
    private ScreenRecorderLocal mRecorderLocal;

    /**
     * 视频socket
     */
    private ServerSocket mVideoServerSocket;
    private Socket mVideoSocket;

    /**
     * 交互socket
     */
//    private ServerSocket mMsgServerSocket;
//    private Socket mMsgSocket;

    private MediaProjection mMediaProjection;

    private VideoSocketThread mVideoSocketThread;

    private Messenger mMessenger;

    private int mDpi = 1;

    private File mFile;

    private String teacherName;
    private TextView tipTv;

    /**
     * 用于监听新用户tcp连接
     */
    Handler mainHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent it = new Intent("request.screen.recorder.permission");
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(it);
        }
    };

    /**
     * 用于处理申请录屏权限
     */
    Handler startHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("RecorderService", msg.toString());
            MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            mMediaProjection = mMediaProjectionManager.getMediaProjection(msg.arg1, (Intent) msg.obj);
            if(mMediaProjection != null){
                Log.d("MediaProjection", mMediaProjection.toString());
                showTeacherLookingScreenTip();
                startRecorder();
            }
            else{
                Log.d("MediaProjection", "mMediaProjection is null");
            }
        }
    };

    private WindowManager mWindowManager;

    private void showTeacherLookingScreenTip(){
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        tipTv = new TextView(this);
        tipTv.setBackgroundColor(Color.parseColor("#282828"));
        tipTv.setTextColor(Color.WHITE);
        tipTv.setTextSize(16);
        tipTv.setText(teacherName+"老师正在查看你的屏幕");
        tipTv.setGravity(Gravity.CENTER);
        int paddingLeftPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, this.getResources().getDisplayMetrics());
        tipTv.setPadding(paddingLeftPx, 0, paddingLeftPx, 0);

        /*为View设置参数*/
        WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
        //设置View默认的摆放位置
        mLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        //设置window type
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置背景为透明
        mLayoutParams.format = PixelFormat.RGBA_8888;
        //注意该属性的设置很重要，FLAG_NOT_FOCUSABLE使浮动窗口不获取焦点,若不设置该属性，屏幕的其它位置点击无效，应为它们无法获取焦点
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        int heightPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, this.getResources().getDisplayMetrics());
        //设置视图的宽、高
        mLayoutParams.height = heightPx;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;

        mWindowManager.addView(tipTv, mLayoutParams);
    }

    private void hideTeacherLookingScreenTip(){
        if(tipTv!=null){
            mWindowManager.removeView(tipTv);
        }
    }


    @Override
    public void onCreate() {
//        android.os.Debug.waitForDebugger();
        super.onCreate();
        Log.i("RecorderService", "onCreate");

        mFile = new File( Environment.getExternalStorageDirectory() + File.separator +"test.mp4");

        mMessenger = new Messenger(startHandler);

        try {
            mVideoServerSocket = new ServerSocket(6000);
            mVideoSocketThread = new VideoSocketThread();
            mVideoSocketThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public boolean startRecorder() {
        if (mMediaProjection == null) {
            releaseClient();
            return false;
        }

        if(mFile.exists()){
            mFile.delete();
        }

        mRecorder = new ScreenRecorder(AppContext.getInstance().getWidth(),
                AppContext.getInstance().getHeight(),
                AppContext.getInstance().getBitRate(),
                AppContext.getInstance().getFrame(),
                mDpi, mMediaProjection, mVideoSocket);
        mRecorder.setOnStateListener(new ScreenRecorder.OnStateListener(){
            @Override
            public void onSocketClose() {
                releaseClient();
                hideTeacherLookingScreenTip();
            }
        });
        mRecorder.start();
        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseClient();
        release();
    }

    public synchronized void release(){
        if (mRecorder != null) {
            mRecorder.quit();
            mRecorder = null;
        }

        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }

        if(mVideoSocketThread != null){
            mVideoSocketThread.interrupt();
            mVideoServerSocket = null;
        }

        if(mVideoServerSocket != null && !mVideoServerSocket.isClosed()){
            try {
                mVideoServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mVideoServerSocket = null;
        }
    }

    public synchronized void releaseClient(){
        if(mVideoSocket != null && !mVideoSocket.isClosed()){
            try {
                mVideoSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mVideoSocket = null;
        }
    }

    private class VideoSocketThread extends Thread{
        public VideoSocketThread(){

        }

        @Override
        public void run() {
            while (!isInterrupted()){
                try {
                    Socket socket = mVideoServerSocket.accept();
                    releaseClient();
                    mVideoSocket = socket;
                    initTeacherName();
                    mainHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void initTeacherName() throws IOException {
            InputStream iptStream = mVideoSocket.getInputStream();
            byte[] bytes = new byte[50];//就是一个老师名字 不能超过50个字节
            int length = 0;
            StringBuilder resultSb = new StringBuilder();
            length = iptStream.read(bytes);
            if(length>0){
                byte[] tmpBytes = new byte[length];
                System.arraycopy(bytes, 0, tmpBytes, 0, length);
                resultSb.append(new String(tmpBytes));
            }
            int index = resultSb.indexOf("\\r\\n");
            if(index > 0){
                teacherName = resultSb.substring(0, index);
            }
        }
    }

//    private class MsgSocketThread extends Thread{
//        public MsgSocketThread(){
//
//        }
//
//        @Override
//        public void run() {
//            while (!isInterrupted()){
//                try {
//                    Socket socket = mMsgServerSocket.accept();
//                    releaseClient();
//                    mMsgSocket = socket;
//                    mainHandler.sendEmptyMessage(0);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
