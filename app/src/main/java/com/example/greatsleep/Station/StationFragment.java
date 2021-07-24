package com.example.greatsleep.Station;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.greatsleep.R;
import com.example.greatsleep.SettingActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

public class StationFragment extends Fragment implements View.OnClickListener{
    private View mainView;
    private Spinner startlines;
    private Spinner endlines;
    private String startStation;
    private String endStation;
    private String linecolor;
    private int stopt,runt,totalt;
    Button blue,brown,red,green,orange,yellow, confirm;
    int time;
    Button time1,time2,time3;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Intent intent;
    PendingIntent pendingIntentSet;
    AlarmManager alarm;
    private ArrayAdapter<CharSequence> BL;
    private ArrayAdapter<CharSequence> BR;
    private ArrayAdapter<CharSequence> Red;
    private ArrayAdapter<CharSequence> Gre;
    private ArrayAdapter<CharSequence> Org;
    private ArrayAdapter<CharSequence> Yel;
    Calendar calendar;
    private Toolbar toolbar;
    TextView information;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.fragment_station, container, false);

        toolbar=mainView.findViewById(R.id.toolbar_station);
        preferences = getActivity().getSharedPreferences("station", Context.MODE_PRIVATE);
        editor = preferences.edit();

        toolbar.inflateMenu(R.menu.menu_example);
        information=mainView.findViewById(R.id.station_information);

        intent = new Intent(getActivity(), StationAlarm.class);
        pendingIntentSet= PendingIntent.getActivity(getActivity(), 2, intent, 0);
        alarm= (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);

        blue=mainView.findViewById(R.id.s1);
        brown=mainView.findViewById(R.id.s2);
        red=mainView.findViewById(R.id.s3);
        green=mainView.findViewById(R.id.s4);
        orange=mainView.findViewById(R.id.s5);
        yellow=mainView.findViewById(R.id.s6);
        confirm=mainView.findViewById(R.id.confirm);

        blue.setOnClickListener(this);
        brown.setOnClickListener(this);
        red.setOnClickListener(this);
        green.setOnClickListener(this);
        orange.setOnClickListener(this);
        yellow.setOnClickListener(this);
        confirm.setOnClickListener(this);

        time=60;
        time1=(Button)mainView.findViewById(R.id.time1);
        time2=(Button)mainView.findViewById(R.id.time2);
        time3=(Button)mainView.findViewById(R.id.time3);

        time1.setOnClickListener(this);
        time2.setOnClickListener(this);
        time3.setOnClickListener(this);

        calendar =Calendar.getInstance();
        //設定圖片格式
        SubsamplingScaleImageView imageView = mainView.findViewById(R.id.imageView);
        imageView.setImage(ImageSource.resource(R.drawable.metrotaipeimap));
        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
        imageView.setScaleAndCenter(0.18f, new PointF(2500, 4500));
        imageView.setMaxScale(0.8f);
        imageView.setMinScale(0.2f);
        imageView.bringToFront();

        startStation="頂埔";
        endStation="頂埔";
        //捷運站選擇
        StationChoose();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_setting:
                        Intent intent=new Intent(getActivity(), SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_cancel:
                        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                        View view = getLayoutInflater().inflate(R.layout.alarm_dialog_design,null);//嵌入View
                        Button d_confirm=view.findViewById(R.id.alarm_dialog_confirm);
                        Button d_cancel=view.findViewById(R.id.alarm_dialog_cancel);
                        TextView dialog_message=view.findViewById(R.id.alarm_dialog_message);
                        ab.setView(view);
                        AlertDialog dialog=ab.create();
                        dialog_message.setText("到站提示尚未提醒，確定取消嗎?");
                        d_confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editor.putBoolean("is_alarm",false);
                                editor.putString("name",null);
                                information.setText(null);
                                editor.apply();

                                toolbar.getMenu().findItem(R.id.action_cancel).setVisible(false);
                                Toast.makeText(getActivity(), "已取消", Toast.LENGTH_SHORT).show();
                                alarm.cancel(pendingIntentSet);
                                dialog.dismiss();
                            }
                        });
                        d_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                }
                return false;
            }
        });
        return mainView;
    }

    private void StationChoose() {
        //0:藍線 1:棕線 2:紅線 3:綠線 4:橘線 5:黃線
        startlines=(Spinner)mainView.findViewById(R.id.start_station);
        endlines=(Spinner)mainView.findViewById(R.id.end_station);

        BL=ArrayAdapter.createFromResource(getActivity().getApplicationContext(),R.array.station_bl, R.layout.spinner_style);
        BR=ArrayAdapter.createFromResource(getActivity().getApplicationContext(),R.array.station_br, R.layout.spinner_style);
        Red=ArrayAdapter.createFromResource(getActivity().getApplicationContext(),R.array.station_red, R.layout.spinner_style);
        Gre=ArrayAdapter.createFromResource(getActivity().getApplicationContext(),R.array.station_green, R.layout.spinner_style);
        Org=ArrayAdapter.createFromResource(getActivity().getApplicationContext(),R.array.station_o, R.layout.spinner_style);
        Yel=ArrayAdapter.createFromResource(getActivity().getApplicationContext(),R.array.station_y, R.layout.spinner_style);


        BL.setDropDownViewResource(R.layout.spinner_item);
        BR.setDropDownViewResource(R.layout.spinner_item);
        Red.setDropDownViewResource(R.layout.spinner_item);
        Gre.setDropDownViewResource(R.layout.spinner_item);
        Org.setDropDownViewResource(R.layout.spinner_item);
        Yel.setDropDownViewResource(R.layout.spinner_item);

        //Adapter更改Spinner內容
        stationSelect(BL);
        linecolor="BL";
    }


    //按下按鈕
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.s1:
                stationSelect(BL);
                linecolor="BL";
                break;
            case R.id.s2:
                stationSelect(BR);
                linecolor="BR";
                break;
            case R.id.s3:
                stationSelect(Red);
                linecolor="R";
                break;
            case R.id.s4:
                stationSelect(Gre);
                linecolor="G";
                break;
            case R.id.s5:
                stationSelect(Org);
                linecolor="O";
                break;
            case R.id.s6:
                stationSelect(Yel);
                linecolor="Y";
                break;
            case R.id.time1:
                editor.putInt("time_button",R.id.time1);
                editor.apply();
                time1.setBackgroundResource(R.drawable.station_time_select);
                time2.setBackgroundResource(R.drawable.station_time_btn);
                time3.setBackgroundResource(R.drawable.station_time_btn);
