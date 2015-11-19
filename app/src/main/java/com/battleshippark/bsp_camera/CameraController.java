package com.battleshippark.bsp_camera;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.hardware.Camera;
import android.view.SurfaceHolder;

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

	}

	public void openAsync(final int id) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					release();
					mCamera = Camera.open(id);

					List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();

					Camera.Parameters p = mCamera.getParameters();
					p.setPreviewSize(localSizes.get(0).width, localSizes.get(0).height);
					mCamera.setParameters(p);
					LOG.i(CameraController.class.getSimpleName(), "Preview=(%d,%d)", localSizes.get(0).width, localSizes.get(0).height);

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
			}
		});
	}

	public void release() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;

			mSurfaceHolder.removeCallback(CameraController.this);
		}
	}
}
