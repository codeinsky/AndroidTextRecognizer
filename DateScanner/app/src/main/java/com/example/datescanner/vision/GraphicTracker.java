package com.example.datescanner.vision;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.UiThread;
import androidx.fragment.app.FragmentManager;

import com.example.datescanner.Activities.ScannedBottle;
import com.example.datescanner.Beans.Bottle;
import com.example.datescanner.Fragments.DateScan;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.text.TextBlock;

import info.androidhive.barcode.camera.GraphicOverlay;

public class GraphicTracker<T> extends Tracker<T> {
    private GraphicOverlay mOverlay;
    private TrackedGraphic<T> mGraphic;
    private BarcodeUpdateListener barcodeUpdateListener;
    private  Context context;

    GraphicTracker(GraphicOverlay overlay, TrackedGraphic<T> graphic , Context context) {
        Log.v("TAG" , "trakcer constructor");
        mOverlay = overlay;
        mGraphic = graphic;
        this.context = context;
        if (context instanceof BarcodeUpdateListener){
            Log.v("TAG" , "that is instance");
            this.barcodeUpdateListener = (BarcodeUpdateListener) context;

        }

    }

    /**
     * Start tracking the detected item instance within the item overlay.
     */
    @Override
    public void onNewItem(int id, T item) {
        mGraphic.setId(id);
    }

    /**
     * Update the position/characteristics of the item within the overlay.
     */
    @Override
    public void onUpdate(Detector.Detections<T> detectionResults, T item) {
        mOverlay.add(mGraphic);
        mGraphic.updateItem(item);
        if (detectionResults==null){
            Log.v("TAG" , "results null");
        }
        else {
            SparseArray<TextBlock> items = (SparseArray<TextBlock>) detectionResults.getDetectedItems();
                for (int i = 0; i < items.size(); ++i) {
                    TextBlock oneItem = items.valueAt(i);
                    if (oneItem.getValue().contains("ITEM")&&oneItem.getValue().contains("EXP")){
                        Log.v("TAG" , "bottle tag found:"  + oneItem.getValue());
                        barcodeUpdateListener.changeFragment();
                        Intent intent = new Intent(context, ScannedBottle.class);
                        context.startActivity(intent);

                    }

                }

        }


    }

    public interface BarcodeUpdateListener {
        @UiThread
        void onBarcodeDetected(Detector.Detections detectionResults, boolean flag);
        @UiThread
        void changeFragment();
    }

    /**
     * Hide the graphic when the corresponding face was not detected.  This can happen for
     * intermediate frames temporarily, for example if the face was momentarily blocked from
     * view.
     */
    @Override
    public void onMissing(Detector.Detections<T> detectionResults) {
        mOverlay.remove(mGraphic);
    }

    /**
     * Called when the item is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mGraphic);
    }
}