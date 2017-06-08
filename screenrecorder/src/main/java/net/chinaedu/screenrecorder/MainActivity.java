package net.chinaedu.screenrecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.chinaedu.screenrecorder.utils.PreferenceService;

public class MainActivity extends Activity{

    private PreferenceService mPreferenceService;

    private EditText mEtWidth;
    private EditText mEtHeight;
    private EditText mEtBitRate;
    private EditText mEtFrame;
    private Button mBtnSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferenceService = new PreferenceService(this);

        mEtWidth = (EditText)findViewById(R.id.et_width);
        mEtHeight = (EditText)findViewById(R.id.et_height);
        mEtBitRate = (EditText)findViewById(R.id.et_bit_rate);
        mEtFrame = (EditText)findViewById(R.id.et_frame);

        mEtWidth.setText(String.valueOf(AppContext.getInstance().getWidth()));
        mEtHeight.setText(String.valueOf(AppContext.getInstance().getHeight()));
        mEtBitRate.setText(String.valueOf(AppContext.getInstance().getBitRate()));
        mEtFrame.setText(String.valueOf(AppContext.getInstance().getFrame()));

        mBtnSave = (Button)findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try{
                    int width = Integer.valueOf(mEtWidth.getText().toString());
                    int height = Integer.valueOf(mEtHeight.getText().toString());
                    int frame = Integer.valueOf(mEtFrame.getText().toString());
                    int bitRate = Integer.valueOf(mEtBitRate.getText().toString());

                    mPreferenceService.save("width", width);
                    mPreferenceService.save("height", height);
                    mPreferenceService.save("frame", frame);
                    mPreferenceService.save("bit_rate", bitRate);
                    Toast.makeText(MainActivity.this, "保存成功!", Toast.LENGTH_SHORT).show();

//                    Intent i1 = new Intent(MainActivity.this, RecorderService.class);
//                    startService(i1);

                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this, "只能输入整数!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
