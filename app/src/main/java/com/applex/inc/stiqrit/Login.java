package com.applex.inc.stiqrit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.applex.inc.stiqrit.ModelItems.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.view.View.GONE;

public class Login extends AppCompatActivity {

  EditText etEmail;
  EditText etPass;
  Button login;
  EditText etUsername;
  Button signup;
  private FirebaseAuth mAuth;
  int val;
  //    private FirebaseAuth.AuthStateListener mAuthListner;
  FirebaseDatabase firebaseDatabase;
  private DatabaseReference databaseReference;
  ProgressBar loading ;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    mAuth= FirebaseAuth.getInstance();

    etEmail=(EditText)findViewById(R.id.email);
    etPass=(EditText)findViewById(R.id.password);
    login=(Button) findViewById(R.id.login);
    signup=(Button)findViewById(R.id.signup);
    etUsername=(EditText)findViewById(R.id.username);

    loading = findViewById(R.id.progressBar1);


    Intent intent = getIntent();
    String str = intent.getStringExtra("value");
    val = Integer.parseInt(str);

    ////////////////LOGIN/////////////////////



    if(val==0)               /////val=0 for signup
    {
      login.setVisibility(View.GONE);
      signup.setVisibility(View.VISIBLE);
      etUsername.setVisibility(View.VISIBLE);

      signup.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          final String username = etUsername.getText().toString().trim();
          String password = etPass.getText().toString().trim();
          String email = etEmail.getText().toString().trim();
          if(email.isEmpty()||!Patterns.EMAIL_ADDRESS.matcher(email).matches()||password.isEmpty()||username.isEmpty()) {
            if (email.isEmpty()) {
              etEmail.setError("Email missing");
              etEmail.requestFocus();
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
              etEmail.setError("Please enter a valid email");
              etEmail.requestFocus();
            }

            if (password.length()<8) {
              etPass.setError("Password must contain at least 8 characters");
              etPass.requestFocus();
            }
            if (username.isEmpty()) {
              etUsername.setError("Username missing");
              etUsername.requestFocus();
            }
          }
          else {
            loading.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                          UserDetails user = new UserDetails(username);
                          FirebaseDatabase.getInstance().getReference("Users")
                                  .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                  .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                              if (task.isSuccessful()){
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {
                                    loading.setVisibility(GONE);
                                    if (task.isSuccessful()) {
                                      Toast.makeText(Login.this, "CHECK YOUR EMAIL TO VERIFY", Toast.LENGTH_LONG).show();
                                      Intent intent = new Intent(Login.this, Login.class);
                                      intent.putExtra("value", "1");
                                      startActivity(intent);
                                      overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                      finish();
                                    } else {
                                      loading.setVisibility(GONE);
                                      Toast.makeText(Login.this, "Email verification failed", Toast.LENGTH_SHORT).show();
                                    }

                                  }
                                });
                              }
                              else {
                                loading.setVisibility(GONE);
                                Toast.makeText(Login.this, "Not Registered!", Toast.LENGTH_SHORT).show();
                              }
                            }
                          });

                        } else {
                          loading.setVisibility(GONE);
                          Toast.makeText(Login.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                        }
                      }
                    });
          }
        }
      });

    }
    else if(val==1)            /////val=1 for login
    {
      login.setVisibility(View.VISIBLE);
      signup.setVisibility(View.GONE);
      etUsername.setVisibility(GONE);

      login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String password = etPass.getText().toString().trim();
          String email = etEmail.getText().toString().trim();

          if(email.isEmpty()||!Patterns.EMAIL_ADDRESS.matcher(email).matches()||email.isEmpty()) {
            if (email.isEmpty()) {
              etEmail.setError("Email missing");
              etEmail.requestFocus();
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
              etEmail.setError("Please enter a valid email");
              etEmail.requestFocus();
            }

            if (password.isEmpty()) {
              etPass.setError("Password missing");
              etPass.requestFocus();
            }
          }
          else {
            loading.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                        loading.setVisibility(GONE);
                        if (!task.isSuccessful()) {
                          Toast.makeText(Login.this, "User not verified", Toast.LENGTH_SHORT).show();

                        } else {
                          //Toast.makeText(Login.this, "cdryttgiut65e", Toast.LENGTH_LONG).show();
                          FirebaseUser fireuser = mAuth.getCurrentUser();
                          if (fireuser.isEmailVerified()) {
                            //   mAuth.addAuthStateListener(mAuthListner);
                            Intent intent = new Intent(Login.this, HistoryActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                            finish();
                          } else {
                            Toast.makeText(Login.this, "Email verification incomplete", Toast.LENGTH_SHORT).show();
                            etPass.setText("");
                            etPass.setHint("password");

                          }

//                                mAuth.addAuthStateListener(mAuthListner);
//                                mAuthListner = new FirebaseAuth.AuthStateListener() {
////                                    @Override
////                                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
////                                        if (firebaseAuth.getCurrentUser()!=null){
////
////                                        }
////                                    }
//                                };
                        }
//
                      }
                    });
          }
        }
      });

    }



  }

  @Override
  protected void onStart() {
    super.onStart();
  }
}
