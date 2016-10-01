package com.misha.mor.letsteamapp.letsteamapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class ChatRoomGridSelector extends AppCompatActivity implements ActivityRoomStateListener {

    //Views
    GridView gridView;


    //Class instance
    CustomGridViewAdapter customGridAdapter;
    FireBaseDAL fdb; //DAL
    BroadcastReceiver innerReceiver;

    //var
    ArrayList<Room> gridArray = new ArrayList<>();
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_grid_selector);

        //register for broadcast from dal
        innerReceiver = new InnerReceiver(ChatRoomGridSelector.this);
        registerReceiver(innerReceiver, new IntentFilter(getString(R.string.BROADCAST_ACTION_POLL_ROOMS)));

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        //initialize data DAL
        fdb = FireBaseDAL.getFireBaseDALInstance();
        fdb.setContext(ChatRoomGridSelector.this);


        getGridData();

    }
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(innerReceiver, new IntentFilter(getString(R.string.BROADCAST_ACTION_POLL_ROOMS)));
        registerRoomStateListener();

    }
    @Override
    public void onPause() {
        super.onPause();
        try{
            unregisterReceiver(innerReceiver);
        }catch (RuntimeException exc){

            Log.e("onPause","unregisterReceiver(innerReceiver) " +exc.getMessage());
        }

        unregisterRoomStateListener();


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            unregisterReceiver(innerReceiver);
        }catch (RuntimeException exc){

            Log.e("onBack","unregisterReceiver(innerReceiver)");
        }

        unregisterRoomStateListener();



    }

    private void getGridData(){
        //fill the gridArray
        fdb.getRoomsState();
    }


    @Override
    public void registerRoomStateListener() {
        fdb.registerStateListener();
    }

    @Override
    public void unregisterRoomStateListener() {
        fdb.unregisterStateListener(this);
    }

    @Override
    public void getRooms() {
        gridArray =  new ArrayList<>(fdb.getRoomHashMap().values());    }

    @Override
    public void notifyListener() {
        getRooms();
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

            getRooms();

            Log.e("innerReceiver", "MyReceiver: broadcast received , rooms");

            //update View
            updateViewGrid();
        }
    }



    public void updateViewGrid(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // here we may create new room/event
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/

        gridView = (GridView) findViewById(R.id.gridView1);

        customGridAdapter = new CustomGridViewAdapter(this, R.layout.row_grid, gridArray);

        gridView.setAdapter(customGridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

                Intent intent = new Intent(ChatRoomGridSelector.this, ChatRoom.class);
                intent.putExtra("roomID",gridArray.get(position).getRoom_ID());
                intent.putExtra("userID",userID);

                startActivity(intent);
            }
        });
    }

}

