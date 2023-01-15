package com.example.studybuddy.model;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.studybuddy.viewModel.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainModel {
    private MainActivity activity;
    //private String userID;
    private FirebaseAuth firebaseAuth;
    private int RC_SIGN_IN;

    public MainModel(MainActivity activity, FirebaseAuth firebaseAuth) {
        this.activity = activity;
        this.firebaseAuth = firebaseAuth;
        this.RC_SIGN_IN = 100;
    }

    public MainActivity getActivity() {
        return activity;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public int getRC_SIGN_IN() {
        return RC_SIGN_IN;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public GoogleSignInClient getClient(GoogleSignInOptions googleSignInOptions) {
        return GoogleSignIn.getClient(this.activity, googleSignInOptions);
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public void on_result(int requestCode, Intent data) {
        if(requestCode == this.RC_SIGN_IN){
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                //Google sign in success, now auth with firebase
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                Log.d("log in", "onActivityResult: "  + account);
                activity.firebaseAuthWithGoogleAccount(account);
            }
            catch (Exception e){
                //failed google sign in
                Log.d("log in", "onActivityResult: "+e.getMessage() + e);
            }
        }
    }

    public void sign_in(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        this.firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //login success
                        Log.d("GOOGLE_SIGN_IN", "onSuccess: Logged In");

                        //get logged user
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        //get user info
                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();

                        Log.d("GOOGLE_SIGN_IN", "onSuccess: Email: "+email+"\nUID: "+uid);


                        //check if user is new or existing
                        if(authResult.getAdditionalUserInfo().isNewUser()){
                            //user is new- account created
                            Log.d("GOOGLE_SIGN_IN", "onSuccess: Account Created...\n"+email);
                            Toast.makeText(activity, "Welcome To Study Buddy! \n"+email, Toast.LENGTH_SHORT).show();
                            //start profile activity
                            activity.start_activity();
                        }
                        else {
                            //existing account
                            Log.d("GOOGLE_SIGN_IN", "onSuccess: Existing user...\n" + email);
                            Toast.makeText(activity, "Welcome Back! \n" + email, Toast.LENGTH_SHORT).show();
                            //start profile activity
                            activity.start_activity();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //login failed
                        Log.d("GOOGLE_SIGN_IN", "onFailure: LogIn failed "+e.getMessage());
                    }
                });
    }
}
