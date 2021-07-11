package com.example.greatsleep;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Diary {

        private String text;
        private String id;
        private static final int MAX_TITLE_LENGHT = 25;


    public Diary(String text, String id)
        {
            this.text = text;
            this.id = id;
        }


    public Diary(String text)
        {
            this.text = text;
            this.id = genId();
        }


        public String getText()
        {
            return text;
        }


        public String getId()
        {
            return id;
        }


        public String getText(int end)
        {
            return getText(0, end);
        }


        public String getText(int start, int end)
        {
            if(text.length() < end)
                return text;

            return text.substring(start, end);
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
            return getText(MAX_TITLE_LENGHT);
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
