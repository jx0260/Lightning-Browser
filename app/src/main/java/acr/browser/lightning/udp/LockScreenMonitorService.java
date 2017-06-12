package acr.browser.lightning.udp;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Created by Jin Liang on 2017/6/10.
 */

public class LockScreenMonitorService extends IntentService {

    private static final String TAG = "LockScreen";

    private Gson mGson = new Gson();

    final static int RECEIVE_LENGTH = 1024;

    private DatagramSocket sendDataSocket;

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

        /*创建socket实例*/
        try {
            sendDataSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        startMonitor();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startMonitor(){
        try {
            int localPort = 9999;
            DatagramSocket datagramSocket = new DatagramSocket(localPort);

//            int destinationPort = 26400;

            byte[] dataBytes = new byte[RECEIVE_LENGTH];
            DatagramPacket datagramPacketToReceive = new DatagramPacket(dataBytes, RECEIVE_LENGTH);
            while(true){
                datagramSocket.receive(datagramPacketToReceive);
                byte[] resultByteArray = new byte[datagramPacketToReceive.getLength()];
                System.arraycopy(datagramPacketToReceive.getData(), 0, resultByteArray, 0, datagramPacketToReceive.getLength());

                ByteBuffer byteBuffer = ByteBuffer.wrap(resultByteArray);
                int length = byteBuffer.getInt(0);
                int commandId = byteBuffer.getInt(4);
                int serialId = byteBuffer.getInt(8);
                int teacherIp = byteBuffer.getInt(12);

                InetAddress teacherAddress = InetAddress.getByName(IpUtil.intToIp(teacherIp));
                int teacherPort = datagramPacketToReceive.getPort();
                // 发送的数据包
                DatagramPacket dataPacket = new DatagramPacket(resultByteArray, resultByteArray.length, teacherAddress, teacherPort);
                sendDataSocket.send(dataPacket);

                String dataStr = new String(resultByteArray, 16, resultByteArray.length-16);
                if(commandId == CommandConstants.LOCK_SCREEN){
                    try {
                        LockMsg lockMsg = mGson.fromJson(dataStr, LockMsg.class);
                        if (lockMsg.isLock()) {
                            // 锁屏
                            Log.i(TAG, "发起锁屏命令");
                        } else {
                            // 解锁
                            Log.i(TAG, "发起解锁命令");
                        }
                    }catch (Exception e){
                    }
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(sendDataSocket!=null && !sendDataSocket.isClosed()){
            sendDataSocket.close();
        }
    }

}
