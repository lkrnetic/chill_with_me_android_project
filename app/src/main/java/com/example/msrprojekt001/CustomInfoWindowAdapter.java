package com.example.msrprojekt001;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.example.msrprojekt001.pojo.Meeting;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity context;

    public CustomInfoWindowAdapter(Activity context){
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.customwindow, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
        TextView tv_description = (TextView) view.findViewById(R.id.tv_description);
        TextView tv_event_time = (TextView) view.findViewById(R.id.tv_event_time);
        Meeting meeting_pom = (Meeting) marker.getTag();
        tv_title.setText(meeting_pom.getTitle());
        tv_user_name.setText(meeting_pom.getUser_first_and_last_name());
        tv_description.setText(meeting_pom.getEvent_description());
        tv_event_time.setText(meeting_pom.getEvent_time());

        return view;
    }
}
