package com.example.greatsleep;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import android.media.MediaPlayer;
import android.widget.Toast;

public class ClockAlarm extends AppCompatActivity {
    SharedPreferences preferences;
    private MediaPlayer mediaPlayer;
    private Vibrator v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_alarm);

        v = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        //取得鈴聲設定
        preferences = getSharedPreferences("alarm_tune", Context.MODE_PRIVATE);
        mediaPlayer=MediaPlayer.create(this,preferences.getInt("tune",0));

        //突破鎖屏代碼
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock( "" );
        keyguardLock.disableKeyguard();
        //彈出對話框
        AlertDialog.Builder ab = new AlertDialog.Builder(ClockAlarm.this);
        ab.setTitle("起床了!!");
        ab.setMessage("時間到囉~~~")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //你可以在這裡加入事件
                        v.cancel();
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer=null;
                        ClockAlarm.this.finish();
                    }
                });

        //點選對話框外動作
        ab.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                v.cancel();
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer=null;
                ClockAlarm.this.finish();
            }
        });
        AlertDialog ad = ab.create();
        ad.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //振動器
        Boolean isVibrate=preferences.getBoolean("vibration",true);
        if(isVibrate){
            v.vibrate(new long[]{1000, 1000, 1000, 1000}, 0);
        }
        else{
            v.cancel();
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

}