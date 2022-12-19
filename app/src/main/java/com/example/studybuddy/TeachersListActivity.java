package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TeachersListActivity extends AppCompatActivity {

//    private static final String TAG = "MyTeachersListActivity";
//
//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private CollectionReference TeachersRef = db.collection("Teachers");


    static ArrayList<Teacher> TeachersList = new ArrayList<Teacher>();
    ListView listView; // list view of teachers

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_list);

        setupData();
        setUpList();
        setUpOnclickListener();
    }

    private void setupData()
    {
        Teacher t1 = new Teacher("111","Talya");
        TeachersList.add(t1);
        Teacher t2 = new Teacher("222", "Ester");
        TeachersList.add(t2);
        Teacher t3 = new Teacher("333", "Noa");
        TeachersList.add(t3);

    }

    private void setUpList()
    {
        listView = (ListView) findViewById(R.id.teachersListView);
        TeacherAdapter adapter = new TeacherAdapter(getApplicationContext(), 0, TeachersList);
        listView.setAdapter(adapter);
//      listView = (ListView) findViewById(R.id.listView);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String> (this, R.layout.activity_teacher_row, R.id.textView, Teachers);
//        listView.setAdapter(arrayAdapter);
    }

    private void setUpOnclickListener()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Teacher selectTeacher = (Teacher) (listView.getItemAtPosition(position));
                Intent showDetail = new Intent(getApplicationContext(), DetailActivityTeacher.class);
                showDetail.putExtra("id",selectTeacher.getId());
                startActivity(showDetail);
            }
        });

    }
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_teachers_list);
//
//        Teachers.add("Reuven_main");
//        Teachers.add("shimon_main");
//        Teachers.add("levi_main");
//
//        listView = (ListView) findViewById(R.id.listView);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>  (this, R.layout.activity_teacher_row, R.id.textView, Teachers);
//        listView.setAdapter(arrayAdapter);
//    }
//    public void loadTeacherList(View v) {
//        Teachers.add("Yehuda_main");
//
//        TeachersRef//.whereArrayContains("courses", "math")
//                // .orderBy("coursenum")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                            Teacher teacher = documentSnapshot.toObject(Teacher.class);
//                            teacher.setDocumentId(documentSnapshot.getId());
//
//                            String name = teacher.getName();
//                            Teachers.add(name);
//                        }
//                   }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, e.toString());
//                    }
//                });
//        listView = (ListView) findViewById(R.id.listView);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String> (this, R.layout.activity_teacher_row, R.id.textView, Teachers);
//        listView.setAdapter(arrayAdapter);
//    }
}