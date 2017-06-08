package net.chinaedu.screenrecorder.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;

import net.chinaedu.screenrecorder.service.RecorderService;

/**
 * Created by qinyun on 2017/5/18.
 */

public class RequestRecorderActivity extends Activity {
    private static final int REQUEST_CODE = 1;

    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private RecorderService mRecorderService;
//    private int mWidth = 360;
//    private int mHeight = 642;
//    private int mBitrate = 25000000;
//    private int mDpi = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_CODE);
    }

    int mResultCode;
    Intent mData;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            mResultCode = resultCode;
            mData = data;
            Intent intent = new Intent(this, RecorderService.class);
            bindService(intent, connection, BIND_AUTO_CREATE);
        }
        finally {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            unbindService(connection);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    Messenger mService = null;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);

            Message msg = Message.obtain(null, 0, 0, 0);
            msg.arg1 = mResultCode;
            msg.obj = mData;
            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {

        }
    };
}
