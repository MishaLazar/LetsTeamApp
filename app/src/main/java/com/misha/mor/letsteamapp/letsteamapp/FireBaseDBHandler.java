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
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Misha on 9/3/2016.
 */
public class FireBaseDBHandler implements Serializable{

    static  FireBaseDBHandler instance = null;
    ArrayList<RoomStateListener> roomsStatelisteners;
    ArrayList<MessageStateListener> messageStatelisteners;
    ArrayList<String> myEventsUser;
    ArrayList<Event> myEvents;
    Firebase fire_db ;

    //Message EventListener
    ValueEventListener messageListener;
    boolean message_first;

    DataSnapshot RoomsSnapshot = null;

    Event eventToStore;

    /* String acceptKey = "-KSOJKnUb6lxvcxWC0hU";*/
    ChildEventListener messageListenerQuery;
    ChildEventListener roomListenerQuery;



    //EventsQuery
    int iteration = 0;
    int bufferSize = -1;



    public FireBaseDBHandler(Context context) {


        Firebase.setAndroidContext(context);
        fire_db = new Firebase("https://letsmeatapp-5b152.firebaseio.com/");

        roomsStatelisteners  = new ArrayList<>();

        messageStatelisteners = new ArrayList<>();

        myEvents = new ArrayList<>();

        myEventsUser = new ArrayList<>();


    }
    public static FireBaseDBHandler getFireBaseDBHandlerInstance(Context context){

        if (instance == null ){
            instance = new FireBaseDBHandler(context);
        }
        return instance;
    }

    //Write functions

