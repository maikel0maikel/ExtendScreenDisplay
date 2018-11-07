package com.sinohb.extendscreen.display.video;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.sinohb.extendscreen.display.BasePresenter;


public interface VideoPresenter extends BasePresenter{

    int openCamera(int w, int h);

    int releaseCamera();

    int startPreview(SurfaceHolder holder);

    Camera.Size getFixedSize(int w, int h);

}
