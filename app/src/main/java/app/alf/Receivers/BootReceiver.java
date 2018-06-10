package app.alf.Receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import app.alf.Utils.Util;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by rony on 21/09/2017.
 */

public class BootReceiver extends WakefulBroadcastReceiver {

    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    public void onReceive(Context context, Intent intent) {
        if (ACTION.equals(intent.getAction())) {

            Util.setUpdateEventsAlarm(getApplicationContext());
            Util.SetAlarmsToAllEvents(getApplicationContext());


        }
    }

}
