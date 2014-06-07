package com.yankteam.yank.app;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.widget.Toast.LENGTH_SHORT;

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

    // for the camera

    String FileName;
    ImageView imageViewPhoto;
    private static final int CAMERA_WITH_DATA = 3023;
    private static final int PHOTO_PICKED_WITH_DATA = 3021;

    private File mCurrentPhotoFile = null; // picture captured from camera
    // path to store the picture
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/ASoohue/CameraCache");

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
        image_view.setClickable(true);

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

        image_view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                doPickPhotoAction();
                Toast.makeText(CreationActivity.this, "new yank !", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (mCurrentPhotoFile == null)
            mCurrentPhotoFile = appInfo.myImageFile;
    }

    // helper functions for the camera and album pick
    private void doPickPhotoAction() {
        Context context = this;
        // Wrap our context to inflate list items using correct theme
        final Context dialogContext = new ContextThemeWrapper(context, android.R.style.Theme_Light);
        String cancel = "Cancel";
        String[] choices;
        choices = new String[2];
        choices[0] = getString(R.string.take_photo);
        choices[1] = getString(R.string.pick_photo);
        final ListAdapter adapter = new ArrayAdapter<String>(dialogContext, android.R.layout.simple_list_item_1,
                choices);
        final AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
        builder.setTitle(R.string.attachToContact);
        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0: {
                        String status = Environment.getExternalStorageState();
                        if (status.equals(Environment.MEDIA_MOUNTED)) { // if there's SD card
                            doTakePhoto(); // take a picture from the camera
                        } else {
                            Toast.makeText(CreationActivity.this, "No SD card available", Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
                    case 1:
                        doPickPhotoFromGallery(); // get one from the user's album
                        break;
                }
            }
        });
        builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    private void doTakePhoto() {
        try {
            // Launch camera to take photo for selected contact
            if (!PHOTO_DIR.exists()) {
                boolean iscreat = PHOTO_DIR.mkdirs(); // build the save path
            }
            FileName = System.currentTimeMillis() + ".jpg";
            mCurrentPhotoFile = new File(PHOTO_DIR, FileName);
            appInfo.myImageFile = mCurrentPhotoFile;
            final Intent intent = getTakePickIntent(mCurrentPhotoFile);
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "photoPickerNotFoundText", Toast.LENGTH_LONG).show();
        }
    }

    public static Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }

    public static Intent getPhotoPickIntent(File f) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        return intent;
    }

    // name the picture using the current time
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date) + ".jpg";
    }

    // call the Gallery activity
    protected void doPickPhotoFromGallery() {
        try {
            Intent intent = getPhotoPickIntent();
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.photoPickerNotFoundText, Toast.LENGTH_LONG).show();
        }
    }

    // issue the Gallery request intent
    public static Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        return intent;
    }

    // return from the two get-picture activities
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PHOTO_PICKED_WITH_DATA:
                // I think both cases here should start a new ac to compose a description and  then
                // do an upload
                Uri uri = data.getData();
                image_view.setImageURI(uri);
                break;
            case CAMERA_WITH_DATA:
                //Uri uri_cam = data.getData();
                Bitmap photo = null;
                try {
                    photo = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(mCurrentPhotoFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image_view.setImageBitmap(photo);

                //image_view.setImageURI(uri_cam);
                break;
        }
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
                json.put("lat", appInfo.my_lat);
                json.put("lng", appInfo.my_lng);

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
