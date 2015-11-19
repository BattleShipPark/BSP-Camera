package com.battleshippark.bsp_camera;

import android.hardware.Camera;

/**
 */
public class CameraController {
    public static Camera getCameraInstance() {
        return Camera.open();
    }
}
