package com.example.studybuddy.viewModel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.studybuddy.R;
import com.example.studybuddy.model.DetailTeacherModel;
import com.example.studybuddy.objects.Teacher;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class DetailActivityTeacher extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    DetailTeacherModel model = new DetailTeacherModel(this,FirebaseAuth.getInstance().getCurrentUser().getUid());
    Teacher currentTeacher;
    private TextView chosenCourse, chosenCost, chosenDate;
    private Spinner coursesSpinner, datesSpinner;

    String courseValueFromSpinner, dateValueFromSpinner;
    private Button bookClass;

    String teacherID;
    String[] classes;
    Integer[] prices;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_teacher);

        Bundle bundle = getIntent().getExtras();
        currentTeacher = (Teacher) bundle.getParcelable("id");

//        Get teacher's ID
        teacherID = currentTeacher.getId();

        chosenCourse = findViewById(R.id.chosenCourse);
        chosenCost = findViewById(R.id.chosenCost);
        chosenDate = findViewById(R.id.chosenDate);
        coursesSpinner = findViewById(R.id.coursesSpinner);
        datesSpinner = findViewById(R.id.datesSpinner);
        bookClass = findViewById(R.id.bookClass);

        coursesSpinner.setOnItemSelectedListener(this);
        datesSpinner.setOnItemSelectedListener(this);

        classes = currentTeacher.getCourses().toArray((new String[currentTeacher.getCourses().size()]));
        prices = currentTeacher.getPrices().toArray((new Integer[currentTeacher.getPrices().size()]));

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
            model.bookClass(classes, courseValueFromSpinner, prices,currentTeacher, dateValueFromSpinner, teacherID);

        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.coursesSpinner) {
            courseValueFromSpinner = parent.getItemAtPosition(position).toString();
            chosenCourse.setText("Chosen course: " + courseValueFromSpinner);
            int index_of_course = Arrays.asList(classes).indexOf(courseValueFromSpinner);
            int cost = prices[index_of_course];
            chosenCost.setText("Price: " + cost + " ₪");
        }
        if (parent.getId() == R.id.datesSpinner) {
            dateValueFromSpinner = parent.getItemAtPosition(position).toString();
            chosenDate.setText("Chosen date: " + dateValueFromSpinner);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void start_activity() {
        startActivity(new Intent(DetailActivityTeacher.this, BookClass.class));
        finish();
    }
}