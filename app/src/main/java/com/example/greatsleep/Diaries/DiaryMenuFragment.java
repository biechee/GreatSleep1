package com.example.greatsleep.Diaries;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.greatsleep.R;
import com.example.greatsleep.SettingActivity;

import static android.app.Activity.RESULT_OK;


public class DiaryMenuFragment extends Fragment implements RecycleItemOnClickListener {
    private static Context context;
    private DiaryAdapter diaryAdapter;
    private Button addbutton;
    private Diary diary;
    private RecyclerView recyclerView;
    View view;
    private Toolbar toolbar;
    public enum IntentOption {
        NEW, EDIT, DELETE, CANCEL
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_diary_menu, container, false);

        toolbar=view.findViewById(R.id.toolbar_diary);
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

        DiaryMenuFragment.context =getActivity().getApplicationContext();
        addbutton = (Button) view.findViewById(R.id.add_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.diary_list);

        try {
            diaryAdapter = new DiaryAdapter(
                    DiaryMenuFragment.getAppContext(),
                    new LinearLayoutManager(getContext()), (RecyclerView) view.findViewById(R.id.diary_list));

        } catch (Exception e) {
            showError("", e.getMessage(), getActivity());
        }

        diaryAdapter.setClickListener(this);
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newButtonClick(addbutton);
            }
        });

        return view;
    }
    /**
     * Reacts on new button click and creates new activity
     * @param v
     */
    public void newButtonClick(View v)
    {
        Intent intent = new Intent(getActivity(), DiaryNew.class);
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
                        showError("", e.getMessage(), getActivity());
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
                        showError("", e.getMessage(), getActivity());
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
                        showError("", e.getMessage(), getActivity());
                    }
                }
            }

        }
    }


    /**
     * Returns App Context
     * @return
     */
    public static Context getAppContext()
    {
        return DiaryMenuFragment.context;
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

        Intent intent = new Intent(getActivity(), DiaryNew.class);
        intent.putExtra("title",data.getTitle());
        intent.putExtra("text", data.getText());
        intent.putExtra("id", data.getId());
        intent.putExtra("MODE", IntentOption.EDIT);
        this.startActivityForResult(intent, 1);
    }

    @Override
    public void onItemLongClick(View v, int position) {
        Diary data = diaryAdapter.getData(position);

        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_design,null);//嵌入View
        Button d_confirm=view.findViewById(R.id.dialog_confirm);
        Button d_cancel=view.findViewById(R.id.dialog_cancel);
        TextView dialog_title=view.findViewById(R.id.dialog_title);
        TextView dialog_message=view.findViewById(R.id.dialog_message);
        ab.setView(view);
        AlertDialog dialog=ab.create();
        dialog_title.setText("確定要刪除嗎?");
        dialog_message.setText("日記的內容將會消失");
        d_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    diaryAdapter.removeData(data);
                }
                catch(Exception e)
                {
                    showError("", e.getMessage(),getActivity());
                }
                diaryAdapter.notifyDataSetChanged();
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