package android.app.mia;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.List;

public class MiaMdmPolicyManager {

	private static final Bitmap Bitmap = null;
	private List List;

	public MiaMdmPolicyManager(Context context) {
		// TODO Auto-generated constructor stub
	}

	// yes
	public void setHomeKey(boolean iscontrol) {
	};

	// yes
	public void setRecentKey(boolean iscontrol) {
	};

	// yes
	public void setNavigaBar(boolean iscontrol) {
	};

	// yes
	public void setBackKey(boolean iscontrol) {
	};



	public int silentUnInstall(String packageName) {
		return -1;
	};

	// yes
	public int silentInstall(String apkPath) {
		Log.d("bailu", "silent_install_test");
		return -1;
	};

	// yes
	public boolean setOnlyCharging(boolean iscontrol) {
		return false;
	};


	// yes
	public Bitmap getMiaScreen() {
		return Bitmap;
	};


	public void setRotationLockMia( final boolean enabled , final int rotation){}
}
