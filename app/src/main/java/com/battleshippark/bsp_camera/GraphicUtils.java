package com.battleshippark.bsp_camera;

import android.content.res.Resources;

/**
 */
public class GraphicUtils {
    public static int dp2px(Resources res, int dp) {
        return (int) (res.getDisplayMetrics().density * dp);
    }
}
