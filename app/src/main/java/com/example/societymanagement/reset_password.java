package com.example.societymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class reset_password extends AppCompatActivity {

    Button resetpass;
    EditText etpass, etrepass;
    String usertype = null;
    FirebaseAuth fAuth;
    FirebaseFirestore firestore;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetpass = findViewById(R.id.resetpass);
        etpass = findViewById(R.id.pass);
        etrepass = findViewById(R.id.repass);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass = etpass.getText().toString();
                String repass = etrepass.getText().toString();

                if (pass.isEmpty()) {
                    etpass.setError("Password must be greater than 6 letter");
                    return;
                }
                if (repass.isEmpty()) {
                    etrepass.setError("Password must be greater than 6 letter");
                    return;
                }
                if (pass.length() < 6) {
                    etpass.setError("Password must be greater than 6 char");
                    return;
                }
                if (!repass.equals(pass)) {
                    etrepass.setError("Confirm Password must match with Password");
                    return;
                }
                user.updatePassword(pass).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Password Reset Successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), login.class);
                        startActivity(i);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        DocumentReference docRef = firestore.collection("Users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                usertype = documentSnapshot.getString("user");
                if(usertype.equals("Secretary"))
                {
                    menuInflater.inflate(R.menu.secretary_menu,menu);
                }
                else if(usertype.equals("Resident"))
                {
                    menuInflater.inflate(R.menu.resident_menu,menu);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),login.class);
                startActivity(intent);
                finishAffinity();
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(usertype.equals("Secretary"))
        {
            switch (item.getItemId())
            {
                case R.id.profile:
                    Intent i1 = new Intent(getApplicationContext(),profile.class);
                    startActivity(i1);
                    break;
                case R.id.logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent i2 =  new Intent(getApplicationContext(),login.class);
                    startActivity(i2);
                    finishAffinity();
                    break;
                case R.id.addnotice:
                    Intent i3 =  new Intent(getApplicationContext(),add_notice.class);
                    startActivity(i3);
                    break;
                case R.id.home:
                    Intent i5 =  new Intent(getApplicationContext(),view_notice.class);
                    startActivity(i5);
                    break;
                case R.id.resident:
                    Intent i6 =  new Intent(getApplicationContext(),residents.class);
                    startActivity(i6);
                    break;
            }
        }
        else if(usertype.equals("Resident"))
        {
            switch (item.getItemId())
            {
                case R.id.profile:
                    Intent i1 = new Intent(getApplicationContext(),profile.class);
                    startActivity(i1);
                    break;

                case R.id.home:
                    Intent i3 =  new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i3);
                    break;

                case R.id.logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent i2 =  new Intent(getApplicationContext(),login.class);
                    startActivity(i2);
                    finishAffinity();
                    break;
            }
        }
        return true;
    }
}