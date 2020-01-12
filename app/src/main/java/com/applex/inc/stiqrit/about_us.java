package com.applex.inc.stiqrit;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class about_us extends AppCompatActivity {

//private AdView mAdView;
//private  AdView adview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        Toolbar tb = findViewById(R.id.toolb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//
//        mAdView = findViewById(R.id.ad_view);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);


//        tvad = (TextView)findViewById(R.id.textViewad);
//        tvsb = (TextView)findViewById(R.id.textViewsb);
//        tvss = (TextView)findViewById(R.id.textViewss);
//        tvsm = (TextView)findViewById(R.id.textViewsm);
//        tvas = (TextView)findViewById(R.id.textViewas);
//        tvam = (TextView)findViewById(R.id.textViewam);
//
//        tvad.setMovementMethod(new ScrollingMovementMethod());
//        tvsb.setMovementMethod(new ScrollingMovementMethod());
//        tvss.setMovementMethod(new ScrollingMovementMethod());
//        tvsm.setMovementMethod(new ScrollingMovementMethod());
//        tvas.setMovementMethod(new ScrollingMovementMethod());
//        tvam.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_web, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
                super.onBackPressed();

        }
//        else if (id == R.id.share) {
//            if (mResultEt.length() > 0) {
//                Intent shareintent = new Intent();
//                shareintent.setAction(Intent.ACTION_SEND);
//                shareintent.putExtra(Intent.EXTRA_TEXT, mResultEt.getText().toString());
//                shareintent.setType("text/plain");
//                startActivity(shareintent);
//
//            } else {
//                Toast.makeText(MainActivity.this, "No text found :(", Toast.LENGTH_SHORT).show();
//            }
//
//        }
        return super.onOptionsItemSelected(item);
    }
}
