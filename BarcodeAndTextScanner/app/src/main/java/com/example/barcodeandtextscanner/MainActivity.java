package com.example.barcodeandtextscanner;
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
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import info.androidhive.barcode.camera.CameraSource;
import info.androidhive.barcode.camera.CameraSourcePreview;
import info.androidhive.barcode.camera.GraphicOverlay;

public class MainActivity extends AppCompatActivity {
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preview = (CameraSourcePreview)findViewById(R.id.preview);
        graphicOverlay = (GraphicOverlay)findViewById(R.id.faceOverlay);
        if (isCameraPermissionsGranted()){
            Log.v("TAG" , "permissions granted");
            createCameraSource();
        }
        else {
            requestReadCameraPermission();
        }


    }

    private void createCameraSource(){
        Context context = getApplicationContext();
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeTrackerFactory = new BarcodeTrackerFactory(graphicOverlay);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeTrackerFactory).build());



        MultiDetector multiDetector = new MultiDetector.Builder()
                .add(barcodeDetector)
                .build();

        if (!multiDetector.isOperational()){
            Log.v("TAG" , " Detector doesn't response");
        }

        cameraSource = new CameraSource.Builder(context , multiDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(15.0f)
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null){
            cameraSource.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
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

            if (cameraSource != null) {
                try {
                    preview.start(cameraSource,graphicOverlay);
                    Log.v("TAG" , "camera started");
                } catch (IOException e) {
                    Log.e("TAG", "Unable to start camera source.", e);
                    cameraSource.release();
                    cameraSource = null;
                }
            }
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



}
