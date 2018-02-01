package com.repina.anastasia.momandbaby.Helpers;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GoogleFitDataParser {

    public static ArrayList<Pair<DataType, Pair<String, String>>> parseData(DataSet dataSet, DataType type, FragmentActivity activity) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();
        ArrayList<Pair<DataType, Pair<String, String>>> parsedData = new ArrayList<>();
        for (DataPoint dp : dataSet.getDataPoints()) {
            String startDate = dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
            String endDate = dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS));
            if (startDate.equals(endDate)) {
                String startTime = timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
                String endTime = timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));
                Log.e("History", "\tStart: " + startDate + " " + startTime);
                Log.e("History", "\tEnd: " + endDate + " " + endTime);
                double doubleValue = 0;
                String stringValue = "";
                if (type.equals(DataType.TYPE_ACTIVITY_SEGMENT)) {
                    //todo sleep
                    Log.e("History", "\tField: " + activity + " Value: " + doubleValue);
                } else {
                    Field field = dp.getDataType().getFields().get(0);
                    Log.e("History", "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                    if(field.getName().equals("nutrients")) // nutrition
                        stringValue = dp.getValue(field).toString().replace("{", "").replace("}", "");
                    else // steps, calories, weight
                        doubleValue = Double.parseDouble(dp.getValue(field).toString());
                }
                if(stringValue.length() == 0) stringValue = String.valueOf(doubleValue);
                Pair<String, String> newDataEntry = new Pair<>(startDate, stringValue);
                Pair<DataType, Pair<String, String>> entry = new Pair<>(type, newDataEntry);
                parsedData.add(entry);
            }
        }
        return parsedData;
    }
}
