package net.chinaedu.screenrecorder.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/6/1.
 */

public class AdbUtils {
    public static boolean adbStart(Context context) {
        try {
//            AdbUtils.setProp("service.adb.tcp.port", "5555");
            try {
                if (AdbUtils.isProcessRunning("adbd")) {
                    AdbUtils.runRootCommand("stop adbd");
                }
            } catch (Exception e) {
            }
            AdbUtils.runRootCommand("start adbd");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isProcessRunning(String processName) throws Exception {
        boolean running = false;
        Process process = null;
        process = Runtime.getRuntime().exec("ps");
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while ((line = in.readLine()) != null) {
            if (line.contains(processName)) {
                running = true;
                break;
            }
        }
        in.close();
        process.waitFor();
        return running;
    }

    public static boolean runRootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }
}
