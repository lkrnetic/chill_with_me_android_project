package com.example.msrprojekt001;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.msrprojekt001.pojo.Greska;
import com.example.msrprojekt001.pojo.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class UserProfile extends AppCompatActivity {
    private String wsUrl = "https://racunalna-znanost.com.hr/chill_with_me/users.php?id=";
    private LatLng latLng_pom;
    private MapsActivity con;
    private String first_and_last_name, email, gender, user_desription;
    private int ages, user_id, user_id_for_request;
    private Intent intent;
    private Context context;
    private Greska err = new Greska();
    private TextView tv_user_name_and_last_name, tv_email, tv_ages, tv_gender, tv_user_description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        context = this;
        tv_user_name_and_last_name = findViewById(R.id.tv_user_name_and_last_name);
        tv_email = findViewById(R.id.tv_email);
        tv_ages = findViewById(R.id.tv_ages);
        tv_gender = findViewById(R.id.tv_gender);
        tv_user_description = findViewById(R.id.tv_user_description);
        intent = getIntent();
        user_id_for_request = intent.getIntExtra("user_id_for_request", 0);
        Log.d("adresa", "https://http://racunalna-znanost.com.hr/chill_with_me/users.php?id="+ user_id_for_request);
        new WSPregledHelper(this).execute(wsUrl + user_id_for_request);
        //lista = findViewById(R.id.comments_list);


        /*
        lista = findViewById(R.id.comments_list);
        lista.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        */
    }

    private static class WSPregledHelper extends AsyncTask<String, Void, User> {
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
        private WeakReference<UserProfile> activityReference;
        // only retain a weak reference to the activity
        public WSPregledHelper(UserProfile context) {
            activityReference = new WeakReference<>(context);
        }
        @Override
        protected User doInBackground(String... urls) {
            int br = urls.length;
            // šaljemo samo 1 parametar (1 URL), iako metoda može primiti polje parametara
            HttpURLConnection conn = null;
            try {
                // povezujemo se sa zadanim URL-om pomoću GET metode
                conn = (HttpURLConnection) new URL(urls[0]).openConnection();
                // postavljamo kodnu stranicu da bi se znakovi prikazali ispravno
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                // vijestistimo HTTP GET metodurlsu za dohvat
                conn.setRequestMethod("GET");
                // dohvaćamo podatke u obliku ulaznog niza
                // ako su podaci u redno dohvaćeni (HTTP kod 200)
                if (conn.getResponseCode() == 200 ) {
                    String res = inputStreamToString(conn.getInputStream());
                    String res2 = res.substring(2, res.length()-1);
                    //JSONObject response = new JSONObject(res);
                    Gson gson = new Gson();
                    User user = gson.fromJson(res, User.class);
                    // metodi onPostExecute šalje se polje objekata tipa User kako bi se
                    // lista popunila podacima pročitanih korisnika

                    return user;
                }
                else {
                    // Inače se vratila greška, pa dohvati poruku greške i pretvori ju u String
                    // Koristi se ErrorStream, ane InputStream koji vraća web servis i pretvaramo ga u JSON String
                    String res = inputStreamToString(conn.getErrorStream());
                    // parsiramo podatke JSON formata u objekt tipa Greska
                    Gson gson = new Gson();
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
        protected void onPostExecute(User user) {
            // get a reference to the activity if it is still there
            // Dogodila se greška kod dohvata
            UserProfile activity = activityReference.get();
            //activity.adapter = (ArrayAdapter<String>) activity.lista.getAdapter();

            if (activity == null || activity.isFinishing()) return;
            // Dogodila se greška kod dohvata
            if (user == null){
                Toast.makeText(activity, activity.err.getError(), Toast.LENGTH_LONG).show();
                return;
            } else {
                activity.tv_user_name_and_last_name.setText(user.getFirst_and_last_name());
                activity.tv_email.setText(user.getEmail());
                activity.tv_ages.setText("" + user.getAges());
                activity.tv_gender.setText(user.getGender());
                activity.tv_user_description.setText(user.getUser_description());
            }

        }
    }
}