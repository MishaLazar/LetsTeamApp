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
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Misha on 9/3/2016.
 */
public class FireBaseDAL implements RoomStateListener, Serializable, MessageStateListener {

    static FireBaseDAL instance = null;
    FireBaseDBHandler fdbHandler;


    HashMap<String,Event> eventHashMap;
    HashMap<String,ChatMessage> messageMap;
    ArrayList<String> eventTags;

    ChatMessage tempMessage;

    Context context;
    String eventUserName;

    public  FireBaseDAL(){
        this.eventHashMap = new HashMap<>();
        this.messageMap = new HashMap<>();
        this.eventTags = new ArrayList<>();
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
    public void getUserInfo(String userID){

        try {
            fdbHandler.queryUserInfo(this,userID);

        }catch (Exception exc){
            Toast.makeText(context,exc.getMessage(),Toast.LENGTH_SHORT).show();
        }


    }

    public void getChaIdCounter(String eventID){
        fdbHandler.getChatIdCounter(eventID);
    }
    public void addChatMessageIdCounter(String eventID){
        fdbHandler.addChatMessageIdCounter(eventID);
    }

    public void sendMessage(ChatMessage message){
         try{

                fdbHandler.registerChatRoomMessage(message.getRoomID(),message);


            }catch (Exception exc){

                Log.e("sendMessage(...)" , exc.getMessage().toString());

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
    @Override
    public void EventsNotifyListener(ArrayList<Event> events) {

        eventHashMap.clear();

        synchronized (this){
            if(events!=null) {
                for (Event event : events) {

                    try {

                        eventHashMap.put(event.getEvent_ID(), event);

                    } catch (Exception exc) {

                        Log.e("EventsNotifyListener", "Incorrect type" + exc.getMessage());

                    }

                }
            }
        }

        // broadcast to all listeners
        Intent intent = new Intent("com.misha.mor.letsteamapp.letsteamapp.BROADCAST_ACTION_POLL_ROOMS");
        context.sendBroadcast(intent);


    }

    @Override
    public void isListedEventsNotifyListener(boolean isListed) {

        Intent intent = new Intent("com.misha.mor.letsteamapp.letsteamapp.BROADCAST_ACTION_POLL_LISTED");
        intent.putExtra("isListed",isListed);
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
    public void notifyUserInfoListeners(DataSnapshot snapshot) {
        synchronized (this){

          /*  for (DataSnapshot postSnapshot: snapshot.getChildren()) {
*/
                try{

                    eventUserName = snapshot.getValue(String.class);


                }catch (Exception exc){

                    Log.e("notifyUserInfoListeners","Incorrect type" + exc.getMessage());

                }

        }
        if(eventUserName!= null && eventUserName.length()>0){
            // broadcast to all listeners
            Intent intent = new Intent("com.misha.mor.letsteamapp.letsteamapp.BROADCAST_ACTION_POLL_LISTED");
            intent.putExtra("isUserInfo",true);
            context.sendBroadcast(intent);
        }

    }

    public void getEventTags() {
        synchronized (this){

            try {
                eventTags.clear();

                eventTags =  fdbHandler.queryGetEventTags();

                Intent intent = new Intent("com.misha.mor.letsteamapp.letsteamapp.BROADCAST_ACTION_POLL_EVENT_TAGS");

                context.sendBroadcast(intent);

            }catch (Exception exc){

                Log.e("triggerRoomsOnce()", "getEvents: "+exc.getStackTrace().toString());

            }

        }

    }
    public void isUserListedForEvent(String eventID,String participantID) {
        synchronized (this){

            try {

                fdbHandler.queryIfUserListedForEvent(this,participantID,eventID);


            }catch (Exception exc){

                Log.e("triggerRoomsOnce()", "getEvents: "+exc.getStackTrace().toString());

            }

        }

    }
    public void getMyEventsState(String OwnerID) {
        synchronized (this){

            try {

                fdbHandler.queryMyEventsState(this,OwnerID);


            }catch (Exception exc){

                Log.e("triggerRoomsOnce()", "getEvents: "+exc.getStackTrace().toString());

            }

        }

    }
    public void getMyListedEventsState(String OwnerID) {
        synchronized (this){

            try {

                fdbHandler.queryListedForEventsState(this,OwnerID);


            }catch (Exception exc){

                Log.e("triggerRoomsOnce()", "getEvents: "+exc.getStackTrace().toString());

            }

        }

    }
    public void addEventParticipant(String eventID,String participantID){

        synchronized (this){

            try {

                fdbHandler.addEventParticipantList(eventID,participantID);


            }catch (Exception exc){

                Log.e("triggerRoomsOnce()", "getEvents: "+exc.getStackTrace().toString());

            }

        }

    }

    public void removeEventParticipant(String eventID,String participantID){

        synchronized (this){

            try {

                fdbHandler.removeEventParticipantFromList(eventID,participantID);


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

    public void clearEventsMap(){
        //clear list (after get)
        eventHashMap.clear();
    }

    public ArrayList<String> getTags() {
        return eventTags;
    }

    public String getUserInfo() {
        String tempUserName =eventUserName;
        eventUserName = "";
        return  tempUserName;

    }
}