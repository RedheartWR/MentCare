package com.example.tanya_green.mentcare;

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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
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
    GraphView graph;
    DataSymptoms mDataSymptoms;
    String path = "data.ser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics);
        graph = findViewById(R.id.graph);
        sleepCheckBox = findViewById(R.id.sleepCheckBox);
        moodCheckBox = findViewById(R.id.moodCheckBox);
        appetiteCheckBox = findViewById(R.id.appetiteCheckBox);
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
            symptoms.put("sleep", 1);
            symptoms.put("mood", 3);
            mDataSymptoms.AddDaySymptoms(new GregorianCalendar(), symptoms);
            symptoms.clear();
            symptoms.put("sleep", 1);
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
            ArrayList<Integer> intArray = new ArrayList<>();
            Toast.makeText(getApplicationContext(),
                    ""+graphData.size(),
                    Toast.LENGTH_SHORT).show();
            for(Map.Entry<Calendar, Integer> item : graphData.entrySet()){
                dateArray.add(item.getKey().getTime());
                intArray.add(item.getValue());
            }
            Toast.makeText(getApplicationContext(),
                    ""+dateArray.size(),
                    Toast.LENGTH_SHORT).show();
            DataPoint[] dataPoint = new DataPoint[dateArray.size()];
            dateArray.sort(new Comparator<Date>(){
                public int compare(Date d1, Date d2) {
                    return d1.compareTo(d2);
                }});

            for (int i = 0; i < dateArray.size(); i++)
                dataPoint[i] = new DataPoint(dateArray.get(i), intArray.get(i));
            Toast.makeText(getApplicationContext(),
                    "there",
                    Toast.LENGTH_SHORT).show();
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoint);
            graph.addSeries(series);
            // set date label formatter
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
            graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

            graph.getViewport().setMinX(dateArray.get(0).getTime());
            graph.getViewport().setMaxX(dateArray.get(dateArray.size() - 1).getTime());
            graph.getViewport().setXAxisBoundsManual(true);

            graph.getGridLabelRenderer().setHumanRounding(false);
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
