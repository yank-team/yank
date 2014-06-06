package com.yankteam.yank.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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


public class SignupActivity extends ActionBarActivity {

    private final String LOG_TAG = "SingupActivity";

    private String password;
    private String confirm_pw;
    private String uid;

    private EditText pw;
    private EditText user_id;
    private EditText confirm;

    AppInfo appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Log.i(LOG_TAG , "in signup activity");

        user_id = (EditText) findViewById(R.id.userid);
        pw = (EditText) findViewById(R.id.password);
        confirm = (EditText) findViewById(R.id.confirm_pw);

        Button submit_btn = (Button) findViewById(R.id.submit);

        Log.i(LOG_TAG, "got all the views");

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = pw.getText().toString();
                confirm_pw = confirm.getText().toString();
                uid = user_id.getText().toString();

                if (!password.equals(confirm_pw))
                    Toast.makeText(SignupActivity.this, "Password not the same", Toast.LENGTH_SHORT).show();

                else {
                    new SignupTask().execute(uid, password);
                }
            }
        });

    }

    private class SignupTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postReq = new HttpPost(getString(R.string.jheron_api) + "auth/");
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
                e.printStackTrace();
            }

            // Return null if failure occurs
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Log.i(LOG_TAG, "Signup json: " + s);
                JSONObject mainObject = new JSONObject(s);
                Boolean success_response = mainObject.getBoolean("success");
                String msg_response = mainObject.getString("msg");

                Log.i(LOG_TAG, "success_response: " + success_response);

                if(success_response){
                    Toast.makeText(SignupActivity.this, "Signed up!", Toast.LENGTH_SHORT).show();
                    //send user through
                    finish();
                }else {

                    Log.i(LOG_TAG, "sign up failed");
                    final AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setTitle(R.string.dialog_signup_failure).setMessage(msg_response)
                            .setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // cancel the dialog
                                    dialog.dismiss();
                                }
                            });

                    //builder.create();
                    builder.show();
                    // clear the texts
                    user_id.setText("");
                    pw.setText("");
                    confirm.setText("");
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
