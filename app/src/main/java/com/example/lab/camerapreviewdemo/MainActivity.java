package com.example.lab.camerapreviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity
{
    CustomCameraPreview mCustomCameraPreview = null;
    FrameLayout mParentLayout = null;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParentLayout = (FrameLayout)findViewById(R.id.camera_preview);

        mCustomCameraPreview = new CustomCameraPreview(this);
    }

    protected void onResume()
    {
        try
        {
            super.onResume();
            mParentLayout.addView(mCustomCameraPreview);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
