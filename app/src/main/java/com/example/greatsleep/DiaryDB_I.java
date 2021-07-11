package com.example.greatsleep;

import java.util.ArrayList;

public interface DiaryDB_I {

        void addData(com.example.greatsleep.Diary data) throws Exception;

        void editData(com.example.greatsleep.Diary newData) throws Exception;

        void removeData(com.example.greatsleep.Diary data) throws Exception;

        ArrayList<com.example.greatsleep.Diary> getContent() throws Exception;
    }

