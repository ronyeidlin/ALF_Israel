package app.alf.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rony on 27/02/2016.
 */
public enum AlarmIntervalOption {

    EMPTY(-1),
    Dont_repeat_the_event_alarms(0),
    Every_30_min_from_first_Alarm(1),
    Every_hour_from_first_Alarm(2),
    Every_half_day_from_first_Alarm(3),
    Every_day_from_first_Alarm(4),
    Every_week_from_first_Alarm(5),
    Every_month_from_first_Alarm(6);

    int value;
    static Map<String, AlarmIntervalOption> value2Enum = new HashMap<String, AlarmIntervalOption>() {{
        put("-1", EMPTY);
        put("0", Dont_repeat_the_event_alarms);
        put("1800000", Every_30_min_from_first_Alarm);
        put("3600000", Every_hour_from_first_Alarm);
        put("43200000", Every_half_day_from_first_Alarm);
        put("86400000", Every_day_from_first_Alarm);
        put("604800000",Every_week_from_first_Alarm);
        put("2419200000", Every_month_from_first_Alarm);
    }};

    AlarmIntervalOption(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static String getValueFromEnum(AlarmIntervalOption interval) {

        for (Map.Entry<String, AlarmIntervalOption> e : value2Enum.entrySet()) {
            if (e.getValue().equals(interval))
                return e.getKey();
        }
        return "0";
    }

    public static AlarmIntervalOption fromValue(String val) {
        return value2Enum.get(val);
    }
}
