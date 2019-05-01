package com.example.tanya_green.mentcare;
import java.util.*;

public class DataSymptoms {

    private Map<Calendar, Map<String, Integer>> data;

    public DataSymptoms(){
        data = new HashMap<Calendar, Map<String, Integer>>();
    }

    public void AddDaySymptoms(Calendar date, Map<String, Integer> symptoms) {
        if (data.containsKey(date)) data.remove(date);
        data.put(date, symptoms);
    }

    public Map<String, Integer> GetDaySymptoms(Calendar date){
        return data.get(date);
    }

    public Map<Calendar, Integer> GetSymptomStatistic(final String symptom){
        final Map<Calendar, Integer> statistics = new HashMap<Calendar, Integer>();
        for(Map.Entry entry: data.entrySet()) {
            Map<String, Integer> symptoms = (Map<String, Integer>) entry.getValue();
            statistics.put((Calendar)entry.getKey(), symptoms.get(symptom));
        }
        return statistics;
    }
}