package com.battleshippark.bsp_camera;

import android.annotation.TargetApi;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.rootView)
    View mRootView;

    @Bind(R.id.preview)
    SurfaceView mPreview;

    @Bind(R.id.filteredPreview)
    GLSurfaceView mFilteredPreview;

    @Bind(R.id.focus)
    ImageView mFocusView;

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
        mGPUImage.setFilter(new GPUImageBrightnessFilter());

        mCameraController = new CameraController(mPreview.getHolder(), mGPUImage);
        mOrientationController = new OrientationController(this);

        mFilteredPreview.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showFocusingDrawable(event.getX(), event.getY());
                mCameraController.setFocusArea(mFilteredPreview.getWidth(), mFilteredPreview.getHeight(), event.getX(), event.getY(),
                        (success, camera) -> {
                            if (success) {
                                showFocusedDrawable(event.getX(), event.getY());
                            }
                        });
            }
            return true;
        });
    }

    private void showFocusingDrawable(float x, float y) {
        mFocusView.setSelected(false);
        mFocusView.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mFocusView.getLayoutParams();
        lp.width = lp.height = GraphicUtils.dp2px(getResources(), 80);
        lp.setMargins((int) x - lp.width / 2, (int) y - lp.height / 2, 0, 0);
        mFocusView.setLayoutParams(lp);

        Animation ani = AnimationUtils.loadAnimation(this, R.anim.camera_focusing);
        mFocusView.startAnimation(ani);
    }

    private void showFocusedDrawable(float x, float y) {
//        LOG.i(MainActivity.class.getSimpleName(), "focused");
        mFocusView.setSelected(true);

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mFocusView.getLayoutParams();
        lp.width = lp.height = GraphicUtils.dp2px(getResources(), 40);
        lp.setMargins((int) x - lp.width / 2, (int) y - lp.height / 2, 0, 0);
        mFocusView.setLayoutParams(lp);
        mFocusView.clearAnimation();
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
