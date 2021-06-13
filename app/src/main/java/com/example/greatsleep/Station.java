package com.example.greatsleep;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.Calendar;

public class Station extends AppCompatActivity {
    //初始宣告
    Spinner startlines[]=new Spinner[6];
    Spinner endlines[]=new Spinner[6];
    ArrayAdapter<CharSequence> Stationline;
    ArrayAdapter<CharSequence> BL;
    ArrayAdapter<CharSequence> BR;
    ArrayAdapter<CharSequence> Red;
    ArrayAdapter<CharSequence> Gre;
    ArrayAdapter<CharSequence> Org;
    ArrayAdapter<CharSequence> Yel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);
        //設定圖片格式
        SubsamplingScaleImageView imageView = findViewById(R.id.imageView);
        imageView.setImage(ImageSource.resource(R.drawable.metrotaipeimap));
        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
        imageView.setScaleAndCenter(0.18f, new PointF(2500, 4200));
        imageView.setMaxScale(0.8f);
        imageView.setMinScale(0.2f);
        imageView.bringToFront();
        //捷運站選擇
        StationChoose();

    }
    private void StationChoose() {

        //0:藍線 1:棕線 2:紅線 3:綠線 4:橘線 5:黃線
        for(int i=0;i<startlines.length;i++)
            startlines[i]=(Spinner)findViewById(R.id.start_station);
        for(int i=0;i<endlines.length;i++)
            endlines[i]=(Spinner)findViewById(R.id.end_station);
        Stationline=ArrayAdapter.createFromResource(this,R.array.start_station, android.R.layout.simple_spinner_item);
        BL=ArrayAdapter.createFromResource(this,R.array.station_bl, android.R.layout.simple_spinner_item);
        BR=ArrayAdapter.createFromResource(this,R.array.station_br, android.R.layout.simple_spinner_item);
        Red=ArrayAdapter.createFromResource(this,R.array.station_red, android.R.layout.simple_spinner_item);
        Gre=ArrayAdapter.createFromResource(this,R.array.station_green, android.R.layout.simple_spinner_item);
        Org=ArrayAdapter.createFromResource(this,R.array.station_o, android.R.layout.simple_spinner_item);
        Yel=ArrayAdapter.createFromResource(this,R.array.station_y, android.R.layout.simple_spinner_item);

        Stationline.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Red.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Gre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Org.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Yel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Adapter更改Spinner內容
        startlines[0].setAdapter(BL);
        endlines[0].setAdapter(BL);

    }
    //按下確認
    public void confirm(View view) {
        new AlertDialog.Builder(this)
                .setTitle("確認訊息")
                .setMessage("提示以振動提醒，手機請放置口袋等容易喚醒的地方")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //你可以在這裡加入事件
                        PendingIntent pendingIntentSet;
                        Intent intent = new Intent(Station.this, StationAlarm.class);
                        //設定響鈴時可以在主頁進行
                        intent.addCategory ( Intent . CATEGORY_DEFAULT );
                        intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK );

                        //時間設定
                        AlarmManager alarm= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Calendar calendar =Calendar.getInstance();
                        int hour=calendar.get(Calendar.HOUR_OF_DAY);
                        int minute=calendar.get(Calendar.MINUTE);
                        int second=calendar.get(Calendar.SECOND);
                        calendar.set(Calendar.HOUR_OF_DAY,hour);
                        calendar.set(Calendar.MINUTE,minute);
                        calendar.set(Calendar.SECOND,second+10);

                        long triggerAtMillis= calendar.getTimeInMillis();
                        pendingIntentSet=PendingIntent.getActivity(Station.this,0,intent,0);
                        alarm.set(AlarmManager.RTC_WAKEUP,triggerAtMillis,pendingIntentSet);

                        //按下確定回到主頁
                        Intent intent2 = new Intent(Station.this, MainActivity.class);
                        startActivity(intent2);

                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }

    //按下藍線
    public void blue(View view) {
        startlines[0].setAdapter(BL);

        startlines[0].setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String text=startlines[0].getSelectedItem().toString();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
        endlines[0].setAdapter(BL);
    }
    //按下棕線
    public void brown(View view) {
        startlines[1].setAdapter(BR);
        endlines[1].setAdapter(BR);
    }
    //按下紅線
    public void red(View view) {
        startlines[2].setAdapter(Red);
        endlines[2].setAdapter(Red);
    }
    //按下綠線
    public void green(View view) {
        startlines[3].setAdapter(Gre);
        endlines[3].setAdapter(Gre);
    }
    //按下橘線
    public void orange(View view) {
        startlines[4].setAdapter(Org);
        endlines[4].setAdapter(Org);
    }
    //按下黃線
    public void yellow(View view) {
        startlines[5].setAdapter(Yel);
        endlines[5].setAdapter(Yel);
    }
}