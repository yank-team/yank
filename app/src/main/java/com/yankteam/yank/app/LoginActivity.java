package com.yankteam.yank.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/*
 * LoginActivity
 * The activity users first see when the app starts, and there's no APIK to try against
 * the server.
 */
public class LoginActivity extends ActionBarActivity {

    public static final String LOG_TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // wire up an onclick listener for the login button
        Button mLoginButton = (Button)findViewById(R.id.btn_login);
        Button mSignupButton = (Button)findViewById(R.id.btn_signup);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressBar loginIndicator = (ProgressBar) findViewById(R.id.login_indicator);
                LinearLayout loginBody     = (LinearLayout) findViewById(R.id.login_container);
                loginIndicator.setVisibility(View.VISIBLE);
                loginBody.setVisibility(View.GONE);

                triggerLogin();
            }
        });

        mSignupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // launch signup activity
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    /* trigger Login async task */
    public void triggerLogin () {
        // gather login info from the edit boxes. Send that to the API Login
        // asynctask

        ProgressBar prog = (ProgressBar) findViewById(R.id.login_indicator);
        prog.setVisibility(View.VISIBLE);

        EditText mEtxtUsername = (EditText) findViewById(R.id.etxt_username);
        EditText mEtxtPassword = (EditText) findViewById(R.id.etxt_password);
        new LoginTask().execute(
                mEtxtUsername.getText().toString(),
                mEtxtPassword.getText().toString()
        );
    }

    // in onResume, we need to pull a saved APIK out of our shared prefs. If it doesn't exist,
    // allow the user to login/signup -- if it exists verify that the key is valid
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sPrefs = this.getPreferences(Context.MODE_PRIVATE);
        String apik = sPrefs.getString("YANK_APIK", null);
    }

    /* switch activities */
    private void gotoLobby() {
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }

    // asynctask to contact the server for the first time
    private class VerifyApikTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postReq = new HttpPost();
            return null;
        }
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        public static final String LOG_TOKEN = "LoginTask";

        @Override
        protected String doInBackground(String... params) {
            return executeReq(asmPostReq(params[0], params[1]));
        }

        /* assemble a JSON POST request */
        private HttpPost asmPostReq (String username, String passwd) {
            // Assemble post request
            String url          = getString(R.string.jheron_api) + "auth/login/";
            HttpPost postReq    = new HttpPost(url);
            JSONObject reqBody  = asmReqBody(username, passwd);
            StringEntity entity = null;

            // Attempt to set headers
            try {
                entity = new StringEntity(reqBody.toString());
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                postReq.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                // something bad happened -- abort
                e.printStackTrace();
                return null;
            }
            return postReq;
        }

        /* assemble the body of a JSON POST request */
        private JSONObject asmReqBody(String username, String passwd) {
            JSONObject reqBody = new JSONObject();
            try {
                reqBody.put("username", username);
                reqBody.put("password", passwd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return reqBody;
        }

        /* execute a post request -- return the contents of the response as a string */
        private String executeReq (HttpPost postReq) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            try {
                HttpResponse response = httpClient.execute(postReq);
                // Pull apart the request with an input stream
                if (response != null){
                    // set up Java's bullshit
                    InputStream in = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder builder = new StringBuilder();
                    // Let Java do some bullshit
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    // return Java's bullshit
                    return builder.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == null) {
                Toast.makeText(getApplicationContext(),"login failed", Toast.LENGTH_SHORT)
                    .show();
                resetProgress();
                return;
            }

            try {
                JSONObject mainObject    = new JSONObject(s);
                Boolean success_response = mainObject.getBoolean("success");
                String msg_response      = mainObject.getString("msg");
                JSONObject data_response = mainObject.getJSONObject("data");
                String api_key = data_response.getString("apik");

                if(success_response){
                    //store api key in AppInfo singleton
                    AppInfo appInfo = AppInfo.getInstance();
                    appInfo.api_key = api_key;

                    //send user through to the lobby
                    gotoLobby();
                } else {
                    resetProgress();
                    Toast.makeText(getApplicationContext(), "Login failed. Try again.",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                //ask user to retry login
                e.printStackTrace();
            }
        }
    }

    private void resetProgress() {
        ProgressBar loginIndicator = (ProgressBar) findViewById(R.id.login_indicator);
        LinearLayout loginBody     = (LinearLayout) findViewById(R.id.login_container);
        loginIndicator.setVisibility(View.GONE);
        loginBody.setVisibility(View.VISIBLE);
    }

}
