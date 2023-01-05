package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


import android.content.Intent;

public class StudentProfileActivity extends AppCompatActivity {

    EditText name, degree, year, age, phone_number;
    Button save;
    RadioGroup gender_group;
    RadioButton gender;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        googleSignInClient= GoogleSignIn.getClient(StudentProfileActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userUID = user.getUid();
        documentReference = db.collection("students").document(userUID);


        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        degree = findViewById(R.id.degree);
        year = findViewById(R.id.year);
        phone_number = findViewById(R.id.phone);
        save = findViewById(R.id.save);
        gender_group = findViewById(R.id.gender_group);


        //onclick listener for updating profile button
        save.setOnClickListener(v -> {
            //Converting fields to text
            int radioID = gender_group.getCheckedRadioButtonId();
            if (radioID == -1) {
                Toast.makeText(StudentProfileActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
            } else {
                gender = findViewById(radioID);
                String textGender = gender.getText().toString();
                String textAge = age.getText().toString();
                String textName = name.getText().toString();
                String textDegree = degree.getText().toString();
                String textYear = year.getText().toString();
                String textPhone = phone_number.getText().toString();

                if (TextUtils.isEmpty(textName) || TextUtils.isEmpty(textDegree) || TextUtils.isEmpty(textYear) || TextUtils.isEmpty(textGender) || TextUtils.isEmpty(textAge) || TextUtils.isEmpty(textPhone)) {
                    Toast.makeText(StudentProfileActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                }else if (textPhone.length() != 9){
                    Toast.makeText(StudentProfileActivity.this, "phone number is illegal", Toast.LENGTH_SHORT).show();
                } else {
                    updateProfile(textName, textYear, textDegree, textGender, textAge, textPhone, user, db);
                    startActivity(new Intent(this, StudentHomeActivity.class));
                }
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
                            String phoneResult = task.getResult().getString("phone");
                            name.setText(nameResult);
                            age.setText(ageResult);
                            year.setText(yearResult);
                            degree.setText(degreeResult);
                            phone_number.setText(phoneResult);
                        }else{
                            Toast.makeText(StudentProfileActivity.this, "no profile yet" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateProfile(String textName, String textYear, String textDegree, String textGender, String textAge, String textPhone, FirebaseUser user, FirebaseFirestore database) {

        assert user != null;
        String userUID = user.getUid();

        Student studentToAdd = new Student(textName, textYear, textDegree, textGender, textAge, textPhone, userUID); //creating a new user
        database.collection("students").document(userUID).set(studentToAdd); //adding user data to database

        Toast.makeText(StudentProfileActivity.this, "Updated Profile successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(StudentProfileActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_student_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_classes:
                startActivity(new Intent(StudentProfileActivity.this, StudentHomeActivity.class));
                return true;
            case R.id.action_edit_profile:
                startActivity(new Intent(StudentProfileActivity.this, StudentProfileActivity.class));
                finish();
                return true;
            case R.id.action_payments:
                startActivity(new Intent(StudentProfileActivity.this, MyPaymentsActivity.class));
                finish();
                return true;
            case R.id.action_book_a_class:
                startActivity(new Intent(StudentProfileActivity.this, BookClass.class));
                finish();
                return true;
            case R.id.action_log_out:
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            // When task is successful, sign out from firebase
                            FirebaseAuth.getInstance().signOut();
                            // Display Toast
                            Toast.makeText(getApplicationContext(), "Logout successfully, See you soon (:", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(StudentProfileActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            case R.id.action_change_user_type:
                startActivity(new Intent(StudentProfileActivity.this, ChooseUserActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
