package com.example.studybuddy.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.studybuddy.viewModel.DetailActivityTeacher;
import com.example.studybuddy.objects.Class;
import com.example.studybuddy.objects.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class DetailTeacherModel {
    private DetailActivityTeacher activity;
    private CollectionReference teachersRef;
    private CollectionReference studentsRef;
    private CollectionReference classesRef;
    private String userID;
    private static final String TAG = "MyDetailActivityTeacher";

    public DetailTeacherModel(DetailActivityTeacher activity, String userID) {
        this.userID = userID;
        this.activity = activity;
        this.teachersRef = FirebaseFirestore.getInstance().collection("teachers");
        this.studentsRef = FirebaseFirestore.getInstance().collection("students");
        this.classesRef = FirebaseFirestore.getInstance().collection("classes");
    }

    public void bookClass(String[] classes, String courseValueFromSpinner, Integer[] prices, Teacher currentTeacher, String dateValueFromSpinner, String teacherID) {
        this.studentsRef.document(this.userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = (String) document.get("name");
                        int index_of_course = Arrays.asList(classes).indexOf(courseValueFromSpinner);
                        int cost = prices[index_of_course];
                        Class newClass = new Class(name, currentTeacher.getName(), courseValueFromSpinner, dateValueFromSpinner, userID, teacherID, cost);
                        classesRef.add(newClass);
                        teachersRef.document(teacherID)
                                .update("dates", FieldValue.arrayRemove(dateValueFromSpinner));
                        activity.start_activity();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}