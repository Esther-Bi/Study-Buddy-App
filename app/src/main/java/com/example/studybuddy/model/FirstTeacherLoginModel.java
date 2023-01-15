package com.example.studybuddy.model;

import android.app.Activity;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.studybuddy.viewModel.FirstTeacherLoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirstTeacherLoginModel {

    Activity activity;
    private String userUID;
    private DocumentReference documentReference;
    private FirebaseUser user;
    private FirebaseFirestore db;


    public FirstTeacherLoginModel (Activity activity){
        this.activity = activity;
        this.db = FirebaseFirestore.getInstance();
        this.userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.documentReference = db.collection("teachers").document(userUID);
        this.user  = FirebaseAuth.getInstance().getCurrentUser();
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public String getUserUID() {
        return userUID;
    }

    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void modelOnStart(EditText name, EditText age, EditText year, EditText degree, FirstTeacherLoginActivity thisactivity) {
        this.documentReference.get()
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
                            Toast.makeText(thisactivity, "no profile yet" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
