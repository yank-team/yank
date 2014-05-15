package com.yankteam.yank.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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

    /* switch activities */
    private void gotoLobby() {
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }
}
