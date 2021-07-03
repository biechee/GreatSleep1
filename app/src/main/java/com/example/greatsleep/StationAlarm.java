package com.example.greatsleep;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;

public class StationAlarm extends AppCompatActivity {
    Vibrator v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_alarm);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //突破鎖屏代碼
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock( "" );
        keyguardLock.disableKeyguard();
        //彈出對話框
        AlertDialog.Builder ab = new AlertDialog.Builder(StationAlarm.this);
        ab.setTitle("起床了!!");
        ab.setMessage("時間到囉")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //你可以在這裡加入事件
                        v.cancel();
                        StationAlarm.this.finish();
                    }
                });
        //點選對話框外動作
        ab.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                v.cancel();
                StationAlarm.this.finish();
            }
        });
        AlertDialog ad = ab.create();
        ad.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //振動器
        v=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        v.vibrate(new long[]{1000,1000, 1000,1000},0);
    }
    public void cc(View view) {
        v.cancel();
    }
}