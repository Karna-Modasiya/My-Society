package com.example.societymanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.drjacky.imagepicker.ImagePicker;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class add_details extends AppCompatActivity {

    AutoCompleteTextView actv;

    FirebaseFirestore firestore;
    FirebaseAuth fAuth;
    FirebaseStorage storage;
    String usertype;
    EditText maddress,spass,mname,mphone;
    Button submit;
    ImageView profimg;
    String Password = "162410";
    TextInputLayout textInputLayout;
    Uri selectedimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);

        actv = findViewById(R.id.actv);
        maddress = findViewById(R.id.address);
        spass = findViewById(R.id.spass);
        submit = findViewById(R.id.submit);
        profimg = findViewById(R.id.profile_image);
        textInputLayout = findViewById(R.id.spass_box);
        mname = findViewById(R.id.name);
        mphone = findViewById(R.id.phone);

        firestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        ArrayList<String> users= new ArrayList<>();
        users.add("Resident");
        users.add("Secretary");
        //users.add("Service Person");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.list_view,users);

        actv.setAdapter(arrayAdapter);

        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                usertype = adapterView.getItemAtPosition(i).toString();
                if(usertype.equals("Secretary"))
                {
                    textInputLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    textInputLayout.setVisibility(View.GONE);
                }
            }
        });

        profimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(add_details.this)
                        .crop()
                        .cropOval()
                        .compress(1024)
                        .maxResultSize(512, 512)
                        .start();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.getCurrentUser().reload();
                if(fAuth.getCurrentUser().isEmailVerified())
                {
                    String name =  mname.getText().toString();
                    String phone = mphone.getText().toString();
                    String address =  maddress.getText().toString();
                    String pass = spass.getText().toString();

                    if(name.isEmpty()){
                        mname.setError("Name can not be Empty");
                        return;
                    }
                    if(phone.length() != 10)
                    {
                        mphone.setError("Phone Number must be of 10 digits");
                        return;
                    }
                    if(address.isEmpty())
                    {
                        maddress.setError("Add Valid Address");
                        return;
                    }
                    if(usertype == null)
                    {
                        actv.setError("Select any one user");
                        return;
                    }
                    if(!usertype.equals("Secretary") && !usertype.equals("Resident"))
                    {
                        actv.setError("Select right user");
                        return;
                    }
                    if(usertype.equals("Secretary"))
                    {
                        if(pass.length() < 6)
                        {
                            spass.setError("Password must be greater than 6 digit");
                            return;
                        }
                        if(!pass.equals(Password)){
                            Toast.makeText(getApplicationContext(), "Secretary Password incorrect", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if(selectedimg != null)
                    {
                        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_add);
                        Sprite circle = new Circle();
                        progressBar.setIndeterminateDrawable(circle);
                        progressBar.setVisibility(View.VISIBLE);
                        StorageReference reference = storage.getReference().child("Profiles").child(fAuth.getCurrentUser().getUid());
                        reference.putFile(selectedimg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                        submit.setEnabled(false);
                                        Map<String,String> items = new HashMap<>();
                                        items.put("name",name);
                                        items.put("email",fAuth.getCurrentUser().getEmail());
                                        items.put("phone",phone);
                                        items.put("user",usertype);
                                        items.put("address",address);
                                        items.put("imageurl",uri.toString());

                                        firestore.collection("Users").document(fAuth.getCurrentUser().getUid()).set(items).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                FirebaseMessaging.getInstance().subscribeToTopic("all");
                                                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(i);
                                                progressBar.setVisibility(View.GONE);
                                                finishAffinity();
                                                //progressBar.setVisibility(View.GONE);
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
                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            };
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Upload Profile Picture", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Verify Your Email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data.getData() != null)
        {
            profimg.setImageURI(data.getData());
            selectedimg = data.getData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fAuth.getCurrentUser().reload();
    }
}