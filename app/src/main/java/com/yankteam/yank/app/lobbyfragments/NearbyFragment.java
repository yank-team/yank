package com.yankteam.yank.app.lobbyfragments;

import android.content.IntentSender;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.yankteam.yank.app.AppInfo;
import com.yankteam.yank.app.components.Entity;
import com.yankteam.yank.app.components.EntityList;
import com.yankteam.yank.app.R;

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
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/*
 * NearbyFragment
 * auto-searches yanks in the immediate vicinity -- displays them on a list.
 */
public class NearbyFragment extends Fragment {
    public static final String LOG_TAG = "NearbyFragment";

    AppInfo appInfo = AppInfo.getInstance();
    String api_key = appInfo.api_key;
    List <Entity> entity_list = new ArrayList<Entity>();
    Entity entity;
    ListView mEntityList;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (container == null)
            return null;

        //give lat an lng
        new GetNearEntitiesTask().execute(0, 0);
        view = inflater.inflate(R.layout.fragment_lobby_nearby, container, false);
        return view;
    }

    private class GetNearEntitiesTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... args) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet getReq = new HttpGet(getString(R.string.jheron_api) + "/entity/");
            HttpResponse response = null;

            // Set up a JSON request body
            JSONObject json = new JSONObject();
            try {
                // Prepare post data
                // json.put("apik", api_key );
                // json.put("lat", args[0] );
                // json.put("lng", args[1] );

                // Assemble request
                // StringEntity entity = new StringEntity(json.toString());
                // entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                // getReq.setEntity(entity);

                // Send request
                response = httpClient.execute(getReq);

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
                Log.d(NearbyFragment.LOG_TAG, "fragment pull json: " + s);
                JSONObject mainObject = new JSONObject(s);
                Log.d(NearbyFragment.LOG_TAG, s);
                Boolean success_response = mainObject.getBoolean("success");
                String msg_response = mainObject.getString("msg");
                JSONArray data_response = mainObject.getJSONArray("data");

                if(success_response){
                    for(int n = 0; n < data_response.length(); n++){
                        JSONObject object = data_response.getJSONObject(n);

                        double lat = object.getDouble("lat");
                        double lng = object.getDouble("lng");
                        String name = object.getString("name");
                        entity = new Entity(0, name, lat, lng);
                        entity_list.add(entity);
                    }
                    mEntityList = (ListView) view.findViewById(R.id.list_nearby_entities);
                    mEntityList.setAdapter(new EntityList(getActivity(), entity_list));
                    Log.d(NearbyFragment.LOG_TAG, msg_response);
                }else {
                    //shit failed
                    Log.d(NearbyFragment.LOG_TAG, "fragment pull failed: likely user");
                    Log.d(NearbyFragment.LOG_TAG, msg_response);

                    //toast user, bad login
                }
            } catch (JSONException e) {
                //ask user to retry login
                e.printStackTrace();
                Log.d(NearbyFragment.LOG_TAG, "fragment pull failed: likely at server");
            }
        }
    }
}
