package fi.jamk.vko_40_golf_json;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Jere on 8.10.2017.
 */

public class GolfInfoWindowAdapter extends Activity implements GoogleMap.InfoWindowAdapter {

    public GolfInfoWindowAdapter(){
    }

    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        View v = getLayoutInflater().inflate(R.layout.golf_info_window, null);

        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
        TextView infoTextView = (TextView) findViewById(R.id.infoTextView);
        ImageView golfImageView = (ImageView) findViewById(R.id.golfImageView);

        titleTextView.setText(getTitle());

        infoTextView.setText(marker.getSnippet());

        //golfImageView.setImageDrawable(Drawable.createFromPath("/" + image));

        return v;
    }
}
