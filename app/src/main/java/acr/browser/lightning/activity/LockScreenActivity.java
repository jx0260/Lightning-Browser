package acr.browser.lightning.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import acr.browser.lightning.R;

/**
 * 锁屏页
 * Created by Jin Liang on 2017/6/19.
 */

public class LockScreenActivity extends Activity {

    private static final String TAG = "LockScreenActivity";

    private RelativeLayout mLockView;
    private FrameLayout contentContainer;

    private LinearLayout lockTokenInputLl;
    private Button showUnLockBtn;
    private EditText lockTokenEt;
    private TextView lockErrorTip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //加载布局文件
        mLockView = (RelativeLayout)getLayoutInflater().inflate(R.layout.lock_view, null);
        setContentView(mLockView);

        mLockView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        lockTokenInputLl = (LinearLayout) mLockView.findViewById(R.id.ll_lock_token_input);

        showUnLockBtn = (Button) mLockView.findViewById(R.id.btn_show_unLock);
        showUnLockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockTokenInputLl.setVisibility(View.VISIBLE);
                v.setVisibility(View.INVISIBLE);
            }
        });

        lockTokenEt = (EditText) mLockView.findViewById(R.id.et_lock_token);
        lockErrorTip = (TextView) mLockView.findViewById(R.id.tv_lock_error_tip);
        final String targetToken = "1234";

        mLockView.findViewById(R.id.btn_unlock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(lockTokenEt.getText().toString())){
                    Toast.makeText(LockScreenActivity.this, "口令为空！", Toast.LENGTH_SHORT).show();
                } else {
                    if(lockTokenEt.getText().toString().equals(targetToken)){
                        finish();
                        Toast.makeText(LockScreenActivity.this, "解锁成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        lockErrorTip.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

//        //将视图添加到Window中
//        contentContainer = new FrameLayout(this) {
//
//            @Override
//            public boolean dispatchKeyEvent(KeyEvent event) {
//                System.out.println("dispatchKeyEvent " + KeyEvent.keyCodeToString(event.getKeyCode()));
//                return true;
//            }
//
//            @Override
//            public boolean dispatchTouchEvent(MotionEvent ev) {
//                System.out.println("dispatchKeyEvent");
//                return super.dispatchTouchEvent(ev);
//            }
//        };
//        contentContainer.addView(mLockView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:{
                Log.i(TAG, "KEYCODE_BACK");
                break;
            }
            case KeyEvent.KEYCODE_HOME:{
                Log.i(TAG, "KEYCODE_HOME");
                break;
            }
            case KeyEvent.KEYCODE_MENU:{
                Log.i(TAG, "KEYCODE_MENU");
                break;
            }
            case KeyEvent.KEYCODE_POWER:{
                Log.i(TAG, "KEYCODE_POWER");
                break;
            }
            case KeyEvent.KEYCODE_VOLUME_UP:{
                Log.i(TAG, "KEYCODE_VOLUME_UP");
                break;
            }
            case KeyEvent.KEYCODE_VOLUME_DOWN:{
                Log.i(TAG, "KEYCODE_VOLUME_DOWN");
                break;
            }
        }
        return true;
    }
}
