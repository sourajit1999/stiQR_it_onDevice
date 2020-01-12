package com.applex.inc.stiqrit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

  private static final long Splash_time_out = 1500;

  Button login;
  TextView signup;
  TextView userNameView;

  TextView useless;
  String name;

  private FirebaseAuth mAuth;
  private FirebaseDatabase database;
  private FirebaseUser fireuser ;

  private DatabaseReference myRef;
//    private ValueEventListener mPostListener;


  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    mAuth=FirebaseAuth.getInstance();


    login=(Button) findViewById(R.id.login);
    signup=(TextView) findViewById(R.id.signup);
    userNameView =(TextView) findViewById(R.id.username);
    useless = (TextView)findViewById(R.id.useless);

    login.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent=new Intent(Splash.this, Login.class);
        intent.putExtra("value","1");
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
      }
    });

    signup.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent homeIntent = new Intent(Splash.this, Login.class);
        homeIntent.putExtra("value","0");
        startActivity(homeIntent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
      }
    });


    fireuser=mAuth.getCurrentUser();
    if(fireuser!=null) {
      if(fireuser.isEmailVerified()){
//        database= FirebaseDatabase.getInstance();
//        myRef = database.getReference("Users")
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        myRef.addValueEventListener(new ValueEventListener() {
//          @Override
//          public void onDataChange(DataSnapshot dataSnapshot) {
//            // This method is called once with the initial value and again
//            // whenever data at this location is updated.
//            UserDetails value = dataSnapshot.getValue(UserDetails.class);
//            name = value.user_name;
//            Toast.makeText(Splash.this,name,Toast.LENGTH_LONG).show();
//            userNameView.setText(name);
//          }
//
//          @Override
//          public void onCancelled(DatabaseError error) {
//            // Failed to read value
//          }
//        });
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
//            Toast.makeText(Splash.this, "from splash!", Toast.LENGTH_SHORT).show();
            Intent homeIntent = new Intent(Splash.this, HistoryActivity.class);
            startActivity(homeIntent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            finish();
          }
        }, Splash_time_out);
      }
      else{
        Toast.makeText(Splash.this,"Email verification incomplete",Toast.LENGTH_LONG).show();
      }
    }
  }

  private void updateUI(FirebaseUser user){

   // userNameView.setVisibility(View.VISIBLE);
    login.setVisibility(View.GONE);
    useless.setVisibility(View.GONE);
    signup.setVisibility(View.GONE);
//    userNameView.setText(user.getEmail().substring(0,user.getEmail().indexOf("@")));

  }

      @Override
      protected void onStart() {
        //mAuth.addAuthStateListener(mAuthListner);
        super.onStart();
        fireuser = mAuth.getCurrentUser();
            if(fireuser!=null) {
                if(fireuser.isEmailVerified()){
                    updateUI(fireuser);
                }

            }
      }

}
