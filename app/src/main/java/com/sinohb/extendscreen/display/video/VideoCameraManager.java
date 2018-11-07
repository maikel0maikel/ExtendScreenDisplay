package com.sinohb.extendscreen.display.video;

import android.hardware.Camera;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceHolder;


import com.sinohb.extendscreen.display.Constants;

import java.io.IOException;
import java.util.List;

public class VideoCameraManager implements VideoCameraManagerable {

    private static final String TAG = "VideoCameraManager";
    private Camera mCamera;
    private static final int STATE_NONE = 0;
    private static final int STATE_OPENING = 1;
    private static final int STATE_OPENED = 2;
    private static final int STATE_PRIVIEW = 3;
    private int mCameraState = STATE_NONE;
    public static final int DEFAULT_WIDTH = 704;
    public static final int DEFAULT_HEIGHT = 576;

    @Override
    public int open() {
        return open(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public int open(int w, int h) {
        if (mCameraState == STATE_OPENING) {
            Log.e(TAG, "method---->[open] camera is opening mCameraState=" + mCameraState);
            return Constants.DEVICE_SUPPORTED;
        }
        if (mCameraState != STATE_NONE) {
            realReleaseCamera();
            Log.e(TAG, "method---->[open] mCameraState is not correct mCameraState=" + mCameraState);
            return open(w, h);
        }
        mCameraState = STATE_OPENING;
        if (mCamera == null) {
            mCamera = Camera.open();
        }
        if (mCamera == null) {
            Log.e(TAG, "method---->[open] Camera.open() camera is null ");
            mCameraState = STATE_NONE;
            return Constants.DEVICE_NOT_SUPPORT;
        }
        setCameraPreviewSize(mCamera, w, h);
        mCameraState = STATE_OPENED;
        return Constants.DEVICE_SUPPORTED;
    }

    @Override
    public int startPreviewDisplay(SurfaceHolder holder) {
        if (mCameraState != STATE_OPENED) {
            Log.e(TAG, "method---->[startPreviewDisplay] mCameraState is not correct mCameraState=" + mCameraState);
            return Constants.DEVICE_STATE_ERROR;
        }
        if (mCamera == null) {
            Log.e(TAG, "method---->[startPreviewDisplay] camera is null");
            return Constants.DEVICE_NOT_SUPPORT;
        }
        try {
            //mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            mCameraState = STATE_PRIVIEW;
            Log.e(TAG, "method---->[startPreviewDisplay] success");
            return Constants.DEVICE_SUPPORTED;
        } catch (IOException e) {
            realReleaseCamera();
            Log.e(TAG, "method---->[startPreviewDisplay] error " + e.getMessage());
        } catch (RuntimeException e) {
            realReleaseCamera();
            Log.e(TAG, "method---->[startPreviewDisplay] error " + e.getMessage());
        }
        return Constants.DEVICE_STATE_ERROR;
    }

    @Override
    public int release() {
        if (mCameraState == STATE_OPENED || mCameraState == STATE_PRIVIEW) {
            realReleaseCamera();
            return Constants.DEVICE_SUPPORTED;
        }
        return Constants.DEVICE_STATE_ERROR;
    }


    private void realReleaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.cancelAutoFocus();
            mCamera.stopPreview();
            try {
                mCamera.setPreviewDisplay(null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mCamera.release();
        }
        mCamera = null;
        mCameraState = STATE_NONE;
    }

    private void setCameraPreviewSize(Camera camera, int expectWidth, int expectHeight) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setRecordingHint(true);
        if (expectHeight == 0) {
            expectHeight = DEFAULT_HEIGHT;
        }
        if (expectWidth == 0) {
            expectWidth = DEFAULT_WIDTH;
        }
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size mPreviewSize = getFixedSize(sizes, expectWidth, expectHeight);
        if (mPreviewSize != null) {
            expectWidth = mPreviewSize.width;
            expectHeight = mPreviewSize.height;
        }
        Log.e(TAG, "[setPreviewSize] ---> method expectWidth:" + expectWidth + ",expectHeight:" + expectHeight);
        parameters.setPreviewSize(expectWidth, expectHeight);
        camera.setParameters(parameters);
    }

    private Camera.Size getFixedSize(List<Camera.Size> sizes, int w, int h) {
        if (sizes == null) {
            return null;
        }
        Camera.Size fixedSize = null;
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        float minDiff = Float.MAX_VALUE;
        int targetHeight = h;
        //找到比较合适的尺寸
        for (Camera.Size size : sizes) {
            float ratio = (float) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                fixedSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        //没有找到合适的，则忽略ASPECT_TOLERANCE
        if (fixedSize == null) {
            minDiff = Float.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    fixedSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return fixedSize;
    }

    @Override
    public Camera.Size getFixedSize(int w, int h) {
        return getCameraPreviewSize(w, h);
    }

    @Nullable
    private Camera.Size getCameraPreviewSize(double w, int h) {
        if (mCamera == null) {
            return null;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        if (sizes == null) {
            return null;
        }
        Camera.Size fixedSize = null;
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = w / h;
        float minDiff = Float.MAX_VALUE;
        int targetHeight = h;
        //找到比较合适的尺寸
        for (Camera.Size size : sizes) {
            float ratio = (float) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                fixedSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        //没有找到合适的，则忽略ASPECT_TOLERANCE
        if (fixedSize == null) {
            minDiff = Float.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    fixedSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return fixedSize;
    }
}
