package app.alf.Utils;

import android.content.Context;



public abstract class SettingsUtils {

    static final String SETTINGS = "SETTINGS";

    public static void setDaysBefore(Context context, int interval) {
        SharedPrefUtils.setInt(context, SETTINGS, SharedPrefUtils.DAYS_BEFORE_EVENTS, interval);
    }

    public static void setDaysBeforePosition(Context context, int pos) {
        SharedPrefUtils.setInt(context, SETTINGS, SharedPrefUtils.INDEX_DAYS_BEFORE_EVENTS, pos);
    }


    public static int getDaysBefore(Context context) {
        return SharedPrefUtils.getInt(context, SETTINGS, SharedPrefUtils.DAYS_BEFORE_EVENTS);
    }

    public static int getDaysBeforePosition(Context context) {
        return SharedPrefUtils.getInt(context, SETTINGS, SharedPrefUtils.INDEX_DAYS_BEFORE_EVENTS, 0);
    }






    public static void setInterval(Context context, String interval) {
        SharedPrefUtils.setString(context, SETTINGS, SharedPrefUtils.INTERVAL_OF_REPEATING_ALERTS, interval);
    }

    public static void setIntervalPosition(Context context, int pos) {
        SharedPrefUtils.setInt(context, SETTINGS, SharedPrefUtils.INDEX_INTERVAL_OF_REPEATING_ALERTS, pos);
    }


    public static String getInterval(Context context) {
        return SharedPrefUtils.getString(context, SETTINGS, SharedPrefUtils.INTERVAL_OF_REPEATING_ALERTS);
    }

    public static int getIntervalPosition(Context context) {
        return SharedPrefUtils.getInt(context, SETTINGS, SharedPrefUtils.INDEX_INTERVAL_OF_REPEATING_ALERTS, 0);
    }




    public static void SetAlarmModificationWasMade(Context context, boolean flag) {
         SharedPrefUtils.setBoolean(context, SETTINGS, SharedPrefUtils.ALARM_MODIFICATION_WAS_MADE,flag);
    }

    public static boolean AlarmModificationWasMade(Context context) {
        return SharedPrefUtils.getBoolean(context, SETTINGS, SharedPrefUtils.ALARM_MODIFICATION_WAS_MADE);
    }





}
