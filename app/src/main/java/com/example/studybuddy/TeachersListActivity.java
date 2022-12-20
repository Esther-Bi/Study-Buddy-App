package com.example.studybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class TeachersListActivity extends AppCompatActivity {


    public static ArrayList<Teacher> teachersList = new ArrayList<Teacher>();

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
        SearchView searchView = (SearchView) findViewById(R.id.teacherListSearchView);

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
        List<String> courses1 = new ArrayList<String>();
        List<String> courses2 = new ArrayList<String>();
        List<String> courses3 = new ArrayList<String>();
        courses1.add("math");
        courses1.add("English");
        courses2.add("English");
        courses2.add("history");
        courses2.add("sciences");
        courses3.add("math");
        courses3.add("history");
        courses3.add("sciences");
        Teacher t1 = new Teacher("111", "Talya", courses1);
        teachersList.add(t1);
        Teacher t2 = new Teacher("222", "Ester", courses2);
        teachersList.add(t2);
        Teacher t3 = new Teacher("333", "Noa", courses3);
        teachersList.add(t3);

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
                Teacher selectShape = (Teacher) (listView.getItemAtPosition(position));
                Intent showDetail = new Intent(getApplicationContext(), DetailActivityTeacher.class);

                showDetail.putExtra("id",selectShape.getId());
                startActivity(showDetail);

            }
        });

    }
}