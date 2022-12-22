package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookClass extends AppCompatActivity {


    public static ArrayList<Teacher> teachersList = new ArrayList<Teacher>();
    private static final String TAG = "MyBookClass";

    ListView listView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference teachersRef = db.collection("teachers");

    GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_class);

        googleSignInClient= GoogleSignIn.getClient(BookClass.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        initSearchWidgets();
        setupData();
        setUpList();
        setUpOnclickListener();
    }

    private void initSearchWidgets()
    {
        SearchView searchView = (SearchView) findViewById(R.id.teacherListSearchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                ArrayList<Teacher> filteredShapes = new ArrayList<Teacher>();

                for(Teacher teacher: teachersList)
                {
                    for(int i=0; i<teacher.getCourses().size(); i++) {
                        String word= teacher.getCourses().get(i);
                        if (word.toLowerCase().contains(s.toLowerCase())) {
                            filteredShapes.add(teacher);
                        }
                    }
                }
                TeacherAdapter adapter = new TeacherAdapter(getApplicationContext(), 0, filteredShapes);
                listView.setAdapter(adapter);

                return false;
            }
        });
    }

    private void setupData() {
        teachersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Teacher teacher = documentSnapshot.toObject(Teacher.class);
                            teacher.setDocumentId(documentSnapshot.getId());

                            teachersList.add(teacher);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    private void setUpList() {
        listView = (ListView) findViewById(R.id.teachersListView);
        TeacherAdapter adapter = new TeacherAdapter(getApplicationContext(), 0, teachersList);
        listView.setAdapter(adapter);
    }


    private void setUpOnclickListener()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Teacher selectTeacher = (Teacher) (listView.getItemAtPosition(position));
                Intent intent = new Intent(getApplicationContext(), DetailActivityTeacher.class);

                intent.putExtra("id",selectTeacher);
                startActivity(intent);
            }
        });
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
            case R.id.action_classes:
                startActivity(new Intent(BookClass.this, StudentHomeActivity.class));
                return true;
            case R.id.action_edit_profile:
                startActivity(new Intent(BookClass.this, StudentProfileActivity.class));
                finish();
                return true;
            case R.id.action_book_a_class:
                startActivity(new Intent(BookClass.this, BookClass.class));
                finish();
                return true;
            case R.id.action_payments:
                startActivity(new Intent(BookClass.this, MyPaymentsActivity.class));
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
                            startActivity(new Intent(BookClass.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            case R.id.action_change_user_type:
                startActivity(new Intent(BookClass.this, ChooseUserActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}