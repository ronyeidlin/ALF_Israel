package app.alf.Services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;

public class NotifyService extends Service {

    public class ServiceBinder extends Binder
    {
        NotifyService getService()
        {
            return NotifyService.this;
        }
    }

    int task_id;
    private static final int NOTIFICATION = 123;
    public static final String INTENT_NOTIFY = "app.alf.Services.INTENT_NOTIFY";
    private NotificationManager mNM;
    SQLiteDatabase database;

    @Override
    public void onCreate() {

        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String tmp_task_brief = null;
        task_id = intent.getIntExtra("task_id", 0);

        loadDatabase();
        Cursor cursor = database.query("task_info", new String[]{"task_brief"}, "task_id=?", new String[]{task_id+""}, null, null, null);
        while(cursor.moveToNext())
        {
            tmp_task_brief = cursor.getString(0);
        }
        cursor.close();

        if(intent.getBooleanExtra(INTENT_NOTIFY, false))
            showNotification(tmp_task_brief);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    private final IBinder mBinder = new ServiceBinder();

    private void showNotification(String tmp_task_brief) {

        CharSequence title = "To Do Task Notification!!";
     //   int icon = R.drawable.e7ca62cff1c58b6709941e51825e738f;
        CharSequence text = tmp_task_brief;
        long time = System.currentTimeMillis();

     //   Notification notification = new Notification(icon, text, time);

   //     PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, TaskDetails.class), 0);

   //     notification.setLatestEventInfo(this, title, text, contentIntent);

    //    notification.flags |= Notification.FLAG_AUTO_CANCEL;

   //     mNM.notify(NOTIFICATION, notification);

        stopSelf();
    }

    void loadDatabase()
    {
        database = openOrCreateDatabase("ToDoDatabase.db",
                SQLiteDatabase.OPEN_READWRITE, null);
    }
}
