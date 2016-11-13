package com.misha.mor.letsteamapp.letsteamapp;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Misha on 9/22/2016.
 */
public class CustomGridViewAdapter extends ArrayAdapter<Event> {
    Context context;
    int layoutResourceId;
    ArrayList<Event> data;



    public CustomGridViewAdapter(Context context, int layoutResourceId, ArrayList<Event> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;
        if (row == null) { LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.item_text);
            holder.imageItem = (ImageView) row.findViewById(R.id.item_image);
           /* holder.imageItem.getLayoutParams().height = R.integer.IMAGE_HEIGHT;
            holder.imageItem.getLayoutParams().width = R.integer.IMAGE_WIDTH;
            holder.imageItem.setScaleType(ImageView.ScaleType.FIT_XY);*/


            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        Event item = data.get(position);
        holder.txtTitle.setText(item.getEvent_DisplayName());
        String caseS = item.getEvent_Type();
        switch (caseS){
            case "local trip":
                holder.imageItem.setImageResource(R.drawable.trip);
                break;
            case "sport":
                holder.imageItem.setImageResource(R.drawable.sport);
                break;
            case "study":
                holder.imageItem.setImageResource(R.drawable.study);
                break;
            case "shopping":
                holder.imageItem.setImageResource(R.drawable.shopping);
                break;
            case "night entertainment":
                holder.imageItem.setImageResource(R.drawable.night_entertainment);
                break;
            case "abroad":
                holder.imageItem.setImageResource(R.drawable.passport);
                break;
            default:
                holder.imageItem.setImageResource(R.drawable.other);
                break;
        }
        holder.roomID = item.getEvent_ID();
        return row;
    }

    static class RecordHolder {
        TextView txtTitle;
        ImageView imageItem;
        String roomID;
    }

}