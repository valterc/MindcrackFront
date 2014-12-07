package com.vcutils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by Valter on 07/12/2014.
 */
public class ScalingUtils {

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * metrics.density + .5f;
        return px;
    }


    public static float convertPixelsToDp(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / metrics.density + .5f;
        return dp;
    }

}
