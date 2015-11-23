package com.battleshippark.bsp_camera;

import android.annotation.TargetApi;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.rootView)
    View mRootView;

    @Bind(R.id.preview)
    SurfaceView mPreview;

    @Bind(R.id.filteredPreview)
    GLSurfaceView mFilteredPreview;

    private CameraController mCameraController;
    private OrientationController mOrientationController;

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
        mOrientationController.enable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOrientationController.disable();
        mCameraController.release();
    }

    @OnClick(R.id.preview)
    void onClickPreview() {
        mCameraController.takePictureAsync();
    }

    private void initData() {
        GPUImage mGPUImage = new GPUImage(this);
        mGPUImage.setGLSurfaceView(mFilteredPreview);
        mGPUImage.setFilter(new GPUImageSepiaFilter());

        mCameraController = new CameraController(mPreview.getHolder(), mGPUImage);
        mOrientationController = new OrientationController(this);
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
