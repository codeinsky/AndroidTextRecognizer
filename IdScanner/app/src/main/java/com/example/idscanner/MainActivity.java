package com.example.idscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    SurfaceView surfaceView;
    TextView textView;
    CameraSource cameraSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surfaceView);
        textView = findViewById(R.id.text_view);
        if(ifCameraPermissionsGranted()){
            Log.v("TAG" , "Camera permissions already granted");
        }else{
            requestCameraPermissions();
        }
        startCameraSource();

    }

    private boolean ifCameraPermissionsGranted(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermissions(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},100);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("TAG", "permissions granted");
                } else {
                    Log.v("TAG", "rejected");
                }
                return;
        }
    }

    private void startCameraSource(){
        final TextRecognizer textRecognizer= new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()){
            Log.v("TAG" , "detector is not operational");
        }
        else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                    cameraSource.start(surfaceView.getHolder());
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size()!=0){
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i=0 ; i < items.size(); i ++){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                textView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });


        }
    }
}
