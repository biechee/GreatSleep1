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
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.Calendar;

public class Station extends AppCompatActivity {
    //初始宣告
    private Spinner startlines;
    private Spinner endlines;
    private String startStation;
    private String endStation;
    private ArrayAdapter<CharSequence> Stationline;
    private ArrayAdapter<CharSequence> BL;
    private ArrayAdapter<CharSequence> BR;
    private ArrayAdapter<CharSequence> Red;
    private ArrayAdapter<CharSequence> Gre;
    private ArrayAdapter<CharSequence> Org;
    private ArrayAdapter<CharSequence> Yel;
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
        startlines=(Spinner)findViewById(R.id.start_station);
        endlines=(Spinner)findViewById(R.id.end_station);
        Stationline=ArrayAdapter.createFromResource(this,R.array.start_station, android.R.layout.simple_spinner_item);
        BL=ArrayAdapter.createFromResource(this,R.array.station_bl, android.R.layout.simple_spinner_item);
        BR=ArrayAdapter.createFromResource(this,R.array.station_br, android.R.layout.simple_spinner_item);
        Red=ArrayAdapter.createFromResource(this,R.array.station_red, android.R.layout.simple_spinner_item);
        Gre=ArrayAdapter.createFromResource(this,R.array.station_green, android.R.layout.simple_spinner_item);
        Org=ArrayAdapter.createFromResource(this,R.array.station_o, android.R.layout.simple_spinner_item);
        Yel=ArrayAdapter.createFromResource(this,R.array.station_y, android.R.layout.simple_spinner_item);

        Stationline.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        BL.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        BR.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        Red.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        Gre.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        Org.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        Yel.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        //Adapter更改Spinner內容
        stationSelect(BL);
    }
    //按下藍線
    public void blue(View view) {
        stationSelect(BL);
    }
    //按下棕線
    public void brown(View view) {
        stationSelect(BR);
    }
    //按下紅線
    public void red(View view) {
        stationSelect(Red);
    }
    //按下綠線
    public void green(View view) {
        stationSelect(Gre);
    }
    //按下橘線
    public void orange(View view) {
        stationSelect(Org);
    }
    //按下黃線
    public void yellow(View view) {
        stationSelect(Yel);
    }
    public void stationSelect(SpinnerAdapter line){
        startlines.setAdapter(line);
        startlines.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        startStation=startlines.getSelectedItem().toString();

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        startStation=startlines.getSelectedItem().toString();

                    }
                });
        endlines.setAdapter(line);
        endlines.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        endStation=endlines.getSelectedItem().toString();

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        endStation=endlines.getSelectedItem().toString();

                    }
                });
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
                        intent.addCategory (Intent.CATEGORY_DEFAULT );
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

}