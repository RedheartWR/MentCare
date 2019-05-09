package com.example.tanya_green.mentcare;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;


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

    DataSymptoms mDataSymptoms;
    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/" + "data.ser";
    GraphView sleepGraph, moodGraph, appetiteGraph;
    TextView sleepText, moodText, appetiteText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics);
        sleepGraph = findViewById(R.id.sleepGraph);
        moodGraph = findViewById(R.id.moodGraph);
        appetiteGraph = findViewById(R.id.appetiteGraph);
        sleepText = findViewById(R.id.sleepText);
        moodText = findViewById(R.id.moodText);
        appetiteText = findViewById(R.id.appetiteText);
        try
        {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            mDataSymptoms = (DataSymptoms) ois.readObject();
            Toast.makeText(getApplicationContext(),
                    "Data was read",
                    Toast.LENGTH_SHORT).show();
            ois.close();;
        }
        catch(Exception ex){
            mDataSymptoms = new DataSymptoms();
            HashMap<String, Integer> symptoms = new HashMap<String, Integer>();
            symptoms.put("sleep", 4);
            symptoms.put("mood", 3);
            mDataSymptoms.AddDaySymptoms(new GregorianCalendar(), symptoms);
            HashMap<String, Integer> symptoms2 = new HashMap<String, Integer>();
            symptoms2.put("sleep", 10);
            mDataSymptoms.AddDaySymptoms(new GregorianCalendar(2019, 4, 3), symptoms2);
        }
        finally {
            makeGraph(sleepGraph, "sleep", sleepText);
            makeGraph(moodGraph, "mood", moodText);
            makeGraph(appetiteGraph, "appetite", appetiteText);
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

    public void makeGraph(GraphView graph, String symptom, TextView text){
        try {
            Map<Calendar, Integer> graphData = mDataSymptoms.GetSymptomStatistic(symptom);
        if (graphData.isEmpty()) return;
        ArrayList<Integer> formateDates = new ArrayList<>();
        ArrayList<Date> dateArray = new ArrayList<Date>();
        for(Map.Entry<Calendar, Integer> item : graphData.entrySet()){
            Calendar date = (Calendar) item.getKey();
            if (item.getValue() == null) continue;
            dateArray.add(item.getKey().getTime());
            formateDates.add(date.get(Calendar.YEAR)*10000 + (date.get(Calendar.MONTH) + 1)*100 +
                    date.get(Calendar.DAY_OF_MONTH));
        }
            Toast.makeText(getApplicationContext(),
                    "state: " + symptom + " " + dateArray.size(),
                    Toast.LENGTH_SHORT).show();
        if (dateArray.size() < 2){
            graph.setVisibility(View.INVISIBLE);
            String str = text.getText() + ": НЕТ ДАННЫХ";
            text.setText(str);
        }
        else
            graph.setVisibility(View.VISIBLE);
        formateDates.sort(new Comparator<Integer>(){
            public int compare(Integer d1, Integer d2) {
                return d1.compareTo(d2);
            }});
        dateArray.sort(new Comparator<Date>(){
            public int compare(Date d1, Date d2) {
                return d1.compareTo(d2);
            }});
        DataPoint[] dataPoints = new DataPoint[dateArray.size()];
        for (int i = 0; i < dataPoints.length; i++){
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(dateArray.get(i));
            dataPoints[i] = new DataPoint(formateDates.get(i), graphData.get(calendar));
        }
            Toast.makeText(getApplicationContext(),
                    "state2: " + symptom,
                    Toast.LENGTH_SHORT).show();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);

        // activate horizontal zooming and scrolling
        graph.getViewport().setScalable(true);

        graph.getViewport().setScrollable(true);

        graph.getViewport().setScalableY(true);

        graph.getViewport().setScrollableY(true);

        graph.getViewport().setXAxisBoundsManual(true);
        if (formateDates.size() > 1){
            graph.getViewport().setMinX(formateDates.get(0));
            graph.getViewport().setMaxX(formateDates.get(formateDates.size() - 1));
        }

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(10);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX){
                if (isValueX){
                    int intValue = (int) value;
                    int day = intValue % 100;
                    intValue /= 100;
                    int month = intValue % 100;
                    intValue /= 100;
                    int year = intValue;
                    return "" + day + "/" + month + "/" + year;
                }
                else
                    return super.formatLabel(value, isValueX);
            }
        });

        graph.addSeries(series);

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
        } catch (Exception ex){
            Toast.makeText(getApplicationContext(),
                    ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }
    /*public void addSeries(String symptom){

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

            graph.getViewport().setScrollable(true);

            graph.getViewport().setScalableY(true);

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
    }*/
}
