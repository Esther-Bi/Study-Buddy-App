package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyCoursesActivity extends AppCompatActivity {

    GoogleSignInClient googleSignInClient;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference teachersRef = db.collection("teachers");
    private static final String TAG = "MyCoursesActivity";

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button popup_course_cancel , popup_course_save;
    private EditText popup_course, popup_grade;
    private String currentID;

    Button add_courses;
    ListView resultsListView;
    List<HashMap<String, String>> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentID = user.getUid();

        resultsListView = (ListView) findViewById(R.id.results_listview);
        int[] colors = {0, 0, 0}; // red for the example
        resultsListView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        resultsListView.setDividerHeight(50);

        add_courses = findViewById(R.id.add_courses);

        setData();

    }

    private void setData() {
        teachersRef.whereEqualTo("id", currentID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                            IterateData(course_and_grade);
                            setUpOnclickListener();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });

        add_courses.setOnClickListener(v -> {
            createCoursePopup();
        });

    }

    private void IterateData(HashMap<String, String> course_and_grade){

        listItems = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(this, listItems, R.layout.course_item,
                new String[]{"Course", "Grade"},
                new int[]{R.id.course, R.id.grade});

        Iterator it = course_and_grade.entrySet().iterator();
        while (it.hasNext()) {
            HashMap<String, String> resultsMap = new HashMap<>();
            Map.Entry pair = (Map.Entry)it.next();
            resultsMap.put("Course", pair.getKey().toString());
            resultsMap.put("Grade", pair.getValue().toString());
            listItems.add(resultsMap);
        }

        resultsListView.setAdapter(adapter);
    }

    public void createCoursePopup(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View coursePopupView = getLayoutInflater().inflate(R.layout.courses_popup , null);
        popup_course = (EditText) coursePopupView.findViewById(R.id.popup_course);
        popup_grade = (EditText) coursePopupView.findViewById(R.id.popup_grade);
        popup_course_cancel = (Button) coursePopupView.findViewById(R.id.popup_course_cancel);
        popup_course_save = (Button) coursePopupView.findViewById(R.id.popup_course_save);

        dialogBuilder.setView(coursePopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        popup_course_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        popup_course_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String course = popup_course.getText().toString();
                String grade = popup_grade.getText().toString();
                if (TextUtils.isEmpty(course) || TextUtils.isEmpty(grade)) {
                    Toast.makeText(MyCoursesActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                } else {
                    String course_and_grade = course + " - " + grade;
                    db.collection("teachers")
                            .document(currentID)
                            .update("courses", FieldValue.arrayUnion(course));
                    db.collection("teachers")
                            .document(currentID)
                            .update("grades", FieldValue.arrayUnion(Integer.parseInt(grade)));

                    Toast.makeText(MyCoursesActivity.this, course_and_grade + " have been added successfully", Toast.LENGTH_SHORT).show();
                    setData();
                    dialog.dismiss();
                }
            }
        });

    }

    private void setUpOnclickListener()
    {
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                HashMap<String, String> item = (HashMap<String, String>) resultsListView.getItemAtPosition(position);
                deleteQuestionPopup(item.get("Course") , item.get("Grade"));
            }
        });
    }

    public void deleteQuestionPopup(String course, String grade) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.delete_question_popup, null);

        TextView question = popupView.findViewById(R.id.question);
        Button yes_button = popupView.findViewById(R.id.yes_button);
        Button no_button = popupView.findViewById(R.id.no_button);
        question.setText("Are you sure you want to delete\n" + course + " course?");

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        yes_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Log.d(TAG, "delete course");
                db.collection("teachers")
                        .document(currentID)
                        .update("courses", FieldValue.arrayRemove(course));
                int grade_int = Integer.parseInt(grade);
                db.collection("teachers")
                        .document(currentID)
                        .update("grades", FieldValue.arrayRemove(grade_int));
                Toast.makeText(MyCoursesActivity.this, "course have been deleted successfully", Toast.LENGTH_SHORT).show();
                setData();
            }
        });

        no_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                Log.d(TAG, "don't delete course");
                dialog.dismiss();
            }
        });

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
                startActivity(new Intent(MyCoursesActivity.this, HomeActivity.class));
                return true;
            case R.id.action_edit_profile:
                startActivity(new Intent(MyCoursesActivity.this, ProfileActivity.class));
                finish();
                return true;
            case R.id.action_payments:
                startActivity(new Intent(MyCoursesActivity.this, MyPaymentsActivity.class));
                finish();
                return true;
            case R.id.action_my_courses:
                startActivity(new Intent(MyCoursesActivity.this, MyCoursesActivity.class));
                finish();
                return true;
            case R.id.action_my_available_dates:
                startActivity(new Intent(MyCoursesActivity.this, MyAvailableDatesActivity.class));
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
                            startActivity(new Intent(MyCoursesActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            case R.id.action_change_user_type:
                startActivity(new Intent(MyCoursesActivity.this, ChooseUserActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}