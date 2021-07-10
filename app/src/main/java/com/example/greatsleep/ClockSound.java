package com.example.greatsleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;

public class ClockSound extends AppCompatActivity {
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton0,mRadioButton1,mRadioButton2;
    AlarmSet sound = new AlarmSet(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_sound);

        SharedPreferences preferences = getSharedPreferences("clock", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        mRadioButton0 = (RadioButton) findViewById(R.id.mRadioButton0);
        mRadioButton1 = (RadioButton) findViewById(R.id.mRadioButton1);
        mRadioButton2 = (RadioButton) findViewById(R.id.mRadioButton2);
        mRadioGroup = (RadioGroup) findViewById(R.id.mRadioGroup);

        mRadioGroup.check(preferences.getInt("button",R.id.mRadioButton0));
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.mRadioButton0:
                        editor.putInt("tune", R.raw.sound1);
                        editor.putInt("button",R.id.mRadioButton0);
                        editor.putString("text","鈴聲一");
                        editor.apply();
                        break;
                    case R.id.mRadioButton1:
                        editor.putInt("tune", R.raw.sound2);
                        editor.putInt("button",R.id.mRadioButton1);
                        editor.putString("text","鈴聲二");
                        editor.apply();
                        break;
                    case R.id.mRadioButton2:
                        editor.putInt("tune", R.raw.sound3);
                        editor.putInt("button",R.id.mRadioButton2);
                        editor.putString("text","鈴聲三");
                        editor.apply();
                        break;
                }
                sound.stopTune();
                sound.chooseTrack(preferences.getInt("tune",0));
                sound.playTune();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sound.stopTune();
    }
}