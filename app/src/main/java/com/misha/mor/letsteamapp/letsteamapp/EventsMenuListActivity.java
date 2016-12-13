package com.misha.mor.letsteamapp.letsteamapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by Misha on 12/10/2016.
 */
public class EventsMenuListActivity extends AppCompatActivity implements ActivityEventStateListener, SearchView.OnQueryTextListener{

    //class instances
    EventMenuArrayAdapter eventArrayAdapter;
    FireBaseDAL fdb;
    InnerReceiver innerReceiver;

    Button btnListed;
    Button btnMyEvents;
    Button btnAllEvenets;

    //views
    ListView listView;


    //var
    boolean isReached = false;
    String eventID;
    String myUserName;
    String myUserID;
    Intent intent;

    ArrayList<Event> Events;
    boolean justEntered = true;
    SharedPreferences sharedPreferences;
    ProgressBar pBar;
    GestureDetector gestureObject;
    int mDiffX;
    int mDiffY;
    float mLastX;
    float mLastY;
    View.OnTouchListener gestureListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list_selector);

        /*gestureObject = new GestureDetector(this,new LearnGesture());
        gestureListener = new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event)
            {
                return gestureObject.onTouchEvent(event);
            }
        };*/
        /*listView.setOnTouchListener(gestureListener);*/
        // progress bar start
        pBar = (ProgressBar) findViewById(R.id.progressBar2);
        pBar.setVisibility(View.VISIBLE);
        pBar.bringToFront();


        //register innerReceiver for Broadcast
        innerReceiver = new InnerReceiver(EventsMenuListActivity.this);
        registerReceiver(innerReceiver, new IntentFilter(
                getString(R.string.BROADCAST_ACTION_POLL_ROOMS)));
        sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        intent = getIntent();
        /*eventID = intent.getStringExtra("eventID");*/
        myUserID = SharedPreferencesUtil.getUserID(sharedPreferences,this);/*intent.getStringExtra("userID");*/
        myUserName = SharedPreferencesUtil.getUserName(sharedPreferences,this);/*intent.getStringExtra("userName");*/

        //get\create singleton db reference
        fdb = FireBaseDAL.getFireBaseDALInstance();
        fdb.setContext(EventsMenuListActivity.this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EventsMenuListActivity.this,CreateEventActivity.class);
                /*intent.putExtra("userID",myUserID);
                intent.putExtra("userEmail",sharedPreferences.getString(getString(R.string.userEmail), ""));*/
                startActivity(intent);
            }
        });
        FloatingActionButton fabSwitch = (FloatingActionButton) findViewById(R.id.fabSwitch);
        fabSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EventsMenuListActivity.this,EventsMenuActivity.class);
                /*intent.putExtra("userID",myUserID);
                intent.putExtra("userEmail",sharedPreferences.getString(getString(R.string.userEmail), ""));*/
                startActivity(intent);
                finish();
            }
        });

        //initialize all views
        initViews();
        initAdapter();
        getEventsData();

        /*registerForMessage(eventID);*/
       /* setChatMessageInitialID(eventID);*/

    }
    @Override //For Activities
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchView searchItem = (SearchView) menu.getItem(0).getActionView();
        searchItem.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        intent.setAction(Intent.ACTION_SEARCH);
        handleQuery(intent, query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    private void handleQuery(Intent intent, String query) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchOnEvents(query);
        }
    }

    private void searchOnEvents(String query){
        ArrayList<Event> tempArray = new ArrayList<>();
        for (int eventId =0; eventId < Events.size(); eventId++)
        {
            if(UtilMethods.containsIgnoreCase(Events.get(eventId).event_DisplayName, query)){
                tempArray.add(Events.get(eventId));
            }
        }
        Events = tempArray;
        eventArrayAdapter.clearListAdapter();
        updateList();
    }
   /*@Override
    public boolean onTouchEvent(MotionEvent event){
        this.gestureObject.onTouchEvent(event);
        return super.onTouchEvent(event);
    }*/

    @Override
    public void onResume() {
        super.onResume();
        eventArrayAdapter.clearListAdapter();
        registerReceiver(innerReceiver, new IntentFilter(getString(R.string.BROADCAST_ACTION_POLL_ROOMS)));
        getEventsData();

    }
    @Override
    public void onPause() {
        super.onPause();
        try{
            unregisterReceiver(innerReceiver);
        }catch (RuntimeException exc){

            Log.e("onPause","unregisterReceiver(innerReceiver) " +exc.getMessage());
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            unregisterReceiver(innerReceiver);
        }catch (RuntimeException exc){

            Log.e("onBack","unregisterReceiver(innerReceiver)");
        }
    }
    public void initAdapter(){

        eventArrayAdapter = new EventMenuArrayAdapter(getApplicationContext()
                , R.layout.activity_list_event_context
                ,myUserID,myUserName);

        listView.setAdapter(eventArrayAdapter);

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        listView.setAdapter(eventArrayAdapter);

        //auto scroll on data change
        eventArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(eventArrayAdapter.getCount() - 1);
            }
        });

    }
    public void initViews(){

        //list view for messages
        listView = (ListView)findViewById(R.id.event_List);
        listView.setOnTouchListener(gestureListener);
        //send message button
        btnAllEvenets = (Button)findViewById(R.id.btnAllEvents);
        btnAllEvenets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pBar.setVisibility(View.VISIBLE);
                pBar.bringToFront();
                eventArrayAdapter.clearListAdapter();
                getEventsData();
            }
        });

        btnListed = (Button)findViewById(R.id.btnListed);
        btnListed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pBar.setVisibility(View.VISIBLE);
                pBar.bringToFront();
                eventArrayAdapter.clearListAdapter();
                fdb.getMyListedEventsState(myUserID);
            }
        });

        btnMyEvents = (Button)findViewById(R.id.btnMyEvents);
        btnMyEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pBar.setVisibility(View.VISIBLE);
                pBar.bringToFront();
                eventArrayAdapter.clearListAdapter();
                fdb.getMyEventsState(myUserID);
            }
        });
    }

    private void getEventsData(){
        //fill the gridArray
        fdb.getEventState();
    }

    public void getEvents() {
        Events =  new ArrayList<>(fdb.getEventHashMap().values());
    }


    class InnerReceiver extends BroadcastReceiver {

                Context context;

                public InnerReceiver() {

        }

        public InnerReceiver(Context context) {
            this.context = context;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            getEvents();

            Log.e("innerReceiver", "MyReceiver: broadcast received , rooms");

            //update View
           /* initAdapter();*/
            updateList();
            pBar.setVisibility(View.GONE);
        }
    }

    private void updateList() {

        for (Event event:Events) {

            eventArrayAdapter.add(event);

        }

        fdb.clearEventsMap();

    }
  /*  class LearnGesture extends GestureDetector.SimpleOnGestureListener {


        @Override
        public  boolean onFling(MotionEvent event1 ,MotionEvent event2,
                                float velocityX,float velocityY){
            switch (event1.getAction()) {

            if((event2.getX() > event1.getX() )){ //left to righ
                Intent intent = new Intent(EventsMenuListActivity.this,EventsMenuActivity.class);
                *//*intent.putExtra("userID",myUserID);
                intent.putExtra("userEmail",sharedPreferences.getString(getString(R.string.userEmail), ""));*//*
                startActivity(intent);
                finish();

            }
            else if (event2.getX() < event1.getX()){//right to left

                Intent intent = new Intent(EventsMenuListActivity.this,CreateEventActivity.class);
                *//*intent.putExtra("userID",myUserID);
                intent.putExtra("userEmail",sharedPreferences.getString(getString(R.string.userEmail), ""));*//*
                startActivity(intent);
            }

            return true;
        }


    }*/

}