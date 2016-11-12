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
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * Created by Misha on 9/3/2016.
 */
public class FireBaseDBHandler implements Serializable {

    static FireBaseDBHandler instance = null;
    ArrayList<RoomStateListener> roomsStatelisteners;
    HashSet<MessageStateListener> messageStatelisteners;
    ArrayList<String> myEventsUser;
    ArrayList<String> eventTags;
    ArrayList<Event> myEvents;
    Firebase fire_db;

    //TABLE_NODES AND ATTRIBUTES
    final String EVENT_NODE = "EventNode" ;
    final String USER_EVENT_LIST = "UserEventList";
    final String CHAT_MESSAGES = "ChatMessages";
    final String USER_LISTED_EVENTS = "UserListedEvents";
    final String EVENT_PARTICIPANT_LIST = "EventParticipantList";
    final String USERS = "Users";
    final String EVENT_TAG_OPTIONS = "EventTagOptions";
    final String CHAT_ID_COUNTER = "ChatIDCounter";


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
    long countParticipants;
    long messageCount = 0;


    public FireBaseDBHandler(Context context) {


        Firebase.setAndroidContext(context);
        fire_db = new Firebase("https://letsmeatapp-5b152.firebaseio.com/");

        roomsStatelisteners = new ArrayList<>();

        messageStatelisteners = new HashSet<>();

        myEvents = new ArrayList<>();

        myEventsUser = new ArrayList<>();

        eventTags = new ArrayList<>();


    }

