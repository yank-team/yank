package com.yankteam.yank.app;

<<<<<<< HEAD
=======
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
>>>>>>> master
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yankteam.yank.app.API.LoginTask;

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
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

<<<<<<< HEAD
    /* trigger Login async task */
    public void triggerLogin () {
        // gather login info from the edit boxes. Send that to the API Login
        // asynctask
        EditText mEtxtUsername = (EditText)findViewById(R.id.etxt_username);
        EditText mEtxtPassword = (EditText)findViewById(R.id.etxt_password);
        new LoginTask(this).execute(
            mEtxtUsername.getText().toString(),
            mEtxtPassword.getText().toString()
        );
=======
    // in onResume, we need to pull a saved APIK out of our shared prefs. If it doesn't exist,
    // allow the user to login/signup -- if it exists verify that the key is valid
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sPrefs = this.getPreferences(Context.MODE_PRIVATE);
        String apik = sPrefs.getString("YANK_APIK", null);

        // if there's an APIK, try it
        /* TODO: flesh this out
        if (apik != null) {
        }
        */
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
            HttpPost postReq = new HttpPost("http://jheron.io/auth/login/");
            HttpResponse response;

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
                    String line;
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
                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Connection failed").setMessage("Please check your connection")
                        .setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog
                                dialog.dismiss();
                            }
                        });

                //builder.create();
                builder.show();
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
>>>>>>> master
    }
}
