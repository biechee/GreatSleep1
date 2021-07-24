package com.example.greatsleep.Station;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.greatsleep.R;

public class StationAlarm extends AppCompatActivity {
    Vibrator v;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_alarm);
        preferences = getSharedPreferences("station", Context.MODE_PRIVATE);
        editor = preferences.edit();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //突破鎖屏代碼
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock( "" );
        keyguardLock.disableKeyguard();
        //彈出對話框
        AlertDialog.Builder ab = new AlertDialog.Builder(StationAlarm.this);
        View view = getLayoutInflater().inflate(R.layout.alarm_dialog_design,null);//嵌入View
        Button d_confirm=view.findViewById(R.id.alarm_dialog_confirm);
        Button d_cancel=view.findViewById(R.id.alarm_dialog_cancel);
        TextView dialog_message=view.findViewById(R.id.alarm_dialog_message);
        dialog_message.setText("快要到站囉!");
        ab.setView(view);
        AlertDialog dialog=ab.create();
        d_cancel.setVisibility(View.INVISIBLE);
        //點選對話框外動作
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                v.cancel();
                editor.putBoolean("is_alarm",false);
                editor.putString("name",null);
                editor.apply();
                StationAlarm.this.finish();
            }
        });
        d_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                v.cancel();
                editor.putBoolean("is_alarm",false);
                editor.putString("name",null);
                editor.apply();
                dialog.dismiss();
                StationAlarm.this.finish();
            }
        });
        dialog.show();
    }
    @Override
    protected void onStop() {
        super.onStop();
        v.cancel();
        editor.putBoolean("is_alarm",false);
        editor.putString("name",null);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //振動器
        v=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        v.vibrate(new long[]{1000,1000, 1000,1000},0);
    }
}