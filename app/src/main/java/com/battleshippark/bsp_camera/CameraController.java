package com.battleshippark.bsp_camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

/**
 */
public class CameraController implements SurfaceHolder.Callback {
    private final SurfaceHolder mSurfaceHolder;
    private final ExecutorService executor;
    private Camera mCamera;

    public CameraController(SurfaceHolder holder) {
        mSurfaceHolder = holder;

        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mSurfaceHolder.getSurface() == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        release();
    }

    public void openAsync(final int id) {
        executor.execute(() -> {
            try {
                release();
                mCamera = Camera.open(id);

                setParameters();

                try {
                    mCamera.setPreviewDisplay(mSurfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.setDisplayOrientation(90);

                mCamera.startPreview();

                mSurfaceHolder.addCallback(CameraController.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setParameters() {
        Camera.Parameters p = mCamera.getParameters();

        List<Camera.Size> previewSizes = p.getSupportedPreviewSizes();
        p.setPreviewSize(previewSizes.get(0).width, previewSizes.get(0).height);
        LOG.i(CameraController.class.getSimpleName(), "Preview=(%d,%d)", previewSizes.get(0).width,
                previewSizes.get(0).height);

        List<String> focusModes = p.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            LOG.i(CameraController.class.getSimpleName(), "FOCUS_MODE_AUTO");
        }

        List<Camera.Size> pictureSizes = p.getSupportedPictureSizes();
        p.setPictureSize(pictureSizes.get(0).width, pictureSizes.get(0).height);
        LOG.i(CameraController.class.getSimpleName(), "Picture=(%d,%d)", pictureSizes.get(0).width,
                pictureSizes.get(0).height);

        mCamera.setParameters(p);
    }

    public void release() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release(); // release the camera for other applications
            mCamera = null;

            mSurfaceHolder.removeCallback(CameraController.this);
        }
    }

    public void takePictureAsync() {
        Display display = ((WindowManager) Application.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        LOG.i(MainActivity.class.getSimpleName(), "rot=%d", display.getRotation());
        executor.execute(() -> mCamera.takePicture(null, null, this::onPictureTaken));
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private void onPictureTaken(byte[] data, Camera camera) {
        String orientation = OrientationController.getExifOrientation();

        try {
            File file = save(data);

            modifyExif(file, orientation);

            addToGallery(file);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Application.getHandler().post(() -> openAsync(0));
    }

    private void modifyExif(File file, String orientation) throws IOException {
        ExifInterface exif = new ExifInterface(file.getAbsolutePath());
        exif.setAttribute(ExifInterface.TAG_ORIENTATION, orientation);
        exif.saveAttributes();
    }

    private File save(byte[] data) throws IOException {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            throw new IOException();
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            LOG.d(CameraController.class.getSimpleName(), "File not found: %s", e.getMessage());
        } catch (IOException e) {
            LOG.d(CameraController.class.getSimpleName(), "Error accessing file: %s", e.getMessage());
        }

        return pictureFile;
    }

    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LOG.d(CameraController.class.getSimpleName(), "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void addToGallery(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        Application.getContext().sendBroadcast(mediaScanIntent);
    }
}
