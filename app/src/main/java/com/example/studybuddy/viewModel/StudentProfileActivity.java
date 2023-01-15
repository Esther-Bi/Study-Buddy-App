package com.example.studybuddy.viewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.studybuddy.R;
import com.example.studybuddy.model.StudentProfileModel;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Intent;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StudentProfileActivity extends AppCompatActivity {

    StudentProfileModel model = new StudentProfileModel(this);

    EditText name, degree, year, age, phone_number;
    Button save;
    RadioGroup gender_group;
    RadioButton gender;
    DocumentReference documentReference;
    FirebaseFirestore db = model.getDb();

    GoogleSignInClient googleSignInClient;

    private String url = "http://" + "10.0.2.2" + ":" + 5000 + "/";
    private String postBodyString;
    private MediaType mediaType;
    private RequestBody requestBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        googleSignInClient= model.googleSignInClient();

        FirebaseUser user = model.getUser();
        String userUID = model.getUserUID();
        documentReference = model.getDocumentReference();


        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        degree = findViewById(R.id.degree);
        year = findViewById(R.id.year);
        phone_number = findViewById(R.id.phone);
        save = findViewById(R.id.save);
        gender_group = findViewById(R.id.gender_group);


        //onclick listener for updating profile button
        save.setOnClickListener(v -> {
            //Converting fields to text
            int radioID = gender_group.getCheckedRadioButtonId();
            if (radioID == -1) {
                Toast.makeText(StudentProfileActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
            } else {
                gender = findViewById(radioID);
                String textGender = gender.getText().toString();
                String textAge = age.getText().toString();
                String textName = name.getText().toString();
                String textDegree = degree.getText().toString();
                String textYear = year.getText().toString();
                String textPhone = phone_number.getText().toString();

                if (TextUtils.isEmpty(textName) || TextUtils.isEmpty(textDegree) || TextUtils.isEmpty(textYear) || TextUtils.isEmpty(textGender) || TextUtils.isEmpty(textAge) || TextUtils.isEmpty(textPhone)) {
                    Toast.makeText(StudentProfileActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                }else if (textPhone.length() != 9){
                    Toast.makeText(StudentProfileActivity.this, "phone number is illegal", Toast.LENGTH_SHORT).show();
                } else {
                    updateProfile(textName, textYear, textDegree, textGender, textAge, textPhone, user, db);
                    startActivity(new Intent(this, StudentHomeActivity.class));
                }
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        model.modelOnStart(name, age,year,degree, StudentProfileActivity.this);
    }

    public void updateProfile(String textName, String textYear, String textDegree, String textGender, String textAge, String textPhone, FirebaseUser user, FirebaseFirestore database) {

        assert user != null;
        String userUID = user.getUid();

//        without server
//        Student studentToAdd = new Student(textName, textYear, textDegree, textGender, textAge, textPhone, userUID); //creating a new user
//        database.collection("students").document(userUID).set(studentToAdd); //adding user data to database

        String data = "add_student:" + textName + "_" + textYear+ "_" + textDegree + "_" + textGender+ "_" + textAge + "_" + textPhone+ "_" + userUID;
        postRequest(data, url);

        startActivity(new Intent(StudentProfileActivity.this, MainActivity.class));
        finish();
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
                startActivity(new Intent(StudentProfileActivity.this, StudentHomeActivity.class));
                return true;
            case R.id.action_edit_profile:
                startActivity(new Intent(StudentProfileActivity.this, StudentProfileActivity.class));
                finish();
                return true;
            case R.id.action_payments:
                startActivity(new Intent(StudentProfileActivity.this, MyPaymentsActivity.class));
                finish();
                return true;
            case R.id.action_book_a_class:
                startActivity(new Intent(StudentProfileActivity.this, BookClass.class));
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
                            startActivity(new Intent(StudentProfileActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                });
            case R.id.action_change_user_type:
                startActivity(new Intent(StudentProfileActivity.this, ChooseUserActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                        Toast.makeText(StudentProfileActivity.this, "Something went wrong:" + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(StudentProfileActivity.this, response.body().string(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


}
