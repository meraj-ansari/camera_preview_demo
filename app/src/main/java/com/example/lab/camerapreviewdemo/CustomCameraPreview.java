package com.example.lab.camerapreviewdemo;

/**
 * Created by lab on 11/15/2016.
 */

import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;
import android.util.Log;
import android.hardware.Camera.CameraInfo;
import android.app.Activity;

import java.util.List;


public class CustomCameraPreview extends SurfaceView implements SurfaceHolder.Callback
{
    Activity mParentActivity = null;
    Camera mCamera = null;
    boolean isCamOpened = false;
    SurfaceHolder mHolder = null;
    List<Camera.Size> mSupportedPreviewSizes = null;
    int mCamId = 0;

    public CustomCameraPreview(Activity parentActivity)
    {
        super(parentActivity);

        mParentActivity = parentActivity;
        mCamera = Camera.open();

        mHolder = this.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int w, int h)
    {
        try
        {
            // fail safe handling
             if(mHolder.getSurface() == null)
             {
                 // No surface exist!!
                 return;
             }

            try
            {
                mCamera.stopPreview();
            }
            catch(Exception e)
            {

            }


            Log.d("CCP", "surfaceChanged():surface.width : " + w + ", surface.height : " + h);
            openCameraSafely(mCamId);

            if(mCamera != null)
            {
                //Set the camera orientation
                CameraInfo info = new CameraInfo();
                android.hardware.Camera.getCameraInfo(mCamId, info);
                mCamera.setDisplayOrientation(getCameraOrientation(info));

                //Set the preview size
                Camera.Parameters camParam = mCamera.getParameters();

                // Get the existing params
                mSupportedPreviewSizes = camParam.getSupportedPreviewSizes();
                for(Camera.Size size  : mSupportedPreviewSizes)
                {
                    Log.d("CCP", "surfaceChanged():mSupportedPreviewSizes.width : " + size.width + ", mSupportedPreviewSizes.height : " + size.height);
                }

                Camera.Size optimalSize = getOptimalPreviewSize(mSupportedPreviewSizes, w, h);
                camParam.setPreviewSize(optimalSize.width, optimalSize.height);
                requestLayout();
                mCamera.setParameters(camParam);

                //start preview
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);

        Log.d("CCP", "onMeasure(): widthMeasureSpec : " + widthMeasureSpec + ", heightMeasureSpec : " + heightMeasureSpec);
        Log.d("CCP", "onMeasure():width : " + width + ", height : " + height);
    }

    public void openCameraSafely(int camId)
    {
        try
        {
            closeCameraSafely();
            mCamera = Camera.open(camId);
            isCamOpened = (mCamera != null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void closeCameraSafely()
    {
        try
        {
            if (mCamera != null)
            {
                mCamera.release();
                mCamera = null;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public int  getCameraOrientation(CameraInfo info)
    {
        int result = 0;

        int rotation = mParentActivity.getWindowManager().getDefaultDisplay().getRotation();

        int degrees = 0;
        switch (rotation)
        {
            case Surface.ROTATION_0:
            {
                degrees = 0;
                break;
            }
            case Surface.ROTATION_90:
            {
                degrees = 90;
                break;
            }
            case Surface.ROTATION_180:
            {
                degrees = 180;
                break;
            }
            case Surface.ROTATION_270:
            {
                degrees = 270;
                break;
            }
        }//_switch


        int camOrientation = info.orientation;

        if(info.facing == CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (camOrientation + degrees) % 360;
            result = (360 - result) % 360;
        }
        else
        {
            result = (camOrientation - degrees + 360) % 360;
        }

        return result;
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h)
    {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}