    public static FireBaseDBHandler getFireBaseDBHandlerInstance(Context context) {

        if (instance == null) {
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
                            } catch (Exception exc) {
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

    public void updateUserEventList(Event event) throws Exception {

        Firebase roomsNodeRef = fire_db.child("UserEventList");


        Firebase userNodeRef = roomsNodeRef.child(event.getEvent_Owner());

        Firebase newNodeRef = userNodeRef.child(event.event_ID);
        //create unique ID - is eventID

        //Firebase newNodeRef = roomsNodeRef.push();
        if (event != null)
            try {
                newNodeRef.setValue(event.event_ID, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            Log.d("registerEvent", "onComplete: Data could not be saved. " + firebaseError.getMessage());
                        } else {
                            Log.d("registerEvent", "onComplete: Data saved successfully.");
                        }
                    }
                });
            } catch (Exception exc) {
                throw new Exception("Something failed.", new Throwable(String.valueOf(Exception.class)));
            }

    }

    public void registerChatRoomMessage(String eventID, ChatMessage message) {


        message.setIdCounter(messageCount);
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

    //add event id in to participant events he listed for
    public void addEventParticipantList(String eventID, String participantID) throws Exception {

        Firebase EventsNodeRef = fire_db.child("UserListedEvents");
        Firebase participantNodeRef = EventsNodeRef.child(participantID);
        Firebase singleEventNodeRef = participantNodeRef.child(eventID);


        if (eventID != null) {

            try {
                singleEventNodeRef.setValue(eventID, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            //Log.d("registerEvent", "onComplete: Data could not be saved. " + firebaseError.getMessage());
                        } else {
                            Log.d("registerEvent", "onComplete: Data saved successfully.");
                            try {

                            } catch (Exception exc) {
                                // Log.d("registerEvent", "onComplete: Data could not be saved. " + exc.getMessage());
                            }
                        }
                    }
                });
            } catch (Exception exc) {
                throw new Exception("Something failed.", new Throwable(String.valueOf(Exception.class)));
            }

        }

        addParticipantInEventList(eventID, participantID);
        UpdateCountEventParticipant(eventID); // counts the number of participants and update the event
    }
    public void addChatMessageIdCounter (String eventID) {

        Firebase EventsNodeRef = fire_db.child(CHAT_ID_COUNTER);
        Firebase singleNodeRef = EventsNodeRef.child(eventID);

        innerMessageCounter messageCounter = new innerMessageCounter(++messageCount) ;

        if (eventID != null) {


            singleNodeRef.setValue(messageCounter, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        //Log.d("registerEvent", "onComplete: Data could not be saved. " + firebaseError.getMessage());
                    } else {
                        Log.d("registerEvent", "onComplete: Data saved successfully.");
                        try {

                        } catch (Exception exc) {
                            // Log.d("registerEvent", "onComplete: Data could not be saved. " + exc.getMessage());
                        }
                    }
                }
            });


        }
    }
    public void getChatIdCounter( String eventID) {


        Firebase EventsNodeRef = fire_db.child(CHAT_ID_COUNTER);
        Firebase singleEventNodeRef = EventsNodeRef.child(eventID);
        Firebase counterNodeRef = singleEventNodeRef.child("counter");




        counterNodeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(snapshot.getValue()!=null) {
                    messageCount = snapshot.getValue(Integer.class);
                }else{
                    messageCount = 0;
                }

               /* updateEventParticipantCount(eventID,countParticipants);*/
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //TODO need to take care of this case
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }



    //add participant in to event participant list
    public void addParticipantInEventList(String eventID, String participantID) throws Exception {

        Firebase EventsNodeRef = fire_db.child("EventParticipantList");
        Firebase singleEventNodeRef = EventsNodeRef.child(eventID);
        Firebase participantNodeRef = singleEventNodeRef.child(participantID);


        if (eventID != null) {

            try {
                participantNodeRef.setValue(eventID, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            //Log.d("registerEvent", "onComplete: Data could not be saved. " + firebaseError.getMessage());
                        } else {
                            Log.d("registerEvent", "onComplete: Data saved successfully.");
                            try {

                            } catch (Exception exc) {
                                // Log.d("registerEvent", "onComplete: Data could not be saved. " + exc.getMessage());
                            }
                        }
                    }
                });
            } catch (Exception exc) {
                throw new Exception("Something failed.", new Throwable(String.valueOf(Exception.class)));
            }

        }
    }

    //remove event from participant events list
    public void removeEventParticipantFromList(String eventID, String participantID) throws Exception {

        Firebase EventsNodeRef = fire_db.child("UserListedEvents");
        Firebase participantNodeRef = EventsNodeRef.child(participantID);
        Firebase singleEventNodeRef = participantNodeRef.child(eventID);


        if (eventID != null) {

            try {

                singleEventNodeRef.removeValue();

            } catch (Exception exc) {
                throw new Exception("Something failed.", new Throwable(String.valueOf(Exception.class)));
            }

        }
        removeParticipantFromEventList(eventID, participantID);
        UpdateCountEventParticipant(eventID); // counts the number of participants and update the event
    }

    //remove participant from event participant list
    public void removeParticipantFromEventList(String eventID, String participantID) throws Exception {

        Firebase EventsNodeRef = fire_db.child("EventParticipantList");
        Firebase singleEventNodeRef = EventsNodeRef.child(eventID);
        Firebase participantNodeRef = singleEventNodeRef.child(participantID);

        if (eventID != null) {

            try {

                participantNodeRef.removeValue();

            } catch (Exception exc) {
                throw new Exception("Something failed.", new Throwable(String.valueOf(Exception.class)));
            }

        }
    }
    public void updateEventParticipantCount(String event,long participantCount) {

        Firebase roomsNodeRef = fire_db.child("EventNode").child(event).child("ParticipantCount");


        if (event != null && participantCount>=0 ) {

            try {


                roomsNodeRef.setValue(participantCount, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            Log.d("ParticipantCount", "onComplete: Data could not be saved. " + firebaseError.getMessage());
                        } else {
                            Log.d("ParticipantCount", "onComplete: Data saved successfully.");

                        }
                    }
                });
            } catch (Exception exc) {
                Log.d("ParticipantCount", "onComplete: Data could not be saved. " + exc.getMessage());
            }

        }
    }

    public void UpdateCountEventParticipant(final String eventID) {

        Firebase rootNodeRef = fire_db.child("EventParticipantList");
        Firebase eventNodeRef = rootNodeRef.child(eventID);

        countParticipants=0;

        eventNodeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(snapshot!=null) {
                    countParticipants = snapshot.getChildrenCount();

                }
                updateEventParticipantCount(eventID,countParticipants);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //TODO need to take care of this case
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });



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

    public void readEventState(final RoomStateListener listener){
        //TODO need to change it into query
        //register new room state listener
        /*roomsStatelisteners.add(listener);*/
        final Firebase rootEventNodeRef = fire_db.child("EventNode");
        //final Firebase ref = new Firebase("https://chatroomapp-6dd82.firebaseio.com/ChatRoomNode");
        // Attach an listener to read rooms state reference
        rootEventNodeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(roomsStatelisteners.size()>0){
                    notifyListeners(listener,snapshot,"EventStateListener");
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

    public void queryEventsState(final RoomStateListener listener){
        //TODO need to change it into query
        //register new room state listener
        /*roomsStatelisteners.add(listener);*/

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
                notifyListeners(listener,snapshot,"EventStateListener");
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

    public void findSingleEventByID(final RoomStateListener listener, String eventID){

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

                for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                    try{

                        Event event = postSnapshot.getValue(Event.class);

                        myEvents.add(event);

                    }catch (Exception exc){

                        Log.e("EventsNotifyListener","Incorrect type" + exc.getMessage());

                    }

                }
                if(bufferSize  == myEvents.size()){
                    eventNotifyListeners(listener,myEvents,"MyEventStateListener");
                }

                /*roomsStatelisteners.remove(listener);*/
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //TODO need to take care of this case
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public ArrayList<String> queryGetEventTags(){

        //register new room state listener

        final Firebase rootEventNodeRef = fire_db.child("EventTagOptions");
        /*final Firebase ref = new Firebase("https://chatroomapp-6dd82.firebaseio.com/ChatRoomNode");*/

        // Attach an listener to read rooms state reference
        //TODO limit for query if needed
        Query queryRef = rootEventNodeRef.orderByKey();
        // Attach an listener to read rooms state reference
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                    try{

                        String tag = postSnapshot.getValue(String.class);

                        eventTags.add(tag);

                    }catch (Exception exc){

                        Log.e("EventsNotifyListener","Incorrect type" + exc.getMessage());

                    }

                }
/*
                if(bufferSize  == myEvents.size()){
                    eventNotifyListeners(roomsStatelisteners,myEvents,"MyEventStateListener");
                }

                roomsStatelisteners.remove(listener);
*/
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //TODO need to take care of this case
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
        return  eventTags;
    }
    public void queryMyEventsState(final RoomStateListener listener,String ownerID){
        //TODO need to change it into query
        //register new room state listener
        /*roomsStatelisteners.add(listener);*/

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


                    findSingleEventByID(listener,entry.getValue());
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //TODO need to take care of this case
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public void queryListedForEventsState(final RoomStateListener listener,String ownerID){
        //TODO need to change it into query
        //register new room state listener
        /*roomsStatelisteners.add(listener);*/

        final Firebase rootEventNodeRef = fire_db.child("UserListedEvents");

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
                if(eventsMapByUser!=null) {
                    bufferSize = eventsMapByUser.size();
                    myEvents.clear();

                    for (Map.Entry<String, String> entry : eventsMapByUser.entrySet()) {


                        findSingleEventByID(listener,entry.getValue());
                    }
                }else{

                    eventNotifyListeners(listener,null,"MyEventStateListener");
                }

                /*roomsStatelisteners.remove(listener);*/

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //TODO need to take care of this case
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }
    public void queryIfUserListedForEvent(final RoomStateListener listener, String participantID, final String eventID ){
        //TODO need to change it into query
        //register new room state listener
        /*roomsStatelisteners.add(listener);*/

        final Firebase rootEventNodeRef = fire_db.child("UserListedEvents");

        Firebase userEventsNodeRef = rootEventNodeRef.child(participantID);

        // Attach an listener to read rooms state reference
        //TODO limit for query if needed
        Query queryRef = userEventsNodeRef.orderByKey();
        // Attach an listener to read rooms state reference
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {


                Map<String,String> eventsMapByUser = (Map<String,String>)snapshot.getValue();
                boolean isListed = false;
                if(eventsMapByUser!=null) {

                    if (eventsMapByUser.containsKey(eventID)) {


                        isListed = true;


                    }

                }
                isListedEventNotifyListeners(listener, isListed, "ListedEventStateListener");
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



                Map<String,String> eventsMapByUser = (Map<String,String>)snapshot.getValue();
                bufferSize = eventsMapByUser.size();
                myEvents.clear();

                for (Map.Entry<String, String> entry : eventsMapByUser.entrySet())
                {

                    findSingleEventByID(listener,entry.getValue());
                }


            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //TODO need to take care of this case
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }
    public void queryIsEventListed(final RoomStateListener listener,String participantID,String eventID){

        //register new room state listener


        final Firebase rootEventNodeRef = fire_db.child("UserListedEvents");

        //single participant node
        Firebase userEventsNodeRef = rootEventNodeRef.child(participantID);

        Firebase EventsNodeRef = userEventsNodeRef.child(eventID);


        Query queryRef = EventsNodeRef.equalTo(1);
        // Attach an listener to read rooms state reference


        queryRef.addValueEventListener(new ValueEventListener() {



            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isListed = false;
                if(dataSnapshot != null){

                    isListed= true;
                }

                isListedEventNotifyListeners(listener,isListed,"ListedEventStateListener");

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


        // Attach an listener to read rooms state reference
        //TODO limit for query if needed
        Query queryRef = rootEventNodeRef.orderByChild("event_Owner").equalTo(OwnerID);

        // Attach an listener to read rooms state reference
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                notifyListeners(listener,snapshot,"EventStateListener");

                //unregister listener
                roomsStatelisteners.remove(listener);

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //TODO need to take care of this case
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public void notifyListeners(Object listener , DataSnapshot snapshot , String typeID){
        if (listener == null || snapshot == null){
            Log.e("notifyListeners error" , "Error");
        }
        else if (typeID.equals("EventStateListener") ){
            Log.d("EventStateListener" , "in notification");
          /*  for (Object listener : listeners) {*/
            RoomStateListener castListener = (RoomStateListener)listener;
            castListener.EventsNotifyListener(snapshot);
            /*}*/
        }
        else if (typeID.equals("MessageStateListener")){
            Log.d("MessageStateListener" , "in notification");
           /* for (Object listener : listeners) {*/
            MessageStateListener castListener = (MessageStateListener)listener;
            castListener.notifyMessageListener(snapshot);
           /* }*/
        }else if (typeID.equals("QueryMessageStateListener")){
            Log.d("MessageStateListener" , "in notification");
            /*for (Object listener : listeners) {*/
            MessageStateListener castListener = (MessageStateListener)listener;
            castListener.notifyQueryMessageListener(snapshot);
            /*}*/
        }
    }
    public void eventNotifyListeners(Object listener , ArrayList<Event> events , String typeID){
        if (listener == null ){
            Log.e("notifyListeners error" , "Error");
        }
        else if (typeID.equals("MyEventStateListener") ){
            Log.d("EventStateListener" , "in notification");
            /*for (Object listener : listeners) {*/
            RoomStateListener castListener = (RoomStateListener)listener;
            castListener.EventsNotifyListener(events);
            /*}*/
        }

    }
    public void isListedEventNotifyListeners(RoomStateListener listeners , boolean isListed , String typeID){
        if (listeners == null ){
            Log.e("notifyListeners error" , "Error");
        }
        else if (typeID.equals("ListedEventStateListener") ){
            Log.d("ListedEventListener" , "in notification");

            listeners.isListedEventsNotifyListener(isListed);

        }

    }
    public void notifyMessageListeners(HashSet<?> listeners , DataSnapshot snapshot,String typeID){
        if (listeners == null || snapshot == null){
            System.out.println("notifyListeners error");
        }else if (typeID.equals("MessageStateListener")){
            Log.d("MessageStateListener" , "in notification");
            for (Object listener : listeners) {
                MessageStateListener castListener = (MessageStateListener)listener;
                castListener.notifyMessageListener(snapshot);
            }
        }else if (typeID.equals("QueryMessageStateListener")){
            Log.d("MessageStateListener" , "in notification");
            for (Object listener : listeners){
                MessageStateListener castListener = (MessageStateListener)listener;
                castListener.notifyQueryMessageListener(snapshot);
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

    private void queryMessageState(String eventID){

        int queryLimit = -1;

        //in case this is first message the first query
        message_first = true;
        //register new room state listener

        //query path
        Firebase rootChatMessageNodeRef = fire_db.child("ChatMessages");
        Firebase ChatMessageNodeRef = rootChatMessageNodeRef.child(eventID);




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
                notifyMessageListeners(messageStatelisteners, dataSnapshot, "QueryMessageStateListener");
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

    class innerMessageCounter {

        long counter;

        public innerMessageCounter(long counter) {
            this.counter = counter;
        }

        public long getCounter() {
            return counter;
        }

        public void setCounter(int counter) {
            this.counter = counter;
        }
    }
}