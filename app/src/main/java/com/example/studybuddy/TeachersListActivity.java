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
import android.widget.SearchView;

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


    static ArrayList<Teacher> TeachersList = new ArrayList<Teacher>();
    ListView listView; // list view of teachers
//    private String selectedFilter = "all";
//    private String currentSearchText = "";
//    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_list);

        initSearchWidgets();
        setupData();
        setUpList();
        setUpOnclickListener();
    }

    private void initSearchWidgets()
    {
        SearchView searchView = (SearchView) findViewById(R.id.shapeListSearchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
//                currentSearchText = s;
                ArrayList<Teacher> filteredShapes = new ArrayList<Teacher>();

                for(Teacher teacher: TeachersList)
                {
                    if(teacher.getName().toLowerCase().contains(s.toLowerCase()))
                    {
                        filteredShapes.add(teacher);
                    }
                }
                TeacherAdapter adapter = new TeacherAdapter(getApplicationContext(), 0, filteredShapes);
                listView.setAdapter(adapter);

                return false;
            }
        });
    }

    private void setupData() {
        Teacher t1 = new Teacher("111", "Talya");
        TeachersList.add(t1);
        Teacher t2 = new Teacher("222", "Ester");
        TeachersList.add(t2);
        Teacher t3 = new Teacher("333", "Noa");
        TeachersList.add(t3);

    }

    private void setUpList() {
        listView = (ListView) findViewById(R.id.teachersListView);
        TeacherAdapter adapter = new TeacherAdapter(getApplicationContext(), 0, TeachersList);
        listView.setAdapter(adapter);
    }

    private void setUpOnclickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Teacher selectTeacher = (Teacher) (listView.getItemAtPosition(position));
                Intent showDetail = new Intent(getApplicationContext(), DetailActivityTeacher.class);
                showDetail.putExtra("id", selectTeacher.getId());
                startActivity(showDetail);
            }
        });

    }
}