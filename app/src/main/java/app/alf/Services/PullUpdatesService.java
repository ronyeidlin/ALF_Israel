package app.alf.Services;

import android.app.IntentService;
import android.content.Intent;

import app.alf.Receivers.AlarmReceiver;
import app.alf.Utils.Util;

/**
 * Created by rony on 21/09/2017.
 */

public class PullUpdatesService extends IntentService{

    private final String TAG = PullUpdatesService.class.getSimpleName();

    public PullUpdatesService(String name) {
        super(name);
    }

    public PullUpdatesService() {
        super("PullUpdatesService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            Util.UpdateDBEventsTable();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AlarmReceiver.completeWakefulIntent(intent);
        }

    }
}
