package com.yankteam.yank.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import com.yankteam.yank.app.components.Note;
import com.yankteam.yank.app.components.NoteList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class EntityProfileActivity extends ActionBarActivity {

    private Integer eid;
    private ArrayList<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_profile);
        eid = savedInstanceState.getInt("EID");

        // Launch async task
    }

    public void resetAdapter() {
        ListView noteList = (ListView) findViewById(R.id.list_entity_notes);
        noteList.setAdapter(new NoteList(this,notes));
    }

    private class GetNotesTask extends AsyncTask<Integer, String, String> {

        @Override
        protected String doInBackground(Integer... params) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet req = new HttpGet(getString(R.string.jheron_api) + "entity/note/" + eid);
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

            try {
                JSONObject json = new JSONObject(s);
                Boolean success = json.getBoolean("success");
                JSONArray data_response = json.getJSONArray("data");

                if (success) {
                    JSONObject tmp;

                    notes.clear();
                    for (int i=0; i<data_response.length(); i++) {
                        tmp = data_response.getJSONObject(i);

                        notes.add(new Note(
                            tmp.getInt("id"),
                            tmp.getInt("owner"),
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
}
