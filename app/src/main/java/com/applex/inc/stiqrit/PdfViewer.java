package com.applex.inc.stiqrit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;

public class PdfViewer extends AppCompatActivity {
    PDFView pd;
    Toolbar tb;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tb = (Toolbar) findViewById(R.id.toolb);
        setSupportActionBar(tb);


        final Animation sildeup = AnimationUtils.loadAnimation(PdfViewer.this,R.anim.slide_up);
//        final Animation slidedown = AnimationUtils.loadAnimation(PdfViewer.this,R.anim.slide_down);


        pd = findViewById(R.id.pdfView);
        String filPath;
        Intent recvIntent = getIntent();
//        String type = recvIntent.getStringExtra("Type");
        String type = "Assets";
        if(type.equals("Assets")) {

            pd.fromAsset("aadhaar.pdf")
                    .password(null)
                    .defaultPage(0)
                    .enableSwipe(true)
                    .swipeHorizontal(true)
                    .enableDoubletap(true)
                    .onDraw(new OnDrawListener() {
                        @Override
                        public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
                        }
                    })
                    .onDrawAll(new OnDrawListener() {
                        @Override
                        public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
                        }
                    })
                    .onPageError(new OnPageErrorListener() {
                        @Override
                        public void onPageError(int page, Throwable t) {
                            Toast.makeText(PdfViewer.this, "Error loading page", Toast.LENGTH_LONG).show();
                        }
                    })
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {

                        }
                    })
                    .onTap(new OnTapListener() {
                        @Override
                        public boolean onTap(MotionEvent e) {
                            if(tb.getVisibility()== View.VISIBLE){
                                tb.collapseActionView();
                            }
                            else if(tb.getVisibility()==View.GONE){
                                tb.setVisibility(View.VISIBLE);
                            }
                            return true;
                        }
                    })
                    .onRender(new OnRenderListener() {
                        @Override
                        public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                            pd.fitToWidth();
                        }
                    })
                    .enableAnnotationRendering(true)
                    .invalidPageColor(Color.WHITE)
                    .load();
        }
        else if(type.equals("Storage"))
        {
            Uri pdfFile = Uri.parse(recvIntent.getStringExtra("fileUri"));
            pd.fromUri(pdfFile)
                    .password(null)
                    .defaultPage(0)
                    .enableSwipe(true)
                    .swipeHorizontal(true)
                    .enableDoubletap(true)
                    .onDraw(new OnDrawListener() {
                        @Override
                        public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
                        }
                    })
                    .onDrawAll(new OnDrawListener() {
                        @Override
                        public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
                        }
                    })
                    .onPageError(new OnPageErrorListener() {
                        @Override
                        public void onPageError(int page, Throwable t) {
                            Toast.makeText(PdfViewer.this, "Error loading page", Toast.LENGTH_LONG).show();
                        }
                    })
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {

                        }
                    })
                    .onTap(new OnTapListener() {
                        @Override
                        public boolean onTap(MotionEvent e) {

//                            if(tb.getVisibility()==View.VISIBLE){
//                                tb.startAnimation(sildeup);
//                                tb.setVisibility(View.GONE);
//                            }
//                            else if(tb.getVisibility()==View.GONE){
//                                tb.setVisibility(View.VISIBLE);
//                                tb.startAnimation(slidedown);
//
//                            }
                            return true;
                        }
                    })
                    .onRender(new OnRenderListener() {
                        @Override
                        public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                            pd.fitToWidth();
                        }
                    })
                    .enableAnnotationRendering(true)
                    .invalidPageColor(Color.WHITE)
                    .load();
        }
    }
}
