package com.example.studybuddy.model;

import android.widget.Toast;

import com.example.studybuddy.objects.Class;
import com.example.studybuddy.viewModel.MyPaymentsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentsModel {
    private MyPaymentsActivity activity;
    private String userID;
    private CollectionReference classesRef;

    public PaymentsModel(MyPaymentsActivity activity, String userID, String classes) {
        this.activity = activity;
        this.userID = userID;
        this.classesRef = FirebaseFirestore.getInstance().collection(classes);
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

    public void onApprovePayment(String name, String subject, String date) {
        classesRef.whereEqualTo("teacher" , this.userID)
                .whereEqualTo("studentName" , name).whereEqualTo("subject" , subject)
                .whereEqualTo("date" , date).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.exists()){
                                Class curr = documentSnapshot.toObject(Class.class);
                                if (curr.getStudentApproval() == 0) {
                                    Toast.makeText(activity, "student didn't pay yet", Toast.LENGTH_SHORT).show();
                                } else{
                                    activity.approvePaymentQuestionPopup(name, subject, date);
                                }
                            }
                        }
                    }
                });
    }


    public void click_yes(String name, String subject, String date) {
        this.classesRef.whereEqualTo("teacher" , this.userID)
                .whereEqualTo("studentName" , name).whereEqualTo("subject" , subject)
                .whereEqualTo("date" , date)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.exists()){
                                String dbKey = documentSnapshot.getId();
                                classesRef.document(dbKey).delete();
                                Toast.makeText(activity, "paid successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
