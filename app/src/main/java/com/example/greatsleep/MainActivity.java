package com.example.greatsleep;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    SharedPreferences station_preferences;
    SharedPreferences.Editor station_editor;

    SharedPreferences clock_preferences;
    SharedPreferences.Editor clock_editor;
    Button station;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //到站提示儲存
        station_preferences = getSharedPreferences("station", Context.MODE_PRIVATE);
        station_editor = station_preferences.edit();
        //鬧鐘儲存
        clock_preferences=getSharedPreferences("clock", Context.MODE_PRIVATE);
        clock_editor=clock_preferences.edit();

        station=(Button)findViewById(R.id.a2);

        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();
    }
    public void analyse_btn(View view) {
        Intent Intent = new Intent(MainActivity.this, Sleep.class);
        startActivity(Intent);
    }
    public void station_btn(View view) {
        //有到站提示true
        if(station_preferences.getBoolean("cancel_station_alarm",false)){
            new AlertDialog.Builder(this)
                    .setTitle("到站提示尚未提醒，確定取消嗎?")
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //你可以在這裡加入事件
                            station_editor.putBoolean("cancel_station_alarm",false);
                            station_editor.putString("name",null);
                            station_editor.apply();
                            Intent Intent = new Intent(MainActivity.this, Station.class);
                            startActivity(Intent);
                        }
                    }).setNegativeButton("取消",null).show();
        }
        else{
            Intent Intent = new Intent(MainActivity.this, Station.class);
            startActivity(Intent);
        }
    }
    public void clock_btn(View view) {
        if(clock_preferences.getBoolean("cancel_clock_alarm",false)){
            new AlertDialog.Builder(this)
                    .setTitle("鬧鐘尚未提醒，確定取消嗎?")
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //你可以在這裡加入事件
                            clock_editor.putBoolean("cancel_clock_alarm",false);
                            clock_editor.apply();
                            Intent Intent = new Intent(MainActivity.this, Clock.class);
                            startActivity(Intent);
                        }
                    }).setNegativeButton("取消",null).show();
        }
        else{
            Intent Intent = new Intent(MainActivity.this, Clock.class);
            startActivity(Intent);
        }
    }
    public void dream_btn(View view) {
        Intent Intent = new Intent(MainActivity.this, Dream.class);
        startActivity(Intent);
    }
    public void diary_btn(View view) {
        Intent Intent = new Intent(MainActivity.this, DiaryMenu.class);
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
    protected void onResume() {
        super.onResume();
        checkUser();
        station.setText(station_preferences.getString("name","到站提示"));
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