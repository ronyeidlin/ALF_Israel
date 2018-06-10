package app.alf.Activities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import app.alf.R;
import app.alf.Utils.SettingsUtils;
import app.alf.Utils.SharedPrefUtils;
import app.alf.Utils.Util;
import app.alf.enums.AlarmIntervalOption;
import app.alf.enums.DaysBeforeOption;

import static android.R.attr.checked;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by rony on 22/09/2017.
 */

public class Settings extends PreferenceFragment {

    private static final String TAG = Settings.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getActivity().getApplicationContext();
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);


        ListPreference days_before_listPreference = (ListPreference) findPreference("days_before");
        days_before_listPreference.setValueIndex(SettingsUtils.getDaysBeforePosition(getApplicationContext()));
        Log.i(TAG,"setValueIndex(daysBeforeOption.getValue()): " + SettingsUtils.getDaysBeforePosition(getApplicationContext()));

        days_before_listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int checked = Integer.parseInt(newValue.toString());

                SettingsUtils.setDaysBefore(getApplicationContext(),Integer.parseInt(newValue.toString()));
                SettingsUtils.setDaysBeforePosition(getApplicationContext(),DaysBeforeOption.fromValue(Integer.parseInt(newValue.toString())).getValue());

                SettingsUtils.SetAlarmModificationWasMade(getApplicationContext(),true);
                Log.i(TAG,"DaysBeforeOption.fromValue(checked): " + DaysBeforeOption.fromValue(checked) + " index of the radioBTN: " + checked);
                Util.SetAlarmsToAllEvents(getActivity().getApplicationContext());
                return true;
            }
        });

        ListPreference repeating_intervals_listPreference = (ListPreference) findPreference("repeating_intervals");
       // AlarmIntervalOption intervaloption = SettingsUtils.getRepeatTimeOption(context);

        repeating_intervals_listPreference.setValueIndex(SettingsUtils.getIntervalPosition(getApplicationContext()));


        Log.i(TAG,"setValueIndex(AlarmIntervalOption.getValue()): " + SettingsUtils.getIntervalPosition(getApplicationContext()));

        repeating_intervals_listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {


                SettingsUtils.setInterval(getApplicationContext(),newValue.toString());
                SettingsUtils.setIntervalPosition(getApplicationContext(),AlarmIntervalOption.fromValue(newValue.toString()).getValue());
                SettingsUtils.SetAlarmModificationWasMade(getApplicationContext(),true);
                Log.i(TAG,"AlarmIntervalOption.fromValue(checked): " + checked + " index of the radioBTN: " + AlarmIntervalOption.fromValue(newValue.toString()).getValue());

                Util.SetAlarmsToAllEvents(getActivity().getApplicationContext());
                return true;
            }
        });



        Preference version_preference = (Preference) findPreference("version");





        version_preference.setTitle("Ver." + SharedPrefUtils.getString(getApplicationContext(),SharedPrefUtils.GENERAL,SharedPrefUtils.APP_VERSION));

        /*
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences. = preferences.edit();
        editor1.putString("version", SharedPrefUtils.getString(getApplicationContext(),SharedPrefUtils.GENERAL,SharedPrefUtils.APP_VERSION));
        editor.commit();*/


    }
}
