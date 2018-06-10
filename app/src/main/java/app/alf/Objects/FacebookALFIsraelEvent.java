package app.alf.Objects;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import app.alf.Utils.Util;

/**
 * Created by rony on 16/09/2017.
 */

public class FacebookALFIsraelEvent {

    public Integer keyID;
    public String description;
    public String end_time;
    public String name;
    public JSONObject place;
    public String start_time;
    public String id;


    public boolean isDateValid(/*String newEventStartingTime*/)
    {

        Calendar calendar = Calendar.getInstance();
        long currentTime=calendar.getTimeInMillis();//Returns Time in milliseconds

        Date date1 =calendar.getTime();

        Calendar calendarNew = Calendar.getInstance();
        String[] dateForAlarm= Util.changeToAlarmFormat(start_time).split("-");

        calendarNew.getTime();
        calendarNew.set(Calendar.MONTH, (Integer.parseInt(dateForAlarm[1])-1));
        calendarNew.set(Calendar.YEAR, Integer.parseInt(dateForAlarm[0]));
        calendarNew.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateForAlarm[2]));
        calendarNew.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dateForAlarm[3]));
        calendarNew.set(Calendar.MINUTE, Integer.parseInt(dateForAlarm[4]));

        Date date2 =calendarNew.getTime();
        long eventTime=calendarNew.getTimeInMillis();//Returns Time in milliseconds


        return eventTime > currentTime;

    }

}


