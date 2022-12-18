package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.annotation.NonNull;
import androidx.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Bundle;

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
//    public void updateDegree(View v) {
//        String description = editTextDegree.getText().toString();
//
//        //Map<String, Object> note = new HashMap<>();
//        //note.put(KEY_DESCRIPTION, description);
//
//        //noteRef.set(note, SetOptions.merge());
//        noteRef.update(KEY_DEGREE, description);
//    }
//    public void deleteDegree(View v) {
//        noteRef.update(KEY_DEGREE, FieldValue.delete());
//    }
//    public void deleteStudent(View v) {
//        noteRef.delete();
//    }
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