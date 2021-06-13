package com.example.greatsleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class SettingActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    Button google_logout;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();
        google_logout=(Button)findViewById(R.id.logout);
        google_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
                accessTokenTracker=new AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                        if(currentAccessToken==null){
                            firebaseAuth.signOut();
                        }
                    }
                };
                LoginManager.getInstance().logOut();
                updateUI();
            }
        });
        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkUser();
    }

    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            //user not login
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
        else{
            //user login
            String email=user.getEmail();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user==null){
            updateUI();
        }
    }

    private void updateUI() {
        startActivity(new Intent(SettingActivity.this,LoginActivity.class));
        finish();
    }
}