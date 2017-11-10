package appmagics.avatardemo;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


import com.huawei.ArCameraAccessories.ArCameraAccessories;
//import com.unity3d.player.UnityPlayer;

import java.nio.ByteBuffer;

import static com.huawei.ArCameraAccessories.ArCameraAccessories.ACTION_USB_PERMISSION;

public class MainActivity extends /*AppCompat*/Activity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

//    private CameraSurfaceView mCameraSurfaceView;
    private Tracker mTracker;
    private ARCamera mARCamera;
//    protected UnityPlayer mUnityPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.camera_surface);
//        mCameraSurfaceView.setCameraPreviewListener(cameraPreviewListener);

//        FrameLayout u3dView = (FrameLayout) findViewById(R.id.unity_view);

//        mUnityPlayer = new UnityPlayer(this);
//        View mUnityView = mUnityPlayer.getView();
//        u3dView.addView(mUnityView);
        
        registerReceiver(ArCameraAccessories.mUsbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
        ArCameraAccessories.init(this, this);
        ArCameraAccessories.openUSB();
        
        mARCamera = new ARCamera();
        mARCamera.openARCamera();
    }
    

//    private ByteBuffer mCameraDataBuffer;
//    private float[] mTrackerDataOutBuffer;
//    int mTrackerDataOutLen = 101 * 2 + 3 + 1;
//    String path = null;
//    CameraSurfaceView.OnCameraPreviewListener cameraPreviewListener =
//            new CameraSurfaceView.OnCameraPreviewListener() {
//                @Override
//                public void callback(byte[] data, int width, int height) {
//                    try {
//                        if (mTracker == null) {
//                            mTracker = new Tracker();
//                            path = TrackUtil.copyFileFromAssets(getApplication(), "as/track_data.dat");
//                        } else {
//                            try {
//                                int size = width * height * 3 / 2;
//
//                                if (mCameraDataBuffer == null) {
//                                    mCameraDataBuffer = ByteBuffer.allocateDirect(size);//分配空间
//                                }
//                                mCameraDataBuffer.rewind();
//                                mCameraDataBuffer.put(data);
//
//                                if (mTrackerDataOutBuffer == null) {
//                                    mTrackerDataOutBuffer = new float[mTrackerDataOutLen];
//                                }
//
//                                if(TextUtils.isEmpty(path)) {
//                                    path = TrackUtil.copyFileFromAssets(getApplication(), "as/track_data.dat");
//                                }
//
//                                mTracker.detect(getApplicationContext(), 0, width, height, 5, path, 270,
//                                        mCameraDataBuffer, mTrackerDataOutBuffer);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
    
    @Override
    protected void onDestroy() {
//        if (mUnityPlayer != null) {
//            mUnityPlayer.quit();
//        }
//        mARCamera.closeARCamera();
//        ArCameraAccessories.closeUSB();
        ArCameraAccessories.deInit();
        unregisterReceiver(ArCameraAccessories.mUsbReceiver);
        super.onDestroy();
    }

    // Pause Unity
    @Override
    protected void onPause() {
//        if (mCameraSurfaceView != null) {
//            isReset = true;
//            mCameraSurfaceView.setVisibility(View.GONE);
//        }

        isReset = true;
//        mUnityPlayer.pause();
        super.onPause();
    }

    private boolean isReset = false;

    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();

//        if (mUnityPlayer != null) {
//            mUnityPlayer.resume();
//        }
//        if (isReset && mCameraSurfaceView != null) {
//            mCameraSurfaceView.setVisibility(View.VISIBLE);
//        }
        isReset = false;
    }
    
}
