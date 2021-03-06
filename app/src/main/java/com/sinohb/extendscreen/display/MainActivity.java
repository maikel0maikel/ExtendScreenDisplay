package com.sinohb.extendscreen.display;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sinohb.extendscreen.display.video.VideoController;
import com.sinohb.extendscreen.display.video.VideoPresenter;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback,BaseView{
    private SurfaceView mSurfaceView;
    private VideoPresenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_video);
        startService(new Intent(this,ExtendDisplayService.class));
//        new VideoController(this);
//        mSurfaceView = (SurfaceView) findViewById(R.id.video_sv);
//        mSurfaceView.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (mSurfaceView != null) {
            mSurfaceView.post(new Runnable() {
                @Override
                public void run() {
                    int w = mSurfaceView.getWidth();
                    int h = mSurfaceView.getHeight();
                    int openResult = ((VideoController) mPresenter).openCamera(w, h);
                    if (openResult == Constants.DEVICE_SUPPORTED) {
                        ((VideoController) mPresenter).startPreview(mSurfaceView.getHolder());
                    }
                }
            });
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mSurfaceView!=null){
            mSurfaceView.getHolder().removeCallback(this);
        }
        mPresenter.releaseCamera();
    }

    @Override
    public void setPresenter(BasePresenter presenter) {
        mPresenter = (VideoPresenter) presenter;
    }
}
