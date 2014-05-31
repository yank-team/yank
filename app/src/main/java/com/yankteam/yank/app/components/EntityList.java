package com.yankteam.yank.app.components;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yankteam.yank.app.AppInfo;
import com.yankteam.yank.app.LobbyActivity;
import com.yankteam.yank.app.R;
import com.yankteam.yank.app.ReadNoteActivity;
import com.yankteam.yank.app.components.Entity;

import java.util.List;

/*
 * List adapter for entities
 */
public class EntityList extends ArrayAdapter<Entity> {

    private final Activity context;
    private final List<Entity> entities;
    AppInfo appInfo = null;

    Animation animSlideDown = AnimationUtils.loadAnimation(getContext(), R.anim.abc_slide_out_bottom);

    // a holder for a list item's view
    static class ViewHolder {
        public ImageView avatar;
        public TextView  name;
        public TextView  desc;
        public LinearLayout ctxButtons;
    }

    public EntityList(Activity context, List <Entity> entities) {
        super(context, R.layout.entity_list_item, entities);
        this.context  = context;
        this.entities = entities;
    }

    @Override
    public View getView (int pos, View convertView, ViewGroup parent) {

        View rowView = convertView;

        // check for existing view
        if (rowView == null) {
            // inflate layout
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.entity_list_item, null);

            // configure view holder and tag it to the row view
            ViewHolder outgoingHolder = new ViewHolder();
            outgoingHolder.avatar     = (ImageView) rowView.findViewById(R.id.img_entity_avatar);
            outgoingHolder.name       = (TextView)  rowView.findViewById(R.id.str_entity_name);
            outgoingHolder.desc       = (TextView)  rowView.findViewById(R.id.str_entity_desc);
            outgoingHolder.ctxButtons = (LinearLayout) rowView.findViewById(R.id.entity_list_buttons);
            rowView.setTag(outgoingHolder);
        }

        // pull values out of the view holder
        final ViewHolder holder = (ViewHolder) rowView.getTag();

        // Set values of layout components here
        holder.name.setText(entities.get(pos).getName());
        holder.desc.setText(entities.get(pos).getDesc());

        // set up a click listener
        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // toggle button visibility
                if (holder.ctxButtons.getVisibility() == View.GONE){
                    holder.ctxButtons.setVisibility(View.VISIBLE);
                } else {
                    holder.ctxButtons.setVisibility(View.GONE);
                }
                return true;
            }
        });

        // set up a click listener for note button
        ImageButton noteButton = (ImageButton) rowView.findViewById(R.id.note_button);
        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // store the eid and launch the ReadNoteActivity
                appInfo = AppInfo.getInstance();
                appInfo.eid = entities.get(pos).getEid();

                Intent intent = new Intent(getContext(), ReadNoteActivity.class);
                startActivity
            }
        });


        return rowView;
    }
}
