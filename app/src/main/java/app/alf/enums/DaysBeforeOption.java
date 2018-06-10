package app.alf.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rony on 27/02/2016.
 */
public enum DaysBeforeOption {

    EMPTY(-1),
    Dont_notify_me_before(0),
    day_1_before(1),
    day_2_before(2),
    day_3_before(3),
    day_4_before(4),
    day_5_before(5),
    day_6_before(6),
    day_7_before(7),
    day_14_before(8),
    day_30_before(9),
    ;

    int value;
    static Map<Integer, DaysBeforeOption> value2Enum = new HashMap<Integer, DaysBeforeOption>() {{
        put(-1, EMPTY);
        put(0, Dont_notify_me_before);
        put(1, day_1_before);
        put(2, day_2_before);
        put(3, day_3_before);
        put(4, day_4_before);
        put(5, day_5_before);
        put(6, day_6_before);
        put(7, day_7_before);
        put(14, day_14_before);
        put(30, day_30_before);
    }};

    DaysBeforeOption(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Integer getValueFromEnum(DaysBeforeOption interval) {

        for (Map.Entry<Integer, DaysBeforeOption> e : value2Enum.entrySet()) {
            if (e.getValue().equals(interval))
                return e.getKey();
        }
        return 0;
    }

    public static DaysBeforeOption fromValue(int val) {
        return value2Enum.get(val);
    }
}