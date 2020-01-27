package com.applex.inc.stiqrit.Util;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.applex.inc.stiqrit.CameraActivity;
import com.applex.inc.stiqrit.DocView;
import com.applex.inc.stiqrit.HistoryActivity;
import com.applex.inc.stiqrit.PdfViewer;
import com.applex.inc.stiqrit.R;
import com.applex.inc.stiqrit.ScannerActivity;
import com.applex.inc.stiqrit.StiQRcontent;
import com.applex.inc.stiqrit.Video_activity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import static com.applex.inc.stiqrit.StiQRcontent.code;

public class BottomSheet extends BottomSheetDialogFragment {


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.popupwindow,container,false);
//        view.setBackgroundColor(Color.TRANSPARENT);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        LinearLayout button1 = view.findViewById(R.id.camera);
        LinearLayout button2 = view.findViewById(R.id.gallery);
        LinearLayout button3 = view.findViewById(R.id.note);
        LinearLayout button4 = view.findViewById(R.id.videoLink);
        LinearLayout button5 = view.findViewById(R.id.pdf);
        LinearLayout button6 = view.findViewById(R.id.voice);
//
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent i = new Intent(StiQRcontent.this, CameraActivity.class);
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                }
                else {
                    pickCamera();
                }
                dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                }
                else{
                    pickGallery();
                }
                dismiss();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DocView.class);
                i.putExtra("stiQR_ID", code);
                startActivity(i);
                dismiss();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Video_activity.class);
                i.putExtra("stiQR_ID", code);
                startActivity(i);
                dismiss();
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PdfViewer.class);
                i.putExtra("stiQR_ID", code);
                startActivity(i);
                dismiss();
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getActivity(), CameraActivity.class);
//                i.putExtra("stiQR_ID", code);
//                startActivity(i);
                Utility.showToast(getActivity(),"Coming soon");
                dismiss();
            }
        });
        return view;

    }

    public void pickGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK );
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera(){
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        intent.putExtra("stiQR_ID",code);
        startActivity(intent);
    }

    ///////////////////////PERMISSION/////////////////////////
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(getActivity(), storagePermission,STORAGE_REQUEST_CODE);

    }

    private boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE )== (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(getActivity(), cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE )== (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        pickCamera();
                    }
                    else{
                        Toast.makeText(getActivity(),"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:
                if(grantResults.length > 0){

                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickGallery();
//                        sel=1;
//                        Intent intent = new Intent(launcher_activity.this, HistoryActivity.class);
//                        intent.putExtra("choice",1);
//                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getActivity(),"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

}

