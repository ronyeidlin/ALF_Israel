package app.alf.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rony on 16/09/2017.
 */

public abstract class SharedPrefUtils {

    //region Shared prefs names
    public static final String GENERAL                          =   "General";
    public static final String SETTINGS                         =   "Settings";
    //endregion

    public static final Set<String> allSharedPrefs = new HashSet<>(Arrays.asList(
            GENERAL,
            SETTINGS
    ));

    //region Shared pref keys under GENERAL
    public static final String IS_FACEBOOK_LOGIN                   =   "IsFacebookLogin";
    public static final String FACEBOOK_TOKEN                      =   "FacebookToken";
    public static final String FACEBOOK_USER_ID                    =   "FacebookUserId";
    public static final String APP_VERSION                    =   "AppVersion";
    //endregion

    //endregion

    //region Shared pref keys under SETTINGS
    public static final String DAYS_BEFORE_EVENTS =   "FirstAlertDaysBeforeEvent";
    public static final String INDEX_DAYS_BEFORE_EVENTS =   "IndexFirstAlertDaysBeforeEvent";
    public static final String INTERVAL_OF_REPEATING_ALERTS =   "RepeatingIntervals";
    public static final String INDEX_INTERVAL_OF_REPEATING_ALERTS =   "IndexRepeatingIntervals";
    public static final String ALARM_MODIFICATION_WAS_MADE =   "AlarmModificationWasMade";

    //endregion

    //region Shared prefs action methods
    //region Getters
    public static int getInt(Context context, String prefsName, String key) {
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        return prefs.getInt(key, 0);
    }

    public static int getInt(Context context, String prefsName, String key, int DefaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        return prefs.getInt(key, DefaultValue);
    }

    public static Long getLong(Context context, String prefsName, String key, Long DefaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        return prefs.getLong(key, DefaultValue);
    }

    public static String getString(Context context, String prefsName, String key) {
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    public static void setStringSet(Context context, String prefsName, String key, Set<String> value){
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.putStringSet(key, value);
        edit.commit();
    }

    public static Set<String> getStringSet(Context context, String prefsName, String key){
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        Set<String> value = new HashSet<String>();
        value =  prefs.getStringSet(key, new HashSet<String>());
        return  value;
    }

    public static Boolean getBoolean(Context context, String prefsName, String key) {
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

    public static Double getDouble(Context context, String prefsName, String key) {
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        return Double.longBitsToDouble(prefs.getLong(key, 0));
    }
    //endregion

    //region Setters
    public static void setDouble(Context context, String prefsName, String key, double value) {
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        prefs.edit().putLong(key, Double.doubleToRawLongBits(value)).apply();
    }

    public static void setInt(Context context, String prefsName, String key, int value) {
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        prefs.edit().putInt(key, value).apply();
    }

    public static void setLong(Context context, String prefsName, String key, Long value) {
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        prefs.edit().putLong(key, value).apply();
    }

    public static void setString(Context context, String prefsName, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        prefs.edit().putString(key, value).apply();
    }

    public static void setBoolean(Context context, String prefsName, String key, Boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, value).apply();
    }
    //endregion

    //region Removers

    /**
     * Removes a key from shared preferences
     * @param context The application context
     * @param prefsName The shared preference name from which to remove
     * @param key The key to remove
     */
    public static void remove(Context context, String prefsName, String key) {
        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        prefs.edit().remove(key).apply();
    }

    /**
     * Removes all keys from shared preferences
     * @param context The application context
     * @param prefsName The shared preference to remove all keys from
     */
    public static void remove(Context context, String prefsName) {

        SharedPreferences prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    /**
     * Removes all keys from all shared preferences except app state
     * @param context
     */
    public static void removeAll(Context context) {

        for(String sharedPrefsName : allSharedPrefs) {
            remove(context, sharedPrefsName);
        }
    }
    //endregion
    //endregion

}
