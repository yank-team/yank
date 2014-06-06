package com.yankteam.yank.app.API;

import android.content.Context;
import android.os.AsyncTask;
import com.yankteam.yank.app.R;

import org.apache.http.client.methods.HttpPost;

/*
 * ProxSearchTask
 * Implements the proximity search API Call
 */
public class ProxSearchTask extends AsyncTask<Double, Void, String> {

    private Context context;
    private String url;

    public ProxSearchTask (Context _context) {
        context = _context;
        url = context.getString(R.string.jheron_api) + "entity/radius/";
    }

    @Override
    protected String doInBackground(Double... params) {
        return null;
    }

    private HttpPost assembleReq (Integer radius, Double lat, Double lng) {

        HttpPost postReq = new HttpPost();

        return null;
    }
}
