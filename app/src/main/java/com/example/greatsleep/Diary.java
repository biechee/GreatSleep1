package com.example.greatsleep;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Diary {

    private String title;
    private String text;
    private String id;
    private static final int MAX_TITLE_LENGHT = 25;


    public Diary(String text, String id,String title)
    {
        this.text = text;
        this.id = id;
        this.title=title;
    }


    public Diary(String text,String title)
    {
        this.text = text;
        this.id = genId();
        this.title=title;
    }


    public String getText()
    {
        return text;
    }


    public String getId()
    {
        return id;
    }


    public String getTitle(int end)
    {
        return getTitle(0, end);
    }


    public String getTitle(int start, int end)
    {
        if(title.length() < end)
            return title;

        return title.substring(start, end);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text)
    {
        this.text = text;
    }


    public String getDate()
    {
        int pos = id.indexOf('#');
        return id.substring(0, pos);
    }


    @Override
    public String toString()
    {
        return getTitle(MAX_TITLE_LENGHT);
    }


    private String genId()
    {
        Random rand = new Random();
        int num = rand.nextInt(10001);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Date date = new Date();

        String atrb = dateFormat.format(date) + "#" + num;

        return atrb;
    }

}
