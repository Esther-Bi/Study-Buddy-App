package com.example.studybuddy;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;

import java.util.HashMap;

public class MyCoursesActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("teachers");


    private FirebaseAuth auth;
    private FirebaseFirestore database;
    Button add;
    EditText course, grade;
    private CourseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setUpRecyclerView();

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        assert user != null;
        String userUID = user.getUid();

        add = findViewById(R.id.add_course);
        course = findViewById(R.id.course_to_add);
        grade = findViewById(R.id.grade_to_add);

        HashMap<String,String> courses = new HashMap<>();
//        courses.put("Math","95");
//        database.collection("courses").document(userUID).set(courses);

        //onclick listener for register button
        add.setOnClickListener(v -> {
            //Converting fields to text
            String textCourse = course.getText().toString();
            String textGrade = grade.getText().toString();

            if (TextUtils.isEmpty(textCourse) || TextUtils.isEmpty(textGrade)) {
                Toast.makeText(MyCoursesActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String,String> my_courses = new HashMap<>();
                my_courses.put(textCourse,textGrade);
                database.collection("courses").document(userUID).update(textCourse, textGrade);
            }
        });


    }

    private void setUpRecyclerView() {
        Query query = notebookRef.orderBy("year", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Course> options = new FirestoreRecyclerOptions.Builder<Course>()
                .setQuery(query, Course.class)
                .build();

        adapter = new CourseAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}