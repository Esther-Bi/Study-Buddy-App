package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

public class DetailActivityTeacher extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Teacher currentTeacher;
    private TextView chosenCourse, chosenDate;
    private Spinner coursesSpinner, datesSpinner;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference teachersRef = db.collection("Teachers");
    private static final String TAG = "MyDetailActivityTeacher";

    String courseValueFromSpinner, dateValueFromSpinner;
    private Button bookClass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_teacher);

        Bundle bundle = getIntent().getExtras();
        Toast.makeText(this, "id", Toast.LENGTH_SHORT).show();
        currentTeacher = (Teacher) bundle.getParcelable("id");
        Toast.makeText(this, currentTeacher.getName(), Toast.LENGTH_SHORT).show();

//        Get teacher's ID
        String teacherID = currentTeacher.getId();
//        Get my ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String studentID = user.getUid();

        chosenCourse = findViewById(R.id.chosenCourse);
        chosenDate = findViewById(R.id.chosenDate);
        coursesSpinner = findViewById(R.id.coursesSpinner);
        datesSpinner = findViewById(R.id.datesSpinner);
        bookClass = findViewById(R.id.bookClass);

        coursesSpinner.setOnItemSelectedListener(this);
        datesSpinner.setOnItemSelectedListener(this);

        String[] classes = currentTeacher.getCourses().toArray((new String[currentTeacher.getCourses().size()]));

        ArrayAdapter courseAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, classes);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coursesSpinner.setAdapter(courseAdapter);

        String[] dates = currentTeacher.getDates().toArray((new String[currentTeacher.getDates().size()]));
        ArrayAdapter datesAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, dates);
        datesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        datesSpinner.setAdapter(datesAdapter);


        bookClass.setOnClickListener(v -> {
            DocumentReference docRef = db.collection("students").document(studentID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = (String) document.get("name");
                            Class newClass = new Class(name, currentTeacher.getName(), courseValueFromSpinner, dateValueFromSpinner, studentID, teacherID);
                            db.collection("classes").add(newClass);
                            db.collection("teachers")
                                    .document(teacherID)
                                    .update("dates", FieldValue.arrayRemove(dateValueFromSpinner));
                            startActivity(new Intent(DetailActivityTeacher.this, BookClass.class));
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            finish();
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.coursesSpinner) {
            courseValueFromSpinner = parent.getItemAtPosition(position).toString();
            chosenCourse.setText("Chosen course: " + courseValueFromSpinner);
        }
        if (parent.getId() == R.id.datesSpinner) {
            dateValueFromSpinner = parent.getItemAtPosition(position).toString();
            chosenDate.setText("Chosen date: " + dateValueFromSpinner);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}