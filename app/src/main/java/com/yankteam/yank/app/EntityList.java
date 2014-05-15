package com.yankteam.yank.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * List adapter for entities
 */
public class EntityList extends ArrayAdapter<Entity> {

    private final Activity context;
    private final Entity[] entities;

    // a holder for a list item's view
    static class ViewHolder {
        public ImageView avatar;
        public TextView  name;
    }

    public EntityList(Activity context, Entity[] entities) {
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
            outgoingHolder.avatar = (ImageView) rowView.findViewById(R.id.img_entity_avatar);
            outgoingHolder.name   = (TextView) rowView.findViewById(R.id.str_entity_name);
            rowView.setTag(outgoingHolder);
        }

        // pull values out of the view holder
        ViewHolder holder = (ViewHolder) rowView.getTag();

        // Set values of layout components here
        holder.name.setText( entities[pos].getName());

        return rowView;
    }
}
