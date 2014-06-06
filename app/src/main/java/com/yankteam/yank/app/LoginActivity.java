package com.yankteam.yank.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.yankteam.yank.app.API.LoginTask;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

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
        EditText mEtxtUsername = (EditText) findViewById(R.id.etxt_username);
        EditText mEtxtPassword = (EditText) findViewById(R.id.etxt_password);
        new LoginTask(this).execute(
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
}
