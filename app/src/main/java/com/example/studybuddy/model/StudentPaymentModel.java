package com.example.studybuddy.model;

import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.studybuddy.objects.Class;
import com.example.studybuddy.viewModel.StudentMyPaymentActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StudentPaymentModel {
    private StudentMyPaymentActivity activity;
    private String userID;
    private CollectionReference classesRef;
    private CollectionReference teachersRef;

    public StudentPaymentModel(StudentMyPaymentActivity activity, String userID, String classes) {
        this.activity = activity;
        this.userID = userID;
        this.classesRef = FirebaseFirestore.getInstance().collection(classes);
        this.teachersRef = FirebaseFirestore.getInstance().collection("teachers");

    }

    public GoogleSignInClient googleSignInClient() {
        return GoogleSignIn.getClient(this.activity, GoogleSignInOptions.DEFAULT_SIGN_IN);
    }

    public void updatePastCourses() {
        this.classesRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if(documentSnapshot.exists()){
                        Date date = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy - HH:mm");
                        Class current_class = documentSnapshot.toObject(Class.class);
                        if (current_class.compare(formatter.format(date))){
                            String dbKey = documentSnapshot.getId();
                            classesRef.document(dbKey)
                                    .update("past", "yes");
                        }
                    }
                }
            }
        });
    }

    public Query buildClassQuery(String field){
        return this.classesRef.whereEqualTo(field, this.userID).whereEqualTo("past","yes");
    }

    public void pay_for_class(String name, String subject, String date) {
        this.classesRef.whereEqualTo("student" , this.userID)
                .whereEqualTo("teacherName" , name).whereEqualTo("subject" , subject)
                .whereEqualTo("date" , date).whereEqualTo("studentApproval" , 0)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.exists()){
                                String teacherID = documentSnapshot.getString("teacher");
                                teachersRef.document(teacherID)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        String pay_box = (String) document.get("payBox");
                                                        activity.openPayBoxApp(pay_box);
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    public void onApprovePayment(String name, String subject, String date) {
        classesRef.whereEqualTo("student" , this.userID)
                .whereEqualTo("teacherName" , name).whereEqualTo("subject" , subject)
                .whereEqualTo("date" , date).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.exists()){
                                Class curr = documentSnapshot.toObject(Class.class);
                                if (curr.getStudentApproval() == 1) {
                                    Toast.makeText(activity, "you already paid", Toast.LENGTH_SHORT).show();
                                } else{
                                    activity.approvePaymentQuestionPopup(name, subject, date);
                                }
                            }
                        }
                    }
                });
    }

    public void approve_click_yes(String name, String date, String subject) {
        this.classesRef.whereEqualTo("student" , this.userID)
                .whereEqualTo("teacherName" , name).whereEqualTo("subject" , subject)
                .whereEqualTo("date" , date)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.exists()){
                                String dbKey = documentSnapshot.getId();
                                classesRef.document(dbKey).update("studentApproval" , 1);
                                Toast.makeText(activity, "paid successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    public void rate_click_save(String name, String date, String subject, RatingBar rt) {
        this.classesRef.whereEqualTo("student" , this.userID)
                .whereEqualTo("teacherName" , name).whereEqualTo("subject" , subject)
                .whereEqualTo("date" , date).whereEqualTo("studentApproval" , 1)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.exists()){
                                String teacherID = documentSnapshot.getString("teacher");
                                teachersRef.document(teacherID)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        String rating = String.valueOf(rt.getRating());
                                                        DecimalFormat df = new DecimalFormat("#.###");
                                                        double curr_rating  = Double.valueOf(df.format((Double) document.get("rating")));
                                                        double new_rating = Double.valueOf(df.format((curr_rating+Double.parseDouble(rating))/2.0));
                                                        teachersRef.document(teacherID).update("rating",new_rating);
                                                    }
                                                }
                                            }
                                        });

                            }
                        }
                    }
                });
    }
}
