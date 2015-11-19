package com.battleshippark.bsp_camera;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
	@Bind(R.id.rootView)
	View mRootView;

	@Bind(R.id.preview)
	SurfaceView mPreview;

	private CameraController mCameraController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		initData();
		initUI();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCameraController.openAsync(0);
	}

	@Override
	protected void onPause() {
		mCameraController.release();
		super.onPause();
	}

	private void initData() {
		mCameraController = new CameraController(mPreview.getHolder());
	}

	private void initUI() {
		hideSystemUI();
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void hideSystemUI() {
		mRootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN
			| View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
			| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
	}
}
