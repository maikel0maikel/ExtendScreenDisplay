package com.sinohb.extendscreen.display;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.sinohb.extendscreen.display.video.VideoController;
import com.sinohb.extendscreen.display.video.VideoPresenter;
import com.sinohb.lib.extendscreen.PresentationService;

public class ExtendDisplayService extends PresentationService implements SurfaceHolder.Callback,BaseView{
    private SurfaceView mSurfaceView;
    private VideoPresenter mPresenter;
    @Override
    protected int getThemeId() {
        return R.style.PlayTheme;
    }

    @Override
    protected View buildPresoView(Context context, LayoutInflater layoutInflater) {
        View mRootView = layoutInflater.inflate(R.layout.fragment_video, null);
        mSurfaceView = (SurfaceView) mRootView.findViewById(R.id.video_sv);
        new VideoController(this);
        mSurfaceView.getHolder().addCallback(this);
        return mSurfaceView;
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
