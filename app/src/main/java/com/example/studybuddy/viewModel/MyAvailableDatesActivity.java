package com.example.studybuddy.viewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.studybuddy.R;
import com.example.studybuddy.model.MyAvailableDatesModel;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MyAvailableDatesActivity extends AppCompatActivity {

    private static final String TAG = "MyAvailableDatesActivity";

    MyAvailableDatesModel model = new MyAvailableDatesModel(this, FirebaseAuth.getInstance().getCurrentUser().getUid());
    ListView listView;
    Button add_available_dates;
    GoogleSignInClient googleSignInClient;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button popup_cancel , popup_save , popup_time, popup_date;
    private DatePickerDialog datePickerDialog;
    int hour, minute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_available_dates);

        listView = findViewById(R.id.datesListView);
        add_available_dates = findViewById(R.id.add_available_dates);
        googleSignInClient= model.googleSignInClient();

        setData();
    }

    private void setData(){
        model.modelSetData();
        add_available_dates.setOnClickListener(v -> {
            createPopup();
        });
    }

    public void setList(ArrayList<String> listToShow){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,listToShow);
        listView.setAdapter(adapter);
    }


    public void setUpOnclickListener()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                String selectedDate = (String) (listView.getItemAtPosition(position));
                deleteQuestionPopup(selectedDate);
            }
        });
    }

    public void createPopup(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.dates_popup , null);
        popup_time = (Button) popupView.findViewById(R.id.popup_time);
        popup_date = (Button) popupView.findViewById(R.id.popup_date);
        popup_cancel = (Button) popupView.findViewById(R.id.popup_cancel);
        popup_save = (Button) popupView.findViewById(R.id.popup_save);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        popup_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePicker();
            }
        });

        popup_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDatePicker();
            }
        });

        popup_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        popup_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time = popup_time.getText().toString();
                String date = popup_date.getText().toString();
                model.upDateTime(time, date);
                setData();
                dialog.dismiss();
            }
        });

    }

    // choose time
    public void popTimePicker()
    {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {
                hour = selectedHour;
                minute = selectedMinute;
                popup_time.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
            }
        };

        int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, style, onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
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
                popup_date.setText(date);
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
                startActivity(new Intent(MyAvailableDatesActivity.this, HomeActivity.class));
                return true;
            case R.id.action_edit_profile:
                startActivity(new Intent(MyAvailableDatesActivity.this, ProfileActivity.class));
                finish();
                return true;
            case R.id.action_payments:
                startActivity(new Intent(MyAvailableDatesActivity.this, MyPaymentsActivity.class));
                finish();
                return true;
            case R.id.action_my_courses:
                startActivity(new Intent(MyAvailableDatesActivity.this, MyCoursesActivity.class));
                finish();
                return true;
            case R.id.action_my_available_dates:
                startActivity(new Intent(MyAvailableDatesActivity.this, MyAvailableDatesActivity.class));
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
                            startActivity(new Intent(MyAvailableDatesActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            case R.id.action_change_user_type:
                startActivity(new Intent(MyAvailableDatesActivity.this, ChooseUserActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteQuestionPopup(String date) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.delete_question_popup, null);

        TextView question = popupView.findViewById(R.id.question);
        Button yes_button = popupView.findViewById(R.id.yes_button);
        Button no_button = popupView.findViewById(R.id.no_button);
        question.setText("Are you sure you want to delete\nyou're available date\n" + date + "?");

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        yes_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Log.d(TAG, "delete date");
                model.removeDate(date);
                Toast.makeText(MyAvailableDatesActivity.this, "date have been deleted successfully", Toast.LENGTH_SHORT).show();
                setData();
            }
        });

        no_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                Log.d(TAG, "don't delete date");
                dialog.dismiss();
            }
        });

    }

}