package app.alf.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import app.alf.ALFMainActivity;
import app.alf.R;
import app.alf.Utils.Util;

public class EventInfoActivity extends AppCompatActivity {
    private final String TAG = EventInfoActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String name = (String) intent.getSerializableExtra(Util.NAME);
        String place = (String) intent.getSerializableExtra(Util.PLACE);
        String city = (String) intent.getSerializableExtra(Util.CITY);
        String street = (String) intent.getSerializableExtra(Util.STREET);
        String start_time = (String) intent.getSerializableExtra(Util.START_TIME);
        String description = (String) intent.getSerializableExtra(Util.DESCRIPTION);
        String end_time = (String) intent.getSerializableExtra(Util.END_TIME);
       final String eventID = (String) intent.getSerializableExtra(Util.EVENT_ID);



        Button eventFace = (Button) findViewById(R.id.facebook_event);

        eventFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    getApplicationContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    String url = "https://www.facebook.com/events/"+eventID;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href="+url));

                  //  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://events/"+eventID));
                    startActivity(intent);

                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/events/"+eventID)));
                }

            }
        });

        TextView nameTV = (TextView) findViewById(R.id.EventName);
        TextView startTV = (TextView) findViewById(R.id.EventStartTime);
        TextView placeTV = (TextView) findViewById(R.id.EventPlace);
        TextView descTV = (TextView) findViewById(R.id.EventDescription);
        TextView endTV = (TextView) findViewById(R.id.EventEndTime);

        if (name != null)
            if (!name.isEmpty())
                nameTV.setText(name);

        if (start_time != null)
            if (!start_time.isEmpty())
                startTV.setText(start_time);

        if (city != null  && street !=null)
            if (!city.isEmpty()  && !street.isEmpty())
                place =city + " " + street;

        if (place != null)
            if (!place.isEmpty())
                placeTV.setText(place);

        if (description != null)
            if (!description.isEmpty())
                descTV.setText(description);

        if (end_time != null)
            if (!end_time.isEmpty())
                endTV.setText(end_time);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ALFMainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }


}
