package com.example.greatsleep.Diaries;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.greatsleep.MainActivity;
import com.example.greatsleep.R;

import java.util.Calendar;

public class DiaryNew extends AppCompatActivity {
    private EditText etext;
    private EditText etitle;
    private Button button;
    private DiaryMenuFragment.IntentOption mode;
    private Diary data;
    private DiaryMenuFragment.IntentOption dialogOption;
    TextView date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_new);


        SharedPreferences shr = PreferenceManager.getDefaultSharedPreferences(DiaryMenuFragment.getAppContext());
        float textSize = Float.parseFloat(shr.getString("editor_text", "18"));

        etext = (EditText)findViewById(R.id.diary_edit);
        etext.setTextSize(textSize);

        etitle=(EditText)findViewById(R.id.diary_title);
        etitle.setTextSize(textSize);

        date=findViewById(R.id.date);

        button = (Button) findViewById(R.id.save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etitle.getText().toString().equals("")){
                    Toast.makeText(DiaryNew.this,"請輸入標題",Toast.LENGTH_SHORT).show();
                }
                else if(etext.getText().toString().equals("")) {
                    Toast.makeText(DiaryNew.this, "請輸入您的日記內容", Toast.LENGTH_SHORT).show();
                }
                else{
                    saveButtonClick(button);
                }
            }
        });

        Intent intent = getIntent();
        mode = (DiaryMenuFragment.IntentOption) intent.getSerializableExtra("MODE");

        switch(mode)
        {
            case NEW:
                break;
            case EDIT:
                String text = intent.getStringExtra("text");
                String id = intent.getStringExtra("id");
                String title=intent.getStringExtra("title");
                data = new Diary(text,id,title);
                break;
            default:
                break;
        }
        String title = getIntent().getStringExtra("title");

        String text = getIntent().getStringExtra("text");

        String date_time=getIntent().getStringExtra("id");
        if(date_time!=null) {
            date.setText(data.getDate());
        }
        else{
            Calendar calendar;
            calendar=Calendar.getInstance();
            int Year=calendar.get(Calendar.YEAR);
            int Month=calendar.get(Calendar.MONTH);
            int Day=calendar.get(Calendar.DATE);
            date.setText(Year+"年"+Month+"月"+Day+"日");
        }

        if(title!=null)
        {
            etitle.setText(title, TextView.BufferType.EDITABLE);

        }

        if(text!=null)
        {
            etext.setText(text, TextView.BufferType.EDITABLE);
        }
    }

    private void cancelActivity()
    {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void reactToUser()
    {
        switch(mode)
        {
            case NEW:
            {
                String title=etitle.getText().toString();

                Intent intent = new Intent();
                if(!title.isEmpty())
                {
                    intent.putExtra("title", title);
                }

                String text = etext.getText().toString();
                if(!text.isEmpty())
                {
                    intent.putExtra("text", text);
                    intent.putExtra("MODE", DiaryMenuFragment.IntentOption.NEW);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else
                {
                    cancelActivity();
                }
            }
            break;

            case EDIT:
            {
                String title=etitle.getText().toString();
                String text = etext.getText().toString();
                String id = data.getId();

                if(text != null && !text.isEmpty())
                {
                    Intent intent = new Intent();
                    intent.putExtra("title",title);
                    intent.putExtra("text", text);
                    intent.putExtra("id", id);
                    intent.putExtra("MODE", DiaryMenuFragment.IntentOption.EDIT);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else//Text is empty, delete it instead of overwrite to empty text
                {
                    finishIntentWithDelete();
                }
            }
            break;

            default:
                cancelActivity();
                break;
        }
    }

    public void saveButtonClick(View v)
    {
        if(mode != DiaryMenuFragment.IntentOption.NEW)
        {
            AlertDialog.Builder ab = new AlertDialog.Builder(DiaryNew.this);
            View view = getLayoutInflater().inflate(R.layout.dialog_design,null);//嵌入View
            Button d_confirm=view.findViewById(R.id.dialog_confirm);
            Button d_cancel=view.findViewById(R.id.dialog_cancel);
            TextView dialog_title=view.findViewById(R.id.dialog_title);
            TextView dialog_message=view.findViewById(R.id.dialog_message);
            ab.setView(view);
            AlertDialog dialog=ab.create();
            dialog_title.setText("確定要重寫嗎?");
            dialog_message.setText("原先的日記內容將會消失");
            d_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dialogOption == DiaryMenuFragment.IntentOption.DELETE)
                    {
                        finishIntentWithDelete();
                    }
                    else if(dialogOption == DiaryMenuFragment.IntentOption.CANCEL)
                    {
                        cancelActivity();
                    }
                    else
                    {
                        reactToUser();
                    }
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
        }
        else
        {
            reactToUser();
        }
    }

    private void finishIntentWithDelete()
    {
        Intent intent = new Intent();
        intent.putExtra("title", data.getTitle());
        intent.putExtra("text", data.getText());
        intent.putExtra("id", data.getId());
        intent.putExtra("MODE", DiaryMenuFragment.IntentOption.DELETE);
        setResult(RESULT_OK, intent);
        finish();
    }

}