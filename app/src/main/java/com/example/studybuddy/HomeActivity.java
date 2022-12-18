package com.example.studybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // add document
//        Map<String, Object> teacher = new HashMap<>();
//        Map<String, Object> courses = new HashMap<>();
//        courses.put("tichnut" , 100);
//        teacher.put("name", "Noa");
//        teacher.put("year", "3");
//        teacher.put("courses", courses);
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("teachers").document("noa")
//                .set(teacher)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot successfully written!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error writing document", e);
//                    }
//                });
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
            case R.id.action_edit_profile:
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                finish();
                return true;
            case R.id.action_payments:
                startActivity(new Intent(HomeActivity.this, MyPaymentsActivity.class));
                finish();
                return true;
            case R.id.action_my_courses:
                startActivity(new Intent(HomeActivity.this, MyCoursesActivity.class));
                finish();
                return true;
            case R.id.action_log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}