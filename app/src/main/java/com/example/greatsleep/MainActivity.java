package com.example.greatsleep;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();
    }
    public void analyse_btn(View view) {
        Intent Intent = new Intent(MainActivity.this, Sleep.class);
        startActivity(Intent);
    }
    public void station_btn(View view) {
        Intent Intent = new Intent(MainActivity.this, Station.class);
        startActivity(Intent);
    }
    public void clock_btn(View view) {
        Intent Intent = new Intent(MainActivity.this, Clock.class);
        startActivity(Intent);
    }
    public void dream_btn(View view) {
        Intent Intent = new Intent(MainActivity.this, Dream.class);
        startActivity(Intent);
    }
    public void setting_btn(View view) {
        Intent Intent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(Intent);
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
    protected void onRestart() {
        super.onRestart();
        checkUser();
    }
    //返回鍵當作home鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}