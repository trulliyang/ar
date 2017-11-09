package appmagics.avatardemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by admin on 2017/10/20.
 */

public class CameraInterface {

    /**
     * 最大宽高比差
     */
    private static final double MAX_ASPECT_DISTORTION = 0.15;

    /**
     * 最小预览界面的分辨率
     */
    private static final int MIN_PREVIEW_PIXELS = 480 * 320;

    public static final int DEFAULT_PREVIEW_PIXELS = 1280 * 720;

    private int mDefaultPreviewPixels = DEFAULT_PREVIEW_PIXELS;

    private TakePictureInterface mTakePictureInterface;


    private static final String TAG = CameraInterface.class.getSimpleName();

    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;

    private String mTag;

    /**
     * 1是前置 0是后置
     */
    private int mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    interface CamOpenOverCallback {
        public void cameraHasOpened();
    }

    private CameraInterface() {

    }

    public void setPreviewPixels(int pixels) {
        this.mDefaultPreviewPixels = pixels;
    }


    static synchronized CameraInterface newInstance() {

        return new CameraInterface();
    }

    /**
     * 打开Camera
     *
     * @param callback 回调
     */
    void doOpenCamera(CamOpenOverCallback callback, int cameraId) {

        if (mCamera == null) {
            int cameraNumbers = Camera.getNumberOfCameras();
            Camera.CameraInfo info = new Camera.CameraInfo();
            for (int i = 0; i < cameraNumbers; i++) {
                Camera.getCameraInfo(i, info);
                if(cameraId == -1) {
                    if (info.facing == mCurrentCameraId) {
                        mCamera = Camera.open(i);
                        mCurrentCameraId = i;
                        break;
                    }
                } else {
                    if (info.facing == cameraId) {
                        mCamera = Camera.open(i);
                        mCurrentCameraId = i;
                        break;
                    }
                }

            }
            if (callback != null) {
                callback.cameraHasOpened();
            }
        } else {
            LogUtil.debug(getClass(), "doOpenCamera");
//            doStopCamera(null);
        }
    }

    public int doSwitchCamera(CamOpenOverCallback callback) {
        if(mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mParams = null;

        }

        isPreviewing = false;

        int cameraId = 0;

        switch (mCurrentCameraId) {
            case Camera.CameraInfo.CAMERA_FACING_BACK:
                cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                break;
            case Camera.CameraInfo.CAMERA_FACING_FRONT:
                cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                break;
        }

        if (mCamera == null) {
            int cameraNumbers = Camera.getNumberOfCameras();
            Camera.CameraInfo info = new Camera.CameraInfo();
            for (int i = 0; i < cameraNumbers; i++) {
                Camera.getCameraInfo(i, info);
                if (info.facing == cameraId) {
                    mCamera = Camera.open(i);
                    mCurrentCameraId = i;
                    break;
                }
            }
            if (callback != null) {
                callback.cameraHasOpened();
            }
        }

        return mCurrentCameraId;
    }

    /**
     * 使用Surfaceview开启预览
     */
    public void doStartPreview(SurfaceHolder holder) {
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            try {

                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            initCamera();
        }


    }

