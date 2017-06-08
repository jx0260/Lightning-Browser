package net.chinaedu.screenrecorder.service;

import android.app.Service;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;

import net.chinaedu.screenrecorder.AppContext;
import net.chinaedu.screenrecorder.recorder.ScreenRecorder;
import net.chinaedu.screenrecorder.recorder.ScreenRecorderLocal;

import java.io.File;
import java.io.IOException;
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
                startRecorder();
            }
            else{
                Log.d("MediaProjection", "mMediaProjection is null");
            }
        }
    };

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
        mRecorderLocal = new ScreenRecorderLocal(AppContext.getInstance().getWidth(),
                AppContext.getInstance().getHeight(),
                AppContext.getInstance().getBitRate(),
                AppContext.getInstance().getFrame(),
                mDpi, mMediaProjection, mFile.getAbsolutePath());
        mRecorderLocal.setOnStateListener(
                new ScreenRecorderLocal.OnStateListener(){
            @Override
            public void onWriteFinish() {
                mRecorder = new ScreenRecorder(AppContext.getInstance().getWidth(),
                        AppContext.getInstance().getHeight(),
                        AppContext.getInstance().getBitRate(),
                        AppContext.getInstance().getFrame(),
                        mDpi, mMediaProjection, mFile.getAbsolutePath(), mVideoSocket);
                mRecorder.setOnStateListener(new ScreenRecorder.OnStateListener(){
                    @Override
                    public void onSocketClose() {
//                        releaseClient();
                    }
                });
                mRecorder.start();
            }
        });
        mRecorderLocal.start();

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

//        if(mMsgSocket != null && !mMsgSocket.isClosed()){
//            try {
//                mMsgSocket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            mMsgSocket = null;
//        }
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
                    mainHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
