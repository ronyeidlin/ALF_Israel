package app.alf;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import app.alf.Activities.EventInfoActivity;
import app.alf.Activities.SetSettingsActivity;
import app.alf.Adapters.ALFEventsAdapter;
import app.alf.Handlers.DatabaseHandler;
import app.alf.Objects.FacebookALFIsraelEvent;
import app.alf.Objects.FacebookEventPlace;
import app.alf.Objects.FacebookEventPlaceLocation;
import app.alf.Utils.SharedPrefUtils;
import app.alf.Utils.Util;

public class ALFMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final long SPLASH_TIME_OUT = 1;
    private final String TAG = ALFMainActivity.class.getSimpleName();
    private List<FacebookALFIsraelEvent> allALFIsraelEvents;
    private ALFEventsAdapter EventsListAdapter = null;
    public static final String CUSTOM_INTENT_PULLED_ALF_EVENTS = "app.alf.custom.intent.action.ALF_FINISHED_EVENTS";
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };

        updateWithToken(AccessToken.getCurrentAccessToken());

        if (!SharedPrefUtils.getBoolean(getApplicationContext(),SharedPrefUtils.GENERAL,SharedPrefUtils.IS_FACEBOOK_LOGIN))
        {
            StartLoginActivity();
        }else
        {
             PullUpcomingEvents();
             app_hashKey();
             InitializeUI();
        }


        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;

            SharedPrefUtils.setString(getApplicationContext(),SharedPrefUtils.GENERAL,SharedPrefUtils.APP_VERSION, String.valueOf(verCode) + "."+version);


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void PullUpcomingEvents() {

        /* make the API call */
     try {
         new GraphRequest(
                 AccessToken.getCurrentAccessToken(),
                 "/" + Util.FACEBOOK_PAGE_ID + "/events",
                 null,
                 HttpMethod.GET,
                 new GraphRequest.Callback() {
                     public void onCompleted(GraphResponse response) {

                         allALFIsraelEvents = new ArrayList<>();
                         JSONArray cast = new JSONArray();
                         try {
                             if (response !=null)
                                 if (response.getJSONObject() !=null)
                                    cast = response.getJSONObject().getJSONArray("data");
                         } catch (JSONException e) {
                             e.printStackTrace();
                         }

                         DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                         Log.d("Insert: ", "Inserting ..");

                    if (cast!=null)
                         for (int i = 0; i < cast.length(); i++) {

                             try {
                                 JSONObject obj = cast.getJSONObject(i);
                                 FacebookALFIsraelEvent event = new Gson().fromJson(obj.toString(), FacebookALFIsraelEvent.class);
                                 event.start_time = Util.returnGoodFormatDate(event.start_time);
                                 event.end_time = Util.returnGoodFormatDate(event.end_time);

                                 try {
                                     if (!obj.get("place").toString().isEmpty())
                                         event.place = (JSONObject) obj.get("place");
                                 }catch (JSONException e) {
                                     e.printStackTrace();
                                 }

                                 if (event.isDateValid())
                                     allALFIsraelEvents.add(event);


                             } catch (JSONException e) {
                                 e.printStackTrace();
                             }
                         }

                         if (allALFIsraelEvents.size() >0)
                             db.deleteTable();

                         db.addAllEventsToDB(allALFIsraelEvents);
                         ListView EventsLV = (ListView) findViewById(R.id.alf_events_lv);
                         retreiveEventsAdapter();

                         EventsLV.setAdapter(EventsListAdapter);
                         EventsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                             @Override
                             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                 Intent i = new Intent(getApplicationContext(), EventInfoActivity.class);
                                 i.putExtra(Util.NAME, allALFIsraelEvents.get(position).name);

                                 FacebookEventPlace eventPlace = new FacebookEventPlace();
                                 if (allALFIsraelEvents.get(position).place != null)
                                     eventPlace = new Gson().fromJson(allALFIsraelEvents.get(position).place.toString(), FacebookEventPlace.class);
                                 String place = eventPlace.name;

                                 if (eventPlace.name != null)
                                     if (!eventPlace.name.isEmpty())
                                         i.putExtra(Util.PLACE, place);

                                 FacebookEventPlaceLocation eventPlaceLocation = new FacebookEventPlaceLocation();
                                 if (eventPlace.location != null)
                                     eventPlaceLocation = new Gson().fromJson(eventPlace.location.toString(), FacebookEventPlaceLocation.class);

                                 if (eventPlaceLocation.city != null)
                                     if (!eventPlaceLocation.city.isEmpty())
                                         i.putExtra(Util.CITY, eventPlaceLocation.city);

                                 if (eventPlaceLocation.street != null)
                                     if (!eventPlaceLocation.street.isEmpty())
                                         i.putExtra(Util.STREET, eventPlaceLocation.street);

                                 i.putExtra(Util.START_TIME, allALFIsraelEvents.get(position).start_time);
                                 i.putExtra(Util.DESCRIPTION, allALFIsraelEvents.get(position).description);
                                 i.putExtra(Util.END_TIME, allALFIsraelEvents.get(position).end_time);

                                 i.putExtra(Util.EVENT_ID, allALFIsraelEvents.get(position).id);
                                 startActivity(i);

                             }
                         });
                         EventsListAdapter.notifyDataSetChanged();
                         Util.SetAlarmsToAllEvents(getApplicationContext());
                     }


                 }
         ).executeAsync();
     }
     catch (Exception e){

         Log.e(TAG, "access to page "+" "+e.getMessage());

     }

        Util.setUpdateEventsAlarm(getApplicationContext());
    }

    private void StartLoginActivity() {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void updateWithToken(final AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    SharedPrefUtils.setString(getApplicationContext(),SharedPrefUtils.GENERAL, SharedPrefUtils.FACEBOOK_TOKEN, currentAccessToken.getToken());
                    SharedPrefUtils.setString(getApplicationContext(),SharedPrefUtils.GENERAL, SharedPrefUtils.FACEBOOK_USER_ID, currentAccessToken.getUserId());
                    SharedPrefUtils.setBoolean(getApplicationContext(),SharedPrefUtils.GENERAL, SharedPrefUtils.IS_FACEBOOK_LOGIN, true);


                    Log.i(TAG,"!!! UPDATED !!!! User ID: "
                            + currentAccessToken.getUserId()
                            + "\n" +
                            "Auth Token: "
                            + currentAccessToken.getToken());



                }
            }, SPLASH_TIME_OUT);
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    SharedPrefUtils.setBoolean(getApplicationContext(),SharedPrefUtils.GENERAL, SharedPrefUtils.IS_FACEBOOK_LOGIN, false);

                    Log.i(TAG,"updateWithToken: TOKEN IS NULL !!! Login To Facebook Again!!");



                    StartLoginActivity();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    private void app_hashKey() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("app.alf", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void InitializeUI() {
        setContentView(R.layout.alf_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {

                    getApplicationContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    String url = "https://www.facebook.com/"+Util.FACEBOOK_PAGE_ID+"/events/";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href="+url));
                   // Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://"+Util.FACEBOOK_PAGE_ID+"/events/"));

                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+Util.FACEBOOK_PAGE_ID+"/events/")));
                }

            }
        });




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        TextView versionTV = (TextView) navigationView.findViewById(R.id.version);
//        versionTV.setText(SharedPrefUtils.getString(getApplicationContext(),SharedPrefUtils.GENERAL,SharedPrefUtils.APP_VERSION));

    }

    public ALFEventsAdapter retreiveEventsAdapter(){

        if (EventsListAdapter == null)
            EventsListAdapter = new ALFEventsAdapter(getApplicationContext(), allALFIsraelEvents);;

        return EventsListAdapter;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alfmain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       /* if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else */if (id == R.id.nav_manage) {

            Intent intent = new Intent();
            intent.setClass(ALFMainActivity.this, SetSettingsActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_share) {

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.invite_title));

            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.invite));
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_via)));



        } else if (id == R.id.nav_send) {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, "ronyahae@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "regarding ALF Israel App");
               /* intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");*/

            startActivity(Intent.createChooser(intent, getResources().getString(R.string.send_mail)));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
