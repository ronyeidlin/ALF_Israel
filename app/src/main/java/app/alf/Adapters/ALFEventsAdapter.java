package app.alf.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import app.alf.Objects.FacebookALFIsraelEvent;
import app.alf.Objects.FacebookEventPlace;
import app.alf.R;

/**
 * Created by rony on 31/07/2017.
 */

public class ALFEventsAdapter extends ArrayAdapter<FacebookALFIsraelEvent>  {
    private List<FacebookALFIsraelEvent> allEvents;
    private final String TAG = ALFEventsAdapter.class.getSimpleName();

    public ALFEventsAdapter(Context context, List<FacebookALFIsraelEvent> allEvents) {
        super(context, 0, allEvents);
        this.allEvents = allEvents;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        FacebookALFIsraelEvent ALFEvent = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_lv_row, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.event_name);
        TextView tvplace = (TextView) convertView.findViewById(R.id.event_place);
        TextView tvDate = (TextView) convertView.findViewById(R.id.event_date);
       // TextView tvTime = (TextView) convertView.findViewById(R.id.event_time);

        tvName.setTag(position);

        FacebookEventPlace eventPlace = new FacebookEventPlace();
        assert ALFEvent != null;
        if (ALFEvent.place != null)
            eventPlace = new Gson().fromJson(ALFEvent.place.toString(), FacebookEventPlace.class);

        String place = eventPlace.name;

        if (ALFEvent !=null) {
            tvName.setText(ALFEvent != null ? ALFEvent.name : null);
            tvplace.setText(ALFEvent != null ? place : null);
            tvDate.setText(ALFEvent != null ? ALFEvent.start_time : null);
            // tvTime.setText(ALFEvent != null ? time : null);
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
