package com.example.societymanagement;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class add_notice extends AppCompatActivity {

    Button add;
    EditText etnotice,etdesc;
    FirebaseAuth fAuth;
    FirebaseFirestore firestore;
    ProgressBar progressBar;
    String pname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);
        add = findViewById(R.id.add);
        etnotice = findViewById(R.id.notice);
        etdesc = findViewById(R.id.notice_desc);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progress_addnotice);
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        DocumentReference docRef = firestore.collection("Users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                pname = documentSnapshot.getString("name");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String notice = etnotice.getText().toString();
                String desc = etdesc.getText().toString();
                if(notice.isEmpty()){
                    etnotice.setError("Notice can not be Empty");
                    return;
                }
                if(desc.isEmpty()){
                    etdesc.setError("Notice description can not be Empty");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                add.setEnabled(false);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();

                Map<String,Object> items = new HashMap<>();

                items.put("name", pname);
                items.put("notice_text",notice);
                items.put("notice_desc",desc);
                items.put("time",dtf.format(now));

                firestore.collection("Notices").add(items)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            add.setEnabled(true);
                            progressBar.setVisibility(View.INVISIBLE);
                            etnotice.setText("");
                            etdesc.setText("");
                            etnotice.clearFocus();
                            etdesc.clearFocus();
                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                                    "/topics/all",notice,desc,getApplicationContext()
                                    ,add_notice.this
                            );
                            notificationsSender.SendNotifications();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.secretary_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.resetpass:
                Intent i1 = new Intent(getApplicationContext(),reset_password.class);
                startActivity(i1);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent i2 =  new Intent(getApplicationContext(),login.class);
                startActivity(i2);
                finishAffinity();
                break;
            case R.id.home:
                Intent i3 =  new Intent(getApplicationContext(),view_notice.class);
                startActivity(i3);
                break;
            case R.id.profile:
                Intent i5 =  new Intent(getApplicationContext(),profile.class);
                startActivity(i5);
                break;
            case R.id.resident:
                Intent i6 =  new Intent(getApplicationContext(),residents.class);
                startActivity(i6);
                break;
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        fAuth.getCurrentUser().reload();
    }
}