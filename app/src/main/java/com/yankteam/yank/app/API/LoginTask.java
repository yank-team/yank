package com.yankteam.yank.app.API;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.yankteam.yank.app.AppInfo;
import com.yankteam.yank.app.LobbyActivity;
import com.yankteam.yank.app.R;

import org.apache.http.HttpResponse;
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

/*
 * Async-Task to log into the API
 */
public class LoginTask extends AsyncTask<String, Void, String> {

    Context context;

    public LoginTask(Context _context) {
        context = _context;
    }

    @Override
    protected String doInBackground(String... params) {
        return executeReq(asmPostReq(params[0], params[1]));
    }

    /* assemble a JSON POST request */
    private HttpPost asmPostReq (String username, String passwd) {
        // Assemble post request
        String url          = context.getString(R.string.jheron_api) + "auth/login/";
        HttpPost postReq    = new HttpPost(url);
        JSONObject reqBody  = asmReqBody(username, passwd);
        StringEntity entity = null;

        // Attempt to set headers
        try {
            entity = new StringEntity(reqBody.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            postReq.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            // something bad happened -- abort
            e.printStackTrace();
            return null;
        }
        return postReq;
    }

    /* assemble the body of a JSON POST request */
    private JSONObject asmReqBody(String username, String passwd) {
        JSONObject reqBody = new JSONObject();
        try {
            reqBody.put("username", username);
            reqBody.put("password", passwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reqBody;
    }

    /* execute a post request -- return the contents of the response as a string */
    private String executeReq (HttpPost postReq) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse response = httpClient.execute(postReq);
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

    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject mainObject    = new JSONObject(s);
            Boolean success_response = mainObject.getBoolean("success");
            String msg_response      = mainObject.getString("msg");
            JSONObject data_response = mainObject.getJSONObject("data");
            String api_key = data_response.getString("apik");

            if(success_response){
                //store api key in AppInfo singleton
                AppInfo appInfo = AppInfo.getInstance();
                appInfo.api_key = api_key;

                //send user through to the lobby
                Intent intent = new Intent(context, LobbyActivity.class);
                context.startActivity(intent);
            }
        } catch (JSONException e) {
            //ask user to retry login
            e.printStackTrace();
        }
    }
}
