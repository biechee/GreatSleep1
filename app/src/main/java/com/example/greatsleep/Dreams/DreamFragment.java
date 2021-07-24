package com.example.greatsleep.Dreams;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

import com.example.greatsleep.R;
import com.example.greatsleep.SettingActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DreamFragment extends Fragment {
    private View mainView;
    private Toolbar toolbar;
    private Button btnshow;
    private TextView textView,errorText;
    private EditText editText;
    private String category="";
    private String str;
    private String sql;
    private Integer i;
    private Spinner spinner;
    private String type;
    private String name;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.fragment_dream, container, false);

        toolbar=mainView.findViewById(R.id.toolbar_dream);
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

        btnshow = (Button) mainView.findViewById(R.id.search);
        textView = (TextView) mainView.findViewById(R.id.textView);
        editText = (EditText)  mainView.findViewById(R.id.editText);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        spinner = (Spinner) mainView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(getActivity().getApplicationContext(),R.array.dream_type,R.layout.spinner_style);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0,false);
        category = "dongwu"; type="動物";
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                name = spinner.getSelectedItem().toString();
                if (name.equals("動物"))
                {
                    category = "dongwu"; type="動物";
                }
                else if(name.equals("鬼神"))
                {
                    category = "guishen";type="鬼神";
                }
                else if(name.equals("活動"))
                {
                    category = "huodong";type="活動";
                }
                else if(name.equals("建築"))
                {
                    category = "jianzhu";type="建築";
                }
                else if(name.equals("其他"))
                {
                    category = "qita";type="其他";
                }
                else if(name.equals("人物"))
                {
                    category = "renwu";type="人物";
                }
                else if(name.equals("生活"))
                {
                    category = "shenghuo";type="生活";
                }
                else if(name.equals("物品"))
                {
                    category = "wupin";type="物品";
                }
                else if(name.equals("孕婦"))
                {
                    category = "yunfu";type="孕婦";
                }
                else if(name.equals("植物"))
                {
                    category = "zhiwu";type="植物";
                }
                else
                {
                    category = "ziran";type="自然";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = editText.getText().toString().trim();
                if(category.equals(""))
                {
                    Toast.makeText(getActivity(),"請選擇夢的種類！", Toast.LENGTH_SHORT).show();
                }
                else if(str.equals("") && !(category.equals("")))
                {
                    sql ="SELECT * FROM " + category ;
                    Toast.makeText(getActivity(),"系統將顯示 『"+type+"』 選項的所有內容\n"+"(周公解夢官網提供)", Toast.LENGTH_SHORT).show();
                    new Task().execute();
                }
                else
                {
                    sql ="SELECT * FROM " + category +" WHERE name LIKE "+"\'"+"%"+str+"%"  +  "\'";
                    new Task().execute();
                    Toast.makeText(getActivity(),"周公解夢官網提供", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Inflate the layout for this fragment
     return mainView;
    }
    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getActivity(),"不輸入搜尋內容可搜尋該類別所有內容！", Toast.LENGTH_SHORT).show();
    }
    class Task extends AsyncTask<Void, Void, Void> {
        String records = "",error = "";
        @Override
        protected Void doInBackground(Void... voids) {
            i=0;
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection
                        ("jdbc:mysql://192.168.20.10:3306/imDB?characterEncoding=utf-8"
                                ,"andro","andro"
                        );
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                if(!resultSet.next())
                {
                    records+="在『"+type+"』裡沒有符合『"+str+"』的搜尋內容";
                }
                else
                {
                    while (resultSet.next()){
                        i++;
                        records+= "\n\n(包含"+str+"的搜尋結果) \n"+i+"、 "+resultSet.getString(2) + "\n\n" + resultSet.getString(3)
                                .replaceAll("。","。\n").replaceAll("？","?\n")
                                .replaceAll("，請看下面由","。\n")
                                .replaceAll("小編幫你整理的夢見"+resultSet.getString(2)+"的詳細解說吧。","")
                                .replaceAll("\\("+"周公解夢官網"+"\\)","");
                    }
                }
            }
            catch (Exception e)
            {
                error = e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            textView.setScrollY(0);
            textView.setText(records);
            if(error != "")
                errorText.setText(error);
            super.onPostExecute(unused);
        }
    }
}