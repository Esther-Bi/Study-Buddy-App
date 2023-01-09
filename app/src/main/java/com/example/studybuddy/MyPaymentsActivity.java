package com.example.studybuddy;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MyPaymentsActivity extends AppCompatActivity implements RecyclerViewInterface {

    private static final String TAG = "MyPaymentsActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private CollectionReference classesRef = db.collection("classes");

    private FirebaseAuth auth;
    private PaymentAdapter adapter;
    private String teacherID;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_payments);

        setUpRecyclerView();

        googleSignInClient= GoogleSignIn.getClient(MyPaymentsActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        classesRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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

    private void setUpRecyclerView() {

        assert user != null;
        teacherID = user.getUid();

        Query query = classesRef.whereEqualTo("teacher", teacherID).whereEqualTo("past","yes");
        FirestoreRecyclerOptions<Class> options = new FirestoreRecyclerOptions.Builder<Class>()
                .setQuery(query, Class.class)
                .build();

        adapter = new PaymentAdapter(options, MyPaymentsActivity.this);

        RecyclerView recyclerView = findViewById(R.id.payments_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_classes:
                startActivity(new Intent(MyPaymentsActivity.this, HomeActivity.class));
                return true;
            case R.id.action_edit_profile:
                startActivity(new Intent(MyPaymentsActivity.this, ProfileActivity.class));
                finish();
                return true;
            case R.id.action_payments:
                startActivity(new Intent(MyPaymentsActivity.this, MyPaymentsActivity.class));
                finish();
                return true;
            case R.id.action_my_courses:
                startActivity(new Intent(MyPaymentsActivity.this, MyCoursesActivity.class));
                finish();
                return true;
            case R.id.action_my_available_dates:
                startActivity(new Intent(MyPaymentsActivity.this, MyAvailableDatesActivity.class));
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
                            startActivity(new Intent(MyPaymentsActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            case R.id.action_change_user_type:
                startActivity(new Intent(MyPaymentsActivity.this, ChooseUserActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCancelClassClick(String name, String subject, String date) {

    }

    @Override
    public void onWhatsAppMessageClick(String name, String subject, String date) {

    }

    @Override
    public void onPayForClassClick(String name, String subject, String date) {

    }

    @Override
    public void onApprovePaymentForClassClick(String name, String subject, String date) {
        classesRef.whereEqualTo("teacher" , teacherID)
                .whereEqualTo("studentName" , name).whereEqualTo("subject" , subject)
                .whereEqualTo("date" , date).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.exists()){
                                Class curr = documentSnapshot.toObject(Class.class);
                                if (curr.getStudentApproval() == 0) {
                                    Toast.makeText(MyPaymentsActivity.this, "student didn't pay yet", Toast.LENGTH_SHORT).show();
                                } else{
                                    approvePaymentQuestionPopup(name, subject, date);
                                }
                            }
                        }
                    }
                });
    }

    public void approvePaymentQuestionPopup(String name, String subject, String date) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.delete_question_popup, null);

        TextView question = popupView.findViewById(R.id.question);
        Button yes_button = popupView.findViewById(R.id.yes_button);
        Button no_button = popupView.findViewById(R.id.no_button);
        question.setText("Are you sure you got paid for\n" + subject + " class\nwith " + name + "\nat " + date + "?");

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Log.d(TAG, "class paid");
                classesRef.whereEqualTo("teacher" , teacherID)
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
                                        Toast.makeText(MyPaymentsActivity.this, "paid successfully", Toast.LENGTH_SHORT).show();
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
            }
        });

        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "class isn't paid yet");
                dialog.dismiss();
            }
        });

    }
}