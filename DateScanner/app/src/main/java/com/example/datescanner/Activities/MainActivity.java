package com.example.datescanner.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.datescanner.Fragments.CreateItem;
import com.example.datescanner.Fragments.DateScan;
import com.example.datescanner.Fragments.ScanItem;
import com.example.datescanner.R;

public class MainActivity extends AppCompatActivity {
    String mainFragmentTag = "mainFrame";
    Button createItemBtn ;
    Button scanItemBtn ;
    Button scanDateBtn;
    FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isCameraPermissionsGranted()){
            Log.v("TAG" , "permissions already granted");
        }
        else {
            Log.v("TAG" , "requesting permissions");
            requestReadCameraPermission();
        }
        createItemBtn = findViewById(R.id.set_item_bnt);
        scanItemBtn = findViewById(R.id.scan_item_btn);
        scanDateBtn = findViewById(R.id.scanDateBtn);
        fm = getSupportFragmentManager();
        CreateItem createItemFragment = (CreateItem)fm.findFragmentById(R.id.fragment_frame);
        if (createItemFragment==null){
            createItemFragment = new CreateItem();
            fm.beginTransaction().add(R.id.fragment_frame, createItemFragment, mainFragmentTag).commit();
        }
        createItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction().replace(R.id.fragment_frame, new CreateItem()).commit();

            }
        });
        scanItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction().replace(R.id.fragment_frame , new ScanItem()).commit();
            }
        });

        scanDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fm.beginTransaction().replace(R.id.fragment_frame , new DateScan()).commit();
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
    public void closeDateScanActivity(){
        fm.beginTransaction().replace(R.id.fragment_frame, new CreateItem()).commit();
    }
}
