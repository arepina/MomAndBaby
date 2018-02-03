package com.repina.anastasia.momandbaby.Helpers;

public class LocalConstants {
    public static final String SERVICE_REQUEST_TYPE = "requestType";

    public static final int TYPE_REQUEST_CONNECTION = 1;
    public static final int TYPE_GET_STEP_TODAY_DATA = 2;
    public static final int TYPE_GET_SLEEP_TODAY_DATA = 3;
    public static final int TYPE_GET_WEIGHT_TODAY_DATA = 4;
    public static final int TYPE_GET_CALORIES_TODAY_DATA = 5;
    public static final int TYPE_GET_NUTRITION_TODAY_DATA = 6;

    public static final String HISTORY_INTENT = "fitHistory";
    public static final String HISTORY_EXTRA_STEPS_TODAY = "stepsToday";
    public static final String HISTORY_EXTRA_CALORIES_TODAY = "caloriesToday";
    public static final String HISTORY_EXTRA_WEIGHT_TODAY = "weightToday";
    public static final String HISTORY_EXTRA_NUTRITION_TODAY = "nutritionToday";
    public static final String HISTORY_EXTRA_SLEEP_TODAY = "sleepToday";
    public static final String HISTORY_EXTRA_AGGREGATED = "aggregated";
    public static final String HISTORY_DATE = "date";

    public static final String FIT_NOTIFY_INTENT = "fitStatusUpdateIntent";
    public static final String FIT_EXTRA_CONNECTION_MESSAGE = "fitFirstConnection";
    public static final String FIT_EXTRA_NOTIFY_FAILED_STATUS_CODE = "fitExtraFailedStatusCode";
    public static final String FIT_EXTRA_NOTIFY_FAILED_INTENT = "fitExtraFailedIntent";

    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String CALLING = "calling";
    static final String CHART = "Activity.ChartActivity";
    public static final String TABS = "Activity.TabsActivity";
}
