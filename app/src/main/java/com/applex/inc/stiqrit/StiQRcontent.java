package com.applex.inc.stiqrit;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.applex.inc.stiqrit.Adapters.gridAdapter;
import com.applex.inc.stiqrit.ModelItems.gridItems;
import com.applex.inc.stiqrit.ModelItems.historyItems;
import com.applex.inc.stiqrit.Util.DatabaseHelper;
import com.applex.inc.stiqrit.Util.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;

import static java.lang.Boolean.TRUE;

public class StiQRcontent extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    String cameraPermission[];
    String storagePermission[];

    ArrayList<gridItems> mList;
    RecyclerView mRecyclerView;
    gridAdapter mAdapter;

    Uri image_uri;

    private ProgressBar loading;

    private String code;
    DatabaseHelper myDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_data);

        final Toolbar tb = findViewById(R.id.toolb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        myDB = new DatabaseHelper(this);
        final Intent i = getIntent();
        code = i.getStringExtra("stiQR_ID");

        Cursor data = myDB.getTitle(code);
        tb.setTitle(data.getString(0));

        mRecyclerView = findViewById(R.id.recyclerview_id);
        loading = findViewById(R.id.progress);

        createList();
        buildRecyclerView(mList);

    }

    /////////////////Recycler view constructing func/////////////////

    public void buildRecyclerView(final ArrayList<gridItems> brvList) {

        mAdapter = new gridAdapter( mList, StiQRcontent.this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        mAdapter.setOnItemClickListener(new gridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                   if(position == 2){
//                        Intent intent = new Intent(StiQRcontent.this,Video_activity.class);
//                        intent.putExtra("videoID","6M5VXKLf4D4");
//                        startActivity(intent);
//                    }

//                        gridItems items = brvList.get(position);
//                        Intent intent = new Intent(StiQRcontent.this, DocView.class);
//                        intent.putExtra("Data", items.getmData());
//                        intent.putExtra("stiQR_ID",code);
//                        intent.putExtra("name",items.getmName());
//                        intent.putExtra("gridId", String.valueOf(position));
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                Utility.showToast(StiQRcontent.this,"item tapped");

                }

            @Override
            public void onShareClick(int position) {

            }
        });

        loading.setVisibility(View.GONE);

    }

    ///////////////////List being fed to recycler view////////////////

    public void createList() {

        mList = new ArrayList<>();

        //TARGET FOLDER
        //File downloadsFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File folder = new File("/storage/emulated/0/stiQR it/"+code+"/");
        folder.mkdirs();

        if (folder.isDirectory()) {
            //GET ALL FILES IN DOWNLOAD FOLDER
            File[] files = folder.listFiles();

            //LOOP THRU THOSE FILES GETTING NAME AND URI
            for (int i = 0; i < files.length; i++) {
                File file=files[i];
                gridItems gi= new gridItems();
                gi.setmName(file.getName());
                if(getFileExtension(Uri.fromFile(file)).equals(".jpg")){
                    gi.setmResourceImage(Uri.fromFile(file));
                }
                mList.add(gi);

            }
            loading.setVisibility(View.GONE);
        }
    }


    ////////////////////////Camera,Gallery//////////////

    public void pickGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK );
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera(){
        Intent intent = new Intent(StiQRcontent.this, CameraActivity.class);
        intent.putExtra("stiQR_ID",code);
        startActivity(intent);
    }

    ///////////////////////PERMISSION/////////////////////////
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
//                        sel=1;
//                        Intent intent = new Intent(launcher_activity.this, HistoryActivity.class);
//                        intent.putExtra("choice",1);
//                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
    ///////////////////////PERMISSION/////////////////////////

    ///////////////////Handle Results///////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                CropImage.activity(data.getData())
                        .setActivityTitle("Crop")
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
                        .setActivityTitle("Crop")
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
            Uri resultUri = result.getUri();

            if (resultCode == RESULT_OK) {

            }
        }

        else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
        {
            Toast.makeText(this,"+error",Toast.LENGTH_SHORT).show();
        }

    }

    ///////////////////Handle Results///////////////////////




    public void showDialogbox(final int gridId){
        String[] items={"Camera","Gallery","Notes/Links","Pdf/Doc"};
        AlertDialog.Builder dialog= new AlertDialog.Builder(this);
        dialog.setTitle("Select Category :");
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

                else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    }
                    else{
                        pickGallery();
                    }
                }
                else if (which == 2) {
                    Intent intent = new Intent(StiQRcontent.this, DocView.class);
                    intent.putExtra("gridId", String.valueOf(mList.size()));
                    intent.putExtra("code",code);
                    Toast.makeText(StiQRcontent.this,code,Toast.LENGTH_LONG).show();
                    startActivity(intent);
//                    if (!checkStoragePermission()) {
//                        requestStoragePermission();
//                    }
//                    else{
//                        pickGallery();
//                    }
                }
                else if (which == 3) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    }
                    else{
                        Intent intent = new Intent(Intent.ACTION_PICK );
                        intent.setType("*/*");
                        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
                    }
                }
            }
        });
        dialog.create().show();
    }

    private  String getFileExtension(Uri uri){
        ContentResolver cr= getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            Intent intent = new Intent(StiQRcontent.this , HistoryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_out_left , R.anim.slide_in_right);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
