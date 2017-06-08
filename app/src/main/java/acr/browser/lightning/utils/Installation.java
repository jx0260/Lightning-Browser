package acr.browser.lightning.utils;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
 * 秘密文件  ^_^ <br>
 * 耗时：写3毫秒，读1毫秒
 * @author Jin Liang 
 * @date 2017-2-7 下午1:51:06
 */
public class Installation {
    private static String sID = null;
	private static final String INSTALLATION = "_ce_install";
	private static final String shitPhoneID = "shit_phone_noImei_noExternalStoragePermission_phoneId_000";

	public synchronized static String id(Context context) {
		if (TextUtils.isEmpty(sID)) {
			String imei = getIMEI(context);
			if(!TextUtils.isEmpty(imei)){
				sID = imei;
				return imei;
			}
			
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				File installInfoFile = new File(Environment.getExternalStorageDirectory(), INSTALLATION);
				try {
					if (!installInfoFile.exists()){
						if(installInfoFile.createNewFile()){
							sID = writeInstallationFile(installInfoFile);
						}
					} else {
						sID = readInstallationFile(installInfoFile);
					}
				} catch (Exception e) {
					sID = shitPhoneID;
					throw new RuntimeException(e);
				}
			}
		}
		return sID;
	}

	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		/*
		 * 唯一的设备ID： GSM手机的 IMEI 和 CDMA手机的 MEID. Return null if device ID is not
		 * available.
		 */
		return tm.getDeviceId();// String
	}

	private static String readInstallationFile(File installation) throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}

	private static String writeInstallationFile(File installation) throws IOException {
		FileOutputStream out = new FileOutputStream(installation);
		String id = UUID.randomUUID().toString();
		out.write(id.getBytes());
		out.close();
		return id;
	}
}