package com.applex.inc.stiqrit;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class ViewImage extends AppCompatActivity {
    ImageView image;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        image=(ImageView)findViewById(R.id.imagev);
        text=(TextView)findViewById(R.id.aboutimg);

    }
}
