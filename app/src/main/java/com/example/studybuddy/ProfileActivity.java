package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
//
//public class ProfileActivity extends AppCompatActivity {
//
//    EditText name, degree, year;
//    Button save;
//    FirebaseDatabase database =  FirebaseDatabase.getInstance();
//    DatabaseReference reference;
//    DocumentReference documentReference;
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_profile);
//
//        name = findViewById(R.id.name);
//        degree = findViewById(R.id.degree);
//        year = findViewById(R.id.year);
//        save = findViewById(R.id.save);




import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studybuddy.User;
import com.example.studybuddy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    EditText name, degree, year;
    Button save;
    FirebaseDatabase database =  FirebaseDatabase.getInstance();
    DatabaseReference reference;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.name);
        degree = findViewById(R.id.degree);
        year = findViewById(R.id.year);
        save = findViewById(R.id.save);


        //onclick listener for register button
        save.setOnClickListener(v -> {
            //Converting fields to text
            String textName = name.getText().toString();
            String textDegree = degree.getText().toString();
            String textYear = year.getText().toString();

            if (TextUtils.isEmpty(textName) || TextUtils.isEmpty(textDegree) || TextUtils.isEmpty(textYear)) {
                Toast.makeText(ProfileActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseAuth auth;
                auth = FirebaseAuth.getInstance();
                FirebaseFirestore database;
                database = FirebaseFirestore.getInstance();
                updateProfile(textName, textDegree, textYear, auth, database);
            }
        });
    }


    public void updateProfile(String textName, String textYear, String textDegree, FirebaseAuth auth, FirebaseFirestore database) {

        FirebaseUser user = auth.getCurrentUser();

        assert user != null;
        String userUID = user.getUid();

        User userToAdd = new User(textName, textYear, textDegree, userUID); //creating a new user
        database.collection("Users").document(userUID).set(userToAdd); //adding user data to database

        Toast.makeText(ProfileActivity.this, "Updated Profile successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        finish();
    }

}

//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updateProfile();
//            }
//        });
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String currentuid = user.getUid();
//        documentReference.collection("users").document(currentuid);
//        documentReference.get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                    }
//                })
//    }
//
//    private void updateProfile() {
//    }
//}