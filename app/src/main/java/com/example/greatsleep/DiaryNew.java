package com.example.greatsleep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DiaryNew extends AppCompatActivity implements DialogClickListener{
    private EditText etext;
    private Button button;
    private DiaryMenu.IntentOption mode;
    private Diary data;
    private DiaryMenu.IntentOption dialogOption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_new);

        SharedPreferences shr = PreferenceManager.getDefaultSharedPreferences(DiaryMenu.getAppContext());
        float textSize = Float.parseFloat(shr.getString("editor_text", "12"));

        etext = (EditText)findViewById(R.id.diary_edit);
        etext.setTextSize(textSize);


        button = (Button) findViewById(R.id.save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButtonClick(button);
            }
        });

        Intent intent = getIntent();
        mode = (DiaryMenu.IntentOption) intent.getSerializableExtra("MODE");

        switch(mode)
        {
            case NEW:
                break;
            case EDIT:
                String text = intent.getStringExtra("text");
                String id = intent.getStringExtra("id");
                data = new Diary(text,id);
                break;
            default:
                break;
        }

        String text = getIntent().getStringExtra("text");


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
                String text = etext.getText().toString();
                if(!text.isEmpty())
                {
                    Intent intent = new Intent();
                    intent.putExtra("text", text);
                    intent.putExtra("MODE", DiaryMenu.IntentOption.NEW);
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
                String text = etext.getText().toString();
                String id = data.getId();

                if(text != null && !text.isEmpty())
                {
                    Intent intent = new Intent();
                    intent.putExtra("text", text);
                    intent.putExtra("id", id);
                    intent.putExtra("MODE", DiaryMenu.IntentOption.EDIT);
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
        if(mode != DiaryMenu.IntentOption.NEW)
        {
            DiaryCheckDialog dialog = new DiaryCheckDialog("確定要重寫嗎?", "原先的日記內容將會消失.", this, this);
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
        intent.putExtra("text", data.getText());
        intent.putExtra("id", data.getId());
        intent.putExtra("MODE", DiaryMenu.IntentOption.DELETE);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDialogCancelClick() {

    }

    public void onDialogOkClick()
    {
        if(dialogOption == DiaryMenu.IntentOption.DELETE)
        {
            finishIntentWithDelete();
        }
        else if(dialogOption == DiaryMenu.IntentOption.CANCEL)
        {
            cancelActivity();
        }
        else
        {
            reactToUser();
        }
    }
}