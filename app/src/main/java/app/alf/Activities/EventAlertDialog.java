package app.alf.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Window;

import app.alf.Receivers.EventAlarmReceiver;
import app.alf.Utils.Util;

/**
 * Created by rony on 21/09/2017.
 */

public class EventAlertDialog extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Intent intent = getIntent();
        String name = (String) intent.getSerializableExtra(Util.NAME);
        String place = (String) intent.getSerializableExtra(Util.PLACE);
        String start_time = (String) intent.getSerializableExtra(Util.START_TIME);
        final String keyID = (String) intent.getSerializableExtra(Util.KEY_ID);

        if (name.isEmpty() && start_time.isEmpty())
            return;

        builder.setMessage(place + " :  "+ start_time )
                .setTitle(name);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
       /* builder.setNegativeButton("Don't Show Again", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(keyID), intent, 0);
                AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pi);
            }
        });*/

        AlertDialog dialog = builder.create();
        //dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);

        dialog.show();

        // Vibrate the mobile phone
        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);

        EventAlarmReceiver.completeWakefulIntent(intent);
    }
}
