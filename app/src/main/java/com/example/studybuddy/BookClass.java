package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TimePicker;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookClass extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    public static ArrayList<Teacher> teachersList = new ArrayList<Teacher>();
    private static final String TAG = "MyBookClass";

    ListView listView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference teachersRef = db.collection("teachers");
    private Spinner coursesSpinner, fromHourSpinner, toHourSpinner;
    String courseValueFromSpinner, dateValueFromButton, fromHourValueFromSpinner, toHourValueFromSpinner;
    private Button start_filter, datesButton;

    private DatePickerDialog datePickerDialog;
    private ArrayList<Teacher> filteredTeachers = new ArrayList<Teacher>();


    GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_class);

        googleSignInClient= GoogleSignIn.getClient(BookClass.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        coursesSpinner = findViewById(R.id.coursesSpinner);
        datesButton = findViewById(R.id.datesButton);
        fromHourSpinner = findViewById(R.id.fromHourSpinner);
        toHourSpinner = findViewById(R.id.toHourSpinner);
        start_filter = findViewById(R.id.filterFromSpinner);

        coursesSpinner.setOnItemSelectedListener(this);
        fromHourSpinner.setOnItemSelectedListener(this);
        toHourSpinner.setOnItemSelectedListener(this);


        ArrayAdapter<CharSequence> coursesSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.courses_array, android.R.layout.simple_spinner_item);
        coursesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coursesSpinner.setAdapter(coursesSpinnerAdapter);

        ArrayAdapter<CharSequence> fromSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.from_hours_array, android.R.layout.simple_spinner_item);
        fromSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromHourSpinner.setAdapter(fromSpinnerAdapter);

        ArrayAdapter<CharSequence> toSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.to_hours_array, android.R.layout.simple_spinner_item);
        toSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toHourSpinner.setAdapter(toSpinnerAdapter);

        dateValueFromButton = "choose date";

        datesButton.setOnClickListener(v -> {
            initDatePicker();
        });

        setupData();
        setUpList();

        start_filter.setOnClickListener(v -> {
            if (courseValueFromSpinner.equals("choose course")){
                Toast.makeText(this, "choose course", Toast.LENGTH_SHORT).show();
            } else {
                initFilteredTeachers();
            }
        });

        initSearchWidgets();
        setUpOnclickListener();
    }

    private void initFilteredTeachers(){
        Boolean flag = true;
        filteredTeachers = new ArrayList<Teacher>();

        for(Teacher teacher: teachersList) {
            if ((!courseValueFromSpinner.equals("choose course")) && flag){
                flag = false;
                for(int i=0; i<teacher.getCourses().size(); i++) {
                    String word= teacher.getCourses().get(i);
                    if (word.toLowerCase().equals(courseValueFromSpinner.toLowerCase())) {
                        flag = true;
                    }
                }
            }

            if ((!dateValueFromButton.equals("choose date")) && flag){
                flag = false;
                for(int i=0; i<teacher.getDates().size(); i++) {
                    String word= teacher.getDates().get(i);
                    if (word.toLowerCase().contains(dateValueFromButton.toLowerCase())) {
                        flag = true;
                    }
                }
            }

            if ((!fromHourValueFromSpinner.equals("choose from hour")) && (!toHourValueFromSpinner.equals("choose to hour")) && flag){
                if (!dateValueFromButton.equals("choose date")){
                    flag = from_to_date(teacher , dateValueFromButton , fromHourValueFromSpinner , toHourValueFromSpinner);
                } else {
                    flag = from_to(teacher , fromHourValueFromSpinner , toHourValueFromSpinner);
                }
            } else if ((!fromHourValueFromSpinner.equals("choose from hour")) && flag){
                if (!dateValueFromButton.equals("choose date")){
                    flag = from_date(teacher , dateValueFromButton , fromHourValueFromSpinner);
                } else {
                    flag = from(teacher , fromHourValueFromSpinner);
                }
            } else if ((!toHourValueFromSpinner.equals("choose to hour")) && flag){
                if (!dateValueFromButton.equals("choose date")){
                    flag = to_date(teacher , dateValueFromButton , toHourValueFromSpinner);
                } else {
                    flag = to(teacher , toHourValueFromSpinner);
                }
            }

            if (flag){
                filteredTeachers.add(teacher);
            }
            flag = true;
        }
        TeacherAdapter adapter = new TeacherAdapter(getApplicationContext(), 0, filteredTeachers);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    private Boolean from_to_date(Teacher teacher, String date, String from, String to) {
        String[] from_parts = from.split(":");
        String[] to_parts = to.split(":");
        Integer intTimeFrom = Integer.parseInt(from_parts[0]);
        Integer intTimeTo = Integer.parseInt(to_parts[0]);
        if (intTimeFrom >= intTimeTo){
            Toast.makeText(this, "illegal hours", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (int time=intTimeFrom ; time<intTimeTo ; time++){
            for(int i=0; i<teacher.getDates().size(); i++) {
                String word= teacher.getDates().get(i);
                if (word.toLowerCase().contains((date+" - "+time).toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean from_to(Teacher teacher, String from, String to) {
        String[] from_parts = from.split(":");
        String[] to_parts = to.split(":");
        Integer intTimeFrom = Integer.parseInt(from_parts[0]);
        Integer intTimeTo = Integer.parseInt(to_parts[0]);
        if (intTimeFrom >= intTimeTo){
            Toast.makeText(this, "illegal hours", Toast.LENGTH_SHORT).show();
            return false;
        }
        for (int time=intTimeFrom ; time<intTimeTo ; time++){
            for(int i=0; i<teacher.getDates().size(); i++) {
                String word= teacher.getDates().get(i);
                if (word.toLowerCase().contains((" - "+time).toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean from_date(Teacher teacher, String date, String from) {
        String[] parts = from.split(":");
        Integer intTime = Integer.parseInt(parts[0]);
        for (int time=intTime ; time<23 ; time++){
            for(int i=0; i<teacher.getDates().size(); i++) {
                String word= teacher.getDates().get(i);
                if (word.toLowerCase().contains((date+" - "+time).toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean from(Teacher teacher, String from) {
        String[] parts = from.split(":");
        Integer intTime = Integer.parseInt(parts[0]);
        for (int time=intTime ; time<23 ; time++){
            for(int i=0; i<teacher.getDates().size(); i++) {
                String word= teacher.getDates().get(i);
                if (word.toLowerCase().contains((" - "+time).toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean to_date(Teacher teacher, String date, String to) {
        String[] parts = to.split(":");
        Integer intTime = Integer.parseInt(parts[0]);
        for (int time=7 ; time<intTime ; time++){
            for(int i=0; i<teacher.getDates().size(); i++) {
                String word= teacher.getDates().get(i);
                if (word.toLowerCase().contains((date+" - "+time).toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean to(Teacher teacher, String to) {
        String[] parts = to.split(":");
        Integer intTime = Integer.parseInt(parts[0]);
        for (int time=7 ; time<intTime ; time++){
            for(int i=0; i<teacher.getDates().size(); i++) {
                String word= teacher.getDates().get(i);
                if (word.toLowerCase().contains((" - "+time).toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
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

                for(Teacher teacher: teachersList) {
                    if (teacher.getName().toLowerCase().contains(s.toLowerCase()))
                        filteredShapes.add(teacher);
//                    for(int i=0; i<teacher.getCourses().size(); i++) {
//                        String word= teacher.getCourses().get(i);
//                        if (word.toLowerCase().contains(s.toLowerCase())) {
//                            filteredShapes.add(teacher);
//                        }
//                    }
                }
                TeacherAdapter adapter = new TeacherAdapter(getApplicationContext(), 0, filteredShapes);
                adapter.notifyDataSetChanged();
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
        adapter.notifyDataSetChanged();
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
                startActivity(new Intent(BookClass.this, StudentMyPaymentActivity.class));
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.coursesSpinner) {
            courseValueFromSpinner = parent.getItemAtPosition(position).toString();
        }
        if (parent.getId() == R.id.fromHourSpinner) {
            fromHourValueFromSpinner = parent.getItemAtPosition(position).toString();
        }
        if (parent.getId() == R.id.toHourSpinner) {
            toHourValueFromSpinner = parent.getItemAtPosition(position).toString();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    // choose date
    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                datesButton.setText(date);
                dateValueFromButton = date;
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.show();

    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

}