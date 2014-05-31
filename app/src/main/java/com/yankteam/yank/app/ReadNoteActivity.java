

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

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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

/**
 * Created by jason on 5/24/14.
 */
public class ReadNoteActivity extends ActionBarActivity {
    public class LoginActivity extends ActionBarActivity {

        public static final String LOG_TAG = "ReadNoteActivity";

        Button mLoginButton;
        Button mSignupButton;

        EditText mEtxtUsername;
        EditText mEtxtPassword;

        AppInfo appInfo;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            // Get references to layout components
            mLoginButton  = (Button) findViewById(R.id.btn_login);
            mSignupButton = (Button) findViewById(R.id.btn_signup);

            mEtxtUsername = (EditText) findViewById(R.id.etxt_username);
            mEtxtPassword = (EditText) findViewById(R.id.etxt_password);

            // For now, we're just going to switch automatically
            mLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // initiate an asynctask
                    new LoginTask().execute(
                            mEtxtUsername.getText().toString(),
                            mEtxtPassword.getText().toString() );
                }
            });
        }

        // in onResume, we need to pull a saved APIK out of our shared prefs. If it doesn't exist,
        // allow the user to login/signup -- if it exists verify that the key is valid
        @Override
        public void onResume() {
            super.onResume();
            SharedPreferences sPrefs = this.getPreferences(Context.MODE_PRIVATE);
            String apik              = sPrefs.getString("YANK_APIK", null);

            // if there's an APIK, try it
            if (apik != null) {
            }
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
            @Override
            protected String doInBackground(String... args) {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost postReq = new HttpPost(getString(R.string.jheron_api) + "/auth/login/");
                HttpResponse response = null;

                // Set up a JSON request body
                JSONObject json = new JSONObject();
                try {
                    // Prepare post data
                    json.put("username", args[0] );
                    json.put("password", args[1] );

                    // Assemble request
                    StringEntity entity = new StringEntity(json.toString());
                    entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    postReq.setEntity(entity);

                    // Send request
                    response = httpClient.execute(postReq);

                    // Pull apart the request with an input stream
                    if (response != null){
                        // set up Java's bullshit
                        InputStream in = response.getEntity().getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder builder = new StringBuilder();

                        // Let Java do some bullshit
                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }

                        // return Java's bullshit
                        return builder.toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // TODO: JSON exception
                    // basically, quit
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    // TODO: String entity exception
                } catch (ClientProtocolException e) {
                    // TODO: incorrect protocol exception
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO: I/O exception
                    e.printStackTrace();
                }

                // Return null if failure occurs
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    Log.d(LoginActivity.LOG_TAG, "login json: " + s);
                    JSONObject mainObject = new JSONObject(s);
                    Log.d(LoginActivity.LOG_TAG, s);
                    Boolean success_response = mainObject.getBoolean("success");
                    String msg_response = mainObject.getString("msg");
                    JSONObject data_response = mainObject.getJSONObject("data");
                    String api_key = data_response.getString("apik");

                    if(success_response){
                        //store api key
                        appInfo = AppInfo.getInstance();
                        appInfo.api_key = api_key;
                        Log.d(LoginActivity.LOG_TAG, msg_response);
                        //send user through
                        gotoLobby();
                    }else {
                        //shit failed
                        Log.d(LoginActivity.LOG_TAG, "login failed: likely user");
                        Log.d(LoginActivity.LOG_TAG, msg_response);

                        //toast user, bad login
                    }



                } catch (JSONException e) {
                    //ask user to retry login
                    e.printStackTrace();
                    Log.d(LoginActivity.LOG_TAG, "login failed: likely at server");

                }

                // TODO: UI THREAD STUFF
                // Log.d(LoginActivity.LOG_TAG, s);
            }
        }
    }

}
