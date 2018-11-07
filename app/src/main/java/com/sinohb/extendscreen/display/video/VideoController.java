package com.sinohb.extendscreen.display.video;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.sinohb.extendscreen.display.BaseView;
import com.sinohb.extendscreen.display.Constants;

public class VideoController  implements VideoPresenter {
    private VideoCameraManagerable cameraManager;
    private BaseView mview;
    public VideoController(BaseView view) {
        view.setPresenter(this);
        init();
    }

    protected void init() {
        cameraManager = new VideoCameraManager();
    }

    @Override
    public int openCamera(int w, int h) {
        return cameraManager == null ? Constants.DEVICE_NOT_SUPPORT : cameraManager.open(w, h);
    }

    @Override
    public int releaseCamera() {
        return cameraManager == null ? Constants.DEVICE_NOT_SUPPORT : cameraManager.release();
    }

    @Override
    public int startPreview(SurfaceHolder holder) {
        return cameraManager == null ? Constants.DEVICE_NOT_SUPPORT : cameraManager.startPreviewDisplay(holder);
    }

    @Override
    public Camera.Size getFixedSize(int w, int h) {
        return cameraManager == null ? null : cameraManager.getFixedSize(w, h);
    }


    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void complete() {

    }

    @Override
    public void destroy() {
        cameraManager.release();
    }

//    @Override
//    public BaseTestTask getTask() {
//        return task;
//    }
}
