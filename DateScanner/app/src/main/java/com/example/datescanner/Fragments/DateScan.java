package com.example.datescanner.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Camera;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.datescanner.Activities.MainActivity;
import com.example.datescanner.R;
import com.example.datescanner.vision.GraphicTracker;
import com.example.datescanner.vision.OcrTrackerFactory;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import java.io.IOException;
import info.androidhive.barcode.camera.CameraSource;
import info.androidhive.barcode.camera.CameraSourcePreview;
import info.androidhive.barcode.camera.GraphicOverlay;

/**
 * A simple {@link Fragment} subclass.
 */
public class DateScan extends Fragment implements GraphicTracker.BarcodeUpdateListener {
    public CameraSource cameraSource = null;
    private CameraSourcePreview cameraSourcePreview;
    private GraphicOverlay graphicOverlay;
    private static volatile String scannedText;
    public static Context context ;
    private volatile boolean stopThread = false;


    public DateScan() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_date_scan, container, false);
        cameraSourcePreview = view.findViewById(R.id.cameraSource);
        graphicOverlay = view.findViewById(R.id.overlay);
        createCameraSource(view);
        return view;
    }

    public void createCameraSource(View view){
        context = getContext();
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        OcrTrackerFactory textTrackerFactory = new OcrTrackerFactory(graphicOverlay, context );
        textRecognizer.setProcessor(new MultiProcessor.Builder<>(textTrackerFactory).build());
        MultiDetector multiDetector = new MultiDetector.Builder()
                .add(textRecognizer)
                .build();
        if (!multiDetector.isOperational()){
            Log.v("TAG", "Detector doesn't word");
        }
        cameraSource = new CameraSource.Builder(context, multiDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280,1024)
                .setRequestedFps(2.0f)
                .build();
    }

    public void startCamera(){

        if (cameraSource != null) {
            try {
                cameraSourcePreview.start(cameraSource,graphicOverlay);
                Log.v("TAG" , "camera started");
            } catch (IOException e) {
                Log.e("TAG", "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startCamera();
        scannedText = null;
        stopThread = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannedText = null;
    }

    @Override
    public void onDestroyView() {
        Log.v("TAG" , "on destroy view");
        super.onDestroyView();
        super.onDestroy();
        if (cameraSource!=null){
            cameraSource.release();
        }
        scannedText = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraSourcePreview.stop();
        scannedText = null;
    }


    @Override
    public void onBarcodeDetected(Detector.Detections detectionResults, boolean flag) {
    }

    public void changeFragment(){
        FragmentManager fm = getChildFragmentManager();
        Fragment main = fm.findFragmentByTag("mainFrame");
        fm.beginTransaction().replace(R.id.fragment_frame, new CreateItem()).commit();
    }

}


