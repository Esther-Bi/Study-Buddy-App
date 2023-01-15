package com.example.studybuddy.viewModel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studybuddy.R;
import com.example.studybuddy.databinding.ActivityMainBinding;
import com.example.studybuddy.model.MainModel;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    private GoogleSignInClient googleSignInClient;

    private static final String TAG = "GOOGLE_SIGN_IN_TAG";

    MainModel model = new MainModel(this, FirebaseAuth.getInstance());

    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: ");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //confirm google signIn
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = model.getClient(googleSignInOptions);


        //init firebase auth
        checkUser();

        //google SignInButton: Click to begin Google SignIn
        binding.googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //begin google sign in
                Log.d(TAG, "onClick: begin Google SignIn");

                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, model.getRC_SIGN_IN());
            }
        });
    }

    private void checkUser() {
        //if user is already signed in then go to choose user type page
        if(model.getCurrentUser() != null){
            Log.d(TAG, "checkUser: Already logged in");
            startActivity(new Intent(this, ChooseUserActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " +requestCode + " " + resultCode +"  " + data);
        model.on_result(requestCode, data);
    }

    public void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        model.sign_in(account);
    }

    public void start_activity(){
        startActivity(new Intent(MainActivity.this, ChooseUserActivity.class));
        finish();
    }
}