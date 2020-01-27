package com.example.msrprojekt001;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.msrprojekt001.pojo.Greska;
import com.example.msrprojekt001.pojo.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Registration extends AppCompatActivity {
    String first_and_last_name_user, password, password2, gender, user_description, email;
    int ages;
    EditText et_first_and_last_user_name, et_password, et_password2, et_user_ages, et_gender, et_user_description, et_email;
    private Greska err = new Greska();
    private User user;
    private Context con;
    private String wsUrl = "https://www.racunalna-znanost.com.hr/chill_with_me/users.php/registration";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        Button btn_registracija = findViewById(R.id.btn_registracija);
        //prijava.setOnClickListener(this);
        et_email = findViewById(R.id.et_email);
        et_first_and_last_user_name = findViewById(R.id.et_first_name_and_last_name);
        et_password = findViewById(R.id.et_password);
        et_password2 = findViewById(R.id.et_password_check);
        et_user_ages = findViewById(R.id.et_ages);
        et_gender = findViewById(R.id.et_gender);
        et_user_description = findViewById(R.id.et_user_description);
        con = this;
    }

    public void registration(View v) {
        first_and_last_name_user = et_first_and_last_user_name.getText().toString();
        password = et_password.getText().toString();
        password2 = et_password2.getText().toString();
        if(et_first_and_last_user_name.length() > 3 && password.length() > 3) {
            if(password.equals(password2)){
                password = sha1Hash(et_password.getText().toString());
                first_and_last_name_user = et_first_and_last_user_name.getText().toString();
                email = et_email.getText().toString();
                ages = Integer.parseInt(et_user_ages.getText().toString());
                gender = et_gender.getText().toString();
                user_description = et_user_description.getText().toString();
                Gson gson = new Gson();
                User user2 = new User(first_and_last_name_user, email, password, ages, user_description, gender);
                String user_pom = gson.toJson(user2, User.class);
                Log.d("wsurl",wsUrl);
                Log.d("user_pom",user_pom);
                new Registration.WSPregledHelper(Registration.this).execute(wsUrl, "POST", user_pom);
            }
            else {
                Toast.makeText(Registration.this, "Unesene lozinke nisu jednake!", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(Registration.this, "Da bi uspješno stvorili korisnički račun, korisničko ime, kao i šifra, moraju biti dulji od tri znaka!", Toast.LENGTH_LONG).show();
        }
    }
    String sha1Hash( String toHash )
    {
        String hash = null;
        try
        {
            MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
            byte[] bytes = toHash.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            // This is ~55x faster than looping and String.formating()
            hash = bytesToHex( bytes );
        }
        catch( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }
        catch( UnsupportedEncodingException e )
        {
            e.printStackTrace();
        }
        return hash;
    }

    // http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex( byte[] bytes )
    {
        char[] hexChars = new char[ bytes.length * 2 ];
        for( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[ j ] & 0xFF;
            hexChars[ j * 2 ] = hexArray[ v >>> 4 ];
            hexChars[ j * 2 + 1 ] = hexArray[ v & 0x0F ];
        }
        return new String( hexChars );
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
        private WeakReference<Registration> activityReference;
        // only retain a weak reference to the activity
        public WSPregledHelper(Registration context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected User doInBackground(String... urls) {
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
                        // parsiramo podatke JSON formatu u objekt tipa Users
                        Gson gson = new Gson();
                        User user = gson.fromJson(res, User.class);
                        activityReference.get().user = user;
                        // metodi onPostExecute šalje se id korisnika
                        return user;
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
        protected void onPostExecute(User user){
            Registration activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            // Uspješna obrada
            if (user != null) {
                //activity.id.setText(Integer.toString(answer_text));
                // brisanje uspješno
                Log.d("id",""+ user.getId());
                if(user.getId() == -1) {
                    Toast.makeText(activity, "Email adresa koju ste unijeli već postoji!", Toast.LENGTH_LONG).show();
                }
                else {

                    Toast.makeText(activity, "Korisnički račun je uspješno stvoren! Dobrodošli " + user.getFirst_and_last_name(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(activity.con, MapsActivity.class);
                    intent.putExtra("user_id", user.getId());
                    intent.putExtra("first_and_last_name", user.getFirst_and_last_name());
                    intent.putExtra("email", user.getEmail());
                    intent.putExtra("gender", user.getGender());
                    intent.putExtra("user_desription", user.getUser_description());
                    intent.putExtra("ages", user.getAges());
                    activity.startActivity(intent);
                }
                /*
                if (tid == 0){
                    activity.id.setText("");
                    activity.korisnik.setText("");
                    activity.ime.setText("");
                    activity.id.requestFocus();
                }
                else {
                    activity.id.setText(Integer.toString(activity.kor.getId()));
                    activity.korisnik.setText(activity.kor.getUsername());
                    activity.ime.setText(activity.kor.getName());
                }
                Toast.makeText(activity, "Obrada uspješna! " + tid, Toast.LENGTH_LONG).show();
                */
            }
            else {
                // Greška
                Toast.makeText(activity, "Greška pri dohvatu!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private static String inputStreamToString(InputStream is){
        Scanner s = new Scanner(is).useDelimiter("\\A");
        String res = s.hasNext() ? s.next() : "";
        s.close();
        return res;
    }

}
