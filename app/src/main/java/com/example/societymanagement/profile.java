package com.example.societymanagement;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.drjacky.imagepicker.ImagePicker;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class profile extends AppCompatActivity {

    FirebaseUser user;
    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;
    ImageView profile_pic;
    ProgressBar progressBar;
    String uid;
    String usertype;
    Button update;
    String pname,pemail,pmobile,paddress;
    String mname,memail,mphone,maddress;
    Uri selectedimg;
    boolean imagechange=false;

    EditText uname,uemail,uphone,uaddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);


        progressBar = (ProgressBar)findViewById(R.id.progress_profile);
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);

        firebaseStorage = FirebaseStorage.getInstance();
        firestore =  FirebaseFirestore.getInstance();

        uname = findViewById(R.id.name);
        uemail =  findViewById(R.id.email);
        uphone = findViewById(R.id.phone);
        uaddress = findViewById(R.id.address);
        profile_pic =  findViewById(R.id.profile_image);
        update =  findViewById(R.id.update);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        StorageReference storageReference = firebaseStorage.getReference().child("Profiles/"+uid);

        try {
            final File localfile = File.createTempFile("tempfile",".jpeg");
            storageReference.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                    profile_pic.setImageBitmap(bitmap);
                    progressBar.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        DocumentReference docRef = firestore.collection("Users").document(uid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                pname = documentSnapshot.getString("name");
                pemail = documentSnapshot.getString("email");
                pmobile = documentSnapshot.getString("phone");
                paddress = documentSnapshot.getString("address");

                uname.setText(pname);
                uemail.setText(pemail);
                uphone.setText(pmobile);
                uaddress.setText(paddress);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(profile.this)
                        .crop()
                        .cropOval()
                        .compress(1024)
                        .maxResultSize(512, 512)
                        .start();
                imagechange = true;
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mname = uname.getText().toString();
                memail = uemail.getText().toString();
                mphone = uphone.getText().toString();
                maddress = uaddress.getText().toString();

                if(!memail.equals(pemail))
                {
                    uemail.setError("Email can not be changable");
                    return;
                }

                if(!mname.equals(pname) || !mphone.equals(pmobile) || !maddress.equals(paddress))
                {
                    if(mname.isEmpty()){
                        uname.setError("Name can not be Empty");
                        return;
                    }

                    if(memail.isEmpty()){
                        uemail.setError("Email can not be Empty");
                        return;
                    }

                    if(mphone.length() != 10)
                    {
                        uphone.setError("Phone Number must be of 10 digits");
                        return;
                    }
                    if(maddress.length() <3)
                    {
                        uaddress.setError("Address length should be more than 2 letters");
                        return;
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    Map<String,Object> items = new HashMap<>();
                    items.put("name",mname);
                    items.put("email",memail);
                    items.put("phone",mphone);
                    items.put("address",maddress);

                    firestore.collection("Users").document(uid).update(items).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            pname = mname;
                            pmobile = mphone;
                            paddress = maddress;
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if(imagechange == true)
                {
                    StorageReference reference = firebaseStorage.getReference().child("Profiles").child(uid);
                    reference.putFile(selectedimg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            imagechange = false;
                            selectedimg = null;
                            Toast.makeText(getApplicationContext(), "Profile Picture Updated Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Update any Field First", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        DocumentReference docRef = firestore.collection("Users").document(uid);
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
                case R.id.resetpass:
                    Intent i1 = new Intent(getApplicationContext(),reset_password.class);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData() != null)
        {
            profile_pic.setImageURI(data.getData());
            selectedimg = data.getData();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuth.getInstance().getCurrentUser().reload();
    }
}