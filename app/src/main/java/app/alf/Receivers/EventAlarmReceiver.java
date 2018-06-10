package app.alf.Receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import app.alf.Activities.EventInfoActivity;
import app.alf.R;
import app.alf.Utils.Util;

/**
 * Created by rony on 21/09/2017.
 */

public class EventAlarmReceiver extends WakefulBroadcastReceiver {


    public void onReceive(Context context, Intent intent) {

        String name = (String) intent.getSerializableExtra(Util.NAME);
        String place = (String) intent.getSerializableExtra(Util.PLACE);
        String city = (String) intent.getSerializableExtra(Util.CITY);
        String street = (String) intent.getSerializableExtra(Util.STREET);
        String start_time = (String) intent.getSerializableExtra(Util.START_TIME);
        String description = (String) intent.getSerializableExtra(Util.DESCRIPTION);
        String end_time = (String) intent.getSerializableExtra(Util.END_TIME);
        String eventID = (String) intent.getSerializableExtra(Util.EVENT_ID);
        final String keyID = (String) intent.getSerializableExtra(Util.KEY_ID);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                //example for large icon
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(name)
                .setContentText(place + " " + start_time)
                .setOngoing(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        Intent i = new Intent(context, EventInfoActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(Util.NAME,name);
        i.putExtra(Util.PLACE,place);
        i.putExtra(Util.START_TIME,start_time);
        i.putExtra(Util.KEY_ID,keyID);
        i.putExtra(Util.END_TIME,end_time);
        i.putExtra(Util.DESCRIPTION,description);
        i.putExtra(Util.EVENT_ID,eventID);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        Integer.parseInt(keyID),
                        i,
                        PendingIntent.FLAG_ONE_SHOT
                );
        // example for blinking LED

        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        mBuilder.setLights(Color.BLUE, 3000, 3000);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setContentIntent(pendingIntent);
        mNotifyMgr.notify(Integer.parseInt(keyID), mBuilder.build());

    }

}
