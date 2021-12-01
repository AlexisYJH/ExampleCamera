package com.example.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NV21DataCallbackActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "NV21DataCallbackActivity";
    ImageView mImageView;
    private Camera mCamera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nv21data_callback);

        mImageView = findViewById(R.id.iv_imageview);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(this);

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
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    //窗口改变
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (mCamera == null) {
            return;
        }
        //当我们的程序开始运行，surFaceView显示当前摄像头获取的内容，获取的NV21数据显示在ImageView控件上
        try {
            mCamera.setPreviewDisplay(holder);//设置摄像机的预览界面
            mCamera.setDisplayOrientation(getDegree());

            if (mCamera != null) {
                //获取摄像头参数
                Camera.Parameters parameters = mCamera.getParameters();

                // 可以根据情况设置参数
                //镜头缩放
                //parameters.setZoom();

                //设置预览照片的大小
                //parameters.setPreviewSize(200, 200);

                //设置预览照片时每秒显示多少帧的最小值和最大值
                //parameters.setPreviewFpsRange(4, 10);

                //设置图片格式
                //parameters.setPictureFormat(ImageFormat.JPEG);

                //设置JPG照片的质量  图片的质量[0-100],100最高
                //parameters.set("jpeg-quality", 85);

                //设置照片的大小
                //parameters.setPictureSize(200, 200);

                //设置预览图片的图像格式
                parameters.setPreviewFormat(ImageFormat.NV21);

                mCamera.setParameters(parameters);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                //处理data，这里面的data数据就是NV21格式的数据，将数据显示在ImageView控件上面
                //获取尺寸
                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                YuvImage yuvImage = new YuvImage(data,
                        ImageFormat.NV21, previewSize.width, previewSize.height, null);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //yuvimage转换成jpg格式
                yuvImage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);
                byte[] imageBytes = baos.toByteArray();

                //将imageBytes转换成bitmap
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;

                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
                mImageView.setImageBitmap(rotateBitmap(bitmap, getDegree()));

            }
        });
        mCamera.startPreview();
    }

    private int getDegree() {
        // 获取当前屏幕旋转的角度
        int rotating = this.getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;// 度数
        // 根据手机旋转的角度，来设置surfaceView的显示的角度
        switch (rotating) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }


    /**
     * 选择变换
     * @param origin 原图
     * @param degree  旋转角度，可正可负
     * @return 旋转后的图片
     */
    private Bitmap rotateBitmap(Bitmap origin, float degree) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);
        // 围绕原地进行旋转
        Bitmap result = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (result.equals(origin)) {
            return result;
        }
        origin.recycle();
        return result;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.release();
        }
    }
}
