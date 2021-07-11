package com.example.greatsleep;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.fragment.app.DialogFragment;

public class DiaryCheckDialog extends DialogFragment {
    private android.app.AlertDialog.Builder builder;
    private com.example.greatsleep.DialogClickListener listener;


    /**
     * Creates new dialog
     * @param listener
     */
    public DiaryCheckDialog(String title, String msg, Activity activity, com.example.greatsleep.DialogClickListener listener)
    {
        this.listener=listener;
        setDialog(title, msg, activity);
    }

    /**
     * Sets dialog
     * @param title Title of the dialog
     * @param msg Message for user
     * @param activity Current activity in which you want to display dialog
     */
    public void setDialog(String title, String msg, Activity activity)
    {
        builder = new android.app.AlertDialog.Builder(activity);
        builder.setTitle(title);//Sets title for the dialog

        //Sets messages of buttons
        builder.setMessage(msg).setPositiveButton("確定", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                listener.onDialogOkClick();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                listener.onDialogCancelClick();
            }
        });

        builder.create();
    }

    /**
     * Shows dialog
     */
    public void show()
    {
        builder.show();
    }




}
