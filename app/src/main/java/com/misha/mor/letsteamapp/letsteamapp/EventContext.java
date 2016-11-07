package com.misha.mor.letsteamapp.letsteamapp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class EventContext extends AppCompatActivity {

    TextView editTextContext;
    TextView editTextName;
    TextView editTextLocation;

    ImageButton btn_openEventChat;
    ImageButton btn_ListInForEvent;
    ImageButton btn_showEventLocation;
    String userID;
    String eventID;

    //set with on create //TODO need to create the participant status validation
    boolean isEventParticipant = false;

    //db
    FireBaseDAL fdb; //DAL
    InnerReceiver innerReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_context);


        //intent info
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        eventID = intent.getStringExtra("eventID");


        //register for broadcast from dal
        innerReceiver = new InnerReceiver(EventContext.this);
        registerReceiver(innerReceiver, new IntentFilter(
                getString(R.string.BROADCAST_ACTION_POLL_LISTED)));

        fdb = FireBaseDAL.getFireBaseDALInstance();
        fdb.setContext(EventContext.this);

        //initialize views
        initViews();

        checkIsUserListedForEvent();
    }
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(innerReceiver, new IntentFilter(getString(R.string.BROADCAST_ACTION_POLL_LISTED)));


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
    private void checkIsUserListedForEvent() {
        fdb.isUserListedForEvent(eventID,userID);
    }

    public void initViews(){
        editTextContext = (TextView)findViewById(R.id.etxtContext);
        editTextContext.setText("This is a new event.\njust to see how it will look with a lot of data.\n" +
                "Trying for it to look full.\nthose who will join will get a lot of amazing cookies.", TextView.BufferType.EDITABLE);

        editTextName = (TextView)findViewById(R.id.etxtCreatorName);
        editTextName.setText("Mor and Misha", TextView.BufferType.EDITABLE);

        editTextLocation = (TextView)findViewById(R.id.etxtLocation);
        editTextLocation.setText("ספיר 7, הרצליה", TextView.BufferType.EDITABLE);


        btn_openEventChat = (ImageButton)findViewById(R.id.btnOpenChat);
        if(btn_openEventChat != null){

            btn_openEventChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(EventContext.this, ChatRoom.class);
                    intent.putExtra("eventID",eventID);
                    intent.putExtra("userID",userID);
                    startActivity(intent);
                }
            });

        }

        btn_showEventLocation = (ImageButton)findViewById(R.id.btnShowEventLocation);
        if(btn_showEventLocation != null){

            btn_showEventLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: get real location to put in intent
                    String strEventLocation = editTextLocation.getText().toString();
                    Intent intent = new Intent(EventContext.this, MapsActivity.class);
                    intent.putExtra("eventLocation",strEventLocation);
                    startActivity(intent);

                }
            });

        }

        btn_ListInForEvent = (ImageButton)findViewById(R.id.btnListIn);
        btn_ListInForEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isEventParticipant){
                    fdb.removeEventParticipant(eventID,userID);
                    isEventParticipant = false;
                    Toast.makeText(EventContext.this, "your are a participant , removing from event participants",
                            Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(EventContext.this, "your are not a participant , adding you to event participants",
                            Toast.LENGTH_SHORT).show();
                    fdb.addEventParticipant(eventID,userID);
                    isEventParticipant = true;
                }


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



            Log.e("innerReceiver", "MyReceiver: broadcast received , listed for");

            boolean isListed = intent.getBooleanExtra("isListed",false);

            //update View
            updateView(isListed);

        }
    }
    private void updateView(boolean isListed) {

        isEventParticipant = isListed;
        if(isListed){
            Toast.makeText(EventContext.this, "your are a participant",
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(EventContext.this, "your are not a participant",
                    Toast.LENGTH_SHORT).show();
        }

    }
}