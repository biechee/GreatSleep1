package com.example.greatsleep;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SleepFragment extends Fragment {
    private View mainView;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_sleep, container, false);

        toolbar=mainView.findViewById(R.id.toolbar_sleep);
        toolbar.inflateMenu(R.menu.menu_example);
        toolbar.getMenu().findItem(R.id.action_cancel).setVisible(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.action_setting){
                    Intent intent=new Intent(getActivity(),SettingActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        return mainView;
    }
}