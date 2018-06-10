package app.alf.Receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import app.alf.Services.PullUpdatesService;

/**
 * Created by rony on 21/09/2017.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {


    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, PullUpdatesService.class);
        startWakefulService(context, service);
    }

}
