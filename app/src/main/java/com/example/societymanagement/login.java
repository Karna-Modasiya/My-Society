package com.example.societymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class login extends AppCompatActivity {

    TextView register,forgotpass;
    Button login;
    EditText lemail,lpass;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore firestore;
    String usertype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        progressBar = (ProgressBar)findViewById(R.id.progress_login);
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);

        checkValidUser();

        register = findViewById(R.id.register);
        forgotpass = findViewById(R.id.forgotpass);
        login = findViewById(R.id.login);
        lemail = findViewById(R.id.email);
        lpass = findViewById(R.id.pass);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),register.class);
                startActivity(i);
                finish();
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),forgot_password.class);
                startActivity(i);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email =  lemail.getText().toString();
                String pass  =  lpass.getText().toString();

                if(email.isEmpty())
                {
                    lemail.setError("Email can not be empty");
                    return;
                }
                if(pass.length() < 6)
                {
                    lpass.setError("Password must be greater than 6 char");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                fAuth.signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        DocumentReference docRef = firestore.collection("Users").document(fAuth.getCurrentUser().getUid());
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                usertype = documentSnapshot.getString("user");
                                if(usertype.equals("Secretary"))
                                {
                                    Intent i1 = new Intent(getApplicationContext(),view_notice.class);
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(i1);
                                    finishAffinity();
                                }
                                else if(usertype.equals("Resident"))
                                {
                                    Intent i2 =  new Intent(getApplicationContext(),MainActivity.class);
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(i2);
                                    finishAffinity();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    public void checkValidUser()
    {
        if(fAuth.getCurrentUser() != null)
        {
            progressBar.setVisibility(View.VISIBLE);
            fAuth.getCurrentUser().reload();
            if (!fAuth.getCurrentUser().isEmailVerified())
            {
                progressBar.setVisibility(View.INVISIBLE);
                Intent i = new Intent(getApplicationContext(),add_details.class);
                Toast.makeText(getApplicationContext(), "Verify Your Email", Toast.LENGTH_SHORT).show();
                startActivity(i);
                finishAffinity();
                return;
            }

            DocumentReference docRef = firestore.collection("Users").document(fAuth.getCurrentUser().getUid());
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    progressBar.setVisibility(View.INVISIBLE);
                    usertype = documentSnapshot.getString("user");
                    if(usertype == null)
                    {
                        Intent i3 =  new Intent(getApplicationContext(),add_details.class);
                        startActivity(i3);
                        finishAffinity();
                    }
                    else if(usertype.equals("Secretary"))
                    {
                        FirebaseMessaging.getInstance().subscribeToTopic("all");
                        Intent i1 = new Intent(getApplicationContext(),view_notice.class);
                        startActivity(i1);
                        finishAffinity();
                    }
                    else if(usertype.equals("Resident"))
                    {
                        FirebaseMessaging.getInstance().subscribeToTopic("all");
                        Intent i2 =  new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(i2);
                        finishAffinity();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}