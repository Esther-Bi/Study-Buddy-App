package com.example.studybuddy.model;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.studybuddy.objects.Teacher;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BookClassModel {

    Activity activity;
    public ArrayList<Teacher> teachersList;// static
    private ArrayList<Teacher> filteredTeachers;
    private FirebaseFirestore db;
    private CollectionReference teachersRef;
    private String courseValueFromSpinner;
    private String dateValueFromButton;
    private String fromHourValueFromSpinner;
    private String toHourValueFromSpinner;
    private ArrayList<Teacher> filteredShapes;

    public BookClassModel(Activity activity) {
        this.activity = activity;
        this.teachersList = new ArrayList<Teacher>();
        this.filteredTeachers = new ArrayList<Teacher>();
        this.db = FirebaseFirestore.getInstance();
        this.teachersRef = this.db.collection("teachers");
        this.dateValueFromButton = "choose date";
        this.filteredShapes = new ArrayList<Teacher>();
    }

    public GoogleSignInClient googleSignInClient() {
        return GoogleSignIn.getClient(this.activity, GoogleSignInOptions.DEFAULT_SIGN_IN);
    }

    public void modelInitFilteredTeachers() {
        Boolean flag = true;
        this.filteredTeachers = new ArrayList<Teacher>();

        for (Teacher teacher : this.teachersList) {
            if ((!this.courseValueFromSpinner.equals("choose course")) && flag) {
                flag = false;
                for (int i = 0; i < teacher.getCourses().size(); i++) {
                    String word = teacher.getCourses().get(i);
                    if (word.toLowerCase().equals(this.courseValueFromSpinner.toLowerCase())) {
                        flag = true;
                    }
                }
            }

            if ((!this.dateValueFromButton.equals("choose date")) && flag) {
                flag = false;
                for (int i = 0; i < teacher.getDates().size(); i++) {
                    String word = teacher.getDates().get(i);
                    if (word.toLowerCase().contains(this.dateValueFromButton.toLowerCase())) {
                        flag = true;
                    }
                }
            }

            if ((!this.fromHourValueFromSpinner.equals("choose from hour")) && (!this.toHourValueFromSpinner.equals("choose to hour")) && flag) {
                if (!this.dateValueFromButton.equals("choose date")) {
                    flag = from_to_date(teacher, this.dateValueFromButton, this.fromHourValueFromSpinner, this.toHourValueFromSpinner);
                } else {
                    flag = from_to(teacher, this.fromHourValueFromSpinner, this.toHourValueFromSpinner);
                }
            } else if ((!this.fromHourValueFromSpinner.equals("choose from hour")) && flag) {
                if (!this.dateValueFromButton.equals("choose date")) {
                    flag = from_date(teacher, this.dateValueFromButton, this.fromHourValueFromSpinner);
                } else {
                    flag = from(teacher, this.fromHourValueFromSpinner);
                }
            } else if ((!this.toHourValueFromSpinner.equals("choose to hour")) && flag) {
                if (!this.dateValueFromButton.equals("choose date")) {
                    flag = to_date(teacher, this.dateValueFromButton, this.toHourValueFromSpinner);
                } else {
                    flag = to(teacher, this.toHourValueFromSpinner);
                }
            }

            if (flag) {
                this.filteredTeachers.add(teacher);
            }
            flag = true;
        }
    }

    public void modelonQueryTextChange(String str) {
        for (Teacher teacher : this.teachersList) {
            if (teacher.getName().toLowerCase().contains(str.toLowerCase()))
                filteredShapes.add(teacher);
        }
    }

    public void modelsetupData(){

        this.teachersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess (QuerySnapshot queryDocumentSnapshots){

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Teacher teacher = documentSnapshot.toObject(Teacher.class);
                    teacher.setDocumentId(documentSnapshot.getId());

                    teachersList.add(teacher);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure (@NonNull Exception e){
                Log.d("MyBookClass", e.toString());
            }
        });
    }

    public  ArrayList<Teacher> getTeachersList() {
        return teachersList;
    }

    public ArrayList<Teacher> getFilteredTeachers() {
        return filteredTeachers;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public CollectionReference getTeachersRef() {
        return teachersRef;
    }

    public String getCourseValueFromSpinner() {
        return courseValueFromSpinner;
    }

    public String getDateValueFromButton() {
        return dateValueFromButton;
    }

    public String getFromHourValueFromSpinner() {
        return fromHourValueFromSpinner;
    }

    public String getToHourValueFromSpinner() {
        return toHourValueFromSpinner;
    }

    public ArrayList<Teacher> getFilteredShapes() {
        return filteredShapes;
    }

    public void setCourseValueFromSpinner(String courseValueFromSpinner) {
        this.courseValueFromSpinner = courseValueFromSpinner;
    }

    public void setDateValueFromButton(String dateValueFromButton) {
        this.dateValueFromButton = dateValueFromButton;
    }

    public void setFromHourValueFromSpinner(String fromHourValueFromSpinner) {
        this.fromHourValueFromSpinner = fromHourValueFromSpinner;
    }

    public void setToHourValueFromSpinner(String toHourValueFromSpinner) {
        this.toHourValueFromSpinner = toHourValueFromSpinner;
    }

    private Boolean from_to_date(Teacher teacher, String date, String from, String to) {
        String[] from_parts = from.split(":");
        String[] to_parts = to.split(":");
        Integer intTimeFrom = Integer.parseInt(from_parts[0]);
        Integer intTimeTo = Integer.parseInt(to_parts[0]);
        if (intTimeFrom >= intTimeTo){
            Toast.makeText(this.activity, "illegal hours", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (int time=intTimeFrom ; time<intTimeTo ; time++){
            for(int i=0; i<teacher.getDates().size(); i++) {
                String word= teacher.getDates().get(i);
                if (word.toLowerCase().contains((date+" - "+time).toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }
    private Boolean from_to(Teacher teacher, String from, String to) {
        String[] from_parts = from.split(":");
        String[] to_parts = to.split(":");
        Integer intTimeFrom = Integer.parseInt(from_parts[0]);
        Integer intTimeTo = Integer.parseInt(to_parts[0]);
        if (intTimeFrom >= intTimeTo){
            Toast.makeText(this.activity, "illegal hours", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (int time=intTimeFrom ; time<intTimeTo ; time++){
            for(int i=0; i<teacher.getDates().size(); i++) {
                String word= teacher.getDates().get(i);
                if (word.toLowerCase().contains((" - "+time).toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }
    private Boolean from_date(Teacher teacher, String date, String from) {
        String[] parts = from.split(":");
        Integer intTime = Integer.parseInt(parts[0]);
        for (int time=intTime ; time<23 ; time++){
            for(int i=0; i<teacher.getDates().size(); i++) {
                String word= teacher.getDates().get(i);
                if (word.toLowerCase().contains((date+" - "+time).toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean from(Teacher teacher, String from) {
        String[] parts = from.split(":");
        Integer intTime = Integer.parseInt(parts[0]);
        for (int time=intTime ; time<23 ; time++){
            for(int i=0; i<teacher.getDates().size(); i++) {
                String word= teacher.getDates().get(i);
                if (word.toLowerCase().contains((" - "+time).toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean to_date(Teacher teacher, String date, String to) {
        String[] parts = to.split(":");
        Integer intTime = Integer.parseInt(parts[0]);
        for (int time=7 ; time<intTime ; time++){
            for(int i=0; i<teacher.getDates().size(); i++) {
                String word= teacher.getDates().get(i);
                if (word.toLowerCase().contains((date+" - "+time).toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean to(Teacher teacher, String to) {
        String[] parts = to.split(":");
        Integer intTime = Integer.parseInt(parts[0]);
        for (int time=7 ; time<intTime ; time++){
            for(int i=0; i<teacher.getDates().size(); i++) {
                String word= teacher.getDates().get(i);
                if (word.toLowerCase().contains((" - "+time).toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }
}

