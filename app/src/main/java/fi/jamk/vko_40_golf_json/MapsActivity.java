package fi.jamk.vko_40_golf_json;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private JSONArray courses;
    private JSONObject course;
    private String url = "http://ptm.fi/materials/golfcourses/golf_courses.json";
    private FetchJSONTask fetchJSONTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        fetchJSONTask = new FetchJSONTask();
        fetchJSONTask.execute(url);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    class FetchJSONTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            JSONObject json = null;
            try{
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                json = new JSONObject(stringBuilder.toString());
            } catch (IOException e){
                e.printStackTrace();
            } catch (JSONException e){
                e.printStackTrace();
            }
            finally {
                if(urlConnection != null) urlConnection.disconnect();
            }
            return json;
        }

        protected void onPostExecute(JSONObject json) {
            try{
                courses = json.getJSONArray("courses");
            } catch (JSONException e)
            {
                Log.e("JSON", "ERROR getting data");
            }

            LatLng place = null;
            double lat = 0;
            double lng = 0;
            String type = null;
            String courseName = null;
            String address = null;
            String phone = null;
            String email = null;
            String web = null;
            String image = null;
            String text = null;

            for(int i = 0; i < courses.length(); i++){
                try {
                    course = courses.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    lat = course.getDouble("lat");
                    lng = course.getDouble("lng");
                    type = (course.getString("type"));
                    courseName = course.getString("course");
                    address = course.getString("address");
                    phone = course.getString("phone");
                    email = course.getString("email");
                    web = course.getString("web");
                    image = course.getString("image");
                    text = course.getString("text");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                place = new LatLng(lat, lng);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(place)
                        .title(courseName)
                        .snippet(address + "\n" + phone + "\n" + email + "\n" + web + "\n" + image)
                );

                switch (type){
                    case "Kulta":
                    {
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        break;
                    }
                    case "Kulta/Etu":
                    {
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        break;
                    }
                    case "Etu":
                    {
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        break;
                    }
                    case "?":
                    {
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    }
                }
            }
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    View v = getLayoutInflater().inflate(R.layout.golf_info_window, null);

                    TextView titleTextView = (TextView) v.findViewById(R.id.titleTextView);
                    TextView infoTextView = (TextView) v.findViewById(R.id.infoTextView);
                    ImageView golfImageView = (ImageView) v.findViewById(R.id.golfImageView);

                    titleTextView.setText(marker.getTitle());

                    infoTextView.setText(marker.getSnippet().substring(0, marker.getSnippet().lastIndexOf("\n")));

                    String imgPath = "/" + marker.getSnippet().substring(marker.getSnippet().lastIndexOf("\n") + 1);

                    Picasso.with(v.getContext()).load("http://ptm.fi/materials/golfcourses" + imgPath).into(golfImageView);


                    return v;
                }
            });

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 14.0f));
        }
    }
}
