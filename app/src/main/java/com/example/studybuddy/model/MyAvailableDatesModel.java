package com.example.studybuddy.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.studybuddy.viewModel.MyAvailableDatesActivity;
import com.example.studybuddy.objects.Teacher;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyAvailableDatesModel {

    private MyAvailableDatesActivity activity;
    private FirebaseFirestore db;
    private CollectionReference teachersRef;
    private String userID;

    public MyAvailableDatesModel(MyAvailableDatesActivity activity, String id) {
        this.activity = activity;
        this.db = FirebaseFirestore.getInstance();
        this.teachersRef = this.db.collection("teachers");
        this.userID = id;

    }

    public GoogleSignInClient googleSignInClient() {
        return GoogleSignIn.getClient(this.activity, GoogleSignInOptions.DEFAULT_SIGN_IN);
    }

    public CollectionReference getTeachersRef() {
        return teachersRef;
    }

    public void modelSetData() {
        ArrayList<String> datesList = new ArrayList<>();
        teachersRef.whereEqualTo("id" , userID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Teacher teacher = documentSnapshot.toObject(Teacher.class);
                            for (int i=0 ; i<teacher.getDates().size() ; i++){
                                datesList.add(teacher.getDates().get(i));
                                activity.setList(datesList);
                                activity.setUpOnclickListener();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MyAvailableDatesActivity", e.toString());
                    }
                });
    }

    public void upDateTime(String time, String date) {
        String date_and_time = date + " - " + time;
        db.collection("teachers")
                .document(userID)
                .update("dates", FieldValue.arrayUnion(date_and_time));
    }

    public void removeDate(String date) {
        db.collection("teachers")
                .document(userID)
                .update("dates", FieldValue.arrayRemove(date));
    }
}
