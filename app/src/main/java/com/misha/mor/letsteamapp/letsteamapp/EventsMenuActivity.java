package com.misha.mor.letsteamapp.letsteamapp;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.support.v7.widget.SearchView;


import java.util.ArrayList;

public class EventsMenuActivity extends AppCompatActivity implements ActivityEventStateListener, SearchView.OnQueryTextListener {

    //Views
    GridView gridView;
    Button btnListed;
    Button btnMyEvents;
    Button btnAllEvenets;


    //Class instance
    CustomGridViewAdapter customGridAdapter;
    FireBaseDAL fdb; //DAL
    BroadcastReceiver innerReceiver;

    //var
    ArrayList<Event> gridArray = new ArrayList<>();
    String userID;
    SharedPreferences sharedPreferences;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //MyApp.setLocaleEn(EventsMenuActivity.this);
        setContentView(R.layout.activity_chat_room_grid_selector);

        //register for broadcast from dal
        innerReceiver = new InnerReceiver(EventsMenuActivity.this);
        registerReceiver(innerReceiver, new IntentFilter(
                getString(R.string.BROADCAST_ACTION_POLL_ROOMS)));

        intent = getIntent();
        sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        userID = sharedPreferences.getString(getString(R.string.userID), "");//intent.getStringExtra("userID");



        //initialize data DAL
        fdb = FireBaseDAL.getFireBaseDALInstance();
        fdb.setContext(EventsMenuActivity.this);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EventsMenuActivity.this,CreateEventActivity.class);
                intent.putExtra("userID",userID);
                intent.putExtra("userEmail",sharedPreferences.getString(getString(R.string.userEmail), ""));
                startActivity(intent);
                // here we may create new room/event
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });



        initViews();
        getGridData();
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
        //handleIntent(intent);
        return true;
    }

   /*     @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }*/

    private void handleQuery(Intent intent, String query) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchOnEvents(query);
        }
    }

    private void searchOnEvents(String query){
        ArrayList<Event> tempArray = new ArrayList<>();
        for (int eventId =0; eventId < gridArray.size(); eventId++)
        {
            if(UtilMethods.containsIgnoreCase(gridArray.get(eventId).event_DisplayName, query)){
                tempArray.add(gridArray.get(eventId));
            }
        }
        gridArray = tempArray;
        updateViewGrid();
    }


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(innerReceiver, new IntentFilter(getString(R.string.BROADCAST_ACTION_POLL_ROOMS)));
        /*registerRoomStateListener();*/

    }
    @Override
    public void onPause() {
        super.onPause();
        try{
            unregisterReceiver(innerReceiver);
        }catch (RuntimeException exc){

            Log.e("onPause","unregisterReceiver(innerReceiver) " +exc.getMessage());
        }
/*

        unregisterRoomStateListener();
*/


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            unregisterReceiver(innerReceiver);
        }catch (RuntimeException exc){

            Log.e("onBack","unregisterReceiver(innerReceiver)");
        }

     /*   unregisterRoomStateListener();
*/


    }

    private void getGridData(){
        //fill the gridArray
        fdb.getEventState();
    }

/*
    @Override
    public void registerRoomStateListener() {
        fdb.registerStateListener();
    }

    @Override
    public void unregisterRoomStateListener() {
        fdb.unregisterStateListener(this);
    }*/

    public void getEvents() {
        gridArray =  new ArrayList<>(fdb.getEventHashMap().values());    }

    /*@Override
    public void notifyListener() {
        getEvents();
    }*/
    private void initViews(){

        btnAllEvenets = (Button)findViewById(R.id.btnAllEvents);
        btnAllEvenets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGridData();
            }
        });

        btnListed = (Button)findViewById(R.id.btnListed);
        btnListed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fdb.getMyListedEventsState(userID);
            }
        });

        btnMyEvents = (Button)findViewById(R.id.btnMyEvents);
        btnMyEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fdb.getMyEventsState(userID);
            }
        });

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
            updateViewGrid();

        }
    }



    public void updateViewGrid(){

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);*/
        //TODO take care duplication in notifications
        gridView = (GridView) findViewById(R.id.gridView);

        //clean view
        gridView.setAdapter(null);

        customGridAdapter = new CustomGridViewAdapter(this, R.layout.row_grid, gridArray);


        gridView.setAdapter(customGridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

                /*Intent intent = new Intent(EventsMenuActivity.this, ChatRoom.class);*/
                Intent intent = new Intent(EventsMenuActivity.this, EventContext.class);
                intent.putExtra("eventID",gridArray.get(position).getEvent_ID());
                intent.putExtra("userID",userID);
                intent.putExtra("eventName",gridArray.get(position).event_DisplayName);
                //TODO: change event owner to mail address
                intent.putExtra("eventCreator",gridArray.get(position).event_Owner);
                intent.putExtra("eventCreatorEmail",gridArray.get(position).event_Owner_email);
                intent.putExtra("eventLocation",gridArray.get(position).event_location);
                intent.putExtra("eventContext",gridArray.get(position).event_Context);
                intent.putExtra("eventEndDate",gridArray.get(position).event_closeDate);

                startActivity(intent);
            }
        });
    }

}

