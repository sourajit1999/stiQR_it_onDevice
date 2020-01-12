package com.applex.inc.stiqrit;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebView extends AppCompatActivity {

    private android.webkit.WebView wv;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Toolbar tb = findViewById(R.id.toolb);
        tb.setTitle("Translate");
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressBar=findViewById(R.id.progressBar1);

        wv = findViewById(R.id.webview) ;
        wv.setVisibility(View.INVISIBLE);
        wv.setWebChromeClient(new WebChromeClient());

        WebSettings wset = wv.getSettings();
        wset.setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(WebView.this,"Loading...",Toast.LENGTH_SHORT).show();

            }
            @Override
            public void onPageFinished(android.webkit.WebView view, String url) {

                super.onPageFinished(view, url);
                wv.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(WebView.this,"Paste Text...",Toast.LENGTH_SHORT).show();

            }
        });

        wv.loadUrl("https://translate.google.com.au");

    }

    @Override
    public void onBackPressed() {
        if(wv.canGoBack()){
            wv.goBack();
        }
        else {
            AlertDialog.Builder builder= new AlertDialog.Builder(WebView.this);
            builder.setTitle("Back")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            WebView.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("Cancel",null);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
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

        return super.onOptionsItemSelected(item);
    }


}


