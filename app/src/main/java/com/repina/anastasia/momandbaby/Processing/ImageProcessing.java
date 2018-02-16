package com.repina.anastasia.momandbaby.Processing;

import com.google.android.gms.fitness.data.DataType;
import com.repina.anastasia.momandbaby.DataBase.DatabaseNames;
import com.repina.anastasia.momandbaby.R;

import java.util.HashMap;

/**
 * Image processing
 */
public class ImageProcessing {

    /**
     * Get image id
     *
     * @param name  image name
     * @param value values
     * @return image id
     */
    public static int getImageId(String name, HashMap<String, String> value) {
        if (name.equals(DatabaseNames.METRICS)) {
            if ("0".equals(String.valueOf(value.get("weight"))))
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
        if (name.equals(DatabaseNames.TEETH))
            return R.mipmap.teeth;
        return -1;
    }

    /**
     * Get image name
     *
     * @param name  image name
     * @param value values
     * @return image name
     */
    public static String getImageName(String name, HashMap<String, String> value) {
        if (name.equals(DatabaseNames.METRICS)) {
            if ("0".equals(String.valueOf(value.get("weight"))))
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
        if (name.equals(DatabaseNames.TEETH))
            return "R.mipmap.teeth";
        return "";
    }

    /**
     * Type to string
     *
     * @param type fit data type
     * @return type name
     */
    static String typeToString(DataType type) {
        if (type.equals(DataType.TYPE_STEP_COUNT_DELTA))
            return "Шаги";
        if (type.equals(DataType.TYPE_CALORIES_EXPENDED))
            return "Калории";
        if (type.equals(DataType.TYPE_NUTRITION))
            return "Питание";
        if (type.equals(DataType.TYPE_ACTIVITY_SEGMENT))
            return "Сон";
        if (type.equals(DataType.TYPE_WEIGHT))
            return "Вес";
        return type.toString();
    }
}
