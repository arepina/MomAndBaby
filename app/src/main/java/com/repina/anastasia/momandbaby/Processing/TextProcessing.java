package com.repina.anastasia.momandbaby.Processing;

import android.content.Context;
import android.util.Pair;

import com.google.android.gms.fitness.data.DataType;
import com.google.firebase.database.DataSnapshot;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.Helpers.NotificationsShow;
import com.repina.anastasia.momandbaby.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.repina.anastasia.momandbaby.Processing.ImageProcessing.typeToString;

/**
 * Text processing
 */
public class TextProcessing {

    /**
     * Form baby item description
     *
     * @param value  values
     * @param dbName DB name
     * @return text of baby item
     */
    public static String formBabyDescription(HashMap<String, String> value, String dbName, Context context) {
        StringBuilder line = new StringBuilder();
        value.remove("babyId");
        value.remove("date");
        String lang = Locale.getDefault().getDisplayLanguage();
        for (Map.Entry<String, String> entry : value.entrySet()) {
            String val = String.valueOf(entry.getValue());
            try {
                double number = Double.parseDouble(val);
                if (number != 0) {
                    if (lang.equals(context.getString(R.string.russian))) {
                        line.append(translateWord(entry.getKey())).append(": ").append(val);
                    }else
                        line.append(entry.getKey()).append(": ").append(val);
                    if (!"\n".equals(String.valueOf(line.charAt(line.length() - 1))))
                        line.append("\n");
                }
            } catch (NumberFormatException e) { // not a number
                if (dbName.equals(DatabaseNames.TEETH)) return context.getString(R.string.new_teeth);
                if (lang.equals(context.getString(R.string.russian))) {
                    line.append(translateWord(entry.getKey())).append(": ").append(val);
                }else
                    line.append(entry.getKey()).append(": ").append(val);
                if (!"\n".equals(String.valueOf(line.charAt(line.length() - 1))))
                    line.append("\n");
            }
        }
        line = new StringBuilder(line.substring(0, line.length() - 1));
        return line.toString();
    }

    /**
     * Form mom report
     *
     * @param sumData report data
     * @param context context
     * @param start   start date
     * @param end     finish date
     */
    public static void formMomReport(ArrayList<Pair<DataType, Pair<String, String>>> sumData, Context context, String start, String end) {
        StringBuilder report = new StringBuilder();
        for (int i = 0; i < sumData.size(); i++) {
            Pair<DataType, Pair<String, String>> it = sumData.get(i);
            DataType type = it.first;
            String translatedType = typeToString(type);
            String date = it.second.first;
            StringBuilder data = new StringBuilder(it.second.second);
            String lang = Locale.getDefault().getDisplayLanguage();
            if (DataType.TYPE_NUTRITION.equals(type)) {
                StringBuilder translatedData = new StringBuilder();
                for (String item : data.toString().split(", ")) {
                    String key = item.substring(0, item.indexOf("=")).replace(" ", "");
                    if (lang.equals(context.getString(R.string.russian))) {
                        key = translateWord(key);
                    }
                    String value = item.substring(item.indexOf("=") + 1, item.length()).replace(" ", "");
                    translatedData.append(key).append("=").append(value).append(", ");
                }
                translatedData = new StringBuilder(translatedData.substring(0, translatedData.length() - 2));
                data = translatedData;
            }
            if (DataType.TYPE_ACTIVITY_SEGMENT.equals(type) && data.toString().equals("0.0")) // ignore 0 data for sleep
                continue;
            report.append(translatedType).append(" ").append(date).append(" ").append(data).append("\n");
        }
        if (report.length() == 0)
            NotificationsShow.showToast(context, context.getString(R.string.no_data));
        else
            FileProcessing.sendFile(report.toString(), context, start, end);
    }

    /**
     * Clean data
     *
     * @param map            values
     * @param singleSnapshot snapshot
     * @return cleaned text
     */
    public static String cleanData(HashMap<String, String> map, DataSnapshot singleSnapshot, Context context) {
        String dateValue = "";
        ArrayList<String> keys = new ArrayList<>(map.keySet());
        ArrayList<String> values = new ArrayList<>(map.values());
        String lang = Locale.getDefault().getDisplayLanguage();
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

            String translation = key;
            //translate to russian id needed
            if (lang.equals(context.getString(R.string.russian))) {
                translation = TextProcessing.translateWord(key);
            }
            keys.set(i, translation);
        }
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            line.append(String.valueOf(keys.get(i))).append(": ").append(String.valueOf(values.get(i))).append("; ");
        }
        line = new StringBuilder(line.toString().substring(0, line.length() - 2));
        String dbName = singleSnapshot.getKey();
        if (lang.equals(context.getString(R.string.russian))) {
            dbName = dbNameToString(singleSnapshot.getKey());
        }
        return dbName + " " + dateValue + "; " + line + "\n";
    }

    /**
     * Words translation
     *
     * @param word word
     * @return translated word
     */
    public static String translateWord(String word) {
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
            case "calcium":
                return "Кальций";
            case "calories":
                return "Калории";
            case "carbs.total":
                return "Углеводы";
            case "cholesterol":
                return "Холестерин";
            case "dietary_fiber":
                return "Пищевые волокна";
            case "fat.monounsaturated":
                return "Мононенасыщенные жиры";
            case "fat.polyunsaturated":
                return "Полиненасыщенные жиры";
            case "fat.saturated":
                return "Жиры насыщенные";
            case "fat.total":
                return "Общие жиры";
            case "fat.trans":
                return "Трансжиры";
            case "iron":
                return "Железо";
            case "potassium":
                return "Калий";
            case "protein":
                return "Протеин";
            case "sodium":
                return "Натрий";
            case "sugar":
                return "Сахар";
            case "vitamin_c":
                return "Витамин С";
            case "activity":
                return "Активность";
            case "duration":
                return "Продолжительность";
            case "num_segments":
                return "Количество сегментов";
            default:
                return word;
        }
    }

    /**
     * DB name to String text
     *
     * @param dbName DB name
     * @return String text
     */
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
