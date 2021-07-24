package com.example.greatsleep.Diaries;

import java.util.ArrayList;

public interface DiaryDB_I {

        void addData(Diary data) throws Exception;

        void editData(Diary newData) throws Exception;

        void removeData(Diary data) throws Exception;

        ArrayList<Diary> getContent() throws Exception;
    }

