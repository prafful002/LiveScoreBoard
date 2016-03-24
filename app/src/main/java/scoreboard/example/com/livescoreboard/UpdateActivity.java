package scoreboard.example.com.livescoreboard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Prafful Um on 23-03-2016.
 */
public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {

    EditText team1Score;
    EditText team2Score;
    EditText senderName;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_activity);
        context = this;
        team1Score = (EditText) findViewById(R.id.editTeam1);
        team2Score = (EditText) findViewById(R.id.editTeam2);
        senderName = (EditText) findViewById(R.id.editName);
        Button submitButton = (Button) findViewById(R.id.buttonSubmit);
        submitButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSubmit) {
            String t1Score = team1Score.getText().toString();
            String t2Score = team2Score.getText().toString();
            String name = senderName.getText().toString();

            if (t1Score.length() > 0 && t2Score.length() > 0 && name.length() > 0)
                makePost(t1Score, t2Score, name);
            else
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
        }
    }

    private void makePost(String t1Score, String t2Score, String name) {
        String postUrl = "https://campus-connect-recruitment.appspot.com/_ah/api/clubs/v1/updateScore";
        new DoPost().execute(postUrl, t1Score, t2Score, name); // make the POST request from a worker thread
    }

    private class DoPost extends AsyncTask<String, Void, Integer> {

        ProgressDialog pDialog;

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == 1) // on successful POST
            {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = ProgressDialog.show(context, "", "Updating Scores...");
            pDialog.setCancelable(true);
            pDialog.show();

        }

        /*
            makes the POST request. returns 1 on success. 0 on failure
         */

        @Override
        protected Integer doInBackground(String... params) {

            URL url = null;
            HttpURLConnection con = null;
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // open and configure the HTTPUrlConnection
            try {
                con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                con.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // create the JSONObject to be sent in the POST request
            JSONObject details = new JSONObject();
            try {
                details.put("name", params[3]);
                details.put("team1", "nitk");
                details.put("score1", params[1]);
                details.put("team2", "rvce");
                details.put("score2", params[2]);

                Log.v("postData", details.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // write the JSONObject into the body of the POST request
            try {
                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(details.toString());
                wr.flush();
                Log.v("write", "success");
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_NO_CONTENT) {

                    storeName(params[3]); // stores the name in the sharedPreferences
                    return 1; // return 1 on successful POST
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // return 0 when the POST fails
            return 0;
        }

        /*
             stores the name of the person in Shared Preferences
         */
        private void storeName(String name) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("name", name);
            editor.apply();
        }
    }
}
