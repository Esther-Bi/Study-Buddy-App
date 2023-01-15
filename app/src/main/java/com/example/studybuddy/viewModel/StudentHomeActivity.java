package com.example.studybuddy.viewModel;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studybuddy.R;
import com.example.studybuddy.adapter.StudentClassAdapter;
import com.example.studybuddy.model.StudentHomeModel;
import com.example.studybuddy.objects.Class;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;


public class StudentHomeActivity extends AppCompatActivity implements RecyclerViewInterface {
    StudentHomeModel model = new StudentHomeModel(this, FirebaseAuth.getInstance().getCurrentUser().getUid(), "classes", "students", "teachers");
    private StudentClassAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
        googleSignInClient = model.googleSignInClient();

        setUpRecyclerView();

        googleSignInClient= GoogleSignIn.getClient(StudentHomeActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

    }

    private void setUpRecyclerView() {
        Query query = model.buildClassQuery("student");
        FirestoreRecyclerOptions<Class> options = new FirestoreRecyclerOptions.Builder<Class>()
                .setQuery(query, Class.class)
                .build();

        adapter = new StudentClassAdapter(options, StudentHomeActivity.this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_student);
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
        inflater.inflate(R.menu.menu_student_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_profile:
                startActivity(new Intent(StudentHomeActivity.this, StudentProfileActivity.class));
                finish();
                return true;
            case R.id.action_book_a_class:
                startActivity(new Intent(StudentHomeActivity.this, BookClass.class));
                finish();
                return true;
            case R.id.action_payments:
                startActivity(new Intent(StudentHomeActivity.this, StudentMyPaymentActivity.class));
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
                            startActivity(new Intent(StudentHomeActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            case R.id.action_change_user_type:
                startActivity(new Intent(StudentHomeActivity.this, ChooseUserActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onWhatsAppMessageClick(String name, String subject, String date) {
        model.onWhatsAppMessageClick(name, subject, date);
    }

    @Override
    public void onPayForClassClick(String name, String subject, String date) {

    }

    @Override
    public void onApprovePaymentForClassClick(String name, String subject, String date) {

    }

    @Override
    public void onCancelClassClick(String name, String subject, String date) {
        deleteQuestionPopup(name, subject, date);
    }

    public void deleteQuestionPopup(String name, String subject, String date) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.delete_question_popup, null);

        TextView question = popupView.findViewById(R.id.question);
        Button yes_button = popupView.findViewById(R.id.yes_button);
        Button no_button = popupView.findViewById(R.id.no_button);
        question.setText("Are you sure you want to delete you're\n" + subject + " class\nwith " + name + "\nat " + date + "?");

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Log.d(TAG, "delete class");
                model.click_yes(name, subject,date);
                adapter.notifyDataSetChanged();
            }
        });

        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "don't delete class");
                dialog.dismiss();
            }
        });

    }

}