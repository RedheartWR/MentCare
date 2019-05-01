package com.example.tanya_green.mentcare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.Toast;
import android.widget.SeekBar;
import java.io.*;
import java.util.*;


public class MainActivity extends AppCompatActivity {

    CalendarView mCalendarView;
    DataSymptoms mDataSymptoms;
    SeekBar sleepSeekBar, appetiteSeekBar, moodSeekBar;
    int mCurrentDay;
    int mCurrentMonth;
    int mCurrentYear;
    String path = "data.ser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCalendarView = (CalendarView)findViewById(R.id.calendar_view);

        mCalendarView.setOnDateChangeListener(new OnDateChangeListener(){// Описываем метод выбора даты в календаре:
            @Override
            public void onSelectedDayChange(CalendarView view, int year,int month, int dayOfMonth) {
                mCurrentDay = dayOfMonth;
                mCurrentMonth = month + 1;
                mCurrentYear = year;
                // При выборе любой даты отображаем Toast сообщение с данными о выбранной дате (Год, Месяц, День):
                Toast.makeText(getApplicationContext(),
                        "Год: " + year + "\n" +
                                "Месяц: " + (month + 1) + "\n" +
                                "День: " + dayOfMonth,
                        Toast.LENGTH_SHORT).show();
            }});

        sleepSeekBar = (SeekBar) findViewById(R.id.sleepSeekBar);
        appetiteSeekBar = (SeekBar) findViewById(R.id.appetiteSeekBar);
        moodSeekBar = (SeekBar) findViewById(R.id.moodSeekBar);

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("person.dat")))
        {
            mDataSymptoms = (DataSymptoms) ois.readObject();
            updateCurrentData();
        }
        catch(Exception ex){
            mDataSymptoms = new DataSymptoms();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.graphics)
        {
            Intent intent = new Intent(this, GraphicsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public void saveChanges(){
        Calendar date = new GregorianCalendar(mCurrentYear, mCurrentMonth, mCurrentDay);
        Map<String, Integer> symptoms = new HashMap<String, Integer>();
        symptoms.put("sleep", sleepSeekBar.getProgress());
        symptoms.put("appetite", appetiteSeekBar.getProgress());
        symptoms.put("mood", moodSeekBar.getProgress());
        mDataSymptoms.AddDaySymptoms(date, symptoms);
    }

    public void updateCurrentData(){
        //TODO
    }

    public void updateFile(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path)))
        {
            oos.writeObject(mDataSymptoms);
        }
        catch(Exception ex){

        }
    }
}
