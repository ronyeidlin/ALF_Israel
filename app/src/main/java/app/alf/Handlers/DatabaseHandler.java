package app.alf.Handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.alf.Objects.FacebookALFIsraelEvent;
import app.alf.Utils.Util;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    private final String TAG = DatabaseHandler.class.getSimpleName();
    // Database Name
    private static final String DATABASE_NAME = "ALFDATABASE";

    // Contacts table name
    private static final String TABLE_ALF_ISRAEL_EVENTS = "alfIsraelEventsTable";

    // Contacts Table Columns names

    private static final String KEY_ID = "id";
    private static final String description = Util.DESCRIPTION;
    private static final String end_time = Util.END_TIME;
    private static final String name = Util.NAME;
    private static final String place = Util.PLACE;
    private static final String start_time = Util.START_TIME;
    private static final String eventId = "eventId";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALF_EVENTS_TABLE = "CREATE TABLE " + TABLE_ALF_ISRAEL_EVENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + description + " TEXT," + end_time + " TEXT,"
                + name + " TEXT,"  + place + " TEXT,"  + start_time + " TEXT," + eventId + " TEXT" + ")";
        db.execSQL(CREATE_ALF_EVENTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALF_ISRAEL_EVENTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    public void addAllEventsToDB(List<FacebookALFIsraelEvent> eventList) {

        SQLiteDatabase db = this.getWritableDatabase();

        if (eventList.size() > 0)
        {
            //db.execSQL("delete from "+ TABLE_ALF_ISRAEL_EVENTS);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_ALF_ISRAEL_EVENTS);
            onCreate(db);

        }



        for (int i=0; i<eventList.size(); i++) {

            FacebookALFIsraelEvent event = eventList.get(i);

            ContentValues values = new ContentValues();

            values.put(description, event.description);
            values.put(end_time, event.end_time);
            values.put(name, event.name);

            if (event.place != null)
                 values.put(place, event.place.toString());

            values.put(start_time, event.start_time);
            values.put(eventId, event.id);

            // Inserting Row
            db.insert(TABLE_ALF_ISRAEL_EVENTS, null, values);

        }

        db.close(); // Closing database connection
    }

    // Getting single contact
    public FacebookALFIsraelEvent getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ALF_ISRAEL_EVENTS, new String[] { KEY_ID,
                        description, end_time,name ,place ,start_time , eventId , }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        FacebookALFIsraelEvent event = new FacebookALFIsraelEvent();

        event.keyID =Integer.parseInt(cursor.getString(0));
        event.description = cursor.getString(1);
        event.end_time = cursor.getString(2);
        event.name = cursor.getString(3);

        try {

            JSONObject obj = new JSONObject(cursor.getString(4));
            event.place = obj;
            Log.d(TAG, obj.toString());

        } catch (Throwable t) {
            Log.e(TAG, "Could not parse malformed JSON: \"" + cursor.getString(4) + "\"");
        }

        event.start_time = cursor.getString(5);
        event.id = cursor.getString(6);

        // return contact
        return event;
    }

    // Getting All Contacts
    public List<FacebookALFIsraelEvent> getAllEvents() {
        List<FacebookALFIsraelEvent> eventsList = new ArrayList<FacebookALFIsraelEvent>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ALF_ISRAEL_EVENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FacebookALFIsraelEvent event = new FacebookALFIsraelEvent();

                event.keyID =Integer.parseInt(cursor.getString(0));
                event.description = cursor.getString(1);
                event.end_time = cursor.getString(2);
                event.name = cursor.getString(3);

                try {

                    JSONObject obj = new JSONObject(cursor.getString(4));
                    event.place = obj;
                    Log.d(TAG, obj.toString());

                } catch (Throwable t) {
                    Log.e(TAG, "Could not parse malformed JSON: \"" + cursor.getString(4) + "\"");
                }

                event.start_time = cursor.getString(5);
                event.id = cursor.getString(6);

                eventsList.add(event);
            } while (cursor.moveToNext());
        }

        // return contact list
        return eventsList;
    }

    // Updating single contact
    public int updateContact(FacebookALFIsraelEvent event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(description, event.description);
        values.put(end_time, event.end_time);
        values.put(name, event.name);
        values.put(place, event.place.toString());
        values.put(start_time, event.start_time);
        values.put(eventId, event.id);

        // updating row
        return db.update(TABLE_ALF_ISRAEL_EVENTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(event.keyID) });
    }

    // Deleting single contact
    public void deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALF_ISRAEL_EVENTS, null,null  );
        db.close();
    }


}