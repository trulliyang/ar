package appmagics.avatardemo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils
 * Created by Hope on 16/11/23.
 */

public class Utils {

    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static String arrayToString(float[] a) {
        if (a == null)
            return "";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "";

        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.toString();
            b.append(", ");
        }
    }

    public static Bitmap addIcon(Bitmap bitmap, Bitmap logoBitmap) {
        Canvas canvas = new Canvas(bitmap);
        int logoWidth = Math.min(logoBitmap.getWidth(), bitmap.getWidth());
        int logoHeight = Math.min(logoBitmap.getHeight(), bitmap.getHeight());

        int paddingLeft = 8;
        int paddingBottom = 8;

        Rect dst = new Rect();
        dst.left = bitmap.getWidth() - paddingLeft - logoWidth;
        dst.top = bitmap.getHeight() - logoHeight - paddingBottom;
        dst.right = bitmap.getWidth() - paddingLeft;
        dst.bottom = bitmap.getHeight() - paddingBottom;

        canvas.drawBitmap(logoBitmap, null, dst, null);

        return bitmap;
    }
}
