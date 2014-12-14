package com.epam.dzmitry_slutski.gesturetest;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;


public class UiUtils {
    static int sDisplayWidth = -1;
    static int sDisplayHeight = -1;

    @TargetApi(value = Build.VERSION_CODES.HONEYCOMB_MR2)
    private static void initDisplayDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            sDisplayWidth = size.x;
            sDisplayHeight = size.y;
        } else {
            sDisplayWidth = display.getWidth();
            sDisplayHeight = display.getHeight();
        }
    }

    public static int getDisplayHeight(Context context) {
        if (sDisplayHeight == -1) {
            initDisplayDimensions(context);
        }
        return sDisplayHeight;
    }

    public static int getDisplayWidth(Context context) {
        if (sDisplayWidth == -1) {
            initDisplayDimensions(context);
        }
        return sDisplayWidth;
    }

    /**
     * Convert px to dp.
     * @param context context
     * @param px value in px
     * @return dp value
     */
    public static Float getDp(final Context context, final Float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return px * scale + 0.5f;
    }

    /**
     * Convert px to dp.
     * @param context context
     * @param px value in px
     * @return dp value
     */
    public static int getDp(final Context context, final int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return Float.valueOf(px * scale + 0.5f).intValue();
    }

    /**
     * Gets fonts value for different resolutions.
     * @param context context
     * @param px value in px
     * @return sp value
     */
    public static int getFontSize(final Context context, final int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        int result = Float.valueOf(px * scale + 0.5f).intValue();
        if (result < 7) {
            result = result + 3;
        }
        return result;
    }

    /**
     * Convert dp value to the px value.
     * @param context context
     * @param dp value in dp
     * @return px value
     */
    public static Float getPx(final Context context, final Float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dp - 0.5f) * scale;
    }
}
