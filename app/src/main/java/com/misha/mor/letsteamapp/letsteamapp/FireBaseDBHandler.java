package com.misha.mor.letsteamapp.letsteamapp;
/**
 * Created by Misha on 9/10/2016.
 */
import android.content.Context;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by Misha on 9/3/2016.
 */
public class FireBaseDBHandler implements Serializable{

    static  FireBaseDBHandler instance = null;
    ArrayList<RoomStateListener> roomsStatelisteners;
    ArrayList<MessageStateListener> messageStatelisteners;
    Firebase fire_db ;

    //Message EventListener
    ValueEventListener messageListener;
    boolean message_first;

    DataSnapshot RoomsSnapshot = null;

    String acceptKey = "-KSOJKnUb6lxvcxWC0hU";
    ChildEventListener messageListenerQuery;
    ChildEventListener roomListenerQuery;


    public FireBaseDBHandler(Context context) {


        Firebase.setAndroidContext(context);
        fire_db = new Firebase("https://chatroomapp-6dd82.firebaseio.com/");

        roomsStatelisteners  = new ArrayList<>();

        messageStatelisteners = new ArrayList<>();
    }
    public static FireBaseDBHandler getFireBaseDBHandlerInstance(Context context){

        if (instance == null ){
            instance = new FireBaseDBHandler(context);
        }
        return instance;
    }

    //Write functions

