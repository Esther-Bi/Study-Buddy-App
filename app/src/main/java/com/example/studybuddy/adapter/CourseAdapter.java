package com.example.studybuddy.adapter;
//import android.support.annotation.NonNull;
//import android.support.v7.androidwidget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studybuddy.objects.Course;
import com.example.studybuddy.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CourseAdapter extends FirestoreRecyclerAdapter<Course, CourseAdapter.CourseHolder> {

    public CourseAdapter(@NonNull FirestoreRecyclerOptions<Course> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CourseHolder holder, int position, @NonNull Course model) {
        holder.course.setText(model.getName());
        holder.grade.setText(String.valueOf(model.getGrade()));
    }

    @NonNull
    @Override
    public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item,
                parent, false);
        return new CourseHolder(v);
    }

    class CourseHolder extends RecyclerView.ViewHolder {
        TextView course;
        TextView grade;

        public CourseHolder(View itemView) {
            super(itemView);
            course = itemView.findViewById(R.id.course);
            grade = itemView.findViewById(R.id.grade);
        }
    }
}