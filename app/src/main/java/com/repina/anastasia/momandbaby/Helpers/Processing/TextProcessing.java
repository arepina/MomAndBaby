package com.repina.anastasia.momandbaby.Helpers.Processing;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.repina.anastasia.momandbaby.Adapters.GridItem;
import com.repina.anastasia.momandbaby.Adapters.GridItemArrayAdapter;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.Helpers.ToastShow;
import com.repina.anastasia.momandbaby.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TextProcessing {

    public static String formBabyDescription(HashMap<String, String> value) {
        StringBuilder line = new StringBuilder();
        value.remove("babyId");
        value.remove("date");
        for (Map.Entry<String, String> entry : value.entrySet()) {
            String val = String.valueOf(entry.getValue());
            try {
                double number = Double.parseDouble(val);
                if (number != 0) {
                    line.append(translateWord(entry.getKey())).append(": ").append(val);
                    if (!"\n".equals(String.valueOf(line.charAt(line.length() - 1))))
                        line.append("\n");
                }
            } catch (NumberFormatException e) { // not a number
                line.append(translateWord(entry.getKey())).append(": ").append(val);
                if (!"\n".equals(String.valueOf(line.charAt(line.length() - 1))))
                    line.append("\n");
            }
        }
        line = new StringBuilder(line.substring(0, line.length() - 1));
        return line.toString();
    }

    public static void formMomReport(GridItemArrayAdapter adapter, Context context, String start, String end) {
        StringBuilder report = new StringBuilder();
        for (int i = 0; i < adapter.getCount(); i++) {
            GridItem it = adapter.getItem(i);
            report.append(ImageProcessing.imageToString(it.getItemImgName())).append(" ").append(it.getItemDate()).append(" ").append(it.getItemDesc()).append("\n");
        }
        if (report.length() == 0)
            ToastShow.show(context, context.getString(R.string.no_data));
        else
            FileProcessing.sendFile(report.toString(), context, start, end);
    }

    public static String cleanData(HashMap<String, String> map, DataSnapshot singleSnapshot) {
        String dateValue = "";
        ArrayList<String> keys = new ArrayList<>(map.keySet());
        ArrayList<String> values = new ArrayList<>(map.values());
        for (int i = 0; i < keys.size(); i++) {
            String key = String.valueOf(keys.get(i));
            String value = String.valueOf(values.get(i));
            if (key.equals("babyId"))//remove babyID data
            {
                keys.remove(i);
                values.remove(i);
                i--;
                continue;
            }
            //solve height and weight problem
            if (key.contains("weight")) {
                String[] weightArr = value.split("=");
                if (Double.parseDouble(weightArr[0].replace(",", "")) == 0) {
                    keys.remove(i);
                    values.remove(i);
                    i--;
                    continue;
                }
            }
            if (key.contains("height")) {
                String[] heightArr = value.split("=");
                if (Double.parseDouble(heightArr[0].replace(",", "")) == 0) {
                    keys.remove(i);
                    values.remove(i);
                    i--;
                    continue;
                }
            }
            if (key.contains("date")) {
                dateValue = value;
                keys.remove(i);
                values.remove(i);
                i--;
                continue;
            }

            //translate to russian
            String translation = TextProcessing.translateWord(key);
            keys.set(i, translation);
        }
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            line.append(String.valueOf(keys.get(i))).append(": ").append(String.valueOf(values.get(i))).append("; ");
        }
        line = new StringBuilder(line.toString().substring(0, line.length() - 2));
        return dbNameToString(singleSnapshot.getKey()) + " " + dateValue + "; " + line + "\n";
    }

    private static String translateWord(String word) {
        switch (word) {
            case "weight":
                return "Вес";
            case "height":
                return "Рост";
            case "howMuch":
                return "Оценка";
            case "temperature":
                return "Температура";
            case "length":
                return "Длительность";
            case "pills":
                return "Таблетки";
            case "note":
                return "Заметка";
            case "symptomes":
                return "Название и симптомы";
            case "vaccinationName":
                return "Название прививки";
            case "date":
                return "Дата";
            default:
                return word;
        }
    }

    public static String dbNameToString(String dbName) {
        switch (dbName) {
            case DatabaseNames.METRICS:
                return "Метрики";
            case DatabaseNames.STOOL:
                return "Стул";
            case DatabaseNames.VACCINATION:
                return "Прививки";
            case DatabaseNames.ILLNESS:
                return "Болезни";
            case DatabaseNames.FOOD:
                return "Питание";
            case DatabaseNames.OUTDOOR:
                return "Прогулки";
            case DatabaseNames.SLEEP:
                return "Сон";
            case DatabaseNames.OTHER:
                return "Другое";
            default:
                return "";
        }
    }
}