//                time3.setBackgroundColor(Color.parseColor("#0F0A4F"));
                time=60;
                break;
            case R.id.time2:
                editor.putInt("time_button",R.id.time2);
                editor.apply();
                time1.setBackgroundResource(R.drawable.station_time_btn);
                time2.setBackgroundResource(R.drawable.station_time_select);
                time3.setBackgroundResource(R.drawable.station_time_btn);
                time=120;
                break;
            case R.id.time3:
                editor.putInt("time_button",R.id.time3);
                editor.apply();
                time1.setBackgroundResource(R.drawable.station_time_btn);
                time2.setBackgroundResource(R.drawable.station_time_btn);
                time3.setBackgroundResource(R.drawable.station_time_select);
                time=180;
                break;
            case R.id.confirm:
                if(startStation.equals(endStation))
                    Toast.makeText(getActivity(), "您選到了同一站", Toast.LENGTH_SHORT).show();
                else if(diff())
                    Toast.makeText(getActivity(), "兩者為不同線，請選擇大橋頭站此中轉站", Toast.LENGTH_SHORT).show();
                else if(preferences.getBoolean("is_alarm",false)){
                    Toast.makeText(getActivity(), "請先取消上次提醒再進行設定", Toast.LENGTH_SHORT).show();
                }
                else {
                    new Async().execute();

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View view = getLayoutInflater().inflate(R.layout.dialog_design,null);//嵌入View
                    Button d_confirm=view.findViewById(R.id.dialog_confirm);
                    Button d_cancel=view.findViewById(R.id.dialog_cancel);
                    TextView dialog_title=view.findViewById(R.id.dialog_title);
                    TextView dialog_message=view.findViewById(R.id.dialog_message);
                    builder.setView(view);
                    AlertDialog dialog=builder.create();
                    dialog_title.setText("確認訊息");
                    dialog_message.setText("提示以震動提醒，請將手機放置口袋等容易感知的地方");
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            stopt = 0;
                            runt = 0;
                            totalt = 0;
                        }
                    });
                    d_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //時間設定
                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                            int minute = calendar.get(Calendar.MINUTE);
                            int s = calendar.get(Calendar.SECOND);
                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, s+3);
                            //計算剩餘時間
                            int left_minute = (totalt-time) / 60;
//                            if(totalt-time<=180){
//                                Toast.makeText(getActivity(),  "很快就到站囉，請下次再設定", Toast.LENGTH_SHORT).show();
//                            }
//                            else{
                                String left_time = "將於" + left_minute + "分鐘後提醒您";
                                Toast.makeText(getActivity(), left_time + "", Toast.LENGTH_SHORT).show();

                                long triggerAtMillis = calendar.getTimeInMillis();
                                alarm.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntentSet);

                                editor.putBoolean("is_alarm", true);
                                information.setText(startStation + " To \n" + endStation);
                                toolbar.getMenu().findItem(R.id.action_cancel).setVisible(true);
                                editor.putString("name", startStation + " To " + endStation);
                                editor.apply();
//                            }

                            dialog.dismiss();
                        }
                    });
                    d_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            stopt = 0;
                            runt = 0;
                            totalt = 0;
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                break;
        }
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
                        startStation = startlines.getSelectedItem().toString();
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
    @Override
    public void onResume() {
        super.onResume();
        information.setText(preferences.getString("name",""));
        if(preferences.getBoolean("is_alarm",false)){
            toolbar.getMenu().findItem(R.id.action_cancel).setVisible(true);
        }
        else {
            toolbar.getMenu().findItem(R.id.action_cancel).setVisible(false);
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