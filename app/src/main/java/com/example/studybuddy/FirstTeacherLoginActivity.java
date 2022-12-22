package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Intent;

public class FirstTeacherLoginActivity extends AppCompatActivity {

    EditText name, degree, year, age;
    Button save;
    RadioGroup gender_group;
    RadioButton gender;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String userUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_teacher_login);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userUID= user.getUid();
        documentReference = db.collection("teachers").document(userUID);


        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        degree = findViewById(R.id.degree);
        year = findViewById(R.id.year);
        save = findViewById(R.id.save);
        gender_group = findViewById(R.id.gender_group);


        //onclick listener for updating profile button
        save.setOnClickListener(v -> {
            //Converting fields to text
            int radioID = gender_group.getCheckedRadioButtonId();
            gender = findViewById(radioID);
            String textGender = gender.getText().toString();
            String textAge = age.getText().toString();
            String textName = name.getText().toString();
            String textDegree = degree.getText().toString();
            String textYear = year.getText().toString();

            if (TextUtils.isEmpty(textName) || TextUtils.isEmpty(textDegree) || TextUtils.isEmpty(textYear) || TextUtils.isEmpty(textGender) || TextUtils.isEmpty(textAge)) {
                Toast.makeText(FirstTeacherLoginActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
            } else {
                updateProfile(textName, textYear, textDegree, textGender, textAge, user, db);
                startActivity(new Intent(FirstTeacherLoginActivity.this, ProfileActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            String nameResult = task.getResult().getString("name");
                            String ageResult = task.getResult().getString("age");
                            String yearResult = task.getResult().getString("year");
                            String degreeResult = task.getResult().getString("degree");
                            name.setText(nameResult);
                            age.setText(ageResult);
                            year.setText(yearResult);
                            degree.setText(degreeResult);
                        }else{
                            Toast.makeText(FirstTeacherLoginActivity.this, "no profile yet" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateProfile(String textName, String textYear, String textDegree, String textGender, String textAge, FirebaseUser user, FirebaseFirestore database) {

        assert user != null;
        String userUID = user.getUid();

        Teacher teacherToAdd = new Teacher(textName, textYear, textDegree, textGender, textAge, userUID); //creating a new user
        database.collection("teachers").document(userUID).set(teacherToAdd); //adding user data to database

        Toast.makeText(FirstTeacherLoginActivity.this, "Updated Profile successfully", Toast.LENGTH_SHORT).show();
    }
}
