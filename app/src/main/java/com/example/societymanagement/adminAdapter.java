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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class adminAdapter extends RecyclerView.Adapter<adminAdapter.myViewholder> {

    Context context;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ArrayList<Notice> noticeList;

    public adminAdapter(Context context, ArrayList<Notice> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public adminAdapter.myViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_list_admin,parent,false);
        return new myViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adminAdapter.myViewholder holder, int position) {
        Notice notice = noticeList.get(position);

        String name = notice.getName();
        String notice_text = notice.getNotice_text();
        String time = notice.getTime();
        String notice_desc = notice.getNotice_desc();
        holder.name.setText(name);
        holder.notice_text.setText(notice_text);
        holder.time.setText(time);
        holder.notice_desc.setText(notice_desc);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noticeList.remove(holder.getAdapterPosition());
                firestore.collection("Notices").document(notice.documentid)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context.getApplicationContext(), "Notice deleted Succesfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();   
                    }
                });
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }
    public static class myViewholder extends RecyclerView.ViewHolder{

        TextView name,notice_text,time,notice_desc;
        ImageView delete;

        public myViewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            notice_text = itemView.findViewById(R.id.notice_text);
            time = itemView.findViewById(R.id.time);
            notice_desc = itemView.findViewById(R.id.notice_desc);
            delete = itemView.findViewById(R.id.delete);

        }
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
