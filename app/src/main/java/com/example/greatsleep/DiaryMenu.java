package com.example.greatsleep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DiaryMenu extends AppCompatActivity implements RecycleItemOnClickListener{
    private static Context context;
    private DiaryAdapter diaryAdapter;
    private Button addbutton;
    private Diary diary;
    private RecyclerView recyclerView;


    public enum IntentOption
    {
        NEW, EDIT, DELETE, CANCEL
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_menu);

        DiaryMenu.context = getApplicationContext();

        addbutton = (Button) findViewById(R.id.add_button);
        recyclerView = (RecyclerView)findViewById(R.id.diary_list);

        try{
            diaryAdapter = new DiaryAdapter(
                    DiaryMenu.getAppContext(),
                    new LinearLayoutManager(this),(RecyclerView) findViewById(R.id.diary_list));
        }
        catch(Exception e)
        {
            showError("", e.getMessage(), this);
        }

        diaryAdapter.setClickListener(this);
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newButtonClick(addbutton);
            }
        });
    }

    /**
     * Reacts on new button click and creates new activity
     * @param v
     */
    public void newButtonClick(View v)
    {
        Intent intent = new Intent(DiaryMenu.this, DiaryNew.class);
        intent.putExtra("MODE", IntentOption.NEW);
        this.startActivityForResult(intent, 1);
    }

    /**
     * Method reacts to activity result, according to this result it adds, deletes or edits data.
     * @param requestCode
     * @param resultCode
     * @param data result of the activity: modes: 'new', 'edit', 'delete'
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                IntentOption mode = (IntentOption) data.getSerializableExtra("MODE");

                if(mode == IntentOption.NEW)
                {
                    try
                    {
                        String title=data.getStringExtra("title");
                        String text = data.getStringExtra("text");
                        diaryAdapter.addData(new Diary(text,title));
                        Log.v("diary",title+"  3  "+text );
                    }
                    catch(Exception e)
                    {
                        showError("", e.getMessage(), this);
                    }
                }
                else if(mode == IntentOption.EDIT)
                {
                    String title=data.getStringExtra("title");
                    String text = data.getStringExtra("text");
                    String id = data.getStringExtra("id");

                    try
                    {
                        diaryAdapter.editData(new Diary(text, id, title));
                        Log.v("diary",title+"  4");
                    }
                    catch(Exception e)
                    {
                        showError("", e.getMessage(), this);
                    }
                }
                else if(mode == IntentOption.DELETE)
                {
                    String title=data.getStringExtra("title");
                    String text = data.getStringExtra("text");
                    String id = data.getStringExtra("id");

                    try
                    {
                        diaryAdapter.removeData(new Diary(text, id,title));
                        Log.v("diary",title+"  5");
                    }
                    catch(Exception e)
                    {
                        showError("", e.getMessage(), this);
                    }
                }
            }

            finish();
            startActivity(getIntent());
        }
    }


    /**
     * Returns App Context
     * @return
     */
    public static Context getAppContext()
    {
        return DiaryMenu.context;
    }

    /**
     * Reacts on item click, passes data to new Activity by Intent, mode -> 'edit' extras -> 'text', 'id'
     * @param v
     * @param position
     */
    @Override
    public void onItemClick(View v, int position)
    {
        Diary data = diaryAdapter.getData(position);

        Intent intent = new Intent(DiaryMenu.this, DiaryNew.class);
        intent.putExtra("title",data.getTitle());
        intent.putExtra("text", data.getText());
        intent.putExtra("id", data.getId());
        intent.putExtra("MODE", IntentOption.EDIT);
        this.startActivityForResult(intent, 1);
    }

    @Override
    public void onItemLongClick(View v, int position) {
        Diary data = diaryAdapter.getData(position);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("確定要刪除嗎?");
        dialog.setMessage("日記的內容將會消失");
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try
                {
                    diaryAdapter.removeData(data);
                }
                catch(Exception e)
                {
                    showError("", e.getMessage(),DiaryMenu.this);
                }
                diaryAdapter.notifyDataSetChanged();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.create().show();


    }



    public static void showError(String title, String msg, Activity activity)
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(msg).setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
            }
        });
        builder.create().show();
    }

}