    public void registerRoom(Room room) throws Exception{

        Firebase roomsNodeRef = fire_db.child("ChatRoomNode");

        //create unique ID - is roomID
        Firebase newNodeRef = roomsNodeRef.push();
        if (room != null)
            try {
                room.setRoom_ID(newNodeRef.getKey());

                newNodeRef.setValue(room,new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            Log.d("registerRoom","onComplete: Data could not be saved. " + firebaseError.getMessage());
                        } else {
                            Log.d("registerRoom","onComplete: Data saved successfully.");
                        }
                    }
                });
            }catch (Exception exc){
                throw new Exception("Something failed.", new Throwable(String.valueOf(Exception.class)));
            }

    }

    public void registerChatRoomMessage(String roomID,ChatMessage message) {
        Firebase roomsNodeRef = fire_db.child("ChatRoomNode");
        Firebase roomNodeRef = roomsNodeRef.child(roomID);
        Firebase allMessagesNodeRef = roomNodeRef.child("ChatMessages");
        /*Firebase dateMessageNode = allMessagesNodeRef.child(message.getDateOnly());
        Firebase timeMessageNode = dateMessageNode.child(message.getTimeOnly());
        Firebase newMessageNode = timeMessageNode.push();*/
        Firebase newMessageNode = allMessagesNodeRef.push();

        if (roomID != null) {


            String postId = newMessageNode.getKey();

            message.setId(postId);

            newMessageNode.setValue(message, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        System.out.println("Data could not be saved. " + firebaseError.getMessage());
                    } else {
                        System.out.println("Data saved successfully.");
                    }
                }
            });

        }
    }

    public String registerUser(ChatRoomUser newUser) throws Exception{
        //TODO Add Listener to notify on complete
        Firebase roomsNodeRef = fire_db.child("Users");
        Firebase newNodeRef = roomsNodeRef.push();
        if (newUser != null)
            try {
                newNodeRef.setValue(newUser,new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            System.out.println("Data could not be saved. " + firebaseError.getMessage());
                        } else {
                            System.out.println("Data saved successfully.");
                        }
                    }
                });
                String postId = newNodeRef.getKey();
                return postId;
            }catch (Exception exc){
                throw new Exception("Something failed.", new Throwable(String.valueOf(Exception.class)));
            }
        return  null;
    }

    //update functions
    public void changeRoomStatus(Room room , String roomID) throws Exception{
        //TODO Add Listener to notify on complete
        Firebase roomsNodeRef = fire_db.child("ChatRoomNode");
        Firebase roomRef = roomsNodeRef.child(roomID);
        if (room != null)
            try {
                roomRef.setValue(room, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            System.out.println("Data could not be saved. " + firebaseError.getMessage());
                        } else {
                            System.out.println("Data saved successfully.");
                        }
                    }
                });
            }catch (Exception exc){
                throw new Exception("Something failed.", new Throwable(String.valueOf(Exception.class)));
            }
    }

    public void removeReadChatRoomsState(RoomStateListener listener){
        roomsStatelisteners.remove(this);
    }
    public void removeReadChatMessageState(RoomStateListener listener){

    }
    public void readChatRoomsState(RoomStateListener listener){
        //TODO need to change it into query
        //register new room state listener
        roomsStatelisteners.add(listener);

        final Firebase ref = new Firebase("https://chatroomapp-6dd82.firebaseio.com/ChatRoomNode");
        // Attach an listener to read rooms state reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(roomsStatelisteners.size()>0){
                    notifyListeners(roomsStatelisteners,snapshot,"RoomStateListener");
                    ref.removeEventListener(this);
                }
                RoomsSnapshot = snapshot;
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //TODO need to take care of this case
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void queryChatRoomsState(RoomStateListener listener){
        //TODO need to change it into query
        //register new room state listener
        roomsStatelisteners.add(listener);

        final Firebase ref = new Firebase("https://chatroomapp-6dd82.firebaseio.com/ChatRoomNode");
        // Attach an listener to read rooms state reference
        Query queryRef = ref.orderByChild("room_DisplayName");
        // Attach an listener to read rooms state reference
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //if(roomsStatelisteners.size()>0){
                notifyListeners(roomsStatelisteners,snapshot,"RoomStateListener");
                //ref.removeEventListener(this);
                //}
                //RoomsSnapshot = snapshot;
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //TODO need to take care of this case
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }
    public void readChatRoomsOnce(){
        final Firebase ref = new Firebase("https://chatroomapp-6dd82.firebaseio.com/ChatRoomNode");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once
                Log.d("readChatRoomsOnce" , "before onDataChange ");
                RoomsSnapshot = snapshot;
                Log.d("readChatRoomsOnce" , "after onDataChange ");
                ref.removeEventListener(this);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
    public void triggerRoomsOnce() throws Exception{
        //TODO Add Listener to notify on complete
        Firebase roomsNodeRef = fire_db.child("ChatRoomNode");
        Firebase newNodeRef = null;
        if(acceptKey == null){
            newNodeRef = roomsNodeRef.push();
            acceptKey = newNodeRef.getKey();
        }else{
            newNodeRef = roomsNodeRef.child(acceptKey);
        }
        // Firebase newNodeRef = roomsNodeRef.push();
        try {
            newNodeRef.setValue("accept",new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        Log.d("triggerRoomsOnce","onComplete Data could not be saved. " + firebaseError.getMessage());
                    } else {
                        Log.d("triggerRoomsOnce"," onComplete Data saved successfully.");
                    }
                }
            });

        }catch (Exception exc){
            throw new Exception("Something failed.", new Throwable(String.valueOf(Exception.class)));
        }


    }


    public void notifyListeners(ArrayList listeners , DataSnapshot snapshot , String typeID){
        if (listeners == null || snapshot == null){
            Log.e("notifyListeners error" , "Error");
        }
        else if (typeID.equals("RoomStateListener") ){
            Log.d("RoomStateListener" , "in notification");
            for (Object listener : listeners) {
                RoomStateListener castListener = (RoomStateListener)listener;
                castListener.roomNotifyListener(snapshot);
            }
        }
        else if (typeID.equals("MessageStateListener")){
            Log.d("MessageStateListener" , "in notification");
            for (Object listener : listeners) {
                MessageStateListener castListener = (MessageStateListener)listener;
                castListener.notifyMessageListener(snapshot);
            }
        }else if (typeID.equals("QueryMessageStateListener")){
            Log.d("MessageStateListener" , "in notification");
            for (Object listener : listeners) {
                MessageStateListener castListener = (MessageStateListener)listener;
                castListener.notifyQueryMessageListener(snapshot);
            }
        }
    }

    public void notifyMessageListeners(ArrayList<?> listeners , DataSnapshot snapshot){
        if (listeners == null || snapshot == null){
            System.out.println("notifyListeners error");
        }
        else if (listeners instanceof RoomStateListener){
            for (Object listener : listeners) {
                MessageStateListener castListener = (MessageStateListener)listener;
                castListener.notifyMessageListener(snapshot);
            }
        }
    }
    public void registerMessageListener(MessageStateListener listener,String roomID){
        //add for notifications
        messageStatelisteners.add(listener);

        Log.d("readMessageState","Register Listener");

        //readMessageState(roomID);//old version

        queryMessageState(roomID);

    }
    public void unregisterMessageListener(MessageStateListener listener,String roomID){
        //path
        String dbURL = "https://chatroomapp-6dd82.firebaseio.com/ChatRoomNode"
                +"/"+roomID;
        Firebase ref = new Firebase(dbURL);

        //remove from list of listeners
        messageStatelisteners.remove(listener);

        Log.d("unregisterM.Listener","Listener for roomID: "+roomID);

        //remove from fire base listeners
        ref.removeEventListener(messageListenerQuery);
    }

    private void readMessageState(String roomID){
        //register new room state listener
        message_first = true;
        String dbURL = "https://chatroomapp-6dd82.firebaseio.com/ChatRoomNode"
                +"/"+roomID+"/"+"ChatMessages"+"/"+UtilMethods.getDateSimple()
                +"/"+UtilMethods.getTimeSimple();
        final Firebase ref = new Firebase(dbURL);
        // Attach an listener to read rooms state reference
        messageListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("readMessageState","onDataChange");
                if(!message_first) {
                    notifyListeners(messageStatelisteners, snapshot, "MessageStateListener");
                }else{
                    message_first = false;
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //TODO need to take care of this case
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
    private void queryMessageState(String roomID){

        int queryLimit = -1;

        //in case this is first message the first query
        message_first = true;
        //register new room state listener

        //query path
        String dbURL = "https://chatroomapp-6dd82.firebaseio.com/ChatRoomNode"
                +"/"+roomID+"/"+"ChatMessages"/*+"/"+UtilMethods.getDateSimple()
                +"/"+UtilMethods.getTimeSimple()*/;

        Firebase ref = new Firebase(dbURL);

        if(message_first){
            queryLimit = 10;
        }else {
            queryLimit = 1;
        }


        Query queryRef = ref.limitToLast(queryLimit);

        // Attach an listener to read rooms state reference
        messageListenerQuery = queryRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d("readMessageState","onDataChange");
                //if(!message_first) {
                notifyListeners(messageStatelisteners, dataSnapshot, "QueryMessageStateListener");
                // }else{
                message_first = false;
                //}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public DataSnapshot getUpdatedRooms() {
        return RoomsSnapshot;
    }
}