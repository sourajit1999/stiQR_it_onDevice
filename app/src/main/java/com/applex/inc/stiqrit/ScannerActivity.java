package com.applex.inc.stiqrit;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.applex.inc.stiqrit.ModelItems.historyItems;
import com.applex.inc.stiqrit.Util.DatabaseHelper;
import com.applex.inc.stiqrit.Util.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static java.lang.Boolean.FALSE;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;

    DatabaseHelper myDB;
    private DatabaseReference mDatabase;
    Dialog mydialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView=new ZXingScannerView(this);
        setContentView(scannerView);

        myDB = new DatabaseHelper(this);

        mydialogue = new Dialog(this);
    }

    @Override
    public void handleResult(final Result result) {

        final String code = result.getText().trim();
        final String hash = Utility.md5(code);
        Cursor data = myDB.getListContents();

        if(data.getCount()==0){
            if (Utility.checkConnection(ScannerActivity.this)){
                mDatabase = FirebaseDatabase.getInstance().getReference("Codes")
                        .child(hash);

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            if(dataSnapshot.getValue().toString().equals("0")){
                                showdialogstiQR(code);

                            }
                            else{
                                Toast.makeText(ScannerActivity.this,"stiQR already in use!!", Toast.LENGTH_LONG)
                                        .show();
                                Intent intent = new Intent(ScannerActivity.this, HistoryActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        else {
                            Toast.makeText(ScannerActivity.this,"stiQR not recognized!", Toast.LENGTH_LONG)
                                    .show();
                            Intent intent = new Intent(ScannerActivity.this, HistoryActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else{
                Utility.showToast(ScannerActivity.this,"Network unavailable...");
            }
        }

        else {   //Database not empty

            File folder = new File(Environment.getExternalStorageDirectory(), "stiQR it");
            folder.mkdirs();

            int flag = 0;

            if (folder.isDirectory()) {
                File[] files = folder.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file.getName().matches(code)) {
                        flag = 1;
                        Intent intent = new Intent(ScannerActivity.this, StiQRcontent.class);
                        intent.putExtra("stiQR_ID", code);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                        break;
                    }
                }
            }
            if (flag == 0) {
                if (Utility.checkConnection(ScannerActivity.this)) {
                    mDatabase = FirebaseDatabase.getInstance().getReference("Codes")
                            .child(hash);

                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                if (dataSnapshot.getValue().toString().equals("0")) {
                                    showdialogstiQR(code);

                                } else {
                                    Toast.makeText(ScannerActivity.this, "stiQR already in use!!", Toast.LENGTH_LONG)
                                            .show();
                                    Intent intent = new Intent(ScannerActivity.this, HistoryActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Toast.makeText(ScannerActivity.this, "stiQR not recognized!", Toast.LENGTH_LONG)
                                        .show();
                                Intent intent = new Intent(ScannerActivity.this, HistoryActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Utility.showToast(ScannerActivity.this, "Network unavailable...");
                }
            }
        }
    }

    public void updateFirebase(final String code){

        String hash = Utility.md5(code);
        FirebaseDatabase.getInstance().getReference("Codes")
                .child(hash)
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(ScannerActivity.this , StiQRcontent.class);
                        intent.putExtra("stiQR_ID" , code);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                });
    }

    public void showdialogstiQR(final String code){
        Button save;
        final EditText qrName;
        final EditText qrDesc;

        mydialogue.setContentView(R.layout.qr_description_dialog);
        mydialogue.setCanceledOnTouchOutside(FALSE);

        save = mydialogue.findViewById(R.id.save);
        qrName = mydialogue.findViewById(R.id.QRname);
        qrDesc = mydialogue.findViewById(R.id.QRdesc);
        //        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis()) ;
        Calendar calendar = Calendar.getInstance();
        final String timeStamp = DateFormat.getDateInstance().format(calendar.getTime());
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qrName.getText().toString().isEmpty()) {
                    qrName.setError("Title missing");
                    qrName.requestFocus();
                }
                else{
                    File folder =  new File(Environment.getExternalStorageDirectory()+"/stiQR it",code);
                    folder.mkdirs();
                    boolean insertData = myDB.addData(code , qrName.getText().toString() , qrDesc.getText().toString() , timeStamp);
                    if(!insertData){
                        Toast.makeText(ScannerActivity.this,"Something went wrong :(",Toast.LENGTH_SHORT).show();
                    }
                    else {
//                        Intent intent = new Intent(ScannerActivity.this , StiQRcontent.class);
//                        intent.putExtra("stiQR_ID" , code);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                        finish();
                        updateFirebase(code);
                    }
                }
            }
        });
        mydialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mydialogue.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

}
