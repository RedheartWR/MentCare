package com.example.tanya_green.mentcare;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;


import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;



public class GraphicsActivity extends AppCompatActivity {

    CheckBox sleepCheckBox, appetiteCheckBox, moodCheckBox;
    DataSymptoms mDataSymptoms;
    String path = "data.ser";
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    //SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics);
        sleepCheckBox = findViewById(R.id.sleepCheckBox);
        moodCheckBox = findViewById(R.id.moodCheckBox);
        appetiteCheckBox = findViewById(R.id.appetiteCheckBox);
        graph = (GraphView) findViewById(R.id.graph);
        /*graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX){
                if (isValueX)
                    return sdf.format(new Date((long)value));
                return super.formatLabel(value, isValueX);
            }
        });*/
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path)))
        {
            mDataSymptoms = (DataSymptoms) ois.readObject();
            Toast.makeText(getApplicationContext(),
                    "Data was read",
                    Toast.LENGTH_SHORT).show();
        }
        catch(Exception ex){
            mDataSymptoms = new DataSymptoms();
            HashMap<String, Integer> symptoms = new HashMap<String, Integer>();
            symptoms.put("sleep", 4);
            symptoms.put("mood", 3);
            mDataSymptoms.AddDaySymptoms(new GregorianCalendar(), symptoms);
            symptoms.clear();
            symptoms.put("sleep", 10);
            mDataSymptoms.AddDaySymptoms(new GregorianCalendar(2019, 4, 3), symptoms);
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
        if (item.getItemId() == R.id.calendar)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return true;
    }

    public void onCheckboxClicked(View view) {
        graph.removeAllSeries();
        if (sleepCheckBox.isChecked()) {
            writeGraph("sleep");
        }
        if (moodCheckBox.isChecked()) {
            writeGraph("mood");
        }
        if (appetiteCheckBox.isChecked()) {
            writeGraph("appetite");
        }
    }

    public void writeGraph(String symptom){
        try {
            Map<Calendar, Integer> graphData = mDataSymptoms.GetSymptomStatistic(symptom);
            if (graphData == null) return;
            ArrayList<Date> dateArray = new ArrayList<Date>();
            Toast.makeText(getApplicationContext(),
                    ""+graphData.size(),
                    Toast.LENGTH_SHORT).show();
            for(Map.Entry<Calendar, Integer> item : graphData.entrySet()){
                dateArray.add(item.getKey().getTime());
            }
            Toast.makeText(getApplicationContext(),
                    ""+dateArray.size(),
                    Toast.LENGTH_SHORT).show();
            dateArray.sort(new Comparator<Date>(){
                public int compare(Date d1, Date d2) {
                    return d1.compareTo(d2);
                }});
            Toast.makeText(getApplicationContext(),
                    "state1",
                    Toast.LENGTH_SHORT).show();
            DataPoint[] dataPoints = new DataPoint[dateArray.size()];
            for (int i = 0; i < dataPoints.length; i++){
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(dateArray.get(i));
                dataPoints[i] = new DataPoint(dateArray.get(i), graphData.get(calendar));
            }
            Toast.makeText(getApplicationContext(),
                    "state2",
                    Toast.LENGTH_SHORT).show();
            series = new LineGraphSeries<>(dataPoints);
            // styling series
            series.setColor(Color.GREEN);
            series.setDrawDataPoints(true);
            series.setDataPointsRadius(10);
            series.setThickness(8);

            // custom paint to make a dotted line
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
            series.setCustomPaint(paint);

            // activate horizontal zooming and scrolling
            graph.getViewport().setScalable(true);

// activate horizontal scrolling
            graph.getViewport().setScrollable(true);

// activate horizontal and vertical zooming and scrolling
            graph.getViewport().setScalableY(true);

// activate vertical scrolling
            graph.getViewport().setScrollableY(true);

            graph.addSeries(series);
            Toast.makeText(getApplicationContext(),
                    "DONE",
                    Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex){
            Toast.makeText(getApplicationContext(),
                    ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
