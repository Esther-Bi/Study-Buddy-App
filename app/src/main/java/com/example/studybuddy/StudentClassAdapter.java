package com.example.studybuddy;
//import android.support.annotation.NonNull;
//import android.support.v7.androidwidget.RecyclerView;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class StudentClassAdapter extends FirestoreRecyclerAdapter<Class, StudentClassAdapter.ClassHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    public StudentClassAdapter(@NonNull FirestoreRecyclerOptions<Class> options , RecyclerViewInterface recyclerViewInterface) {
        super(options);
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @Override
    protected void onBindViewHolder(@NonNull ClassHolder holder, int position, @NonNull Class model) {
//        holder.studentName.setText(model.getStudentName());
        holder.teacherName.setText(model.getTeacherName());
        holder.subject.setText(model.getSubject());

        holder.date.setText(model.getDate());
//        holder.grade.setText(String.valueOf(model.getGrade()));
    }

    @NonNull
    @Override
    public ClassHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item,
                parent, false);
        return new ClassHolder(v, this.recyclerViewInterface);
    }

    class ClassHolder extends RecyclerView.ViewHolder {
        TextView teacherName;
        TextView subject;
        TextView date;

        public ClassHolder(View itemView , RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            teacherName = itemView.findViewById(R.id.teacherName);
            subject  = itemView.findViewById(R.id.subject);
            date = itemView.findViewById(R.id.date);
            itemView.findViewById(R.id.cancel_class).setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onCancelClassClick(teacherName.getText().toString(),subject.getText().toString(),date.getText().toString());
                        }
                    }
                }
            });
            itemView.findViewById(R.id.whatsapp_message).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onWhatsAppMessageClick(teacherName.getText().toString(),subject.getText().toString(),date.getText().toString());
                        }
                    }
                }
            });
        }
    }
}