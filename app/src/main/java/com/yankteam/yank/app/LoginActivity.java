package com.yankteam.yank.app;

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
    }

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
    }
}
