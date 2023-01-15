package com.example.studybuddy.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.studybuddy.viewModel.MyCoursesActivity;
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

import java.util.HashMap;

public class CoursesModel {
    private MyCoursesActivity activity;
    private String userID;
    private CollectionReference teachersRef;

    public CoursesModel(MyCoursesActivity activity, String userID) {
        this.activity = activity;
        this.userID = userID;
        this.teachersRef = FirebaseFirestore.getInstance().collection("teachers");
    }

    public GoogleSignInClient googleSignInClient() {
        return GoogleSignIn.getClient(this.activity, GoogleSignInOptions.DEFAULT_SIGN_IN);
    }
    public void setData(){
        this.teachersRef.whereEqualTo("id", this.userID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Teacher teacher = documentSnapshot.toObject(Teacher.class);
                            String[] courses = teacher.getCourses().toArray((new String[teacher.getCourses().size()]));
                            Integer[] grades = teacher.getGrades().toArray((new Integer[teacher.getGrades().size()]));

                            HashMap<String, String> course_and_grade = new HashMap<>();
                            for (int i = 0; i < teacher.getGrades().size(); i++) {
                                course_and_grade.put(courses[i], String.valueOf(grades[i]));
                            }
                            activity.IterateData(course_and_grade);
                            activity.setUpOnclickListener();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MyCoursesActivity", e.toString());
                    }
                });
    }

    public void updateDataBase(String course, String grade, String price) {
        this.teachersRef.document(this.userID)
                .update("courses", FieldValue.arrayUnion(course));

        this.teachersRef.document(this.userID)
                .update("grades", FieldValue.arrayUnion(Integer.parseInt(grade)));

        this.teachersRef.document(this.userID)
                .update("prices", FieldValue.arrayUnion(Integer.parseInt(price)));
    }

    public void yes_click(String course, String grade) {
        this.teachersRef.document(this.userID)
                .update("courses", FieldValue.arrayRemove(course));
        int grade_int = Integer.parseInt(grade);
        this.teachersRef.document(this.userID)
                .update("grades", FieldValue.arrayRemove(grade_int));
    }
}
