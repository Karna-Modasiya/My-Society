package com.example.societymanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class myAdapter extends RecyclerView.Adapter<myAdapter.myViewholder> {

    Context context;
    ArrayList<Notice> noticeList;

    public myAdapter(Context context, ArrayList<Notice> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
    }

    @NonNull
    @Override
    public myAdapter.myViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_list,parent,false);
        return new myViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myAdapter.myViewholder holder, int position) {
        Notice notice = noticeList.get(position);

        String name = notice.getName();
        String notice_text = notice.getNotice_text();
        String time = notice.getTime();
        String notice_desc = notice.getNotice_desc();
        holder.name.setText(name);
        holder.notice_text.setText(notice_text);
        holder.time.setText(time);
        holder.notice_desc.setText(notice_desc);
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }
    public static class myViewholder extends RecyclerView.ViewHolder{

        TextView name,notice_text,time,notice_desc;

        public myViewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            notice_text = itemView.findViewById(R.id.notice_text);
            time = itemView.findViewById(R.id.time);
            notice_desc = itemView.findViewById(R.id.notice_desc);
        }
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
