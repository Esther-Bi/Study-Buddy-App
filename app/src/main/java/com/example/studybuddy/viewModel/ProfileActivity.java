package com.example.studybuddy.viewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.studybuddy.R;
import com.example.studybuddy.model.ProfileModel;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import android.app.DatePickerDialog;
import android.content.Intent;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileActivity extends AppCompatActivity {

    ProfileModel model = new ProfileModel(this);

    EditText name, degree, year, age, phone_number, pay_box;
    Button save, add_courses, add_dates;
    RadioGroup gender_group;
    RadioButton gender;
    DocumentReference documentReference;
    FirebaseFirestore db = model.getDb();

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button popup_cancel , popup_save , popup_time, popup_date;
    private DatePickerDialog datePickerDialog;

    private Button popup_course_cancel , popup_course_save;
    private EditText popup_course, popup_grade, popup_price;

    GoogleSignInClient googleSignInClient;

    int hour, minute;

    private String userUID;

    private String url = "http://" + "10.0.2.2" + ":" + 5000 + "/";
    private String postBodyString;
    private MediaType mediaType;
    private RequestBody requestBody;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        googleSignInClient= model.googleSignInClient();

        FirebaseUser user = model.getUser();
        userUID= model.getUserUID();
        documentReference = model.getDocumentReference();


        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        degree = findViewById(R.id.degree);
        year = findViewById(R.id.year);
        phone_number = findViewById(R.id.phone);
        pay_box = findViewById(R.id.pay_box);

        save = findViewById(R.id.save);
        gender_group = findViewById(R.id.gender_group);
        add_courses = findViewById(R.id.add_courses);
        add_dates = findViewById(R.id.add_dates);


        //onclick listener for moving to adding courses for teacher
        add_courses.setOnClickListener(v -> {
            createCoursePopup();
        });

        //onclick listener for adding available dates for teacher
        add_dates.setOnClickListener(v -> {
            createDatePopup();
        });

        //onclick listener for updating profile button
        save.setOnClickListener(v -> {
            //Converting fields to text
            int radioID = gender_group.getCheckedRadioButtonId();
            if (radioID == -1) {
                Toast.makeText(ProfileActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
            } else {
                gender = findViewById(radioID);
                String textGender = gender.getText().toString();
                String textAge = age.getText().toString();
                String textName = name.getText().toString();
                String textDegree = degree.getText().toString();
                String textYear = year.getText().toString();
                String textPhone = phone_number.getText().toString();
                String textPayBox = pay_box.getText().toString();


                if (TextUtils.isEmpty(textName) || TextUtils.isEmpty(textDegree) || TextUtils.isEmpty(textYear) || TextUtils.isEmpty(textGender) || TextUtils.isEmpty(textAge) || TextUtils.isEmpty(textPhone) || TextUtils.isEmpty(textPayBox)) {
                    Toast.makeText(ProfileActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                }else if (textPhone.length() != 9){
                    Toast.makeText(ProfileActivity.this, "phone number is illegal", Toast.LENGTH_SHORT).show();
                } else {
                    updateProfile(textName, textYear, textDegree, textGender, textAge, textPhone, textPayBox, user, db);
                    startActivity(new Intent(this, HomeActivity.class));
                }
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        model.modelOnStart(name, age,year,degree, ProfileActivity.this);
    }

    public void updateProfile(String textName, String textYear, String textDegree, String textGender, String textAge, String textPhone, String textPayBox, FirebaseUser user, FirebaseFirestore database) {
        assert user != null;
        String userUID = user.getUid();
//        without server
//        database.collection("teachers").document(userUID).update("name" , textName,
//                                                                "year" , textYear,
//                                                                                 "degree" , textDegree,
//                                                                                 "age" , textAge,
//                                                                                 "gender" , textGender,
//                                                                                  "phone" , textPhone,
//                                                                                  "payBox" , textPayBox);
//        Toast.makeText(ProfileActivity.this, "updated profile successfully", Toast.LENGTH_SHORT).show();
        String data = "add_teacher:" + textName + "_" + textYear+ "_" + textDegree + "_" + textGender+ "_" + textAge + "_" + textPhone+ "_" + userUID;
        postRequest(data, url);
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        finish();
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
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                return true;
            case R.id.action_edit_profile:
                startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                finish();
                return true;
            case R.id.action_payments:
                startActivity(new Intent(ProfileActivity.this, MyPaymentsActivity.class));
                finish();
                return true;
            case R.id.action_my_courses:
                startActivity(new Intent(ProfileActivity.this, MyCoursesActivity.class));
                finish();
                return true;
            case R.id.action_my_available_dates:
                startActivity(new Intent(ProfileActivity.this, MyAvailableDatesActivity.class));
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
                            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            case R.id.action_change_user_type:
                startActivity(new Intent(ProfileActivity.this, ChooseUserActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createDatePopup(){
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
                if (time.equals("choose time") || date.equals("choose date")) {
                    Toast.makeText(ProfileActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                } else {
                    String date_and_time = date + " - " + time;
                    model.updateData(date_and_time);
                    Toast.makeText(ProfileActivity.this, date_and_time + " have been added successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
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

    public void createCoursePopup(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View coursePopupView = getLayoutInflater().inflate(R.layout.courses_popup , null);
        popup_course = (EditText) coursePopupView.findViewById(R.id.popup_course);
        popup_grade = (EditText) coursePopupView.findViewById(R.id.popup_grade);
        popup_price = (EditText) coursePopupView.findViewById(R.id.popup_price);
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
                String price = popup_price.getText().toString();

                if (TextUtils.isEmpty(course) || TextUtils.isEmpty(grade) || TextUtils.isEmpty(price)) {
                    Toast.makeText(ProfileActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                } else {
                    String course_grade_price = course + " - " + grade + " - " + price + " ₪";
                    db.collection("teachers")
                            .document(userUID)
                            .update("courses", FieldValue.arrayUnion(course));
                    db.collection("teachers")
                            .document(userUID)
                            .update("grades", FieldValue.arrayUnion(Integer.parseInt(grade)));
                    db.collection("teachers")
                            .document(userUID)
                            .update("prices", FieldValue.arrayUnion(Integer.parseInt(price)));

                    Toast.makeText(ProfileActivity.this, course_grade_price + " have been added successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

    }

    private RequestBody buildRequestBody(String msg) {
        postBodyString = msg;
        mediaType = MediaType.parse("text/plain");
        requestBody = RequestBody.create(postBodyString, mediaType);
        return requestBody;
    }


    private void postRequest(String message, String URL) {
        RequestBody requestBody = buildRequestBody(message);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().post(requestBody).url(URL).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ProfileActivity.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        call.cancel();
                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(ProfileActivity.this, response.body().string(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
