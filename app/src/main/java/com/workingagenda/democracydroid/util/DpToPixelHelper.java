package com.workingagenda.democracydroid.util;

import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by derrickrocha on 7/16/17.
 */

public class DpToPixelHelper {

    /**
     * Converting dp to pixel
     */
    public static int dpToPx(int dp, DisplayMetrics metrics) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics));
    }
}
