package com.codesaid.ui.publish;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.codesaid.R;
import com.codesaid.databinding.ActivityCaptureLayoutBinding;
import com.codesaid.lib_base.view.RecordView;
import com.codesaid.lib_navannotation.ActivityDestination;

import java.io.File;
import java.util.ArrayList;

/**
 * Created By codesaid
 * On :2020-05-04 15:02
 * Package Name: com.codesaid.ui.publish
 * desc:
 *
 * @author codesaid
 */

@ActivityDestination(pageUrl = "main/tabs/publish", needLogin = true)
public class CaptureActivity extends AppCompatActivity {

    private ActivityCaptureLayoutBinding mBinding;

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};

    private static final int PERMISSION_CODE = 1000;
    private ArrayList<String> deniedPermission = new ArrayList<>();

    // 相机摄像头 默认 后置
    private CameraX.LensFacing mLensFacing = CameraX.LensFacing.BACK;
    // 旋转角度
    private int rotation = Surface.ROTATION_0;
    // 分辨率
    private Size resolution = new Size(1280, 720);
    // 宽高比
    private Rational rational = new Rational(9, 16);
    private Preview mPreview;
    private ImageCapture mImageCapture;
    private VideoCapture mVideoCapture;

    private boolean takingPicture;
    private String outputFilePath;

    public static final String RESULT_FILE_PATH = "file_path";
    public static final String RESULT_FILE_WIDTH = "file_width";
    public static final String RESULT_FILE_HEIGHT = "file_height";
    public static final String RESULT_FILE_TYPE = "file_type";

    public static final int REQUEST_CODE = 1000;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, CaptureActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_capture_layout);
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);

        mBinding.recordView.setOnRecordListener(new RecordView.onRecordListener() {
            @Override
            public void onClick() {
                takingPicture = true;
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        System.currentTimeMillis() + ".jpeg");
                mImageCapture.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        onFileSaved(file);
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        showErrorToast(message);
                    }
                });
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onLongClick() {
                takingPicture = false;
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        System.currentTimeMillis() + ".mp4");
                mVideoCapture.startRecording(file, new VideoCapture.OnVideoSavedListener() {
                    @Override
                    public void onVideoSaved(File file) {
                        onFileSaved(file);
                    }

                    @Override
                    public void onError(VideoCapture.UseCaseError useCaseError, String message, @Nullable Throwable cause) {
                        showErrorToast(message);
                    }
                });
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onFinish() {
                mVideoCapture.stopRecording();
            }
        });
    }

    private void onFileSaved(File file) {
        outputFilePath = file.getAbsolutePath();
        String mimeType = takingPicture ? "image/jpeg" : "video/mp4";
        MediaScannerConnection.scanFile(this,
                new String[]{outputFilePath}, new String[]{mimeType}, null);
        PreviewActivity.startActivity(this, outputFilePath, !takingPicture, "完成");
    }

    private void showErrorToast(@NonNull String message) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(CaptureActivity.this, message, Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(() -> Toast.makeText(CaptureActivity.this, message, Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            deniedPermission.clear();
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int result = grantResults[i];
                if (result != PackageManager.PERMISSION_GRANTED) {
                    deniedPermission.add(permission);
                }
            }

            if (deniedPermission.isEmpty()) {
                initCameraX();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.capture_permission_message))
                        .setNegativeButton(getString(R.string.capture_permission_no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                CaptureActivity.this.finish();
                            }
                        })
                        .setPositiveButton(getString(R.string.capture_permission_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String[] denied = new String[deniedPermission.size()];
                                ActivityCompat.requestPermissions(CaptureActivity.this,
                                        deniedPermission.toArray(denied), PERMISSION_CODE);
                            }
                        })
                        .create()
                        .show();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void initCameraX() {
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                //前后摄像头
                .setLensFacing(mLensFacing)
                //旋转角度
                .setTargetRotation(rotation)
                // 分辨率
                .setTargetResolution(resolution)
                // 宽高比
                .setTargetAspectRatio(rational)
                .build();

        mPreview = new Preview(previewConfig);

        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig
                .Builder()
                .setTargetAspectRatio(rational)
                .setTargetResolution(resolution)
                .setLensFacing(mLensFacing)
                .setTargetRotation(rotation)
                .build();

        mImageCapture = new ImageCapture(imageCaptureConfig);

        VideoCaptureConfig videoCaptureConfig = new VideoCaptureConfig
                .Builder()
                .setTargetRotation(rotation)
                .setTargetResolution(resolution)
                .setTargetAspectRatio(rational)
                .setLensFacing(mLensFacing)
                .setBitRate(3 * 1024 * 1024)
                .setVideoFrameRate(25)
                .build();

        mVideoCapture = new VideoCapture(videoCaptureConfig);

        mPreview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                TextureView textureView = mBinding.textureView;
                ViewGroup parent = (ViewGroup) textureView.getParent();
                parent.removeView(textureView);
                parent.addView(textureView, 0);

                textureView.setSurfaceTexture(output.getSurfaceTexture());
            }
        });

        CameraX.unbindAll();
        CameraX.bindToLifecycle(this, mPreview, mImageCapture, mVideoCapture);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PreviewActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra(RESULT_FILE_PATH, outputFilePath);
            //当设备处于竖屏情况时，宽高的值 需要互换，横屏不需要
            intent.putExtra(RESULT_FILE_WIDTH, resolution.getHeight());
            intent.putExtra(RESULT_FILE_HEIGHT, resolution.getWidth());
            intent.putExtra(RESULT_FILE_TYPE, !takingPicture);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
