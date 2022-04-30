package com.example.societymanagement;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class residentAdapter extends RecyclerView.Adapter<residentAdapter.myViewholder> {

    Context context;
    ArrayList<Resident> residentList;

    public residentAdapter(Context context, ArrayList<Resident> residentList) {
        this.context = context;
        this.residentList = residentList;
    }

    @NonNull
    @Override
    public myViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resident_list,parent,false);
        return new myViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewholder holder, int position) {
        Resident Resident = residentList.get(position);

        String name = Resident.getName();
        String email = Resident.getEmail();
        String imageurl = Resident.getImageurl();
        holder.name.setText(name);
        holder.email.setText(email);
        Glide.with(context).load(imageurl).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return residentList.size();
    }
    public static class myViewholder extends RecyclerView.ViewHolder{

        TextView name,email;
        ImageView image;

        public myViewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            image = itemView.findViewById(R.id.image);
        }
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
