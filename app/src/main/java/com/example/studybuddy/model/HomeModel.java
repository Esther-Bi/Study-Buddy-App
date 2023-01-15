package com.example.studybuddy.model;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeModel {
    private Activity activity;
    private String userID;
    private CollectionReference classesRef;
    private CollectionReference teachersRef;
    private CollectionReference studentsRef;


    public HomeModel(Activity activity, String userID, String classes, String students, String teachers) {
        this.activity = activity;
        this.userID = userID;
        this.classesRef = FirebaseFirestore.getInstance().collection(classes);
        this.studentsRef = FirebaseFirestore.getInstance().collection(students);
        this.teachersRef = FirebaseFirestore.getInstance().collection(teachers);
    }

    public Query buildClassQuery(String field){
        return this.classesRef.whereEqualTo(field, this.userID);
    }

    public GoogleSignInClient googleSignInClient() {
        return GoogleSignIn.getClient(this.activity, GoogleSignInOptions.DEFAULT_SIGN_IN);
    }

    public void onWhatsAppMessageClick(String name, String subject, String date) {
        this.classesRef.whereEqualTo("teacher" , this.userID)
                .whereEqualTo("studentName" , name).whereEqualTo("subject" , subject)
                .whereEqualTo("date" , date)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.exists()){
                                String studentID = documentSnapshot.getString("student");
                                studentsRef.document(studentID)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        String mobile_number = (String) document.get("phone");
                                                        openWhatsApp(mobile_number);
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
                    }
                });
    }

    private void openWhatsApp(String mobile_number){
        boolean installed = appInstalledOrNot("com.whatsapp");
        if (installed){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData ( Uri.parse ( "https://wa.me/" + "+972" + mobile_number + "/?text=" + "" ) );
            this.activity.startActivity(intent);
        } else {
            Toast.makeText(this.activity.getApplicationContext(), "whatsApp not installed on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean appInstalledOrNot(String url){
        boolean app_installed;
        try{
            this.activity.getPackageManager().getPackageInfo(url, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void click_yes(String name, String subject, String date){
        this.classesRef.whereEqualTo("teacher" , this.userID)
                .whereEqualTo("studentName" , name).whereEqualTo("subject" , subject)
                .whereEqualTo("date" , date)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.exists()){
                                String dbKey = documentSnapshot.getId();
                                Log.d(TAG, "The database Key is : "+ dbKey);
                                classesRef.document(dbKey).delete();
                                teachersRef.document(userID)
                                        .update("dates", FieldValue.arrayUnion(date));
                                Toast.makeText(activity, "class have been canceled successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
