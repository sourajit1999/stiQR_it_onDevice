package com.applex.inc.stiqrit;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.applex.inc.stiqrit.ModelItems.gridItems;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static java.lang.Boolean.TRUE;


public class DocView extends AppCompatActivity {

    EditText mResultEt;
    com.github.clans.fab.FloatingActionMenu flmenu;
    Button ocr;

    Dialog mydialogue;
    Uri path;
    public static Uri image_uri,resultUri;
    ImageView pv;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_view);

        mydialogue = new Dialog(this);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        final Intent i =getIntent();
//        Toast.makeText(DocView.this,i.getStringExtra("gridId"),Toast.LENGTH_LONG).show();


        mResultEt = findViewById(R.id.docViewer) ;
        ocr = findViewById(R.id.ocr);
        pv = findViewById(R.id.dummyPhoto) ;


        flmenu = findViewById(R.id.menu);
        flmenu.close(true);

        ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        flmenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mydialogue.setContentView(R.layout.note_name_dialog);
                mydialogue.setCanceledOnTouchOutside(TRUE);

                final Button save;
                final EditText fileName;

                save= mydialogue.findViewById(R.id.save);

                fileName =mydialogue.findViewById(R.id.Notename);

                if(i.getStringExtra("Data")!=null){
                    save.setText("Save");
                    fileName.setText(i.getStringExtra("name"));
                }

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            if(mResultEt.length()>0){
//                                gridItems ig = new gridItems(R.drawable.ic_format_align_left_black_24dp,fileName.getText().toString(),mResultEt.getText().toString());
//                                FirebaseDatabase.getInstance().getReference("CodesData")
//                                        .child(i.getStringExtra("code"))
//                                        .child(i.getStringExtra("gridId"))
//                                        .setValue(ig).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if(task.isSuccessful() ){
//                                            Toast.makeText(DocView.this,"Note saved",Toast.LENGTH_LONG).show();
//                                            mydialogue.dismiss();
//                                        }
//                                    }
//                                });
                            }
                            else {
                                Toast.makeText(DocView.this,"Note Empty",Toast.LENGTH_LONG).show();
                            }


                    }
                });
                mydialogue.show();

            }
        });

        com.github.clans.fab.FloatingActionButton docfile  = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.doc);
        docfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mResultEt.length()!=0) {
                    saveDoc();
                }
                else {
                    Toast.makeText(DocView.this,"Field Empty",Toast.LENGTH_LONG).show();
                    flmenu.close(true);
                }

            }


        });


        com.github.clans.fab.FloatingActionButton pdffile = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.pdf);
        pdffile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mResultEt.length()!=0) {
                    savePdf();
                }
                else {
                    Toast.makeText(DocView.this,"Field Empty",Toast.LENGTH_LONG).show();
                    flmenu.close(true);
                }
            }
        });

    }



    public void savePdf(){
        Button save;
        final EditText fileName;
        TextView ext ;
//
        mydialogue.setContentView(R.layout.file_name_dialog);
        mydialogue.setCanceledOnTouchOutside(TRUE);

        ext =  mydialogue.findViewById(R.id.extension);
        ext.setText(".pdf");

        save= mydialogue.findViewById(R.id.save);
        fileName = mydialogue.findViewById(R.id.fname);
        final String fName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis()) ;
        fileName.setHint(fName);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileName.length() != 0) {
                    final Document mDoc = new Document();
                    final String filName=fileName.getText().toString().trim().replaceAll(" ","_");
                    File f=  new File(Environment.getExternalStorageDirectory()+"/stiQR it","Pdf");
                    f.mkdirs();
                    final String filPath = Environment.getExternalStorageDirectory() + "/stiQR it/Pdf/" + filName + ".pdf";
                    try {
                        PdfWriter.getInstance(mDoc, new FileOutputStream(filPath));
                        mDoc.open();
                        String pdfText = mResultEt.getText().toString().replaceAll("\n", " ");
                        mDoc.add(new Paragraph(pdfText));
                        mDoc.close();

                        Snackbar snackbar = Snackbar.make(flmenu, "PDF has been saved", Snackbar.LENGTH_LONG)
                                .setAction("Share", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        File file = new File(filPath);
                                        if (file.exists()) {

                                            path = FileProvider.getUriForFile(
                                                    DocView.this,
                                                    "com.sourajit.stiQRit.fileprovider",
                                                    file);
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_SEND);
                                            intent.putExtra(Intent.EXTRA_TEXT, "sharing");
                                            intent.putExtra(Intent.EXTRA_STREAM, path);
                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            intent.setType("Document/*");
                                            startActivity(Intent.createChooser(intent, "SHARE"));

                                        } else {
                                            Toast.makeText(DocView.this, filName + " missing " + filPath, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                        snackbar.show();

                    } catch (Exception e) {
                        Toast.makeText(DocView.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    final Document mDoc = new Document();
                    final String filName=fName;
                    File f=  new File(Environment.getExternalStorageDirectory()+"/stiQR it","Pdf");
                    f.mkdirs();
                    final String filPath = Environment.getExternalStorageDirectory() + "/stiQR it/Pdf/" + filName + ".pdf";
                    try {
                        PdfWriter.getInstance(mDoc, new FileOutputStream(filPath));
                        mDoc.open();
                        String pdfText = mResultEt.getText().toString().replaceAll("\n", " ");
                        mDoc.add(new Paragraph(pdfText));
                        mDoc.close();

                        Snackbar snackbar = Snackbar.make(flmenu, "PDF has been saved", Snackbar.LENGTH_LONG)
                                .setAction("Share", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        File file = new File(filPath);
                                        if (file.exists()) {

                                            path = FileProvider.getUriForFile(
                                                    DocView.this,
                                                    "com.sourajit.stiQRit.fileprovider",
                                                    file);
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_SEND);
                                            intent.putExtra(Intent.EXTRA_TEXT, "sharing");
                                            intent.putExtra(Intent.EXTRA_STREAM, path);
                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            intent.setType("Document/*");
                                            startActivity(Intent.createChooser(intent, "SHARE"));

                                        } else {
                                            Toast.makeText(DocView.this, filName + " missing " + filPath, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                        snackbar.show();

                    } catch (Exception e) {
                        Toast.makeText(DocView.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                mydialogue.dismiss();
                flmenu.close(true);
            }
        });
        mydialogue.show();

    }

    public void saveDoc(){

        Button save;
        final EditText fileName;
        TextView ext ;

        mydialogue.setContentView(R.layout.file_name_dialog);
        mydialogue.setCanceledOnTouchOutside(TRUE);

        ext = mydialogue.findViewById(R.id.extension);
        ext.setText(".txt");

        save= mydialogue.findViewById(R.id.save);
        fileName = mydialogue.findViewById(R.id.fname);
        final String fName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        fileName.setHint(fName);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fileName.length()!=0){
                    final String filName=fileName.getText().toString().trim().replaceAll(" ","_")+".txt";
                    String filPath;
                    FileOutputStream fos= null;

                    try {
                        String pdfText =mResultEt.getText().toString().replaceAll("\n"," ");
                        File f=  new File(Environment.getExternalStorageDirectory()+"/stiQR it","Text doc");
                        f.mkdirs();
                        final File file = new File(Environment.getExternalStorageDirectory()+"/stiQRit/Text doc",filName);
                        fos= new FileOutputStream(file);
                        fos.write(pdfText.getBytes());
                        fos.close();
                        filPath = getFilesDir()+"/"+filName;

                        final String finalFilPath = filPath;


                        Snackbar snackbar = Snackbar.make(flmenu , "Doc. has been saved", Snackbar.LENGTH_LONG)
                                .setAction("Share", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(file.exists()) {
                                            path = FileProvider.getUriForFile(
                                                    DocView.this,
                                                    "com.sourajit.stiQRit.fileprovider",
                                                    file);
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_SEND);
                                            intent.putExtra(Intent.EXTRA_TEXT, "sharing");
                                            intent.putExtra(Intent.EXTRA_STREAM, path);
                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            intent.setType("Document/*");
                                            startActivity(Intent.createChooser(intent, "SHARE"));

//                                Toast.makeText(HistoryActivity.this,getFilesDir()+"/"+filName,Toast.LENGTH_LONG).show();

                                        }
                                        else{
                                            Toast.makeText(DocView.this,filName+" missing "+ finalFilPath,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                        snackbar.show();

                    }
                    catch (Exception e){
                        Toast.makeText(DocView.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    finally {
                        if(fos!=null){
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else{
                    final String filName=fName+".txt";

                    String filPath;
                    FileOutputStream fos= null;

                    try {
                        String pdfText =mResultEt.getText().toString().replaceAll("\n"," ");

                        File f=  new File(Environment.getExternalStorageDirectory()+"/stiQR it","Text doc");
                        f.mkdirs();
                        final File file = new File(Environment.getExternalStorageDirectory()+"/stiQR it/Text doc",filName);

                        fos= new FileOutputStream(file);
                        fos.write(pdfText.getBytes());
                        fos.close();;
                        filPath = getFilesDir()+"/"+filName;

                        final String finalFilPath = filPath;


                        Snackbar snackbar = Snackbar.make(flmenu , "Doc. has been saved", Snackbar.LENGTH_LONG)
                                .setAction("Share", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        if(file.exists()) {
                                            path = FileProvider.getUriForFile(
                                                    DocView.this,
                                                    "com.sourajit.stiQRit.fileprovider",
                                                    file);
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_SEND);
                                            intent.putExtra(Intent.EXTRA_TEXT, "sharing");
                                            intent.putExtra(Intent.EXTRA_STREAM, path);
                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            intent.setType("Document/*");
                                            startActivity(Intent.createChooser(intent, "SHARE"));

//                                Toast.makeText(HistoryActivity.this,getFilesDir()+"/"+filName,Toast.LENGTH_LONG).show();

                                        }
                                        else{
                                            Toast.makeText(DocView.this,filName+" missing "+ finalFilPath,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                        snackbar.show();

                    }
                    catch (Exception e){
                        Toast.makeText(DocView.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    finally {
                        if(fos!=null){
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                mydialogue.dismiss();
                flmenu.close(true);
            }
        });
        mydialogue.show();

    }


    public void pickGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK );
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera(){

        startActivity(new Intent(DocView.this,CameraActivity.class));

    }


    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermission,STORAGE_REQUEST_CODE);

    }

    private boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE )== (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this,
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
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:
                if(grantResults.length > 0){

                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickGallery();
                    }
                    else{
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                CropImage.activity(data.getData())
                        .setActivityTitle("SnapCrop")
                        .setAllowRotation(TRUE)
                        .setAllowCounterRotation(TRUE)
                        .setAllowFlipping(TRUE)
                        .setAutoZoomEnabled(TRUE)
                        .setMultiTouchEnabled(TRUE)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);

            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {

                CropImage.activity(image_uri)
                        .setActivityTitle("SnapCrop")
                        .setAllowRotation(TRUE)
                        .setAllowCounterRotation(TRUE)
                        .setAllowFlipping(TRUE)
                        .setAutoZoomEnabled(TRUE)
                        .setMultiTouchEnabled(TRUE)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);

            }

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                pv.setImageURI(resultUri);

                BitmapDrawable bitmapDrawable = (BitmapDrawable) pv.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();

                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {

                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        if(i != items.size()-1) {
                            sb.append("\n");
                        }
                        mResultEt.setText(sb.toString());

                    }
                }
            }
        }

        else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
        {
            Toast.makeText(this,"+error",Toast.LENGTH_SHORT).show();
        }

    }

    public void showDialog(){
        String[] items={"Camera","Gallery"};
        AlertDialog.Builder dialog= new AlertDialog.Builder(this);
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    }
                    else {
                        pickCamera();
                    }
                }

                if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    }
                    else{
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();
    }

    @Override
    public void onBackPressed() {
        if(flmenu.isOpened()){
            flmenu.close(true);
        }
        else {
            super.onBackPressed();
        }
    }
}
