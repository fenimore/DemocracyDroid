package com.workingagenda.democracydroid.Helpers;

import android.util.DisplayMetrics;
import android.util.TypedValue;


public class DpToPixelHelper {

    /**
     * Converting dp to pixel
     */
    public static int dpToPx(int dp, DisplayMetrics metrics) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics));
    }
}
