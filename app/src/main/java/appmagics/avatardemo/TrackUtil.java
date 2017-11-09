package appmagics.avatardemo;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * utils
 * Created by hopeliao on 2017/5/11.
 */

public class TrackUtil {

    private static String getModelPath(Context mContext, String modelName) {
        String path = null;
        File dataDir = mContext.getApplicationContext().getExternalFilesDir(null);
        if (dataDir != null) {
            path = dataDir.getAbsolutePath() + File.separator + modelName;
        }
        return path;
    }

    public static String copyFileFromAssets(Context context, String filename) {

        String path = getModelPath(context,filename);
        if (path != null) {
            File modelFile = new File(path);
            if (modelFile.exists() == false ) {
                //如果模型文件不存在或者当前模型文件的版本跟sdcard中的版本不一样
                try {
                    if (modelFile.exists())
                        modelFile.delete();
                    if (!modelFile.getParentFile().exists()) {
                        modelFile.getParentFile().mkdirs();
                    }
                    modelFile.createNewFile();
                    InputStream in = context.getAssets().open(filename);
                    if (in == null) {
                        Log.e("MultiTrack106", "the src module is not existed");
                    }
                    OutputStream out = new FileOutputStream(modelFile);
                    byte[] buffer = new byte[4096];
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    modelFile.delete();
                    return null;
                }
            }

        }
        return path;
    }

}
