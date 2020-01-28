package com.example.msrprojekt001;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.msrprojekt001.pojo.Greska;
import com.example.msrprojekt001.pojo.Meeting;
import com.example.msrprojekt001.pojo.Meetings;
import com.example.msrprojekt001.pojo.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public Intent intent;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final float INITIAL_ZOOM = 12f;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private Meeting meeting;
    private Greska err = new Greska();
    private String wsUrl = "https://racunalna-znanost.com.hr/chill_with_me/meetings.php";
    private LatLng latLng_pom;
    private MapsActivity con;
    private String first_and_last_name, email, gender, user_desription;
    private int ages, user_id;
    private Marker marker_long_click_pom;
    private List<Meeting> existing_markers = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        intent = getIntent();
        user_id = intent.getIntExtra("user_id", 0);
        ages = intent.getIntExtra("ages", 0);
        first_and_last_name = intent.getStringExtra("first_and_last_name");
        email = intent.getStringExtra("email");
        gender = intent.getStringExtra("gender");
        user_desription = intent.getStringExtra("user_desription");
        // Log.d("intent_inf", "user_id: " + user_id + " ages:" + ages + " first_and_last_name" + first_and_last_name + " email " +  email + " gender " + gender + " user_description " + user_desription);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        con = this;

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
        /*
        // Add a marker in MELBOURNE and move the camera
        LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
        Marker melbourne = mMap.addMarker(new MarkerOptions()
                .position(MELBOURNE)
                .title("Melbourne"));
        melbourne.showInfoWindow();
        mMap.addMarker(new MarkerOptions().position(MELBOURNE));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(MELBOURNE));
        enableMyLocation(mMap); // Enable location tracking.
        // Enable going into StreetView by clicking on an InfoWindow from a
        // point of interest.
        //setInfoWindowClickToPanorama(mMap);
        //napuniMapu(mMap);
        */
        setMapLongClick(mMap); // Set a long click listener for the map;
        setPoiClick(mMap); // Set a click listener for points of interest.
        enableMyLocation(mMap); // Enable location tracking.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Meeting pom;
                pom = (Meeting)marker.getTag();
                //updateStore(pom, pom.getTitle(), pom.getDescription(), marker.getPosition());
                return false;
            }
        });



        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(MapsActivity.this);
        mMap.setInfoWindowAdapter(adapter);

        /*
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            /*
            @Override
            public void onMarkerDrag(Marker m) {
                System.out.println("Radi: " + m.getPosition().toString());

            }

            @Override
            public void onMarkerDragEnd(Marker m) {
                updateStore(pom, pom.getNaslov(), pom.getOpis(), m.getPosition());
                System.out.println(m.getPosition().toString());
            }

            @Override
            public void onMarkerDragStart(Marker m) {
                pom = (Zapis)m.getTag();
                System.out.println("poc: "+pom.getLatLng().toString());
            }
        });
        */
        new WSPregledHelper2(con).execute(wsUrl);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Meeting pomocni_meeting = (Meeting) marker.getTag();
                Intent intent2 = new Intent(con, UserProfile.class);
                intent2.putExtra("user_id_for_request", pomocni_meeting.getUser_id());
                startActivity(intent2);
            }
        });
    }

    private void enableMyLocation(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    private void setMapLongClick(final GoogleMap map) {
        // Add a blue marker to the map when the user performs a long click.
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Boolean zastavica = false;
                if (existing_markers != null) {
                    for (Meeting meeting : existing_markers){
                        double latitude_pom_ = latLng.latitude;
                        double longitude_pom = latLng.longitude;
                        if ((Double.compare(latitude_pom_, meeting.getLatitude()) == 0) && (Double.compare(longitude_pom, meeting.getLongitude()) == 0)) {
                            zastavica = true;
                            Log.d("zastavica", "zastavica");
                        }
                    }
                }

                showAlertDialogForPoint(map, latLng);
                /*
                String snippet = String.format(Locale.getDefault(),
                        getString(R.string.lat_long_snippet),
                        latLng.latitude,
                        latLng.longitude);
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(getString(R.string.dropped_pin))
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker
                                (BitmapDescriptorFactory.HUE_BLUE)));
                                */

            }
        });
    }

    public void ShowMyProfil(View v) {
        Intent intent3 = new Intent(con, UserProfile.class);
        intent3.putExtra("user_id_for_request", user_id);
        startActivity(intent3);
    }

    // DZ - otvori alert prozorčić za unos naslova i opisa
    // Display the alert that adds the marker
    private void showAlertDialogForPoint(final GoogleMap map, final LatLng latLng) {
        // inflate message_item.xml view
        View messageView = LayoutInflater.from(MapsActivity.this).inflate(R.layout.message_item, null);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set message_item.xml to AlertDialog builder
        alertDialogBuilder.setView(messageView);
        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        // Configure dialog button (OK)
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Define color of marker icon
                        BitmapDescriptor defaultMarker =
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                        // Extract content from alert dialog
                        String event_description = ((EditText) alertDialog.findViewById(R.id.etDescription)).getText().toString();
                        String event_time = ((EditText) alertDialog.findViewById(R.id.etTime)).getText().toString();
                        String title = ((EditText) alertDialog.findViewById(R.id.etTitle)).getText().toString();
                        Double latitude = latLng.latitude;
                        Double longitude = latLng.longitude;
                        latLng_pom = new LatLng(latitude, longitude);
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String address_pom = "Ime adrese nepoznato";
                        if (addresses != null) {
                            //Address returnedAddress = addresses.get(0);
                            address_pom = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        }
                        String address = address_pom;
                        String user_first_and_last_name = first_and_last_name;
                        Meeting meeting_pom = new Meeting(event_description, address, longitude, latitude, title, event_time, user_id, user_first_and_last_name);
                        // Creates and adds marker to the map
                        Gson gson = new Gson();
                        String meeting_pom_string = gson.toJson(meeting_pom, Meeting.class);
                        new WSPregledHelper(MapsActivity.this).execute(wsUrl, "POST", meeting_pom_string);
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng_pom)
                                .draggable(false)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).setTag(meeting_pom);

                    }
                    //addMarker(map, meeting_pom, latLng);
                });

        // Configure dialog button (Cancel)

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { dialog.cancel(); }
                });
        // Display the dialog
        alertDialog.show();
    }

    public void addMarkers(GoogleMap map, List<Meeting> meetings){
        for (Meeting meeting : meetings){
            LatLng lat_ltn_add_marker = new LatLng(meeting.getLatitude(), meeting.getLongitude());
            Log.d("latlng"," "+  meeting.getLatitude() + " " + meeting.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(lat_ltn_add_marker)
                    .draggable(false)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).setTag(meeting);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lat_ltn_add_marker));
        }
    }

    private void setPoiClick(final GoogleMap map) {
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                Marker poiMarker = map.addMarker(new MarkerOptions()
                        .position(poi.latLng)
                        .title(poi.name));
                poiMarker.showInfoWindow();
                poiMarker.setTag(getString(R.string.poi));
            }
        });
    }

    private void setInfoWindowClickToPanorama(GoogleMap map) {
        map.setOnInfoWindowClickListener(
                new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        // Check the tag
                        if (marker.getTag() == "poi") {

                            // Set the position to the position of the marker
                            StreetViewPanoramaOptions options =
                                    new StreetViewPanoramaOptions().position(
                                            marker.getPosition());

                            SupportStreetViewPanoramaFragment streetViewFragment
                                    = SupportStreetViewPanoramaFragment
                                    .newInstance(options);

                            // Replace the fragment and add it to the backstack
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.map,
                                            streetViewFragment)
                                    .addToBackStack(null).commit();
                        }
                    }
                });
    }




    private static class WSPregledHelper extends AsyncTask<String, Void, Meeting> {
        // How to use a static inner AsyncTask class
        //
        // To prevent leaks, you can make the inner class static. The problem
        // with that, though, is that you no longer have access to the Activity's
        // UI views or member variables. You can pass in a reference to the
        // Context but then you run the same risk of a memory leak. (Android can't
        // garbage collect the Activity after it closes if the AsyncTask class has
        // a strong reference to it.)
        // The solution is to make a weak reference to the Activity (or whatever
        // Context you need).
        // https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur#answer-46166223
        private WeakReference<MapsActivity> activityReference;

        // only retain a weak reference to the activity
        public WSPregledHelper(MapsActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Meeting doInBackground(String... urls) {
            int br = urls.length;
            // šaljemo samo 1 parametar (1 URL), iako metoda može primiti polje parametara
            HttpURLConnection conn = null;
            try {
                // metoda prima više parametara:
                // 1. parametar - URL web servisa
                // 2. parametar - metoda (POST, PUT, DELETE)
                // 3. parametar - string s JSON porukom koja se šalje
                conn = (HttpURLConnection) new URL(urls[0]).openConnection();
                // postavljamo kodnu stranicu da bi se znakovi prikazali ispravno
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                // koristi se zadane metoda (GET, POST, PUT ili DELETE)
                //conn.setRequestMethod(urls[1]);
                conn.setRequestMethod("POST");
                // POST i PUT primaju parametre u tijelu upita (OutputStream)
                if (urls[1].equals("POST") || urls[1].equals("PUT")) {
                    conn.setDoOutput(true);
                    OutputStream output = conn.getOutputStream();
                    output.write(urls[2].getBytes("UTF-8"));
                    output.close();
                    // Ako je obrada prošla u redu - dohvati povratnu poruku (InputStream) i pretvori ju u String
                    if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                        // Dohvaćamo InputStream koji vraća web servis i pretvaramo ga u JSON String
                        String res = inputStreamToString(conn.getInputStream());
                        String res2 = res.substring(2, res.length()-1);
                        // parsiramo podatke JSON formatu u objekt tipa Users
                        Gson gson = new Gson();
                        Meeting meeting = gson.fromJson(res, Meeting.class);
                        activityReference.get().meeting = meeting;
                        // metodi onPostExecute šalje se id korisnika
                        return meeting;
                    } else {
                        // Inače se vratila greška, pa dohvati poruku greške i pretvori ju u String
                        // Koristi se ErrorStream, ane InputStream koji vraća web servis i pretvaramo ga u JSON String
                        String res = inputStreamToString(conn.getErrorStream());
                        Gson gson = new Gson();
                        activityReference.get().err = gson.fromJson(res, Greska.class);
                        return null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                activityReference.get().err.setError(e.getMessage());
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Meeting meeting) {
            MapsActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            // Uspješna obrada
            if (meeting != null) {
                Toast.makeText(activity, "Meeting uspješno dodan!", Toast.LENGTH_LONG).show();
                activity.existing_markers.add(meeting);
            }
            else {
                // Greška
                Toast.makeText(activity, "Greška pri dohvatu!", Toast.LENGTH_LONG).show();
            }
        }
        private static String inputStreamToString(InputStream is) {
            Scanner s = new Scanner(is).useDelimiter("\\A");
            String res = s.hasNext() ? s.next() : "";
            s.close();
            return res;
        }
    }

    private static class WSPregledHelper2 extends AsyncTask<String, Void, List<Meeting>> {
        // How to use a static inner AsyncTask class
        //
        // To prevent leaks, you can make the inner class static. The problem
        // with that, though, is that you no longer have access to the Activity's
        // UI views or member variables. You can pass in a reference to the
        // Context but then you run the same risk of a memory leak. (Android can't
        // garbage collect the Activity after it closes if the AsyncTask class has
        // a strong reference to it.)
        // The solution is to make a weak reference to the Activity (or whatever
        // Context you need).
        // https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur#answer-46166223
        private WeakReference<MapsActivity> activityReference;
        // only retain a weak reference to the activity
        WSPregledHelper2(MapsActivity context) {
            activityReference = new WeakReference<>(context);
        }
        @Override
        protected List<Meeting> doInBackground(String... urls) {
            int br = urls.length;
            // šaljemo samo 1 parametar (1 URL), iako metoda može primiti polje parametara
            HttpURLConnection conn = null;
            try {
                // povezujemo se sa zadanim URL-om pomoću GET metode
                conn = (HttpURLConnection) new URL(urls[0]).openConnection();
                // postavljamo kodnu stranicu da bi se znakovi prikazali ispravno
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                // vijestistimo HTTP GET metodu za dohvat
                conn.setRequestMethod("GET");
                // dohvaćamo podatke u obliku ulaznog niza
                // ako su podaci u redno dohvaćeni (HTTP kod 200)
                if (conn.getResponseCode() == 200) {
                    // pretvaramo ulazni InputStream u String
                    String res = inputStreamToString(conn.getInputStream());
                    String res2 = res.substring(2, res.length()-1);
                    //JSONObject response = new JSONObject(res);
                    Gson gson = new Gson();
                    Meetings meetings = gson.fromJson(res2, Meetings.class);
                    // metodi onPostExecute šalje se polje objekata tipa User kako bi se
                    // lista popunila podacima pročitanih korisnika
                    return meetings.getMeetings();                    // metodi onPostExecute šalje se polje objekata tipa Vijest kako bi se
                    // lista popunila podacima pročitanih vijestisnika
                } else {
                    // Inače se vratila greška, pa dohvati poruku greške i pretvori ju u String
                    // vijestisti se ErrorStream, ane InputStream koji vraća web servis i pretvaramo ga u JSON String
                    String res = inputStreamToString(conn.getErrorStream());
                    // parsiramo podatke JSON formata u objekt tipa Greska
                    Gson gson = new Gson();
                    // Ažuriramo informaciju o grešci, ako se dogodila
                    activityReference.get().err = gson.fromJson(res, Greska.class);
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
            return null;
        }
        /*
    Pomoćna metoda koja dohvaća String iz primljenog input ili error streama
        */
        private String inputStreamToString(InputStream is) {
            Scanner s = new Scanner(is).useDelimiter("\\A");
            String res = s.hasNext() ? s.next() : "";
            s.close();
            return res;
        }
        @Override
        protected void onPostExecute(List<Meeting> rez) {
            // get a reference to the activity if it is still there
            // Dogodila se greška kod dohvata
            MapsActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            // Dogodila se greška kod dohvata
            if (rez == null) {
                //Toast.makeText(activity, activity.err.getError(), Toast.LENGTH_LONG).show();
                return;
            } else {
                activity.existing_markers = rez;

                for (int i = 0; i < rez.size(); i++) {
                    Log.d("poruka", " " + rez.get(i).getLatitude() + rez.get(i).getLongitude());
                }
            }
            activity.addMarkers(activity.mMap, activity.existing_markers);
            // Inače ažuriraj listu
            //activity.kor = rez;
        }
    }
}