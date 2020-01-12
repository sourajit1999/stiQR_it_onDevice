package com.applex.inc.stiqrit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applex.inc.stiqrit.Adapters.HistoryAdapter;
import com.applex.inc.stiqrit.ModelItems.UserDetails;
import com.applex.inc.stiqrit.ModelItems.historyItems;
import com.applex.inc.stiqrit.Util.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseHelper myDB;

//    private SwipeRefreshLayout mySwipeRefreshLayout;

    private static final int CAMERA_REQUEST_CODE = 200;
    String cameraPermission[];

    private FirebaseAuth mAuth;
    private FirebaseUser fireuser ;

    public ArrayList<historyItems> mList ;
    public RecyclerView mRecyclerView;
    public HistoryAdapter mAdapter;

    ProgressBar loading ;

    Toolbar toolbar;

    public static RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cameraPermission = new String[]{Manifest.permission.CAMERA};

        mAuth=FirebaseAuth.getInstance();
        fireuser=mAuth.getCurrentUser();

        FloatingActionButton fab = findViewById(R.id.fab);
        loading = findViewById(R.id.progressBar1);
//        mySwipeRefreshLayout= findViewById(R.id.swiperefresh);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        updateNavHeader();


        /////////////////////Recycler View//////////////////

        mRecyclerView = findViewById(R.id.history) ;

//        mySwipeRefreshLayout.setOnRefreshListener(
//                new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
//                        mySwipeRefreshLayout.setRefreshing(true);
////                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
//
//                        // This method performs the actual data-refresh operation.
//                        // The method calls setRefreshing(false) when it's finished.
//                        myUpdateOperation();
//                    }
//                }
//        );

        /////////////////////Recycler View//////////////////


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!checkCameraPermission()) {
                    requestCameraPermission();
                }
                else {
                    Intent intent = new Intent(HistoryActivity.this,ScannerActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    public void buildRecyclerView(final ArrayList<historyItems> brvList) {

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new HistoryAdapter(brvList,this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final historyItems item = brvList.get(position);
                Intent intent = new Intent(HistoryActivity.this,StiQRcontent.class);
                intent.putExtra("stiQR")
            }
        });
    }


    public void createList() {
        mList = new ArrayList<>();

        //TARGET FOLDER
        //File downloadsFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File folder= new File("/storage/emulated/0/stiQR it/");
        folder.mkdirs();

        if(folder.isDirectory())
        {
            //GET ALL FILES IN DOWNLOAD FOLDER
            File[] files=folder.listFiles();

            //LOOP THRU THOSE FILES GETTING NAME AND URI
            for (int i=0;i<files.length;i++)
            {
                File file=files[i];
                String stiQR_code = file.getName();

                Cursor data = myDB.getItemId(stiQR_code);
                int itemID = -1;
                while (data.moveToNext()) {
                    itemID = data.getInt(0);
                }
                if (itemID > -1) {
                    //1 = stiQR_ID
                    //2 = title
                    //3 = description
                    //4 = date
                    mList.add(new historyItems(data.getString(1),data.getString(2),data.getString(3),data.getString(4)));
                }
            }
//        if(mList.isEmpty()){
//            Calendar calendar = Calendar.getInstance();
//            String currDate = DateFormat.getDateInstance().format(calendar.getTime());
//            mList.add(new historyItems("stiQR it","Scan your first stiQR ",currDate));
//            buildRecyclerView(mList);
//            loading.setVisibility(View.GONE);
       }
    }

    /////////////////////PERMISSIONS////////////////////

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        return result ;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
//                    boolean writeStorageAccepted = grantResults[0] ==
//                            PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted ){

                        Intent intent = new Intent(HistoryActivity.this,ScannerActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;

//            case STORAGE_REQUEST_CODE:
//                if(grantResults.length > 0){
//
//                    boolean writeStorageAccepted = grantResults[0] ==
//                            PackageManager.PERMISSION_GRANTED;
//                    if(writeStorageAccepted){
//                        pickGallery();
//                    }
//                    else{
//                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
//                    }
//                }
//                break;
        }
    }

    /////////////////////PERMISSIONS////////////////////

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        switch (id) {
//
            // Check if user triggered a refresh:
//            case R.id.menu_refresh:
//                // Signal SwipeRefreshLayout to start the progress indicator
//                mySwipeRefreshLayout.setRefreshing(true);
//
//                // Start the refresh background task.
//                // This method calls setRefreshing(false) when it's finished.
//                myUpdateOperation();
//
//                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else
        if (id == R.id.aboutUs) {
            Intent intent= new Intent(HistoryActivity.this,about_us.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_tools) {
            Intent intent= new Intent(HistoryActivity.this,Video_activity.class);
            startActivity(intent);

        }
        else if (id == R.id.contactus) {

            }
        else if (id == R.id.nav_logout) {
            mAuth.signOut();
            finish();
            Intent intent=new Intent(HistoryActivity.this,Splash.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader(){

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        DatabaseReference myRef;

        final TextView userName;
        final TextView userEmail;

        userEmail = headerView.findViewById(R.id.useremail123);
        userName = headerView.findViewById(R.id.username123);

        myRef =  FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                UserDetails value = dataSnapshot.getValue(UserDetails.class);
                userEmail.setText(fireuser.getEmail());
                userName.setText(value.user_name);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }

//    private void myUpdateOperation(){
//        createList();
//        buildRecyclerView(mList);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        loading.setVisibility(View.VISIBLE);
        createList();
        buildRecyclerView(mList);
    }
}
