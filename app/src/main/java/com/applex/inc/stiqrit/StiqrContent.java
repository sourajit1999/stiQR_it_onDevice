package com.applex.inc.stiqrit;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
//import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

import static java.lang.Boolean.TRUE;

public class StiqrContent extends AppCompatActivity {

    public static ArrayList<gridItems> mList;
    RecyclerView mRecyclerView;
    gridAdapter mAdapter;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;

    private ProgressBar loading;

    public static String code;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_data);

        final Toolbar tb = findViewById(R.id.toolb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent i = getIntent();
        tb.setTitle(i.getStringExtra("title"));


//        Toast.makeText(StiqrContent.this, i.getStringExtra("title"), Toast.LENGTH_SHORT).show();

        code = i.getStringExtra("code");


        FirebaseDatabase.getInstance().getReference("UsersData")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(code)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            historyItems i = dataSnapshot.getValue(historyItems.class);
                            tb.setTitle(i.getmTitle());

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });;

//        mAuth= FirebaseAuth.getInstance();

        mRecyclerView = findViewById(R.id.recyclerview_id);
        loading = findViewById(R.id.progress);

        mStorageRef= FirebaseStorage.getInstance().getReference();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Uploads");


        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


    }

    /////////////////Recycler view constructing func/////////////////

    public void buildRecyclerView(final ArrayList<gridItems> brvList) {

        mRecyclerView.setHasFixedSize(true);
        mAdapter = new gridAdapter( mList, StiqrContent.this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        mAdapter.setOnItemClickListener(new gridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                   if(position == 2){
//                        Intent intent = new Intent(StiqrContent.this,Video_activity.class);
//                        intent.putExtra("videoID","6M5VXKLf4D4");
//                        startActivity(intent);
//                    }

                    if (position == 0){
                        showDialogbox(mList.size());
                    }
                    else  {
                        gridItems items = brvList.get(position);
                        Intent intent = new Intent(StiqrContent.this, DocView.class);
                        intent.putExtra("Data", items.getmData());
                        intent.putExtra("code",code);
                        intent.putExtra("name",items.getmName());
                        intent.putExtra("gridId", String.valueOf(position));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }

            @Override
            public void onShareClick(int position) {

            }
        });

        loading.setVisibility(View.GONE);

    }

    ///////////////////List being fed to recycler view////////////////

    public void createList() {

        mList=new ArrayList<>();
        mList.add(new gridItems(R.drawable.add_content,"Add Note","Data"));

//            FirebaseDatabase.getInstance().getReference("CodesData")
//                    .child(code)
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()) {
//                                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
//                                    gridItems i = itemSnapshot.getValue(gridItems.class);
//                                    mList.add(i);
////                                Toast.makeText(HistoryActivity.this,"added",Toast.LENGTH_LONG).show();
//
//                                }
//                                buildRecyclerView(mList);
//                            }
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            loading.setVisibility(View.GONE);
//                        }
//                    });

        loading.setVisibility(View.GONE);
    }


    ////////////////////////Camera,Gallery//////////////

    public void pickGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK );
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera(){
        Intent intent = new Intent(StiqrContent.this, CameraActivity.class);
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
                if(mUploadtask!=null && mUploadtask.isInProgress()){
                  Toast.makeText(StiqrContent.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
                }else {
//                    uploadFile(resultUri);
                }
//                FirebaseUser user=mAuth.getCurrentUser();
//                String userID= user.getUid();
//                String name="No name";
//                StorageReference storageReference=mStorageRef.child("images/users/"+ userID+ "/"+name+ ".jpg");
//                storageReference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(StiqrContent.this,"upload successful",Toast.LENGTH_SHORT).show();;
//
//                    }
//                });



//                resultUri = result.getUri();
//                pv.setImageURI(resultUri);
//
//                BitmapDrawable bitmapDrawable = (BitmapDrawable) pv.getDrawable();
//                Bitmap bitmap = bitmapDrawable.getBitmap();
//
//                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
//
//                if (!recognizer.isOperational()) {
//                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
//
//                } else {
//                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
//                    SparseArray<TextBlock> items = recognizer.detect(frame);
//                    StringBuilder sb = new StringBuilder();
//                    for (int i = 0; i < items.size(); i++) {
//                        TextBlock myItem = items.valueAt(i);
//                        sb.append(myItem.getValue());
//                        if(i != items.size()-1) {
//                            sb.append("\n");
//                        }
//
//                    }
//                    mResultEt.setText(sb.toString());
//                    if(mResultEt.length()!=0) {
//                        OCRtext=mResultEt.getText().toString();
//                        databaseAdder();
//                        added =1;
//                    }
//                }



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
                    Intent intent = new Intent(StiqrContent.this, DocView.class);
                    intent.putExtra("gridId", String.valueOf(mList.size()));
                    intent.putExtra("code",code);
                    Toast.makeText(StiqrContent.this,code,Toast.LENGTH_LONG).show();
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
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        createList();
        buildRecyclerView(mList);
        loading.setVisibility(View.GONE);
    }
}
