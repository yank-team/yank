package com.yankteam.yank.app.components;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yankteam.yank.app.R;

import java.util.List;

/* Holder for a list of Notes */
public class NoteList  extends ArrayAdapter<Note> {
    private final Activity context;
    private final List<Note> notes;

    static class ViewHolder {
        public TextView username;
        public TextView content;
    }

    public NoteList(Activity context, List <Note> notes) {
        super(context, R.layout.entity_list_item, notes);
        this.context = context;
        this.notes   = notes;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.note_list_item, null);

            ViewHolder outgoingHolder = new ViewHolder();
            outgoingHolder.username = (TextView) convertView.findViewById(R.id.str_note_username);
            outgoingHolder.content  = (TextView) convertView.findViewById(R.id.str_note_content);

            convertView.setTag(outgoingHolder);
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();

        Log.d("NoteList", notes.get(pos).getContent());
        holder.username.setText(notes.get(pos).getUsername());
        holder.content.setText(notes.get(pos).getContent());

        return convertView;
    }
}
