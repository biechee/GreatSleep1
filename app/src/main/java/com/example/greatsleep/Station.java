package com.example.greatsleep;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class Station extends AppCompatActivity {
    //初始宣告
    private Spinner startlines;
    private Spinner endlines;
    private String startStation;
    private String endStation;
    private String linecolor;
    private int stopt,runt,totalt;
    int time;
    Button time1,time2,time3;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Intent intent ;
    PendingIntent pendingIntentSet;
    AlarmManager alarm;
    private ArrayAdapter<CharSequence> Stationline;
    private ArrayAdapter<CharSequence> BL;
    private ArrayAdapter<CharSequence> BR;
    private ArrayAdapter<CharSequence> Red;
    private ArrayAdapter<CharSequence> Gre;
    private ArrayAdapter<CharSequence> Org;
    private ArrayAdapter<CharSequence> Yel;
    Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);

        preferences = getSharedPreferences("station", Context.MODE_PRIVATE);
        editor = preferences.edit();

        intent = new Intent(Station.this, StationAlarm.class);
        pendingIntentSet= PendingIntent.getActivity(Station.this, 2, intent, 0);
        alarm= (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        time1=(Button)findViewById(R.id.time1);
        time2=(Button)findViewById(R.id.time2);
        time3=(Button)findViewById(R.id.time3);

        calendar =Calendar.getInstance();

        //設定圖片格式
        SubsamplingScaleImageView imageView = findViewById(R.id.imageView);
        imageView.setImage(ImageSource.resource(R.drawable.metrotaipeimap));
        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
        imageView.setScaleAndCenter(0.18f, new PointF(2500, 4200));
        imageView.setMaxScale(0.8f);
        imageView.setMinScale(0.2f);
        imageView.bringToFront();

        startStation="頂埔";
        endStation="頂埔";
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
        linecolor="BL";
    }
    //按下藍線
    public void blue(View view) {
        stationSelect(BL);
        linecolor="BL";
    }
    //按下棕線
    public void brown(View view) {
        stationSelect(BR);
        linecolor="BR";
    }
    //按下紅線
    public void red(View view) {
        stationSelect(Red);
        linecolor="R";
    }
    //按下綠線
    public void green(View view) {
        stationSelect(Gre);
        linecolor="G";
    }
    //按下橘線
    public void orange(View view) {
        stationSelect(Org);
        linecolor="O";
    }
    //按下黃線
    public void yellow(View view) {
        stationSelect(Yel);
        linecolor="Y";
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
    public void time1(View view) {
        editor.putInt("time_button",R.id.time1);
        editor.apply();
        time1.setBackgroundColor(Color.parseColor("#100b00"));
        time2.setBackgroundColor(Color.parseColor("#8f6200"));
        time3.setBackgroundColor(Color.parseColor("#8f6200"));
        time=1;
    }

    public void time2(View view) {
        editor.putInt("time_button",R.id.time2);
        editor.apply();
        time1.setBackgroundColor(Color.parseColor("#8f6200"));
        time2.setBackgroundColor(Color.parseColor("#100b00"));
        time3.setBackgroundColor(Color.parseColor("#8f6200"));
        time=2;
    }

    public void time3(View view) {
        editor.putInt("time_button",R.id.time3);
        editor.apply();
        time1.setBackgroundColor(Color.parseColor("#8f6200"));
        time2.setBackgroundColor(Color.parseColor("#8f6200"));
        time3.setBackgroundColor(Color.parseColor("#100b00"));
        time=3;
    }

    //按下確認
    public void confirm(View view) {
        if(startStation.equals(endStation))
            Toast.makeText(Station.this, "您選到了同一站", Toast.LENGTH_SHORT).show();
        else if(diff())
            Toast.makeText(Station.this, "兩者為不同線，請選擇大橋頭站此中轉站", Toast.LENGTH_SHORT).show();
        else {
            new Async().execute();
            new AlertDialog.Builder(this)
                    .setTitle("確認訊息")
                    .setMessage("提示以振動提醒，手機請放置口袋等容易喚醒的地方")
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //你可以在這裡加入事件
                            //設定響鈴時可以在主頁進行
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //時間設定
                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                            int minute = calendar.get(Calendar.MINUTE);
                            int s=calendar.get(Calendar.SECOND);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, s+totalt);
                            //計算剩餘時間
                            int left_minute = totalt/60;
                            String left_time = "將於" + left_minute + "分鐘後提醒您";

                            Toast.makeText(Station.this, left_time +"" , Toast.LENGTH_SHORT).show();
                            long triggerAtMillis = calendar.getTimeInMillis();

                            alarm.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntentSet);

                            editor.putBoolean("is_alarm",true);
                            editor.putBoolean("cancel_station_alarm",true);
                            editor.putString("name","到站提示   "+startStation+" To "+endStation);
                            editor.apply();

                            //按下確定回到主頁
                            Intent intent2 = new Intent(Station.this, MainActivity.class);
                            startActivity(intent2);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stopt = 0;
                    runt = 0;
                    totalt = 0;
                }
            }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    stopt = 0;
                    runt = 0;
                    totalt = 0;
                }
            }).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //true
        if(preferences.getBoolean("is_alarm",false)){
            alarm.cancel(pendingIntentSet);
        }
    }

    //連線資料庫做查找動作
    class Async extends AsyncTask<Void, Void, Void> {
        String error = "";
        @Override
        protected Void doInBackground(Void... voids) {
            int startid=0,endid=0;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Log.v("DB", "加載驅動成功");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.0.11:3306/greatsleep?characterEncoding=utf-8",
                        "root", "a33646509");
                Log.v("DB", "遠端連接成功");
                Statement statement = connection.createStatement();
                //判斷是否有頭站，尋找頭站資料
                if(startStation.equals("頂埔"))
                    startid=1;
                else if(startStation.equals("動物園"))
                    startid=23;
                else if(startStation.equals("新店"))
                    startid=46;
                else if(startStation.equals("南勢角"))
                    startid=64;
                else if(startStation.equals("象山"))
                    startid=89;
                else if(startStation.equals("大坪林")&&linecolor.equals("Y"))
                    startid=115;
                else{
                    ResultSet startstation = statement.executeQuery("SELECT * FROM signature WHERE "+"("+"fsnt = "+"\'"+startStation+"\'"
                            + " AND fsid LIKE "+"\'"+linecolor+"%"+"\'"+")");
                    if(startstation.next()){
                        startid=startstation.getInt(1);
                    }
                    startstation.last();
                }
                //判斷是否有尾站，尋找尾站資料 大坪林要算綠線
                if(endStation.equals("頂埔"))
                    endid=1;
                else if(endStation.equals("動物園"))
                    endid=23;
                else if(endStation.equals("新店"))
                    endid=46;
                else if(endStation.equals("南勢角"))
                    endid=64;
                else if(endStation.equals("象山"))
                    endid=89;
                else if(endStation.equals("大坪林")&&linecolor.equals("Y"))
                    endid=115;
                else{
                    ResultSet endstation = statement.executeQuery("SELECT * FROM signature WHERE"+"("+"fsnt ="+"\'"+endStation+"\'"
                            + " AND fsid LIKE "+"\'"+linecolor+"%"+"\'"+")");
                    if(endstation.next()){
                        endid=endstation.getInt(1)+1;
                    }
                    endstation.last();
                }
                //順  while迴圈 if判斷是否加到目標站
                if((startid-endid)>=0){
                    //順的資料" 9stopt=0,8rout=0,totalt=0
                    ResultSet station_orderbydesc = statement.executeQuery("SELECT * FROM signature WHERE "+"("
                            +endid+" <= signatureid AND signatureid <= "+startid+")"
                            +"ORDER BY signatureid DESC");
                    while(station_orderbydesc.next()){
                        //到站
                        if(station_orderbydesc.getInt(1)==endid){
                            runt=runt+station_orderbydesc.getInt(8);
                            break;
                        }
                        runt=runt+station_orderbydesc.getInt(8);
                        stopt=stopt+station_orderbydesc.getInt(9);
                    }
                    totalt=runt+stopt;
                    station_orderbydesc.last();

                }
                //逆 for迴圈 外 內
                else {
                    //逆的資料
                    ResultSet station_orderby = statement.executeQuery("SELECT * FROM signature WHERE "+"("
                            +endid+" >= signatureid and signatureid >= "+startid+")"
                            +"ORDER BY signatureid");
                    //尾站一站
                    if(startid==endid-1){
                        station_orderby.next();
                        runt=runt+station_orderby.getInt(8);
                    }
                    else{
                        //查詢含尾站
                        if(startStation.equals("頂埔")||startStation.equals("動物園")||startStation.equals("新店")||
                                startStation.equals("南勢角")||startStation.equals("象山")||(startStation.equals("大坪林")&&linecolor.equals("Y"))){
                            station_orderby.next();
                            runt=station_orderby.getInt(8);
                            startid=startid+1;
                            station_orderby.absolute(1);
                            while(station_orderby.next()){
                                if(startid==endid-1){
                                    runt=runt+station_orderby.getInt(8);
                                    stopt=stopt+station_orderby.getInt(9);
                                    break;
                                }
                                //到站
                                runt=runt+station_orderby.getInt(8);
                                stopt=stopt+station_orderby.getInt(9);
                                startid++;
                            }
                        }
                        //查詢不含尾站
                        else{
                            station_orderby.absolute(1);
                            station_orderby.next();
                            runt=station_orderby.getInt(8);
                            station_orderby.absolute(2);
                            startid=startid+1;
                            while(station_orderby.next()){
                                if(startid==endid-1){
                                    break;
                                }
                                runt=runt+station_orderby.getInt(8);
                                stopt=stopt+station_orderby.getInt(9);
                                startid++;
                            }
                        }
                    }
                    totalt=runt+stopt;
                    station_orderby.last();
                }
                Log.v("Second", "總秒數: "+totalt);
            } catch (Exception e) {
                error = e.toString();
                Log.e("DB", "加載驅動失敗");
                Log.e("DB", "遠端連接失敗");
                Log.e("DB", e.toString());
            }
            return null;
        }
    }

    private boolean diff(){
        Boolean d=false;
        if(startStation.equals("台北橋")||startStation.equals("菜寮")||startStation.equals("三重")||startStation.equals("先嗇宮")
                ||startStation.equals("頭前庄")||startStation.equals("新莊")||startStation.equals("輔大")||startStation.equals("丹鳳")
                ||startStation.equals("迴龍"))
        {
            if(endStation.equals("三重國小")||endStation.equals("三和國中")||endStation.equals("徐匯中學")||endStation.equals("三民高中")
                    ||endStation.equals("蘆洲"))
            {
                d= true;
            }
        }
        else if(startStation.equals("三重國小")||startStation.equals("三和國中")||startStation.equals("徐匯中學")||startStation.equals("三民高中")
                ||startStation.equals("蘆洲")){
            if(endStation.equals("台北橋")||endStation.equals("菜寮")||endStation.equals("三重")||endStation.equals("先嗇宮")
                    ||endStation.equals("頭前庄")||endStation.equals("新莊")||endStation.equals("輔大")||endStation.equals("丹鳳")
                    ||endStation.equals("迴龍")){
                d= true;
            }
        }
        return d;
    }
}