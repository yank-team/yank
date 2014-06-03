package com.yankteam.yank.app.ORM;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.yankteam.yank.app.R;
import com.yankteam.yank.app.components.Entity;
import com.yankteam.yank.app.ORM.models.ModelEntity;
import com.yankteam.yank.app.ORM.models.ModelSQL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/*
 * Yank Object-Relational Model
 * provides really simple bindings for SQLite so we can cache yank-server data
 * without going crazy.
 *
 * We're also using this as the single-source of truth for API data...
 *
 * TODO: implement this as a service so the UI thread is put under less stress by this
 */
public class YankORM {

    // basic items
    private SQLiteDatabase db;
    private YankORMHelper helper;
    private Context context;

    /*
     * Local data -- these data structures keep a local copy of data fresh for any UI thread
     * item that might want them.
     */
    HashMap<Integer, Entity> entityHashMap;

    public YankORM(Context context) {
        helper        = new YankORMHelper(context);
        entityHashMap = new HashMap<Integer, Entity>();
        context       = context;
    }

    public void openWritable() throws SQLException {
        db = helper.getWritableDatabase();
    }
    public void openReadable() throws SQLException {
        db = helper.getReadableDatabase();
    }
    public void close() {
        helper.close();
    }

    /* Attachment points -- we call these with  */

    // Update data set with nearby entities
    public void requestNearbyEntities() {
        // start async task

        // start request-unknowns -- pass through callback
    }

    // Retrieve unknown EIDs from the server
    public void requestUnknownEids() {
        // start async task

        // call the callback
    }

    /* API Response processing helpers -- this is where the DB comes into play */

    // After performing a nearby entity search, compare what comes back to what's on the
    // local database
    private ArrayList<Integer> processNearbyEntities (JSONArray entities) throws JSONException {

        // declare a list of IDs to test for existence in the
        // cache
        ArrayList<Integer> testList = new ArrayList<Integer>();

        // build an SQL query out of the given JSON array
        JSONObject entity;
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(ModelSQL.SELECT_ALL);
        queryBuilder.append(ModelEntity.EntityItem.TABLE_NAME);
        queryBuilder.append(" WHERE ");

        for (int i=0; i<entities.length(); i++) {
            // build up the query as well as the test list
            Integer eid = entities.getJSONObject(i).getInt("eid");
            testList.add(eid);

            queryBuilder.append("`eid`=");
            queryBuilder.append(eid);

            // OR clause, or the end of the list
            queryBuilder.append( i < (entities.length()-1) ? " OR " : "" );
        }
        // Ensure we order by increasing IDs
        queryBuilder.append(" ORDER BY `eid` ASC;");
        String sqlQuery = queryBuilder.toString();

        // sort the test list to prepare for comparison
        Collections.sort(testList);

        // Connect to DB -- perform query
        Cursor sqlCursor;
        try {
            openReadable();
            sqlCursor = db.rawQuery(sqlQuery, null);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        // check what came back
        sqlCursor.moveToFirst();
        for (Integer i : testList) {
            // quit if we're out of items from the DB
            if (sqlCursor.isAfterLast()) { break; }

            // remove i from test list if it's on the DB
            if (sqlCursor.getInt(0) == i) {
                // we also want to ensure we loaded it into memory already...
                pingEntityHmap(i, sqlCursor.getString(1), sqlCursor.getDouble(2),
                        sqlCursor.getDouble(3));
                testList.remove(i);
            }
        }
        sqlCursor.close();
        // close connection
        close();

        // what remains is everything we couldn't find on the DB
        return testList;
    }

    /* Hashmap manipulation helpers */

    // check hashmap for index, insert it if it's not there
    private void pingEntityHmap(Integer i, String name, Double lat, Double lng) {
        if (!entityHashMap.containsKey(i)) {
            // create an entity for this DB data while we're here
            entityHashMap.put(i, new Entity(i, name, lat, lng));
        }
    }

    /* Async Task JSON helpers */

    // construct a radius JSON request body
    public JSONObject entityRadiusReq(Integer radius, Double lat, Double lng) {
        JSONObject json = new JSONObject();
        // Prepare post data
        try {
            json.put("radius", radius);
            json.put("lat"   , lat);
            json.put("lng"   , lng);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    /*
     * API connection asynctasks -- we implement API calls as asynchronous tasks that queue up and
     * connect to the API to refresh the local data set.
     *
     * Every time this happens, we update that data on the SQLite database. Since the tasks are
     * asynchronous, they can all monitor a semaphore to prevent a race condition from occurring
     * on the database.
     */

    // get nearby entities -- no search terms -- just everything around the current position
    private class GetNearbyEntitiesTask extends AsyncTask<Double, Void, String> {
        @Override
        protected String doInBackground(Double... args) {

            // connect to the API
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postReq = new HttpPost(context.getString(R.string.jheron_api)+"entity/radius/");
            HttpResponse response = null;

            // passing radius as a Double -- kind of jankey, but it should work.
            JSONObject json = entityRadiusReq(args[0].intValue(), args[1], args[2]);

            try {
                // prepare the request
                StringEntity entity = new StringEntity(json.toString());
                postReq.setEntity(entity);
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                postReq.setEntity(entity);

                // send HTTP request
                response = httpClient.execute(postReq);

                // Pull apart the request with an input stream
                if (response != null){
                    InputStream in = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    return builder.toString();
                }
                return null;

            // TODO: handle all of these exceptions properly
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        // TODO: this is monsterous.. we need to move this out into a function
        // or something
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                // get root JSON object
                JSONObject root = new JSONObject(s);
                // if we failed, we're done here
                if (!root.getBoolean("success")) { return; }
                ArrayList<Integer> unknownEIDs = processNearbyEntities(root.getJSONArray("data"));

                // Now we need to trigger another async task to finish the job.
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Retrieve all entities with the given EIDs. this is a MUCH simpler query, and
    // what it returns is simply pushed to the database/hashmap and we quit.
}
