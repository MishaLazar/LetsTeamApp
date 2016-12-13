package com.misha.mor.letsteamapp.letsteamapp;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Misha on 12/10/2016.
 */
public class EventMenuArrayAdapter extends ArrayAdapter<Event> {


    String userID;
    String userName;
    List<Event> eventArrayList = new ArrayList<>();
    Context context;
    Event currentEvent;
    Event event;


    @Override
    public void add(Event object) {
        this.currentEvent = object;
        eventArrayList.add(currentEvent);
        /*Collections.sort(eventArrayList,Comparators.MessageOrder2);*/
        super.add(currentEvent);
    }

    public EventMenuArrayAdapter(Context context, int textViewResourceId ,String userID,String userName) {
        super(context, textViewResourceId);
        this.context = context;
        this.userID = userID;
        this.userName = userName;



    }
    public EventMenuArrayAdapter(Context context, int textViewResourceId ,String userID,List<Event> data) {
        super(context, textViewResourceId,data);
        this.context = context;
        this.userID = userID;
        this.eventArrayList = data;



    }
    public void clearListAdapter(){
        eventArrayList.clear();
    }

    public int getCount() {
        return this.eventArrayList.size();
    }

    public Event getItem(int index) {
        return this.eventArrayList.get(index);
    }
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;
        event = getItem(position);


        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.activity_list_event_context, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String eventLocation;

        holder.editTextContext.setText(event.getEvent_Context());
        holder.EventNameHeadrer.setText(event.getEvent_DisplayName());
        if(event.getEvent_location().length()<= 3){
            eventLocation = "Unspecified";
        }else {
            eventLocation = event.getEvent_location();
        }
        holder.editTextLocation.setText(eventLocation);
        holder.editTextSatrtDate.setText(event.getEvent_Start());
        holder.editTextEndDate.setText(event.getEvent_End());
        holder.editTextName.setText(event.getEvent_OwnerName());
       /* setIEventImg(eventType);
        try {
            holder.messageOwnerProfilePic.setImageBitmap(
                    ImageConverter.getRoundedCornerBitmap(ImageConverter.StringToBitMap(
                            chatMessage.getBitmapStringUserPic()),40));
        }catch (NullPointerException exc){
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.default_man);
            holder.messageOwnerProfilePic.setImageBitmap(
                    ImageConverter.getRoundedCornerBitmap(icon,40));
        }*/


        return convertView;
    }



    private ViewHolder createViewHolder(View v) {
        final ViewHolder holder = new ViewHolder();
        holder. editTextContext = (TextView)v.findViewById(R.id.etxrContext2);
        holder.editTextLocation = (TextView)v.findViewById(R.id.etxtLocation);
        holder.editTextName = (TextView) v.findViewById(R.id.etxtCreatorName);
        holder.EventNameHeadrer = (TextView)v.findViewById(R.id.txtEventName);
        holder.editTextSatrtDate = (TextView)v.findViewById(R.id.etxtEventStartDate);
        holder.editTextEndDate = (TextView)v.findViewById(R.id.etxtEventEndDate);
        holder.roomID = event.getEvent_ID();
        holder.btn_openEventChat = (ImageButton) v.findViewById(R.id.btnOpenChat);
        holder.btn_openEventChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ChatRoom.class);
                intent.putExtra("eventID",holder.roomID);
                intent.putExtra("userID",userID);
                intent.putExtra("userName",userName);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    context.startActivity(intent);
                }catch (Exception exc){
                    exc.getStackTrace();
                }
            }
        });
        holder.btn_ListInForEvent= (ImageButton) v.findViewById(R.id.btnListIn);
        holder.btn_ListInForEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
               if(isEventParticipant){
                    fdb.removeEventParticipant(currentEvent.getEvent_ID(),userID);
                    isEventParticipant = false;
                    btn_ListInForEvent.setImageDrawable
                            (ContextCompat.getDrawable(context,R.drawable.star));
                }else {
                    btn_ListInForEvent.setImageDrawable
                            (ContextCompat.getDrawable(context,R.drawable.star_blue_outline));

                    fdb.addEventParticipant(eventID,userID);
                    isEventParticipant = true;
                }*/
            }
        });
        holder.btn_showEventLocation  = (ImageButton) v.findViewById(R.id.btnShowEventLocation);
        holder.btn_showEventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEventLocation = holder.editTextLocation.getText().toString();
                if(!strEventLocation.equals("Unspecified"))
                {
                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("eventLocation",strEventLocation);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        context.startActivity(intent);
                    }catch (Exception exc){
                        exc.getStackTrace();
                    }
                }
                else {
                    Toast.makeText(context, "The location was not specified.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.linearLayout = (LinearLayout) v.findViewById(R.id.linearLayoutEvent);
        int resID = -1 ;
        switch (event.getEvent_Type()){
                case "sport":
                    resID = R.drawable.sportbackground;
                    break;
                case "study":
                    resID = R.drawable.studybackground;
                    break;
                case "local trip":
                    resID = R.drawable.trip_camping;
                    break;
                case "night entertainment":
                    resID = R.drawable.night_life2;
                    break;
                case "abroad":
                    resID = R.drawable.abroad_passport;
                    break;
                default :
                    resID = R.drawable.diary_event;

        }
        holder.linearLayout.setBackgroundResource(resID);
        return holder;
    }

    private static class ViewHolder {
        String roomID;
        TextView editTextContext;
        TextView editTextLocation;
        TextView editTextEndDate;
        TextView editTextName;
        TextView editTextSatrtDate;
        TextView EventNameHeadrer;
        ImageButton btn_openEventChat;
        ImageButton btn_ListInForEvent;
        ImageButton btn_showEventLocation;
        LinearLayout linearLayout;

    }
}