package acr.browser.lightning.udp;

import android.app.IntentService;
import android.app.mia.MiaMdmPolicyManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

import acr.browser.lightning.R;
import acr.browser.lightning.activity.LockScreenActivity;

/**
 * 锁屏监听service
 * Created by Jin Liang on 2017/6/10.
 */

public class LockScreenMonitorService extends IntentService {

    private static final String TAG = "LockScreen";

    private Gson mGson = new Gson();

    final static int RECEIVE_LENGTH = 320;

    private DatagramSocket sendDataSocket;

    private boolean isLocked = false;

    private WindowManager mWindowManager;
    private LayoutInflater mLayoutInflater;
    private View mFloatView;

    private FrameLayout contentContainer;
    private LockScreenMonitorService mService;

    // 发送确认包 目标端口
    private int teacherPort = 29011;

    LinkedBlockingQueue<byte[]> packetQueue = new LinkedBlockingQueue<>();

    private MiaMdmPolicyManager mpm;

    public LockScreenMonitorService(){
        this("lockScreen-monitor-01");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LockScreenMonitorService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mService = this;

        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mpm = new MiaMdmPolicyManager(getApplicationContext()); // 获取接口服务
        mLayoutInflater = LayoutInflater.from(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        startHandle();
        startMonitor();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startMonitor(){
        try {
            sendDataSocket = new DatagramSocket();

            int localPort = 9999;
            DatagramSocket datagramSocket = new DatagramSocket(localPort);

            byte[] dataBytes = new byte[RECEIVE_LENGTH];
            DatagramPacket datagramPacketToReceive = new DatagramPacket(dataBytes, RECEIVE_LENGTH);
            while(true){
                datagramSocket.receive(datagramPacketToReceive);

                byte[] resultByteArray = new byte[datagramPacketToReceive.getLength()];
                System.arraycopy(datagramPacketToReceive.getData(), 0, resultByteArray, 0, datagramPacketToReceive.getLength());

                packetQueue.put(resultByteArray);
                Log.i(TAG, "收到数据包内容："+resultByteArray+" 放入队列（size="+packetQueue.size()+")");
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startHandle(){
        new HandlePacketThread("handleLockScreenPacket").start();
    }

    class HandlePacketThread extends Thread{
        public HandlePacketThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            super.run();
            while(true) {
                byte[] resultByteArray = new byte[0];
                try {
                    resultByteArray = packetQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }

                ByteBuffer byteBuffer = ByteBuffer.wrap(resultByteArray);
                int length = byteBuffer.getInt(0);
                int commandId = byteBuffer.getInt(4);
                int serialId = byteBuffer.getInt(8);
                int teacherIp = byteBuffer.getInt(12);

                InetAddress teacherAddress = null;
                try {
                    teacherAddress = InetAddress.getByName(IpUtil.intToIp(teacherIp));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    return;
                }
                // 发送的数据包
                DatagramPacket dataPacket = new DatagramPacket(resultByteArray, resultByteArray.length, teacherAddress, teacherPort);
                try {
                    sendDataSocket.send(dataPacket);
                    Log.i(TAG, "发送确认包(id " + serialId + "):地址" + teacherAddress.toString() + " 端口" + teacherPort + " 命令号" + commandId);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                String dataStr = new String(resultByteArray, 16, resultByteArray.length - 16);
                Log.i(TAG, "数据内容是："+dataStr);

                if (commandId == CommandConstants.LOCK_SCREEN) {
                    try {
                        LockMsg lockMsg = mGson.fromJson(dataStr, LockMsg.class);
                        if (lockMsg.isLock()) {
                            if (!isLocked) {
                                // 锁屏
                                Log.i(TAG, "执行锁屏, 解锁口令是：" + lockMsg.getToken());
                                mpm.setNavigaBar(true); //3个虚拟按键一起禁用
                                showLockActivity(lockMsg.getToken());
//                            showLock();
                                isLocked = true;
                            }
                        } else {
                            if (isLocked) {
                                // 解锁
                                Log.i(TAG, "执行解锁");
                                mpm.setNavigaBar(false); //3个虚拟按键一起启用
                                hideLockActivity();
//                            clearLock();
                                isLocked = false;
                            }
                        }
                    } catch (Exception e) {
                        Log.i(TAG, "LockMsg json解析出错，没有执行锁屏或解锁操作。");
                        Log.i(TAG, dataStr);
                        Log.i(TAG, Log.getStackTraceString(e));
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(sendDataSocket!=null && !sendDataSocket.isClosed()){
            sendDataSocket.close();
        }
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (null == intent) {
//            return START_STICKY;
//        }
//        int action = intent.getIntExtra("action", -1);
//        if (0 == action) {
//            clearLock();
//        } else if (1 == action) {
//            showLock();
//        }
//        return START_STICKY;
//    }

    private void showLockActivity(String rightToken){
        Intent intent = new Intent(this, LockScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(LockScreenActivity.RIGHT_TOKEN_KEY, rightToken);
        startActivity(intent);
    }

    private void showLock() {
        if (null != contentContainer) {
            return;
        }
        //加载布局文件
        mFloatView = mLayoutInflater.inflate(R.layout.lock_view, null);
        //为View设置监听，以便处理用户的点击和拖动

        mFloatView.findViewById(R.id.btn_unlock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(TestService.this, TestService.class);
//                intent.putExtra("action", 0);
//                startService(intent);
            }
        });

       /*为View设置参数*/
        WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
        //设置View默认的摆放位置
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        //设置window type
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG;//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //设置背景为透明
        mLayoutParams.format = PixelFormat.RGBA_8888;
        //注意该属性的设置很重要，FLAG_NOT_FOCUSABLE使浮动窗口不获取焦点,若不设置该属性，屏幕的其它位置点击无效，应为它们无法获取焦点
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mLayoutParams.flags = 1280;
        //设置视图的宽、高
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        //将视图添加到Window中
        contentContainer = new FrameLayout(this) {

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                System.out.println("dispatchKeyEvent " + KeyEvent.keyCodeToString(event.getKeyCode()));
                return true;
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                System.out.println("dispatchKeyEvent");
                return super.dispatchTouchEvent(ev);
            }
        };
        contentContainer.addView(mFloatView);
        mWindowManager.addView(contentContainer, mLayoutParams);
    }

    private void clearLock() {
        if (null == contentContainer) {
            return;
        }
        mWindowManager.removeView(contentContainer);
        mFloatView = null;
        contentContainer = null;
    }

    private void hideLockActivity(){
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mService);
        lbm.sendBroadcast(new Intent(LockScreenActivity.ACTION_CLOSE_LOCK_ACTIVITY));
    }

}