    public void registerEvent(Event event) throws Exception {

        Firebase roomsNodeRef = fire_db.child("EventNode");

        eventToStore = event;
        //create unique ID - is eventID
        Firebase newNodeRef = roomsNodeRef.push();
        if (event != null) {


            try {
                event.setEvent_ID(newNodeRef.getKey());

                newNodeRef.setValue(eventToStore, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            Log.d("registerEvent", "onComplete: Data could not be saved. " + firebaseError.getMessage());
                        } else {
                            Log.d("registerEvent", "onComplete: Data saved successfully.");
                            try {
                                updateUserEventList(eventToStore);
                            }catch (Exception exc){
                                Log.d("registerEvent", "onComplete: Data could not be saved. " + exc.getMessage());
                            }
                        }
                    }
                });
            } catch (Exception exc) {
                throw new Exception("Something failed.", new Throwable(String.valueOf(Exception.class)));
            }

        }
    }
    public void updateUserEventList(Event event) throws Exception{

        Firebase roomsNodeRef = fire_db.child("UserEventList");


        Firebase userNodeRef = roomsNodeRef.child(event.getEvent_Owner());

        Firebase newNodeRef = userNodeRef.child(event.event_ID);
        //create unique ID - is eventID

        //Firebase newNodeRef = roomsNodeRef.push();
        if (event != null)
            try {
                newNodeRef.setValue(event.event_ID,new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            Log.d("registerEvent","onComplete: Data could not be saved. " + firebaseError.getMessage());
                        } else {
                            Log.d("registerEvent","onComplete: Data saved successfully.");
                        }
                    }
                });
            }catch (Exception exc){
                throw new Exception("Something failed.", new Throwable(String.valueOf(Exception.class)));
            }

    }

    public void registerChatRoomMessage(String eventID,ChatMessage message) {

        Firebase rootEventNodeRef = fire_db.child("ChatMessages");
        Firebase eventNodeRef = rootEventNodeRef.child(eventID);
        Firebase newMessageNode = eventNodeRef.push();

        if (eventID != null) {


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

    public void registerUser(User newUser) throws Exception{
        //TODO Add Listener to notify on complete
        Firebase roomsNodeRef = fire_db.child("Users");
        //set user unique id
        Firebase newNodeRef = roomsNodeRef.child(newUser.getsUserUniqueID());
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


            }catch (Exception exc){
                throw new Exception("Something failed.", new Throwable(String.valueOf(Exception.class)));
            }

    }

    //update functions
    public void changeRoomStatus(Event event, String eventID) throws Exception{
        //TODO Add Listener to notify on complete
        Firebase roomsNodeRef = fire_db.child("EventNode");
        Firebase roomRef = roomsNodeRef.child(eventID);
        if (event != null)
            try {
                roomRef.setValue(event, new Firebase.CompletionListener() {
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
    public void readEventState(RoomStateListener listener){
        //TODO need to change it into query
        //register new room state listener
        roomsStatelisteners.add(listener);
        final Firebase rootEventNodeRef = fire_db.child("EventNode");
        //final Firebase ref = new Firebase("https://chatroomapp-6dd82.firebaseio.com/ChatRoomNode");
        // Attach an listener to read rooms state reference
        rootEventNodeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(roomsStatelisteners.size()>0){
                    notifyListeners(roomsStatelisteners,snapshot,"EventStateListener");
                    //TODO to much listeners
                    fire_db.removeEventListener(this);
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

    public void queryEventsState(RoomStateListener listener){
        //TODO need to change it into query
        //register new room state listener
        roomsStatelisteners.add(listener);

        final Firebase rootEventNodeRef = fire_db.child("EventNode");
        /*final Firebase ref = new Firebase("https://chatroomapp-6dd82.firebaseio.com/ChatRoomNode");*/

        // Attach an listener to read rooms state reference
        //TODO limit for query if needed
        Query queryRef = rootEventNodeRef.orderByChild("event_DisplayName");
        // Attach an listener to read rooms state reference
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //if(roomsStatelisteners.size()>0){
                notifyListeners(roomsStatelisteners,snapshot,"EventStateListener");
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

    public void findSingelEventByID (String eventID){

        //register new room state listener


        final Firebase rootEventNodeRef = fire_db.child("EventNode");
        /*final Firebase ref = new Firebase("https://chatroomapp-6dd82.firebaseio.com/ChatRoomNode");*/

        // Attach an listener to read rooms state reference
        //TODO limit for query if needed
        Query queryRef = rootEventNodeRef.orderByKey().equalTo(eventID);
        // Attach an listener to read rooms state reference
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //if(roomsStatelisteners.size()>0){
                //notifyListeners(roomsStatelisteners,snapshot,"EventStateListener");
                //ref.removeEventListener(this);
                //}
                //RoomsSnapshot = snapshot;
                //Map<String,Event> eventsMapByUser = (Map<String,Event>)snapshot.getValue();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                    try{

                        Event event = postSnapshot.getValue(Event.class);

                        myEvents.add(event);

                    }catch (Exception exc){

                        Log.e("EventsNotifyListener","Incorrect type" + exc.getMessage());

                    }

                }
                if(bufferSize  == myEvents.size()){
                    eventnotifyListeners(roomsStatelisteners,myEvents,"MyEventStateListener");
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //TODO need to take care of this case
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public void queryMyEventsState(final RoomStateListener listener,String ownerID){
        //TODO need to change it into query
        //register new room state listener
        roomsStatelisteners.add(listener);

        final Firebase rootEventNodeRef = fire_db.child("UserEventList");

        Firebase userEventsNodeRef = rootEventNodeRef.child(ownerID);

        // Attach an listener to read rooms state reference
        //TODO limit for query if needed
        Query queryRef = userEventsNodeRef.orderByKey();
        // Attach an listener to read rooms state reference
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //if(roomsStatelisteners.size()>0){

                Map<String,String> eventsMapByUser = (Map<String,String>)snapshot.getValue();
                bufferSize = eventsMapByUser.size();
                myEvents.clear();

                for (Map.Entry<String, String> entry : eventsMapByUser.entrySet())
                {

                    //System.out.println(entry.getKey() + "/" + entry.getValue());
                    findSingelEventByID(entry.getValue());
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //TODO need to take care of this case
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }
    public void queryMyListedEventsState(final RoomStateListener listener,String ownerID){
        //TODO need to change it into query
        //register new room state listener
        roomsStatelisteners.add(listener);

        final Firebase rootEventNodeRef = fire_db.child("UserEventList");

        Firebase userEventsNodeRef = rootEventNodeRef.child(ownerID);

        // Attach an listener to read rooms state reference
        //TODO limit for query if needed
        Query queryRef = userEventsNodeRef.orderByKey();
        // Attach an listener to read rooms state reference
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //if(roomsStatelisteners.size()>0){


                Map<String,String> eventsMapByUser = (Map<String,String>)snapshot.getValue();
                bufferSize = eventsMapByUser.size();
                myEvents.clear();

                for (Map.Entry<String, String> entry : eventsMapByUser.entrySet())
                {

                    //System.out.println(entry.getKey() + "/" + entry.getValue());
                    findSingelEventByID(entry.getValue());
                }


            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //TODO need to take care of this case
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }
    public void queryMyEventsListState(final RoomStateListener listener,String OwnerID){
        //TODO need to change it into query
        //register new room state listener
        roomsStatelisteners.add(listener);

        final Firebase rootEventNodeRef = fire_db.child("EventNode");
        /*final Firebase ref = new Firebase("https://chatroomapp-6dd82.firebaseio.com/ChatRoomNode");*/

        // Attach an listener to read rooms state reference
        //TODO limit for query if needed
        Query queryRef = rootEventNodeRef.orderByChild("event_Owner").equalTo(OwnerID);

        // Attach an listener to read rooms state reference
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //if(roomsStatelisteners.size()>0){
                notifyListeners(roomsStatelisteners,snapshot,"EventStateListener");

                //unregister listener
                roomsStatelisteners.remove(listener);
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
   /* public void readChatRoomsOnce(){
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
    }*/
    /*public void triggerRoomsOnce() throws Exception{
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


    }*/


    public void notifyListeners(ArrayList listeners , DataSnapshot snapshot , String typeID){
        if (listeners == null || snapshot == null){
            Log.e("notifyListeners error" , "Error");
        }
        else if (typeID.equals("EventStateListener") ){
            Log.d("EventStateListener" , "in notification");
            for (Object listener : listeners) {
                RoomStateListener castListener = (RoomStateListener)listener;
                castListener.EventsNotifyListener(snapshot);
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
    public void eventnotifyListeners(ArrayList listeners , ArrayList<Event> events , String typeID){
        if (listeners == null || events == null){
            Log.e("notifyListeners error" , "Error");
        }
        else if (typeID.equals("MyEventStateListener") ){
            Log.d("EventStateListener" , "in notification");
            for (Object listener : listeners) {
                RoomStateListener castListener = (RoomStateListener)listener;
                castListener.EventsNotifyListener(events);
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

        //readMessageState(eventID);//old version

        queryMessageState(roomID);

    }
    public void unregisterMessageListener(MessageStateListener listener,String eventID){
        //path
        String dbURL = "https://letsmeatapp-5b152.firebaseio.com/EventNode"
                +"/"+eventID;
        Firebase ref = new Firebase(dbURL);

        //remove from list of listeners
        messageStatelisteners.remove(listener);

        Log.d("unregisterM.Listener","Listener for eventID: "+eventID);

        //remove from fire base listeners
        ref.removeEventListener(messageListenerQuery);
    }

    /*private void readMessageState(String eventID){
        //register new room state listener
        message_first = true;
        String dbURL = "https://chatroomapp-6dd82.firebaseio.com/ChatRoomNode"
                +"/"+eventID+"/"+"ChatMessages"+"/"+UtilMethods.getDateSimple()
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
    }*/
    private void queryMessageState(String eventID){

        int queryLimit = -1;

        //in case this is first message the first query
        message_first = true;
        //register new room state listener

        //query path
        Firebase rootChatMessageNodeRef = fire_db.child("ChatMessages");
        Firebase ChatMessageNodeRef = rootChatMessageNodeRef.child(eventID);

        /*String dbURL = "https://letsmeatapp-5b152.firebaseio.com/EventNode"
                +"/"+eventID+"/"+"ChatMessages"*//*+"/"+UtilMethods.getDateSimple()
                +"/"+UtilMethods.getTimeSimple()*//*;*/

        //Firebase ref = new Firebase(dbURL);


        if(message_first){
            queryLimit = 10;
        }else {
            queryLimit = 1;
        }


       /* Query queryRef = ref.limitToLast(queryLimit);*/
        Query queryRef = ChatMessageNodeRef.limitToLast(queryLimit);

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