    /**
     * 使用TextureView预览Camera
     *
     * @param surface     SurfaceTexture
     */
    void doStartPreview(SurfaceTexture surface) {
        if (isPreviewing) {
            isPreviewing = false;
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(surface);
            } catch (IOException e) {
                e.printStackTrace();
            }
            initCamera();
        }

    }


    /**
     * 停止预览，释放Camera
     */
    void doStopCamera(String tag) {
        if(!TextUtils.isEmpty(mTag) && !TextUtils.isEmpty(tag) && !mTag.equals(tag)) {
            return;
        }
        if (null != mCamera ) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();

            mParams = null;

            isPreviewing = false;
            mCamera.release();
            mCamera = null;

            mTakePictureInterface = null;

//            mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

            LogUtil.debug(getClass(), "doStopCamera");
        }
    }

    /**
     * 拍照
     */
    void doTakePicture() {
        if (isPreviewing && (mCamera != null)) {
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    public Camera getCamera() {
        return mCamera;
    }

    boolean isPreviewing() {
        return isPreviewing;
    }

    /**
     * 找出最适合的预览界面分辨率
     */
    private Camera.Size findBestPreviewResolution() {
        if (mParams != null) {
            Camera.Size defaultPreviewResolution = mParams.getPreviewSize();

            List<Camera.Size> rawSupportedSizes = mParams.getSupportedPreviewSizes();
            if (rawSupportedSizes == null) {
                return defaultPreviewResolution;
            }

            // 按照分辨率从大到小排序
            List<Camera.Size> supportedPreviewResolutions = new ArrayList<Camera.Size>(rawSupportedSizes);
            Collections.sort(supportedPreviewResolutions, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size a, Camera.Size b) {
                    int aPixels = a.height * a.width;
                    int bPixels = b.height * b.width;
                    if (bPixels < aPixels) {
                        return -1;
                    }
                    if (bPixels > aPixels) {
                        return 1;
                    }
                    return 0;
                }
            });

            StringBuffer previewResolutionSb = new StringBuffer();
            for (Camera.Size supportedPreviewResolution : supportedPreviewResolutions) {
                previewResolutionSb.append(supportedPreviewResolution.width).append('x').append(supportedPreviewResolution.height)
                        .append(' ');
            }
            LogUtil.debug(getClass(), "previewResolutionSb = " + previewResolutionSb);

            // 移除不符合条件的分辨率
//            double screenAspectRatio = (double) App.getInstance().getScreenWidth()
//                    / (double) App.getInstance().getScreenHeight();
            double screenAspectRatio = 720.0/1280.0;
            Iterator<Camera.Size> it = supportedPreviewResolutions.iterator();

            while (it.hasNext()) {
                Camera.Size supportedPreviewResolution = it.next();
                int width = supportedPreviewResolution.width;
                int height = supportedPreviewResolution.height;

                if (width * height == mDefaultPreviewPixels) { //有默认的
                    return supportedPreviewResolution;
                }
            }

            while (it.hasNext()) {
                Camera.Size supportedPreviewResolution = it.next();
                int width = supportedPreviewResolution.width;
                int height = supportedPreviewResolution.height;
                // 移除低于下限的分辨率，尽可能取高分辨率
                if (width * height < MIN_PREVIEW_PIXELS) {
                    it.remove();
                    continue;
                }

                // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
                // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
                // 因此这里要先交换然preview宽高比后在比较
                boolean isCandidatePortrait = width > height;
                int maybeFlippedWidth = isCandidatePortrait ? height : width;
                int maybeFlippedHeight = isCandidatePortrait ? width : height;
                double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
                double distortion = Math.abs(aspectRatio - screenAspectRatio);
                if (distortion > MAX_ASPECT_DISTORTION) {
                    it.remove();
                    continue;
                }

                // 找到与屏幕分辨率完全匹配的预览界面分辨率直接返回
//                if (maybeFlippedWidth == App.getInstance().getScreenWidth()
//                        && maybeFlippedHeight == App.getInstance().getScreenHeight()) {
//                    return supportedPreviewResolution;
//                }

                if (maybeFlippedWidth == 720 && maybeFlippedHeight == 1280) {
                    return supportedPreviewResolution;
                }

            }


            // 如果没有找到合适的，并且还有候选的像素，则设置其中最大比例的，对于配置比较低的机器不太合适
            if (!supportedPreviewResolutions.isEmpty()) {
                return supportedPreviewResolutions.get(0);
            }

            // 没有找到合适的，就返回默认的
            return defaultPreviewResolution;
        }
        return null;
    }

    private Camera.Size findBestPictureResolution() {
        if (mParams != null) {
            //获取相机支持的分辨率大小
            List<Camera.Size> supportedPicResolutions = mParams.getSupportedPictureSizes(); // 至少会返回一个值

            StringBuilder picResolutionSb = new StringBuilder();
            for (Camera.Size supportedPicResolution : supportedPicResolutions) {
                picResolutionSb.append(supportedPicResolution.width).append('x')
                        .append(supportedPicResolution.height).append(" ");
            }
            LogUtil.debug(getClass(), "picResolutionSb = " + picResolutionSb);

            Camera.Size defaultPictureResolution = mParams.getPictureSize();

            // 排序
            List<Camera.Size> sortedSupportedPicResolutions = new ArrayList<Camera.Size>(
                    supportedPicResolutions);
            Collections.sort(sortedSupportedPicResolutions, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size a, Camera.Size b) {
                    int aPixels = a.height * a.width;
                    int bPixels = b.height * b.width;
                    if (bPixels < aPixels) {
                        return -1;
                    }
                    if (bPixels > aPixels) {
                        return 1;
                    }
                    return 0;
                }
            });


            // 移除不符合条件的分辨率
