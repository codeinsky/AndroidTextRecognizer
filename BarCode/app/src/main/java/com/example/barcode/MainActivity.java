package com.example.barcode;

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
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private SurfaceView surfaceView;
    private TextView textView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private String barcodeData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surface_view);
        textView = findViewById(R.id.bar_code_text);
        if (isCameraPermissionsGranted()){
            Log.v("TAG" , "permissions already granted");
        }else {
            requestReadCameraPermission();
        }

        startBarCodeScanner();


    }

    private void startBarCodeScanner(){
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        cameraSource = new CameraSource.Builder(this,barcodeDetector)
                .setRequestedPreviewSize(1920 , 1080)
                .setAutoFocusEnabled(true)
                .build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(surfaceView.getHolder());
                }catch (IOException e ){
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
            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    if (barcodes.size() != 0) {


                        textView.post(new Runnable() {

                            @Override
                            public void run() {

                                if (barcodes.valueAt(0).email != null) {
                                    textView.removeCallbacks(null);
                                    barcodeData = barcodes.valueAt(0).email.address;
                                    textView.setText(barcodeData);
                                    Toast.makeText(getApplicationContext() , "Scanned email" , Toast.LENGTH_SHORT).show();
                                } else {

                                    barcodeData = barcodes.valueAt(0).displayValue;
                                    textView.setText(barcodeData);
                                    Toast.makeText(getApplicationContext() , "Scanned text" , Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }
                }

            });


    }

    private boolean isCameraPermissionsGranted(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestReadCameraPermission(){
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA} , 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("DB" , "Permissions granted");

                } else {
                    Log.v("DB" , "Permissions rejected");
                }
                return;
            }

        }
    }
}
