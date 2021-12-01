package com.example.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class CameraTexureViewActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private static final String TAG = "CameraTexureViewActivity";
    private Camera mCamera;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_texture_view);

        TextureView textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);

        try {
            //打开摄像头并将展示方向旋转90度
            mCamera = Camera.open(0);
            mCamera.setDisplayOrientation(90);
        } catch (Exception e) {
            Log.e(TAG, "fail: " + e);
            Toast.makeText(this, "相机打开失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        if (mCamera == null) {
            return;
        }
        //在控件创建的时候，进行相应的初始化工作
        try {
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        if (mCamera != null) {
            mCamera.release();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }
}
