package com.example.studybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class ChooseUserActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference coursesRef = db.collection("courses");


    private FirebaseAuth auth;
    private FirebaseFirestore database;
    Button sign_as_student , sign_as_teacher , first_sign_as_student , first_sign_as_teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        sign_as_student = findViewById(R.id.sign_as_student);
        sign_as_teacher = findViewById(R.id.sign_as_teacher);
        first_sign_as_student = findViewById(R.id.first_sign_as_student);
        first_sign_as_teacher = findViewById(R.id.first_sign_as_teacher);

        sign_as_student.setOnClickListener(v -> {
            startActivity(new Intent(ChooseUserActivity.this, StudentHomeActivity.class));
        });

        sign_as_teacher.setOnClickListener(v -> {
            startActivity(new Intent(ChooseUserActivity.this, HomeActivity.class));
        });

        first_sign_as_student.setOnClickListener(v -> {
            startActivity(new Intent(ChooseUserActivity.this, StudentProfileActivity.class));
        });

        first_sign_as_teacher.setOnClickListener(v -> {
            startActivity(new Intent(ChooseUserActivity.this, FirstTeacherLoginActivity.class));
        });

    }
}