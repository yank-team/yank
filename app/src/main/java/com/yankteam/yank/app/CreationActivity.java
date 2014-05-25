package com.yankteam.yank.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

/**
 * Created by jason on 5/24/14.
 */
public class CreationActivity extends ActionBarActivity {

    public static final String LOG_TAG = "CreationActivity";
    EditText title_field;
    EditText full_text_field;
    ImageView image_view;
    Button submit_button;
    AppInfo appInfo;
    String api_key;
    Integer entity_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);
        appInfo = AppInfo.getInstance();
        api_key = appInfo.api_key;

        // Get references to layout components
        submit_button = (Button) findViewById(R.id.submit_button);
        title_field = (EditText) findViewById(R.id.title_field);
        full_text_field = (EditText) findViewById(R.id.full_text_field);
        image_view = (ImageView) findViewById(R.id.image_view);

        // For now, we're just going to switch automatically
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initiate an asynctask
                new SubmitEntityTask().execute(
                        title_field.getText().toString(),
                        full_text_field.getText().toString());
                gotoLobby();
            }
        });
    }

    private class SubmitEntityTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postReq = new HttpPost(getString(R.string.jheron_api) + "/entity/compound/");
            HttpResponse response = null;

            // Set up a JSON request body
            JSONObject json = new JSONObject();
            try {
                // Prepare post data
                json.put("apik", api_key);
                json.put("name", args[0]);
                json.put("content", args[1]);
                json.put("lat", 0);
                json.put("lng", 0);

                // Assemble request
                Log.d(CreationActivity.LOG_TAG, "entity creation: json creation: " + json.toString());
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
                Log.d(CreationActivity.LOG_TAG, "entity creation: json response" + s);
                JSONObject mainObject = new JSONObject(s);
                Log.d(LoginActivity.LOG_TAG, s);
                Boolean success_response = mainObject.getBoolean("success");
                String msg_response = mainObject.getString("msg");
                JSONObject data_response = mainObject.getJSONObject("data");
                entity_id = data_response.getInt("eid");

                if(success_response){
                    //now add the note
                    Log.d(CreationActivity.LOG_TAG, "entity creation: worked");
                    Log.d(CreationActivity.LOG_TAG, msg_response);
                }else {
                    //shit failed
                    Log.d(CreationActivity.LOG_TAG, "entity creation: failed likely user");
                    Log.d(CreationActivity.LOG_TAG, msg_response);
                }
            } catch (JSONException e) {
                //whatever
                e.printStackTrace();
                Log.d(CreationActivity.LOG_TAG, "entity creation: failed likely server (JSON EXCEPTION)");

            }
        }
    }

    private void gotoLobby() {
        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }
}
