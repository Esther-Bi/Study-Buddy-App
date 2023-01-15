package com.example.studybuddy.viewModel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.studybuddy.R;

public class ChooseUserActivity extends AppCompatActivity {

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