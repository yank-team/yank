package com.yankteam.yank.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/*
 * EntityProfileActivity
 * contains a profile for an entity -- includes the name, an image, details about the
 * entity, and a list of notes tied to the entity generated by users.
 */
public class EntityProfileActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_profile);
    }
}
