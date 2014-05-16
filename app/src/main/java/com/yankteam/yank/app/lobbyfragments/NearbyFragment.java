package com.yankteam.yank.app.lobbyfragments;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.widget.ListView;

import com.yankteam.yank.app.Entity;
import com.yankteam.yank.app.EntityList;
import com.yankteam.yank.app.R;

/*
 * NearbyFragment
 * auto-searches yanks in the immediate vicinity -- displays them on a list.
 */
public class NearbyFragment extends Fragment {

    // Lorem ipsum text for testing
    String lorem = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod " +
            "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim";

    // Test list view
    Entity[] entities = {
        new Entity("Porter Squiggle", lorem),
        new Entity("Porter Chessboard", lorem),
        new Entity("Hungry Slug Burritos", lorem),
        new Entity("Owl's Nest Teriyaki", lorem),
        new Entity("Porter Squiggle", lorem),
        new Entity("Porter Chessboard", lorem),
        new Entity("Hungry Slug Burritos", lorem),
        new Entity("Owl's Nest Teriyaki", lorem)
    };

    ListView mEntityList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (container == null)
            return null;

        View view = inflater.inflate(R.layout.fragment_lobby_nearby, container, false);

        mEntityList = (ListView) view.findViewById(R.id.list_nearby_entities);
        mEntityList.setAdapter(new EntityList(getActivity(), entities));

        return view;
    }
}
