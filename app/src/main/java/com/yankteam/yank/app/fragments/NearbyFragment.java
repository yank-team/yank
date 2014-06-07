package com.yankteam.yank.app.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.widget.Button;
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
import java.util.List;

/*
 * NearbyFragment
 * auto-searches yanks in the immediate vicinity -- displays them on a list.
 */
public class NearbyFragment extends Fragment {
    public static final String LOG_TAG = "NearbyFragment";

    AppInfo appInfo = AppInfo.getInstance();
    List <Entity> entity_list = new ArrayList<Entity>();
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (container == null)
            return null;

        //give lat an lng
        new ProxSearchTask().execute(appInfo.my_lat, appInfo.my_lng);
        view = inflater.inflate(R.layout.fragment_lobby_nearby, container, false);

        ListView mEntityList = (ListView) view.findViewById(R.id.list_nearby_entities);
        mEntityList.setAdapter(new EntityList(getActivity(), entity_list));

        return view;
    }

    public void onRefresh() {
        new ProxSearchTask().execute(appInfo.my_lat, appInfo.my_lng);
    }

    private class ProxSearchTask extends AsyncTask<Double, Void, String> {

        public static final String LOG_TAG = "ProxSearchTask";

        private String url;

        public ProxSearchTask () {
            url = getString(R.string.jheron_api) + "entity/radius/";
        }

        private JSONObject asmReqBody(Double lat, Double lng) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("lat", lat);
                obj.put("lng", lng);
                obj.put("radius", 5000);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return obj;
        }

        private HttpPost asmPostReq(Double lat, Double lng){
            HttpPost req    = new HttpPost(url);
            JSONObject body = asmReqBody(lat, lng);
            StringEntity entity = null;
            try {
                entity = new StringEntity(body.toString());
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                req.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return req;
        }

        @Override
        protected String doInBackground(Double... params) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost req = asmPostReq(params[0], params[1]);
            HttpResponse response = null;

            // Set up a JSON request body
            JSONObject json = new JSONObject();
            try {
                // Send request
                response = httpClient.execute(req);

                // Pull apart the request with an input stream
                if (response != null){
                    InputStream in = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder builder = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    return builder.toString();
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ArrayList<Entity> entityList = new ArrayList<Entity>();

            Log.d(LOG_TAG, s);

            try {
                JSONObject mainObject = new JSONObject(s);

                Boolean success_response = mainObject.getBoolean("success");
                String msg_response = mainObject.getString("msg");
                JSONArray data_response = mainObject.getJSONArray("data");

                if(success_response){
                    for(int n = 0; n < data_response.length(); n++){
                        JSONObject object = data_response.getJSONObject(n);

                        double lat = object.getDouble("lat");
                        double lng = object.getDouble("lng");
                        String name = object.getString("name");
                        Entity tmpEntity = new Entity(0, name, lat, lng);
                        entityList.add(tmpEntity);
                    }
                }

                // TODO: don't make a whole new adapter for this.
                ListView mEntityList = (ListView) view.findViewById(R.id.list_nearby_entities);
                mEntityList.setAdapter(new EntityList(getActivity(), entityList));

            } catch (JSONException e) {
                //ask user to retry login
                e.printStackTrace();
                // Log.d(NearbyFragment.LOG_TAG, "fragment pull failed: likely at server");
            }
        }
    }
}
