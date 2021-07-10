package com.example.greatsleep;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import android.media.MediaPlayer;
public class Clock extends AppCompatActivity {
    int hour=0;
    int minutes=0;
    Switch vibrate;
    Button sound;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    PendingIntent pendingIntentSet;
    TimePicker timePicker;
    AlarmManager alarm;
    Intent intent;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        vibrate=(Switch)findViewById(R.id.switch1);
        sound=(Button)findViewById(R.id.soundSet);
        timePicker=(TimePicker)findViewById(R.id.timerpick);
        preferences = getSharedPreferences("clock", Context.MODE_PRIVATE);
        editor = preferences.edit();
        alarm=(AlarmManager) getSystemService(Context.ALARM_SERVICE);

        intent = new Intent(Clock.this, ClockAlarm.class);
        pendingIntentSet=PendingIntent.getActivity(Clock.this,1,intent,0);

        vibrate.setChecked(preferences.getBoolean("vibration",true));
        timePicker.setIs24HourView(true);
        timePicker.setClickable(false);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hour=hourOfDay;
                minutes=minute;
            }
        });

        //前往更改鬧鐘鈴聲頁面
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Clock.this,ClockSound.class);
                startActivity(intent);
            }
        });
    }

    public void clockConfirm(View view) {
        //設定響鈴時可以在主頁進行
        intent.addCategory (Intent.CATEGORY_DEFAULT );
        intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK );
        //時間設定

        Calendar calendar =Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour=timePicker.getHour();
            minutes=timePicker.getMinute();
        }
        else{
            hour=timePicker.getCurrentHour();
            minutes=timePicker.getCurrentMinute();
        }

        if (calendar.get(Calendar.HOUR_OF_DAY)>hour){//如果選擇的時間小於獲取的系統時間日期
            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)+1);
        }
        else if (calendar.get(Calendar.HOUR_OF_DAY)==hour){
            if (calendar.get(Calendar.MINUTE)>=minutes) {
                calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)+1);
            }
        }
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minutes);
        calendar.set(Calendar.SECOND,0);


        int currentDate=calendar.get(Calendar.DATE);
        int currentHour=calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute=calendar.get(Calendar.MINUTE);

        long triggerAtMillis= calendar.getTimeInMillis();

        alarm.set(AlarmManager.RTC_WAKEUP,triggerAtMillis,pendingIntentSet);

        editor.putBoolean("is_alarm",true);
        editor.putBoolean("cancel_clock_alarm",true);
        editor.apply();

        Toast.makeText(Clock.this,"您選擇的時間是："+currentDate+"日"+currentHour+"時"+currentMinute+"分",Toast.LENGTH_SHORT).show();
        //按下確定回到主頁
        Intent intent2 = new Intent(Clock.this, MainActivity.class);
        startActivity(intent2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //true
        if(preferences.getBoolean("is_alarm",false)){
            alarm.cancel(pendingIntentSet);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        vibrate.setChecked(preferences.getBoolean("vibration",true));
        vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //是否震動
                editor.putBoolean("vibration",isChecked);
                editor.apply();
            }
        });
        //更改按鈕文字
        sound.setText("鈴聲   "+preferences.getString("text","鈴聲一"));
    }
}