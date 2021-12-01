package com.example.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Camera.MainActivity";
    private static final int CAMERA_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addOnClickListener(R.id.btn_surfaceview, R.id.btn_textureview, R.id.btn_nv21data_callback);
        
        //申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    private void addOnClickListener(int... ids) {
        for (int i = 0; i < ids.length; i++) {
            Button button = findViewById(ids[i]);
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_surfaceview:
                startActivity(CameraSurfaceViewActivity.class);
                break;
            case R.id.btn_textureview:
                startActivity(CameraTexureViewActivity.class);
                break;
            case R.id.btn_nv21data_callback:
                startActivity(CameraSurfaceViewActivity.class);
                break;
            default:
                break;
        }        
    }
    
    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: 权限已经申请");
            } else {
                Toast.makeText(this, "需要打开相机权限", Toast.LENGTH_LONG).show();
            }
        }
    }
}