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


    HashMap<String,Room> roomHashMap;
    HashMap<String,ChatMessage> messageMap;


    Context context;

    public  FireBaseDAL(){
        this.roomHashMap = new HashMap<>();
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

    public void registerRoom(Context context, Room room){
        try {

            fdbHandler.registerRoom(room);

        }catch (Exception exc){

            Log.e("registerRoom",exc.getMessage());
        }

    }

    //TODO need to ne handled in all activities onpause/onresume/ondestroy
    public void setContext(Context context) {

        this.context = context;
    }

    public void updateRoomStatus(Context context, Room room, String roomStatus){
        //TODO: make the string resource
        if(roomStatus.equals("closeRoom")) {
            room.setRoom_isActive(false);
            room.setRoom_closeDate(UtilMethods.getTimeStamp());
        }
        else if(roomStatus.equals("openRoom")) {
            room.setRoom_isActive(true);
            room.setRoom_closeDate("");
        }
        try {
            fdbHandler.changeRoomStatus(room,room.getRoom_ID());
        }catch (Exception exc){
            Toast.makeText(context,exc.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void registerUser(Context context,ChatRoomUser newUser){
        String userID;
        try {

            userID = fdbHandler.registerUser(newUser);
            newUser.setUser_ID(userID);
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

        fdbHandler.readChatRoomsState(this);

    }
    public void unregisterStateListener(ActivityRoomStateListener roomStateListener) {


        fdbHandler.removeReadChatRoomsState(this);

    }
    public void unregisterMessageListener(String roomID) {


        fdbHandler.unregisterMessageListener(this,roomID);

    }

    @Override
    public void roomNotifyListener(DataSnapshot snapshot) {

        synchronized (this){

            for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                try{

                    Room room = postSnapshot.getValue(Room.class);

                    roomHashMap.put(postSnapshot.getKey(),room);

                }catch (Exception exc){

                    Log.e("roomNotifyListener","Incorrect type" + exc.getMessage());

                }

            }
        }

        // broadcast to all listeners
        Intent intent = new Intent("com.hw.misha.chatroom.BROADCAST_ACTION_POLL_ROOMS");
        context.sendBroadcast(intent);


    }

    public void getRoomsState() {
        synchronized (this){

            try {

                fdbHandler.queryChatRoomsState(this);


            }catch (Exception exc){

                Log.e("triggerRoomsOnce()", "getRooms: "+exc.getStackTrace().toString());

            }

        }

    }

    @Override
    public void registerMessageListener(String roomID) {
        try {

            //DAL registered as a listener for messages events from Firebase by roomID
            fdbHandler.registerMessageListener(this,roomID);

        }catch (Exception exc){

            Log.e("registerMessageListener", exc.getStackTrace().toString() + "roomID: "+roomID);

        }

    }
   /* //TODO is duplication ?
    public void registerMessageListener(MessageStateServiceListener listener,String roomID) {


        fdbHandler.registerMessageListener(this,roomID);

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
            Intent intent = new Intent("com.hw.misha.chatroom.BROADCAST_ACTION_POLL");
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
            Intent intent = new Intent("com.hw.misha.chatroom.BROADCAST_ACTION_POLL");
            context.sendBroadcast(intent);

        }
    }

    public HashMap<String, Room> getRoomHashMap() {
        //return last updated room list
        return roomHashMap;

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