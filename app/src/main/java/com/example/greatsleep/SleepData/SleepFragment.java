package com.example.greatsleep.SleepData;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.greatsleep.R;
import com.example.greatsleep.SettingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;


import com.golife.customizeclass.ScanBluetoothDevice;

import com.golife.database.table.TablePulseRecord;
import com.golife.database.table.TableSleepRecord;
import com.golife.database.table.TableSpO2Record;
import com.golife.database.table.TableStepRecord;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.goyourlife.gofitsdk.GoFITSdk;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Calendar;
import java.util.Map;

public class SleepFragment extends Fragment {
    private View mainView;
    private Toolbar toolbar;
    public Button bt_ini;
    public Button bt_scan;
    public Button bt_pair;
    public Button bt_connect;
    public Button bt_Get;
    public Button bt_vibrate;
    public Button bt_clear;
    public Button bt_replace;
    public TextView state;
    public TextView pair_state;
    public TextView data;
    public TextView data_Save;
    public TextView advice;

    public static GoFITSdk _goFITSdk = null;
    public ScanBluetoothDevice mSelectDevice = null;
    public String productName = null;
    public String mMacAddress = null;
    public String mPairingCode = null;
    public String mPairingTime = null;
    public String mProductID = null;
    public String sdk_certificate = null;
    public String success = "Success !\n";
    public String initial_msg = null;
    public String license = null;


    public String sleep_data = null;
    public String step_data = null;

    public String asleep;
    public String wake;


    long tt_time_min;
    long light_min;
    long td_min;

    public long sum_td_min ;
    public long sum_light_min ;
    public long sum_tt_time_min;

    public Calendar cal = Calendar.getInstance();
    public Calendar m = Calendar.getInstance();
    public long day = 0;
    public static boolean completion;
    TabLayout tabLayout;
    ViewPager viewPager;

    //FireStore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    //????????????
    private int RuleBreaker = 0;//??????????????????
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar calendar = Calendar.getInstance();
    Calendar setNotificationTime = Calendar.getInstance(); //??????????????????
    String awake;
    private static Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_sleep, container, false);

        tabLayout=mainView.findViewById(R.id.tabLayout);
        viewPager=mainView.findViewById(R.id.viewPager);
        getTabs();

        toolbar=mainView.findViewById(R.id.toolbar_sleep);
        toolbar.inflateMenu(R.menu.menu_example);
        toolbar.getMenu().findItem(R.id.action_cancel).setVisible(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.action_setting){
                    Intent intent=new Intent(getActivity(), SettingActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
        SleepFragment.context = getActivity().getApplicationContext();
        //??????Alarm
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(context.ALARM_SERVICE);
        Intent AlarmIntent = new Intent(context, SleepAlarmReceiver.class);
        PendingIntent AlarmPending = PendingIntent.getBroadcast(getContext(), 0, AlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //????????????23?????????
        setNotificationTime = Calendar.getInstance();
        setNotificationTime.set(Calendar.HOUR_OF_DAY,23); //23???00
        setNotificationTime.set(Calendar.MINUTE, 0);
        setNotificationTime.set(Calendar.SECOND, 0);

        //???????????????????????????
        preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = preferences.edit();
        //??????
        String userdocument = "s0958952199@gmail.com";
        //String userdocument = preferences.getString("userdocument","");//??????Email:s0958952199@gmail.com
        Log.d("ss", "userdocument " + userdocument);
        //???FireStore????????????
        CollectionReference colRef = db.collection("User").document(userdocument).collection("sleepdata");
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        Log.d("ss", queryDocumentSnapshot.getId() + " => " + queryDocumentSnapshot.getData());
                        Map<String, Object> awakeData = new HashMap<>();
                        awakeData = queryDocumentSnapshot.getData();
                        awake = awakeData.get("????????????").toString();
                        Log.d("AWAKE", "" + awake);
                        Date date = new Date();
                        try {
                            date = dateFormat.parse(awake);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //??????????????????12???????????????
                        //????????????????????????3???,?????????(RuleBreaker>=3),???????????????,?????????(RuleBreaker=0)
                        calendar.setTime(date);
                        int hour = calendar.get(Calendar.HOUR);
                        Log.d("Hour", String.valueOf(hour));
                        if (hour >= 0 && hour <= 8) {
                            RuleBreaker++;
                        } else {
                            RuleBreaker = 0;
                        }
                    }
                    if (RuleBreaker >= 3) { //????????????3?????????
                        //????????????????????????
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, setNotificationTime.getTimeInMillis()
                                ,AlarmManager.INTERVAL_DAY,AlarmPending);
                    } else {
                        RuleBreaker = 0;
                    }
                }
            }
        });




        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getTabs();
    }

    public void getTabs(){
        final SleepPageSwapAdapter viewPagerAdapter=new SleepPageSwapAdapter(getActivity().getSupportFragmentManager());
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPagerAdapter.addFragment(SleepDataFragment1.getInstance(),"????????????");
                viewPagerAdapter.addFragment(SleepDataFragment2.getInstance(),"????????????");

                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);


            }
        });
    }
}