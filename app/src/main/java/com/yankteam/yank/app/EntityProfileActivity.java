package com.yankteam.yank.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.yankteam.yank.app.components.Note;
import com.yankteam.yank.app.components.NoteList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class EntityProfileActivity extends ActionBarActivity {

    private Integer eid;
    private ArrayList<Note> notes;
    private AppInfo appInfo = AppInfo.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_profile);
        eid   = getIntent().getExtras().getInt("EID");
        notes = new ArrayList<Note>();

        Button mBtnAddNote = (Button) findViewById(R.id.btn_add_note);
        mBtnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mEditNote = (EditText) findViewById(R.id.edit_note_content);
                new PostNotesTask().execute(mEditNote.getText().toString());
            }
        });

        // Launch async task
        new GetNotesTask().execute(eid);
    }

    @Override
    public void onResume() {
        super.onResume();
        notes = new ArrayList<Note>();
    }

    public void resetAdapter() {
        ListView noteList = (ListView) findViewById(R.id.list_entity_notes);
        noteList.setAdapter(new NoteList(this, notes));
    }

    /*
     * get annotations from the server
     */
    private class GetNotesTask extends AsyncTask<Integer, String, String> {

        @Override
        protected String doInBackground(Integer... params) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet req = new HttpGet(getString(R.string.jheron_api) + "entity/note/" + eid + "/");
            try {
                HttpResponse response = httpClient.execute(req);
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) { return; }

            Log.d("ENTITY", s);

            try {
                JSONObject json = new JSONObject(s);
                Boolean success = json.getBoolean("success");
                JSONArray data_response = json.getJSONArray("data");

                if (success) {
                    JSONObject tmp = null;

                    for (int i=0; i<data_response.length(); i++) {
                        tmp = data_response.getJSONObject(i);

                        notes.add(new Note(
                            tmp.getInt("id"),
                            tmp.getString("owner"),
                            tmp.getString("content")
                        ));
                    }
                    resetAdapter();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Post an annotation to the server
     */
    private class PostNotesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            DefaultHttpClient http = new DefaultHttpClient();
            HttpPost req = new HttpPost(getString(R.string.jheron_api) + "/entity/note/");
            HttpResponse response;
            JSONObject json = new JSONObject();

            try {
                json.put("apik", appInfo.api_key);
                json.put("eid", eid);
                json.put("content", params[0]);

                StringEntity entity = new StringEntity(json.toString());
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                req.setEntity(entity);

                response = http.execute(req);

                if(response != null) {
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
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            String toast;
            if (s == null){
                toast = "note could not be posted";
            } else {
                toast = "note posted";
            }
            Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT)
                .show();
        }
    }
}
