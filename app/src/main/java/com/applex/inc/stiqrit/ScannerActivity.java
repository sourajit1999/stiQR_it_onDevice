package com.applex.inc.stiqrit;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.applex.inc.stiqrit.ModelItems.historyItems;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.text.DateFormat;
import java.util.Calendar;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static java.lang.Boolean.FALSE;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;
    private DatabaseReference mDatabase;

    Dialog mydialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView=new ZXingScannerView(this);
        setContentView(scannerView);

        mydialogue = new Dialog(this);
    }

    @Override
    public void handleResult(final Result result) {

        mDatabase = FirebaseDatabase.getInstance().getReference("Codes")
                .child(result.getText().trim());

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    if(dataSnapshot.getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                        Intent intent = new Intent(ScannerActivity.this, StiqrContent.class);
                        intent.putExtra("code",result.getText().trim());
                        startActivity(intent);
                        finish();
                    }
                    else if(dataSnapshot.getValue().toString().equals("0")){
                        showdialogstiQR(result.getText().trim());
                    }
                    else{
                        Toast.makeText(ScannerActivity.this,"stiQR access denied!", Toast.LENGTH_LONG)
                                .show();
                    }
                }
                else {
                    Toast.makeText(ScannerActivity.this,"QR not recognized!", Toast.LENGTH_LONG)
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

//        }

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

    public void showdialogstiQR(final String qrCode){
        Button save;
        final EditText qrName;
        final EditText qrDesc;

        mydialogue.setContentView(R.layout.qr_description_dialog);
        mydialogue.setCanceledOnTouchOutside(FALSE);

        save= mydialogue.findViewById(R.id.save);
        qrName = mydialogue.findViewById(R.id.QRname);
        qrDesc = mydialogue.findViewById(R.id.QRdesc);
        qrDesc.setText("");
//        final String Date = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        final String Date = DateFormat.getDateInstance().format(calendar.getTime());
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qrName.getText().toString().isEmpty()) {
                    qrName.setError("Title missing");
                    qrName.requestFocus();
                }
                else{
                    FirebaseDatabase.getInstance().getReference("Codes")
                            .child(qrCode)
                            .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

//                                    Toast.makeText(ScannerActivity.this, "HGGK", Toast.LENGTH_LONG)
////                                            .show();
                                    historyItems item = new historyItems(qrName.getText().toString(),qrDesc.getText().toString(),Date,qrCode);
                                    FirebaseDatabase.getInstance().getReference("UsersData")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(qrCode).setValue(item)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    mydialogue.dismiss();
                                                    Intent intent = new Intent(ScannerActivity.this, StiqrContent.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                }
                            });

                }
            }
        });
        mydialogue.show();

    }

}
