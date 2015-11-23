package com.battleshippark.bsp_camera;

import android.content.Context;
import android.media.ExifInterface;
import android.view.OrientationEventListener;

/**
 */
public class OrientationController {
    private final OrientationEventListener listener;
    private static int orientation;

    public OrientationController(Context context) {
        listener = new OrientationEventListener(context) {

            @Override
            public void onOrientationChanged(int orientation) {
                OrientationController.orientation = orientation;
            }
        };
    }

    public void enable() {
        if (listener.canDetectOrientation())
            listener.enable();
    }

    public void disable() {
        listener.disable();
    }

    /**
     * ExifInterface에서 사용할 회전 정보를 반환한다. 카메라 미리보기를 회전해서 보여 주고 있으므로
     * 센서에서 읽은 값에도 더해서 반환해야 한다
     */
    public static String getExifOrientation() {
        final int MARGIN = 45;
        int orientation = OrientationController.orientation;

        if (90 - MARGIN <= orientation && orientation < 90 + MARGIN)
            return String.valueOf(ExifInterface.ORIENTATION_ROTATE_90);
        else if (180 - MARGIN <= orientation && orientation < 180 + MARGIN)
            return String.valueOf(ExifInterface.ORIENTATION_ROTATE_180);
        else if (270 - MARGIN < orientation && orientation <= 270 + MARGIN)
            return String.valueOf(ExifInterface.ORIENTATION_ROTATE_270);
        else
            return String.valueOf(ExifInterface.ORIENTATION_NORMAL);
    }

    public static Orientation4P getOrientation4P() {
        final int MARGIN = 45;
        int orientation = OrientationController.orientation;

        if (90 - MARGIN <= orientation && orientation < 90 + MARGIN)
            return Orientation4P.ORIENTATION_90;
        else if (180 - MARGIN <= orientation && orientation < 180 + MARGIN)
            return Orientation4P.ORIENTATION_180;
        else if (270 - MARGIN < orientation && orientation <= 270 + MARGIN)
            return Orientation4P.ORIENTATION_270;
        else
            return Orientation4P.ORIENTATION_0;
    }

    public enum Orientation4P {
        ORIENTATION_0,
        ORIENTATION_90,
        ORIENTATION_180,
        ORIENTATION_270,
    }
}
