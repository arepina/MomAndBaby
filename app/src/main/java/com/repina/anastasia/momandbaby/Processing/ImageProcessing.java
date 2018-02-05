package com.repina.anastasia.momandbaby.Processing;

import android.renderscript.Element;

import com.google.android.gms.fitness.data.DataType;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.R;

import java.util.HashMap;

public class ImageProcessing {
    public static int getImageId(String name, HashMap<String, String> value) {
        if (name.equals(DatabaseNames.METRICS))
        {
            if("0".equals(String.valueOf(value.get("weight"))))
                return R.mipmap.height;
            else
                return R.mipmap.weight;
        }
        if (name.equals(DatabaseNames.STOOL))
            return R.mipmap.diapers;
        if (name.equals(DatabaseNames.VACCINATION))
            return R.mipmap.vaccination;
        if (name.equals(DatabaseNames.ILLNESS))
            return R.mipmap.illness;
        if (name.equals(DatabaseNames.FOOD))
            return R.mipmap.food;
        if (name.equals(DatabaseNames.OUTDOOR))
            return R.mipmap.outdoor;
        if (name.equals(DatabaseNames.SLEEP))
            return R.mipmap.sleep;
        if (name.equals(DatabaseNames.OTHER))
            return R.mipmap.other;
        return -1;
    }

    public static String getImageName(String name, HashMap<String, String> value) {
        if (name.equals(DatabaseNames.METRICS))
        {
            if("0".equals(String.valueOf(value.get("weight"))))
                return "R.mipmap.height";
            else
                return "R.mipmap.weight";
        }
        if (name.equals(DatabaseNames.STOOL))
            return "R.mipmap.diapers";
        if (name.equals(DatabaseNames.VACCINATION))
            return "R.mipmap.vaccination";
        if (name.equals(DatabaseNames.ILLNESS))
            return "R.mipmap.illness";
        if (name.equals(DatabaseNames.FOOD))
            return "R.mipmap.food";
        if (name.equals(DatabaseNames.OUTDOOR))
            return "R.mipmap.outdoor";
        if (name.equals(DatabaseNames.SLEEP))
            return "R.mipmap.sleep";
        if (name.equals(DatabaseNames.OTHER))
            return "R.mipmap.other";
        return "";
    }

    public static String imageToString(String imageName) {
        switch (imageName) {
            case "R.mipmap.height":
                return "Рост";
            case "R.mipmap.weight":
                return "Вес";
            case "R.mipmap.diapers":
                return "Стул";
            case "R.mipmap.vaccination":
                return "Прививки";
            case "R.mipmap.illness":
                return "Болезни";
            case "R.mipmap.food":
                return "Питание";
            case "R.mipmap.outdoor":
                return "Прогулки";
            case "R.mipmap.sleep":
                return "Сон";
            case "R.mipmap.other":
                return "Другое";
            case "R.mipmap.steps":
                return "Шаги";
            case "R.mipmap.calories":
                return "Калории";
            case "R.mipmap.nutrition":
                return "Питание";
            case "R.mipmap.rest":
                return "Сон";
            default:
                return "";
        }
    }

    static String typeToString(DataType type) {
        if(type.equals(DataType.TYPE_STEP_COUNT_DELTA))
                return "Шаги";
        if(type.equals(DataType.TYPE_CALORIES_EXPENDED))
                return "Калории";
        if(type.equals(DataType.TYPE_NUTRITION))
                return "Питание";
        if(type.equals(DataType.TYPE_ACTIVITY_SEGMENT))
                return "Сон";
        if(type.equals(DataType.TYPE_WEIGHT))
            return "Вес";
         return type.toString();
    }
}
