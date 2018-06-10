package app.alf.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.alf.Handlers.DatabaseHandler;
import app.alf.Objects.FacebookALFIsraelEvent;
import app.alf.Objects.FacebookEventPlace;
import app.alf.Objects.FacebookEventPlaceLocation;
import app.alf.Receivers.AlarmReceiver;
import app.alf.Receivers.EventAlarmReceiver;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Util {

    private static final String TAG = Util.class.getSimpleName();


    public static final String NAME = "name";
    public static final String PLACE = "place";
    public static final String CITY = "city";
    public static final String STREET = "street";
    public static final String START_TIME = "start_time";
    public static final String DESCRIPTION = "description";
    public static final String END_TIME = "end_time";
    public static final String KEY_ID = "keyID";
    public static final String EVENT_ID = "event_id";

    public static final String FACEBOOK_PAGE_ID = "447541528699077";


    public static void setUpdateEventsAlarm(Context context) {

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 9999999, intent, 0);

        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 14);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);

    }


    public static void UpdateDBEventsTable() {

        /* make the API call */
        //  GraphRequest getEventsRequest =
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+FACEBOOK_PAGE_ID+"/events",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        ArrayList<FacebookALFIsraelEvent> allALFIsraelEvents = new ArrayList<>();
                        JSONArray cast = new JSONArray();
                        try {
                        if (response !=null)
                            if (response.getJSONObject()!=null)
                            cast = response.getJSONObject().getJSONArray("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Crashlytics.log("Failed to convert facebook response to JSON in the GRAPH Request, reason: " + e.getMessage());
                            return;
                        }

                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        Log.d("Insert: ", "Inserting ..");
                        for (int i=0; i<cast.length(); i++) {

                            try {
                                JSONObject obj = cast.getJSONObject(i);
                                FacebookALFIsraelEvent event = new Gson().fromJson(obj.toString(), FacebookALFIsraelEvent.class);
                                event.start_time = returnGoodFormatDate(event.start_time);
                                event.end_time = returnGoodFormatDate(event.end_time);

                                if (event.isDateValid())
                                    allALFIsraelEvents.add(event);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (allALFIsraelEvents.size() >0)
                            db.deleteTable();

                        db.addAllEventsToDB(allALFIsraelEvents);
                        Util.SetAlarmsToAllEvents(getApplicationContext());
                    }


                }
        ).executeAsync();

    }


    public static void SetAlarmsToAllEvents(Context context){

        long interval = Long.parseLong(SettingsUtils.getInterval(context));

        DatabaseHandler db = new DatabaseHandler(context);

        // Reading all events
        Log.i(TAG, "Reading all events..");
        List<FacebookALFIsraelEvent> events = db.getAllEvents();
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long howLongBefore = SettingsUtils.getDaysBefore(getApplicationContext()) * AlarmManager.INTERVAL_DAY; //Converts 24 Hrs(1 Day) to milliseconds
        Log.i(TAG,"check settings  days before: " +howLongBefore+ " repeat each: " + interval );

        for (FacebookALFIsraelEvent cn : events) {

            Intent i = new Intent(context, EventAlarmReceiver.class);
            i.setAction(cn.keyID.toString());

            i.putExtra(Util.NAME, cn.name);

            FacebookEventPlace eventPlace = new FacebookEventPlace();
            if (cn.place != null)
                eventPlace = new Gson().fromJson(cn.place.toString(), FacebookEventPlace.class);
            String place = eventPlace.name;

            if (eventPlace.name != null)
                if (!eventPlace.name.isEmpty())
                    i.putExtra(Util.PLACE, place);

            FacebookEventPlaceLocation eventPlaceLocation = new FacebookEventPlaceLocation();
            if (eventPlace.location != null)
                eventPlaceLocation = new Gson().fromJson(eventPlace.location.toString(), FacebookEventPlaceLocation.class);

            if (eventPlaceLocation.city != null)
                if (!eventPlaceLocation.city.isEmpty())
                    i.putExtra(Util.CITY, eventPlaceLocation.city);

            if (eventPlaceLocation.street != null)
                if (!eventPlaceLocation.street.isEmpty())
                    i.putExtra(Util.STREET, eventPlaceLocation.street);

            i.putExtra(Util.START_TIME, cn.start_time);
            i.putExtra(Util.DESCRIPTION, cn.description);
            i.putExtra(Util.END_TIME, cn.end_time);
            i.putExtra(Util.KEY_ID, cn.keyID.toString());
            i.putExtra(Util.EVENT_ID, cn.id.toString());

            String[] dateForAlarm = changeToAlarmFormat(cn.start_time).split("-");
            Calendar cal = Calendar.getInstance();// this would default to now
            cal.setTimeInMillis(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();

            long currentTime = calendar.getTimeInMillis();//Returns Time in milliseconds


            if (dateForAlarm.length > 2){
                calendar.set(Calendar.MONTH, (Integer.parseInt(dateForAlarm[1]) - 1));
                calendar.set(Calendar.YEAR, Integer.parseInt(dateForAlarm[0]));
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateForAlarm[2]));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dateForAlarm[3]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(dateForAlarm[4]));

                long eventTime = calendar.getTimeInMillis();//Returns Time in milliseconds
                Date eventdate = calendar.getTime();

                boolean alarmUp = (PendingIntent.getBroadcast(context, cn.keyID,
                        i,
                        PendingIntent.FLAG_NO_CREATE) != null);

                if (eventTime > currentTime && (!alarmUp || SettingsUtils.AlarmModificationWasMade(getApplicationContext()))) {

                    cancelAlarmIfExists(context, cn.keyID, i);
                    long reminderTime = eventTime - howLongBefore;//Time in milliseconds when the alarm will shoot up & you do not need to concider month/year with this approach as time is already in milliseconds.
                    alarmIntent = PendingIntent.getBroadcast(context, cn.keyID, i, 0);
                    //Log.i(TAG, "check alarm id: " + cn.keyID + " Alarm name: " + cn.name);

                    // 2 weeks Before One Time
                    PendingIntent twoWeeksAlarmIntent;
                    twoWeeksAlarmIntent = PendingIntent.getBroadcast(context, cn.keyID * 1000, i, 0);
                    long TwoWeeksReminderTime = eventTime - (14 * AlarmManager.INTERVAL_DAY);//Time in milliseconds when the alarm will shoot up & you do not need to concider month/year with this approach as time is already in milliseconds.
                    alarmMgr.set(AlarmManager.RTC_WAKEUP, TwoWeeksReminderTime, twoWeeksAlarmIntent);

                    //set repeating alarms
                    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, reminderTime, interval, alarmIntent);
                    Calendar calforAlarm = Calendar.getInstance();
                    calforAlarm.setTimeInMillis(reminderTime);
                    Log.i(TAG, "ALERT ON id: " + cn.keyID + " Alarm " + cn.name + " Set: " + calforAlarm.getTime() + " EventTime: " + eventdate);

                } else {

                    if (alarmUp) {
                        Log.i(TAG, "ALERT OFF ALREADY ACTIVE: " + cn.keyID + " Alarm " + cn.name + " EventTime: " + eventdate);
                    } else {
                        Log.i(TAG, "ALERT OFF OLD EVENT id: " + cn.keyID + " Alarm " + cn.name + " EventTime: " + eventdate);
                    }

                }
            }
        }
        SettingsUtils.SetAlarmModificationWasMade(getApplicationContext(),false);
        //Log.i(TAG, "Checking how many alarms for " + events.size() + " Events");
        for (FacebookALFIsraelEvent cn : events) {

            Intent i = new Intent(context, EventAlarmReceiver.class);
            i.setAction(cn.keyID.toString());

          //  Log.i(TAG, "check alarm id: " + cn.keyID + " Alarm name: " + cn.name);

            boolean alarmUp = (PendingIntent.getBroadcast(context, cn.keyID,
                    i,
                    PendingIntent.FLAG_NO_CREATE) != null);

            if (alarmUp) {
                Log.i(TAG, "Alarm is active: id: " + cn.keyID + " Alarm name: " + cn.name);
            }else
                Log.i(TAG, "Alarm is INACTIVE:id: " + cn.keyID + " Alarm name: " + cn.name);
        }


    }


    public static void cancelAlarmIfExists(Context mContext,int requestCode,Intent intent){
        try {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, requestCode, intent,0);
            AlarmManager am=(AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public  static String returnGoodFormatDate(String oldDateFormat){

        SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date dateparsed = new Date();
        try {
            if (oldDateFormat!=null)
                dateparsed = incomingFormat.parse(oldDateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat outgoingFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm ", java.util.Locale.getDefault());

        return outgoingFormat.format(dateparsed);
    }


    public static String changeToAlarmFormat(String oldDateFormat){

        SimpleDateFormat incomingFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm ");
        Date dateparsed = null;
        try {
            dateparsed = incomingFormat.parse(oldDateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat outgoingFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm", java.util.Locale.getDefault());

        if (dateparsed !=null)
                return outgoingFormat.format(dateparsed);
        else
            return "";
    }









}