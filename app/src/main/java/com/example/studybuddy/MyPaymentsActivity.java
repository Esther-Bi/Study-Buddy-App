package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

public class MyPaymentsActivity extends AppCompatActivity {

    private static final String TAG = "MyPaymentsActivity";

    private EditText editTextName;
    private EditText editTextAge;
    private EditText editTextDegree;
    private EditText editTextCourseNum;
    private EditText editTextCourses;
    private TextView textViewData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference studentsRef = db.collection("Students");
    private CollectionReference classesRef = db.collection("classes");
    private DocumentReference noteRef = db.document("Students/student1");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_payments);

        editTextName = findViewById(R.id.edit_text_name);
        editTextAge = findViewById(R.id.edit_text_age);
        editTextDegree = findViewById(R.id.edit_text_degree);
        editTextCourseNum = findViewById(R.id.edit_text_coursenum);
        editTextCourses = findViewById(R.id.edit_text_courses);
        textViewData = findViewById(R.id.text_view_data);
    }
    public void addStudent(View v) {
        String name = editTextName.getText().toString();
        String age = editTextAge.getText().toString();
        String degree = editTextDegree.getText().toString();

        if (editTextCourseNum.length() == 0) {
            editTextCourseNum.setText("0");
        }
        int coursenum = Integer.parseInt(editTextCourseNum.getText().toString());

        String coursesInput = editTextCourses.getText().toString();
        String[] tagArray = coursesInput.split("\\s*,\\s*");
        List<String> courses = Arrays.asList(tagArray);

        Student student = new Student(name, age, degree, coursenum, courses);

        studentsRef.add(student);
    }

    public void saveClass(View v) {
        String studentName = "1";
        String teacherName = "2";
        String subject = "3";
        String date = "4";

        Class newclass = new Class(studentName, teacherName, subject, date);

        classesRef.add(newclass);
    }
    public void moveToFilterTeacher(View v) {

        startActivity(new Intent(MyPaymentsActivity.this, TeachersListActivity.class));
        finish();
    }

    public void loadStudents(View v) {
        studentsRef.whereArrayContains("courses", "nath")
               // .orderBy("coursenum")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Student student = documentSnapshot.toObject(Student.class);
                            student.setDocumentId(documentSnapshot.getId());

                            String documentId = student.getDocumentId();
                            String name = student.getName();
                            String age = student.getAge();
                            String degree = student.getDegree();
                            int coursenum = student.getCoursenum();

                            data += "ID: " + documentId + "\nName: " + name + "\nAge: " + age +
                                    "\nDegree: " + degree + "\nCoursenum: " + coursenum + "\nCoursas: ";

                            for (String course : student.getCourses()) {
                                data += "\n-" + course;
                            }
                            data += "\n\n";
                        }
                        textViewData.setText(data);
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });
    }

}