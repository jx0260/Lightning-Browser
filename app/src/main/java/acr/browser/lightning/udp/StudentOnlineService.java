package acr.browser.lightning.udp;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * 学生上线的服务
 * Created by Jin Liang on 2017/5/26.
 */
@Deprecated
public class StudentOnlineService extends IntentService {

    private static final String TAG = "StudentOnlineService";

    private StudentOnlineMsg studentOnlineMsgObj;
    private Gson mGson = new Gson();
    static String multicastHost = "224.0.0.1";

    public StudentOnlineService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        studentOnlineMsgObj = intent.getParcelableExtra("studentMsg");
        broadcastIamOnline(studentOnlineMsgObj);
    }

    private void broadcastIamOnline(StudentOnlineMsg studentOnlineMsgObj){
        InetAddress groupAddress = null;
        MulticastSocket receiveMulticast = null;
        try {
            groupAddress = InetAddress.getByName(multicastHost);

            if(!groupAddress.isMulticastAddress()){//测试是否为多播地址
                Log.i(TAG, "请使用多播地址");
                // 发送广播
                return;
            }
            int localPort = 26399;
            receiveMulticast = new MulticastSocket(localPort);
            receiveMulticast.joinGroup(groupAddress);

            String studentOnlineMsgStr = mGson.toJson(studentOnlineMsgObj);
            byte[] studentMsgData = studentOnlineMsgStr.getBytes();

            DatagramPacket sendStudentMsgDp = new DatagramPacket(studentMsgData, studentMsgData.length);
            receiveMulticast.send(sendStudentMsgDp);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(receiveMulticast!=null && !receiveMulticast.isClosed()) {
                try {
                    receiveMulticast.leaveGroup(groupAddress);
                    receiveMulticast.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