//            double screenAspectRatio = (double) App.getInstance().getScreenWidth()
//                    / (double) App.getInstance().getScreenHeight();
            double screenAspectRatio = 720.0/1280.0;
            Iterator<Camera.Size> it = sortedSupportedPicResolutions.iterator();
            while (it.hasNext()) {
                Camera.Size supportedPreviewResolution = it.next();
                int width = supportedPreviewResolution.width;
                int height = supportedPreviewResolution.height;

                if (width * height == mDefaultPreviewPixels) { //有默认的
                    return supportedPreviewResolution;
                }

                // 在camera分辨率与屏幕分辨率宽高比不相等的情况下，找出差距最小的一组分辨率
                // 由于camera的分辨率是width>height，我们设置的portrait模式中，width<height
                // 因此这里要先交换然后在比较宽高比
                boolean isCandidatePortrait = width > height;
                int maybeFlippedWidth = isCandidatePortrait ? height : width;
                int maybeFlippedHeight = isCandidatePortrait ? width : height;
                double aspectRatio = (double) maybeFlippedWidth / (double) maybeFlippedHeight;
                double distortion = Math.abs(aspectRatio - screenAspectRatio);
                if (distortion > MAX_ASPECT_DISTORTION) {
                    it.remove();
                }
            }

            // 如果没有找到合适的，并且还有候选的像素，对于照片，则取其中最大比例的，而不是选择与屏幕分辨率相同的
            if (!sortedSupportedPicResolutions.isEmpty()) {
                return sortedSupportedPicResolutions.get(0);
            }

            // 没有找到合适的，就返回默认的
            return defaultPictureResolution;
        }
        return null;
    }


    private void initCamera() {
        if (mCamera != null) {
            try {
                mParams = mCamera.getParameters();
                mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
                mParams.setPreviewFormat(ImageFormat.NV21);

                //设置PreviewSize和PictureSize
                Camera.Size pictureSize = findBestPictureResolution();

                //设置拍照分辨率
                mParams.setPictureSize(pictureSize.width, pictureSize.height);
                Camera.Size previewSize = findBestPreviewResolution();

                mParams.setPreviewSize(previewSize.width, previewSize.height);

                if (Build.VERSION.SDK_INT >= 14) {
                    mCamera.setDisplayOrientation(90);
                } else {
                    mParams.setRotation(90);
                }

                //
                final List<String> focusModes = mParams.getSupportedFocusModes();
                // 连续聚焦
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                // 自动聚焦
                else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
                //-------------------------------------------
                // 可以帮助减少启动录制的时间，如果用opengl预览，用egl获取buffer，用mediacodec录制编码视频，这里好像没有用了
//                mParams.setRecordingHint(true);
                mCamera.setParameters(mParams);
                mCamera.startPreview();//开启预览


                isPreviewing = true;

                mParams = mCamera.getParameters(); //重新get一次

                LogUtil.debug(getClass(), "预览格式：" + mParams.getPreviewFormat());
                LogUtil.debug(getClass(), "NV21：" + ImageFormat.NV21);
                LogUtil.debug(getClass(), "NV12：" + ImageFormat.YV12);

                Log.i(TAG, "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
                        + "Height = " + mParams.getPreviewSize().height);
                Log.i(TAG, "最终设置:PictureSize--With = " + mParams.getPictureSize().width
                        + "Height = " + mParams.getPictureSize().height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int rotate) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation",
                    new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{rotate});
            }
        } catch (Exception e) {
            Log.e("Came_e", "图像出错");
        }
    }


    /*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback()
            //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
    {
        public void onShutter() {

        }
    };
    private Camera.PictureCallback mRawCallback = new Camera.PictureCallback()
            // 拍摄的未压缩原数据的回调,可以为null
    {

        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };

    //对jpeg图像数据的回调,最重要的一个回调
    private Camera.PictureCallback mJpegPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
                mCamera.stopPreview();
                isPreviewing = false;
            }
            if (null != b && mTakePictureInterface != null) {//会有旋转问题
                mTakePictureInterface.onTakePicture(b);
            }
            //再次进入预览
            mCamera.startPreview();
            isPreviewing = true;
        }
    };


    void setTakePictureInterface(TakePictureInterface listener) {
        this.mTakePictureInterface = listener;
    }

    public void setTag(String tag) {
        this.mTag = tag;
    }

}
