package acr.browser.lightning.udp;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 学生端UDP监听服务
 * Created by Jin Liang on 2017/5/25.
 */

public class UDPMonitorService extends IntentService {

    private static final String TAG = "UDPMonitorService";

    private Map<String, SendMsgToTeacherTask> mSendMsgToTeacherTaskMap = new ConcurrentHashMap<>(5);
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    private StudentOnlineMsg studentOnlineMsgObj;
    private Gson mGson = new Gson();

    final static int RECEIVE_LENGTH = 1024;

    // 多播地址
    private String multicastHost = "239.0.0.1";

    // 确定老师已经收到了学生发的单播消息（包含了客户端的信息）
    boolean confirmTeacherReceived = false;

    public UDPMonitorService(){
        this("udp-monitor-01");
    }

    public UDPMonitorService(String name) {
        super(name);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        studentOnlineMsgObj = intent.getParcelableExtra("studentMsg");
        multicastHost = intent.getStringExtra("multicastIp");
        startMonitor();
    }

    private void startMonitor(){
        try {
            InetAddress groupAddress = InetAddress.getByName(multicastHost);
            if(!groupAddress.isMulticastAddress()){//测试是否为多播地址
                Log.i(TAG, "请使用多播地址");
                // 发送广播
                return;
            }

            int localPort = 9998;
            MulticastSocket receiveMulticast = new MulticastSocket(localPort);
            receiveMulticast.joinGroup(groupAddress);

            int destinationPort = 26400;
            // 先发送学生上线的多播
            String studentOnlineMsgStr = mGson.toJson(studentOnlineMsgObj);
            byte[] studentMsgData = studentOnlineMsgStr.getBytes();
            DatagramPacket sendStudentMsgDp = new DatagramPacket(studentMsgData, studentMsgData.length, groupAddress, destinationPort);
            receiveMulticast.send(sendStudentMsgDp);

            // 如果教师没有确认收到了学生端的信息，就开始启动监听
            byte[] dataBytes = new byte[RECEIVE_LENGTH];
            DatagramPacket datagramPacketToReceive = new DatagramPacket(dataBytes, RECEIVE_LENGTH);
            while(true){
                receiveMulticast.receive(datagramPacketToReceive);
                byte[] resultByteArray = new byte[datagramPacketToReceive.getLength()];
                System.arraycopy(datagramPacketToReceive.getData(), 0, resultByteArray, 0, datagramPacketToReceive.getLength());
                String receiveMsg = new String(resultByteArray).trim();

                Log.i(TAG, "学生端 收到消息："+receiveMsg);

                if(receiveMsg.contains("isTeacher")){//如果有isTeacher，说明是教师上线消息
                    // 得到老师ip port
                    InetAddress teacherAddress = datagramPacketToReceive.getAddress();
                    String teacherIp = teacherAddress.getHostAddress();

                    // 收到老师上线消息后，启动任务给老师发自己的信息，只要没有得到确认，就5秒发一次，共发5次
                    SendMsgToTeacherTask sendMsgToTeacherTask = new SendMsgToTeacherTask(studentOnlineMsgObj, teacherIp, destinationPort);
                    mSendMsgToTeacherTaskMap.put(sendMsgToTeacherTask.getId(), sendMsgToTeacherTask);
                    cachedThreadPool.execute(sendMsgToTeacherTask);

                } else if(receiveMsg.contains("confirmReceived")) {
                    // 确定教师收到了消息, 停掉相应的任务
                    ConfirmMsg confirmMsg = mGson.fromJson(receiveMsg, ConfirmMsg.class);
                    String taskConfirmedId = confirmMsg.getSendTaskId();
                    SendMsgToTeacherTask toStopTask = mSendMsgToTeacherTaskMap.get(taskConfirmedId);
                    if(toStopTask!=null){
                        toStopTask.setConfirmTeacherReceived(true);
                        mSendMsgToTeacherTaskMap.remove(toStopTask.getId());
                    }
                }
            }
//            receiveMulticast.leaveGroup(groupAddress);
//            receiveMulticast.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class SendMsgToTeacherTask implements Runnable{

        private String id;
        private StudentOnlineMsg studentOnlineMsgObj;
        private String teacherIp;
        private Integer teacherPort;
        private AtomicBoolean confirmTeacherReceived = new AtomicBoolean(false);

        public SendMsgToTeacherTask(StudentOnlineMsg sourceStudentOnlineMsgObj, String teacherIp, Integer teacherPort) {
            this.id = UUID.randomUUID().toString();

            StudentOnlineMsg toSendMsg = generateStudentOnlineMsg(sourceStudentOnlineMsgObj, this.id);

            this.studentOnlineMsgObj = toSendMsg;
            this.teacherIp = teacherIp;
            this.teacherPort = teacherPort;
        }

        @NonNull
        private StudentOnlineMsg generateStudentOnlineMsg(StudentOnlineMsg sourceStudentOnlineMsgObj, String taskId) {
            StudentOnlineMsg toSendMsg = new StudentOnlineMsg();
            toSendMsg.setSendTaskId(taskId);
            toSendMsg.setrName(sourceStudentOnlineMsgObj.getrName());
            toSendMsg.setuName(sourceStudentOnlineMsgObj.getuName());
            toSendMsg.setState(sourceStudentOnlineMsgObj.getState());
            toSendMsg.setImg(sourceStudentOnlineMsgObj.getImg());
            toSendMsg.setIp(sourceStudentOnlineMsgObj.getIp());
            toSendMsg.setWidth(sourceStudentOnlineMsgObj.getWidth());
            toSendMsg.setHeight(sourceStudentOnlineMsgObj.getHeight());
            return toSendMsg;
        }

        @Override
        public void run() {
            int i = 0;
            while(!confirmTeacherReceived.get() && i<5){
                sendMsgToTeacher(studentOnlineMsgObj, teacherIp, teacherPort);
                i++;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // 给教师发单播消息，告诉教师自己的信息
        private void sendMsgToTeacher(StudentOnlineMsg studentOnlineMsgObj, String teacherIp, int teacherPort){
            String studentOnlineMsgStr = mGson.toJson(studentOnlineMsgObj);
            Log.i("SendMsgToTeacherTask", "发出消息："+studentOnlineMsgStr);
            try {
            /*创建socket实例*/
                DatagramSocket datagramSocket = new DatagramSocket();

                //将本机的IP（这里可以写动态获取的IP）地址放到数据包里，其实server端接收到数据包后也能获取到发包方的IP的
                byte[] studentOnlineMsgData = studentOnlineMsgStr.getBytes();
                InetAddress address = InetAddress.getByName(teacherIp);

                // 发送的数据包
                DatagramPacket dataPacket = new DatagramPacket(studentOnlineMsgData, studentOnlineMsgData.length, address, teacherPort);
                datagramSocket.send(dataPacket);
                datagramSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setConfirmTeacherReceived(boolean confirmTeacherReceived) {
            this.confirmTeacherReceived.set(confirmTeacherReceived);
        }

        public boolean isConfirmTeacherReceived() {
            return confirmTeacherReceived.get();
        }

        public String getId() {
            return id;
        }

    }

}
