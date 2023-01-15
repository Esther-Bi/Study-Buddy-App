package com.example.studybuddy.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studybuddy.R;
import com.example.studybuddy.viewModel.RecyclerViewInterface;
import com.example.studybuddy.objects.Class;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class StudentPaymentAdapter extends FirestoreRecyclerAdapter<Class, StudentPaymentAdapter.StudentPaymentHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    public StudentPaymentAdapter(@NonNull FirestoreRecyclerOptions<Class> options , RecyclerViewInterface recyclerViewInterface) {
        super(options);
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @Override
    protected void onBindViewHolder(@NonNull StudentPaymentHolder holder, int position, @NonNull Class model) {
        holder.teacherName.setText(model.getTeacherName());
        holder.subject.setText(model.getSubject());
        holder.date.setText(model.getDate());
        holder.cost.setText(model.getCost() + " ₪");
    }

    @NonNull
    @Override
    public StudentPaymentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_payment_item,
                parent, false);
        return new StudentPaymentHolder(v, this.recyclerViewInterface);
    }

    class StudentPaymentHolder extends RecyclerView.ViewHolder {
        TextView teacherName;
        TextView subject;
        TextView date;
        TextView cost;

        public StudentPaymentHolder(View itemView , RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            teacherName = itemView.findViewById(R.id.teacherName);
            subject  = itemView.findViewById(R.id.subject);
            date = itemView.findViewById(R.id.date);
            cost = itemView.findViewById(R.id.cost);
            itemView.findViewById(R.id.payment_confirmation).setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onApprovePaymentForClassClick(teacherName.getText().toString(),subject.getText().toString(),date.getText().toString());
                        }
                    }
                }
            });
            itemView.findViewById(R.id.pay_class).setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onPayForClassClick(teacherName.getText().toString(),subject.getText().toString(),date.getText().toString());
                        }
                    }
                }
            });
        }
    }
}