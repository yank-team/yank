package com.yankteam.yank.app.fragments;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;

import com.yankteam.yank.app.R;


/**
 * Created by root on 5/12/14.
 */
public class SavedFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (container == null)
            return null;

        return inflater.inflate(R.layout.fragment_lobby_saved, container, false);

    }
}