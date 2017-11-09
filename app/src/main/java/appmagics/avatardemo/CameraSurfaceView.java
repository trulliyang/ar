package appmagics.avatardemo;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by admin on 2017/10/20.
 */

public class CameraSurfaceView extends SurfaceView {

    private static final int MIN_PREVIEW_PIXELS = 640 * 480;

    private static final String CAMERA_TAG = CameraSurfaceView.class.getSimpleName();

    private OnCameraPreviewListener mOnCameraPreviewListener;

    private Camera.Size size;

    private CameraInterface mCameraInterface;

    public CameraSurfaceView(Context context) {
        super(context);

        initConfig();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initConfig();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initConfig();
    }

    public void resetCamera() {
        mCameraInterface.setPreviewPixels(MIN_PREVIEW_PIXELS);
        mCameraInterface.doOpenCamera(null, -1);
        mCameraInterface.doStartPreview(CameraSurfaceView.this.getHolder());
        setPreviewListener(previewListener);
        mCameraInterface.setTag(CAMERA_TAG);

    }

    public void releaseCamera() {
        mCameraInterface.doStopCamera(CAMERA_TAG);
    }

    private void initConfig() {
        mCameraInterface = CameraInterface.newInstance();

        setFocusable(true);
        getHolder().addCallback(new SurfaceCallback());//为SurfaceView的句柄添加一个回调函数


    }

    private void setPreviewListener(Camera.PreviewCallback previewListener) {
        if(mCameraInterface.getCamera() != null) {

            Camera camera = mCameraInterface.getCamera();
            Camera.Size size = camera.getParameters().getPreviewSize();
            camera.setPreviewCallbackWithBuffer(previewListener);
            camera.addCallbackBuffer(new byte[size.width * size.height * ImageFormat.getBitsPerPixel(camera.getParameters().getPreviewFormat()) / 8]);

            if(previewListener == null) {
                camera.addCallbackBuffer(null);
            }
        }
    }

    public Camera.Size getPreviewSize() {
        if(mCameraInterface != null && mCameraInterface.getCamera() != null) {
            return mCameraInterface.getCamera().getParameters().getPreviewSize();
        }
        return null;
    }

    /*SurfaceCallback*/
    private final class SurfaceCallback implements SurfaceHolder.Callback {

        private boolean isDestroy = false;

        public void surfaceDestroyed(SurfaceHolder holder) {
            try {
                isDestroy = true;
                mCameraInterface.doStopCamera(CAMERA_TAG);
            } catch (Exception e) {
                //相机已经关了
            }

            LogUtil.debug(getClass(), "surfaceDestroyed isDestroy = " + isDestroy);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            isDestroy = false;
            try {
                mCameraInterface.setPreviewPixels(MIN_PREVIEW_PIXELS);
                mCameraInterface.doOpenCamera(null, -1);
                mCameraInterface.doStartPreview(holder);
                mCameraInterface.setTag(CAMERA_TAG);

                setPreviewListener(previewListener);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtil.debug(getClass(), "surfaceChanged isDestroy = " + isDestroy);
            if(!mCameraInterface.isPreviewing()) {
                try {
                    mCameraInterface.setPreviewPixels(MIN_PREVIEW_PIXELS);
                    mCameraInterface.setTag(CAMERA_TAG);
                    mCameraInterface.doOpenCamera(null, -1);
                    mCameraInterface.doStartPreview(holder);
                    setPreviewListener(previewListener);
                    LogUtil.debug(getClass(), "surfaceChanged ");
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }


        }
    }

    Camera.PreviewCallback previewListener = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            try{
                if(mOnCameraPreviewListener != null) {
                    if(size == null) {
                        size = getPreviewSize();
                    }
                    mOnCameraPreviewListener.callback(data, size.width, size.height);
                }
            } finally {
                camera.addCallbackBuffer(data);
            }

        }
    };

    public interface OnCameraPreviewListener {
        void callback(byte[] data, int width, int height);
    }

    public void setCameraPreviewListener(OnCameraPreviewListener listener) {
        this.mOnCameraPreviewListener = listener;

        if(listener == null) {
            setPreviewListener(null);
        } else {
            setPreviewListener(previewListener);
        }
    }
}
