package com.misha.mor.letsteamapp.letsteamapp;

/**
 * Created by Misha on 10/1/2016.
 */
/**
 * Created by Misha on 9/10/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.firebase.client.DataSnapshot;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Misha on 9/3/2016.
 */
public class FireBaseDAL implements RoomStateListener, Serializable, MessageStateListener {

    static FireBaseDAL instance = null;
    FireBaseDBHandler fdbHandler;


    HashMap<String,Event> eventHashMap;
    HashMap<String,ChatMessage> messageMap;


    Context context;

    public  FireBaseDAL(){
        this.eventHashMap = new HashMap<>();
        this.messageMap = new HashMap<>();
        //registerStateListener();
    }

    public static FireBaseDAL getFireBaseDALInstance(){
        if(instance == null){
            instance = new FireBaseDAL( );
        }
        return instance;
    }

    public void setFdbHandler(FireBaseDBHandler fdbHandler) {
        this.fdbHandler = fdbHandler;
    }

    public void registerEvent(Event event){
        try {

            fdbHandler.registerEvent(event);

        }catch (Exception exc){

            Log.e("registerEvent",exc.getMessage());
        }

    }

    //TODO need to ne handled in all activities onpause/onresume/ondestroy
    public void setContext(Context context) {

        this.context = context;
    }

    public void updateRoomStatus(Context context, Event event, String roomStatus){
        //TODO: make the string resource
        if(roomStatus.equals("closeRoom")) {
            event.setEvent_isActive(false);
            event.setEvent_closeDate(UtilMethods.getTimeStamp());
        }
        else if(roomStatus.equals("openRoom")) {
            event.setEvent_isActive(true);
            event.setEvent_closeDate("");
        }
        try {
            fdbHandler.changeRoomStatus(event, event.getEvent_ID());
        }catch (Exception exc){
            Toast.makeText(context,exc.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void registerUser(User newUser){

        try {
            fdbHandler.registerUser(newUser);

        }catch (Exception exc){
            Toast.makeText(context,exc.getMessage(),Toast.LENGTH_SHORT).show();
        }


    }
    public void sendMessage(ChatMessage message){

        if(message != null){
            try{

                fdbHandler.registerChatRoomMessage(message.getRoomID(),message);

            }catch (Exception exc){

                Log.e("sendMessage(...)" , exc.getMessage().toString());

            }

        }
    }

    public void registerStateListener() {

        fdbHandler.readEventState(this);

    }
    public void unregisterStateListener(ActivityEventStateListener roomStateListener) {


        fdbHandler.removeReadChatRoomsState(this);

    }
    public void unregisterMessageListener(String eventID) {


        fdbHandler.unregisterMessageListener(this,eventID);

    }

    @Override
    public void EventsNotifyListener(DataSnapshot snapshot) {

        synchronized (this){

            for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                try{

                    Event event = postSnapshot.getValue(Event.class);

                    eventHashMap.put(postSnapshot.getKey(), event);

                }catch (Exception exc){

                    Log.e("EventsNotifyListener","Incorrect type" + exc.getMessage());

                }

            }
        }

        // broadcast to all listeners
        Intent intent = new Intent("com.misha.mor.letsteamapp.letsteamapp.BROADCAST_ACTION_POLL_ROOMS");
        context.sendBroadcast(intent);


    }

    public void getEventState() {
        synchronized (this){

            try {

                fdbHandler.queryEventsState(this);


            }catch (Exception exc){

                Log.e("triggerRoomsOnce()", "getEvents: "+exc.getStackTrace().toString());

            }

        }

    }

    @Override
    public void registerMessageListener(String eventID) {
        try {

            //DAL registered as a listener for messages events from Firebase by eventID
            fdbHandler.registerMessageListener(this,eventID);

        }catch (Exception exc){

            Log.e("registerMessageListener", exc.getStackTrace().toString() + "eventID: "+eventID);

        }

    }
   /* //TODO is duplication ?
    public void registerMessageListener(MessageStateServiceListener listener,String eventID) {


        fdbHandler.registerMessageListener(this,eventID);

    }*/

    @Override
    public void notifyMessageListener(DataSnapshot snapshot) {

        synchronized (this){

            for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                try {

                    ChatMessage message = postSnapshot.getValue(ChatMessage.class);

                    messageMap.put(postSnapshot.getKey(),message);

                }catch (Exception exc){

                    Log.e("notifyMessage" , exc.getStackTrace().toString());
                }


            }

            // broadcast to all listeners
            Intent intent = new Intent("com.misha.mor.letsteamapp.letsteamapp.BROADCAST_ACTION_POLL");
            context.sendBroadcast(intent);
        }
    }

    @Override
    public void notifyQueryMessageListener(DataSnapshot snapshot) {

        synchronized (this){

            try {

                ChatMessage message = snapshot.getValue(ChatMessage.class);

                messageMap.put(snapshot.getKey(),message);

            }catch (Exception exc){

                Log.e("notifyMessage" , exc.getStackTrace().toString());
            }

            // broadcast to all listeners
            Intent intent = new Intent("com.misha.mor.letsteamapp.letsteamapp.BROADCAST_ACTION_POLL");
            context.sendBroadcast(intent);

        }
    }

    public HashMap<String, Event> getEventHashMap() {
        //return last updated room list
        return eventHashMap;

    }

    public HashMap<String, ChatMessage> getMessagesHashMap() {
        //return last updated room list
        return messageMap;

    }

    public void clearMessageMap(){
        //clear list (after get)
        messageMap.clear();

    }


}