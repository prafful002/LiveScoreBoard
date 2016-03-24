package scoreboard.example.com.livescoreboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

/**
 * Created by Prafful Um on 23-03-2016.
 */
public class ViewScoreActivity extends AppCompatActivity {

    TextView team1; // textview that displays the score of team1
    TextView team2; // textview that displays the score of team2
    Context context;
    ORM datasource = new ORM(this); // An object of ORM class to take care of database transactions


    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_score_activity);
        context = this;
        team1 = (TextView) findViewById(R.id.tvTeam1Score);
        team2 = (TextView) findViewById(R.id.tvTeam2Score);


        new GetResult().execute();


    }

    /*
        checks whether the device is connected to the network or not
     */

    public boolean isConnected() {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private class GetResult extends AsyncTask<Void, Void, Integer> {
        String t1 = null; // to store the score of team1
        String t2 = null; // to store the score of team2
        ProgressDialog pDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = ProgressDialog.show(context, "Wait!", "Retrieving data...");
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            if (result == 1) // on a successful response
            {
                team1.setText(t1);
                team2.setText(t2);
                Log.v("makeentry", t1 + " " + t2);
                datasource.open();
                datasource.deleteAll();
                datasource.createEntry("team1", t1);
                datasource.createEntry("team2", t2);

                datasource.close();

            } else if (result == 0) // when there is no net connectivity
            {
                Toast.makeText(context, "You are Offline! Turn on your network!", Toast.LENGTH_LONG).show();
                datasource.open();

                Map<String, String> results = datasource.getAllScores();
                team1.setText(results.get("team1"));
                team2.setText(results.get("team2"));
                datasource.close();
            } else if (result == -1) // when scores haven't been updated by the user yet
            {
                Toast.makeText(context, "Please update the score first", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to get Data", Toast.LENGTH_SHORT).show();
            }

        }

        /*
         Makes the GET Request. Parses the JSON Response. Populates the TextViews
         to display the scores.
         Returns
         1 on Success
         0 when no NETWORK
         -1 when Scores haven't been updated yet.
         */

        @Override
        protected Integer doInBackground(Void... params) {

            // get the name of the person from the sharedPreferences
            String name = getNameFromSharedPrefs();

            if (name == null) {  // scores haven't been updated yet
                return -1;
            }
            if (!isConnected()) { // No internet available
                return 0;
            }

            URL url = null;
            HttpURLConnection con = null;


            //build the GET Url
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("campus-connect-recruitment.appspot.com")
                    .appendPath("_ah")
                    .appendPath("api")
                    .appendPath("clubs")
                    .appendPath("v1")
                    .appendPath("getScore")
                    .appendQueryParameter("name", name)
                    .build();
            try {
                url = new URL(builder.toString());
                Log.v("GET_Url", url + "");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


            try {

                con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestMethod("GET");


                // if response status is 200 open inputstream to read the data received from the server

                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    parseResponseJson(sb.toString());

                    // return 1 on success
                    return 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // return null on failure
            return null;
        }

        /*
            Parses the Json response and extracts the scores of team1 and team2
        */

        private void parseResponseJson(String s) {
            try {
                JSONObject jsonObj = new JSONObject(s);
                t1 = jsonObj.getString("score1");
                t2 = jsonObj.getString("score2");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /*
            returns name of the person from Shared Preferences
        */
        private String getNameFromSharedPrefs() {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String name = preferences.getString("name", null);
            return name;
        }
    }
}
