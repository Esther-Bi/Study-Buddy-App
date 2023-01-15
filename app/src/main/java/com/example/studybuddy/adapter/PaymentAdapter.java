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

public class PaymentAdapter extends FirestoreRecyclerAdapter<Class, PaymentAdapter.PaymentHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    public PaymentAdapter(@NonNull FirestoreRecyclerOptions<Class> options , RecyclerViewInterface recyclerViewInterface) {
        super(options);
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @Override
    protected void onBindViewHolder(@NonNull PaymentHolder holder, int position, @NonNull Class model) {
        holder.studentName.setText(model.getStudentName());
        holder.subject.setText(model.getSubject());
        holder.date.setText(model.getDate());
        holder.cost.setText(model.getCost() + " ₪");
    }

    @NonNull
    @Override
    public PaymentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_item,
                parent, false);
        return new PaymentHolder(v, this.recyclerViewInterface);
    }

    class PaymentHolder extends RecyclerView.ViewHolder {
        TextView studentName;
        TextView subject;
        TextView date;
        TextView cost;

        public PaymentHolder(View itemView , RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
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
                            recyclerViewInterface.onApprovePaymentForClassClick(studentName.getText().toString(),subject.getText().toString(),date.getText().toString());
                        }
                    }
                }
            });
        }
    }
}