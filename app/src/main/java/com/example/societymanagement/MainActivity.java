package com.example.societymanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    ArrayList<Notice> noticeArrayList;
    myAdapter adapter;
    ProgressBar progressBar;
    String usertype;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.nothing);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar)findViewById(R.id.progress_main);
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);

        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recylerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        noticeArrayList = new ArrayList<>();
        adapter = new myAdapter(this, noticeArrayList);
        recyclerView.setAdapter(adapter);

        EventChangeListner();
    }

    private void EventChangeListner(){
        progressBar.setVisibility(View.VISIBLE);
        firestore.collection("Notices").orderBy("time",Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null)
                        {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            return;
                        }
                        for (DocumentChange dc:queryDocumentSnapshots.getDocumentChanges())
                        {
                            if(dc.getType() == DocumentChange.Type.ADDED)
                            {
                                String documentid = dc.getDocument().getId();
                                Notice notice = dc.getDocument().toObject(Notice.class);
                                notice.setDocumentid(documentid);
                                noticeArrayList.add(notice);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        if(adapter.getItemCount() == 0)
                        {
                            progressBar.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            progressBar.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            layout.setVisibility(View.GONE);
                        }
                    }
                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.resident_menu,menu);
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

            case R.id.profile:
                Intent i3 =  new Intent(getApplicationContext(),profile.class);
                startActivity(i3);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent i2 =  new Intent(getApplicationContext(),login.class);
                startActivity(i2);
                finishAffinity();
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