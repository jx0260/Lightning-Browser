package acr.browser.lightning.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import net.chinaedu.aedu.manager.AeduActivityManager;

import java.io.File;

import acr.browser.lightning.version.view.VersionChecker;

/**
 * Created by MartinKent on 2017/6/6.
 */

public class Installer {
    public static void install(Context context, String file) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(VersionChecker.getUriForFile(context, new File(file)), "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(new File(file)), "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, ("安装失败，请手动打开文件进行安装.\n" + file), Toast.LENGTH_SHORT).show();
        } finally {
            AeduActivityManager.getInstance().finishAllActivity();
        }
    }
}
