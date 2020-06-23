package com.example.textdetect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

import info.androidhive.barcode.camera.CameraSource;
import info.androidhive.barcode.camera.CameraSourcePreview;
import info.androidhive.barcode.camera.GraphicOverlay;

public class MainActivity extends AppCompatActivity implements GraphicTracker.BarcodeUpdateListener{
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPreview = (CameraSourcePreview)findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay)findViewById(R.id.faceOverlay);
        if(isCameraPermissionsGranted()){
            Log.v("TAG" , "Camera permissions granted");
            createCameraSource();
        }else {
            requestReadCameraPermission();
        }


    }

    private void createCameraSource(){
        Context context = getApplicationContext();
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        OcrTrackerFactory ocrTrackerFactory = new OcrTrackerFactory(mGraphicOverlay,this);
        textRecognizer.setProcessor(new MultiProcessor.Builder<>(ocrTrackerFactory).build());
        MultiDetector multiDetector = new MultiDetector.Builder()
                .add(textRecognizer)
                .build();
        if (!multiDetector.isOperational()){
            Log.v("TAG" , "Detector ha issues");

        }
        mCameraSource = new CameraSource.Builder(getApplicationContext() , multiDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280,1024)
                .setRequestedFps(2.0f)
                .build();
    }

    private void startCameraSource(){
        //
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, 9001);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource,mGraphicOverlay);
                Log.v("TAG" , "camera started");
            } catch (IOException e) {
                Log.e("TAG", "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCameraSource!=null){
            mCameraSource.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
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
                    createCameraSource();

                } else {
                    Log.v("DB" , "Permissions rejected");
                }
                return;
            }

        }
    }

    @Override
    public void onBarcodeDetected(Detector.Detections detectionResults, boolean flag) {

    }
}
