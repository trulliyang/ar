package appmagics.avatardemo;

import android.content.Context;

import java.nio.ByteBuffer;

/**
 * Created by admin on 2017/10/20.
 */

public class Tracker {
    static {
        System.loadLibrary("native-lib");
    }

    public void initialise(Context context, int type, int width, int height, int format, String path, int angle) {
    }

    public void detect(Context context, int type, int width, int height, int format, String path,
                       int angle, ByteBuffer data, float[] dataout) {
        if (null == path) {
            path = "/storage/emulated/0/track_data.dat";
        }
        detectJNI(context,type, width,  height, format, path, angle, data, dataout);
    }
    
    public native void testJNI(Context context, int type, int width, int height, int format, String path, int angle);
    public native void detectJNI(Context context, int type, int width, int height, int format,
                                 String path, int angle, ByteBuffer data, float[] dataout);
}
