package org.apache.cordova.camera;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import xiaojiantong.com.R;

import org.apache.cordova.LOG;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 自定义拍照的Acitivity
 */
public class CameraActivity2 extends Activity implements CameraView.CameraListener {

    private static final String TAG = "CameraActivity2";
    public static final int REQUEST_PICTURE_OK = 0x99;
    public static final int REQUEST_PICTURE_FAIL = 0x98;
    private CameraView mCameraView;
    private String mPath;
    private int quality;
    private int targetWidth;
    private int targetHeight;

    public static void startForResult(Activity activity, String path, int requestCode) {
        Intent intent = new Intent(activity, CameraActivity2.class);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, path);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mCameraView = new CameraView(this);
        setContentView(mCameraView);

        quality = this.getIntent().getIntExtra("quality",50);
        targetWidth = this.getIntent().getIntExtra("targetWidth",0);
        targetHeight = this.getIntent().getIntExtra("targetHeight",0);
        mPath = this.getFilePath()+"/"+this.getIntent().getStringExtra("fileName");
        mCameraView.setCameraListener(this);

    }

    @Override
    public void onCapture(Bitmap bitmap) {

        File file = new File(mPath);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {

           // Display display = getWindowManager().getDefaultDisplay();
            //不需要压缩宽高
            if(bitmap.getWidth() == targetWidth && bitmap.getHeight() == targetHeight){
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, this.quality, out);
                out.flush();
                out.close();
            }else{//需要压缩
                sizeCompress(bitmap,file);
            }

            setResult(REQUEST_PICTURE_OK);
        } catch (Exception e) {
            Log.e(TAG, "save picture error", e);
            setResult(REQUEST_PICTURE_FAIL);
        }

        finish();
        if(bitmap!=null){
            bitmap.recycle();
            bitmap = null;
        }

        cleanUp();
    }

    @Override
    public void onCameraClose() {
        finish();
    }

    @Override
    public void onCameraError(Throwable th) {
        Log.e(TAG, "camera error", th);
        onCameraClose();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.onPause();
    }

    private String getFilePath(){
        String cachePath = null;
        // SD Card Mounted
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cachePath = Environment.getExternalStorageDirectory() + "/camera/xjt";
        }
        // Use internal storage
        else {
            cachePath = Environment.getDataDirectory() + "/camera/xjt";
        }

        File cacheFile = new File(cachePath);
        if (cacheFile.exists() == false) {
            cacheFile.mkdirs();
        }
      //  LOG.i(TAG, "cacheFile path:" + cacheFile.getAbsolutePath());
        return cacheFile.getAbsolutePath();
    }

    private void cleanUp(){
        System.gc();
        Runtime.getRuntime().gc();
    }

    private  void sizeCompress(Bitmap bmp, File file) throws Exception{

        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(this.targetWidth, this.targetHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, targetWidth, targetHeight);
        canvas.drawBitmap(bmp, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        result.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
        } finally {
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        canvas = null;
        if(result!=null) {
            result.recycle();
            result = null;
        }
    }
}
