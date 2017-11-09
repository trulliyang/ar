package appmagics.avatardemo;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

/**
 * Created by frank on 2017/10/28.
 */

public class TestActivity extends UnityPlayerActivity {
    private LinearLayout u3dLayout;
    private Button zoomInBtn, zoomOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.u3d_layout);
        u3dLayout = (LinearLayout) findViewById(R.id.u3d_root_layout);
        u3dLayout.addView(mUnityPlayer);
        mUnityPlayer.requestFocus();
        zoomInBtn = (Button) findViewById(R.id.zoom_in_btn);
        zoomOutBtn = (Button) findViewById(R.id.zoom_out_btn);
        zoomInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnityPlayer.UnitySendMessage("HONGRUAN_RECEIVER", "ReceiveFaceData", "你猜");
            }
        });
        zoomOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnityPlayer.UnitySendMessage("Manager", "ZoomOut", "");
            }
        });
    }

    public String getName(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TestActivity.this, str, 1000).show();
            }
        });
        return "我是怪兽，哈哈哈";
    }

    /**
     * 3D调用此方法，用于退出3D
     */
    public void makePauseUnity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mUnityPlayer != null) {
                    try {
                        mUnityPlayer.quit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                TestActivity.this.finish();
            }
        });
    }

    /**
     * 按键点击事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onDestroy();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //UnityPlayer.UnitySendMessage("Manager", "Unload", "");
        mUnityPlayer.quit();
    }

    // Pause Unity
    @Override
    protected void onPause() {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // mUnityPlayer.quit();
        // this.finish();
    }
}
