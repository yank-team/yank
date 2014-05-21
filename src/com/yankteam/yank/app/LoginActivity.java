package com.yankteam.yank.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URL;

public class LoginActivity extends ActionBarActivity {

    Button mLoginButton;
    Button mSignupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginButton  = (Button) findViewById(R.id.btn_login);
        mSignupButton = (Button) findViewById(R.id.btn_signup);

        // For now, we're just going to switch automatically
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLobby();
            }
        });
    }

    // in onResume, we need to pull a saved APIK out of our shared prefs. If it doesn't exist,
    // allow the user to login/signup -- if it exists verify that the key is valid
    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sPrefs = this.getPreferences(Context.MODE_PRIVATE);
        String apik = sPrefs.getString("YANK_APIK", null);

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
}
