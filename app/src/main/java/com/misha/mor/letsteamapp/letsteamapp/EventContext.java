package com.misha.mor.letsteamapp.letsteamapp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static com.misha.mor.letsteamapp.letsteamapp.R.id.fab;

public class EventContext extends AppCompatActivity {

    final String TAG = "EventContext" ;

    TextView editTextContext;
    TextView editTextName;
    TextView editTextLocation;
    TextView editTextEndDate;
    TextView editTextSatrtDate;

    /*ImageButton btn_openEventChat;
    ImageButton btn_ListInForEvent;
    ImageButton btn_showEventLocation;*/
    FloatingActionButton btn_openEventChat;
    FloatingActionButton btn_ListInForEvent;
    FloatingActionButton btn_showEventLocation;
    String userID;
    String eventID;
    String userName;
    Intent intent;

    //set with on create //TODO need to create the participant status validation
    boolean isEventParticipant = false;

    //db
    FireBaseDAL fdb; //DAL
    InnerReceiver innerReceiver;
    boolean isUserInfo = false;
    ProgressBar pBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_event_context);

        pBar = (ProgressBar) findViewById(R.id.progressBarEventContext);
        pBar.setVisibility(View.VISIBLE);
        pBar.bringToFront();

        //intent info
        intent = getIntent();
        userID = intent.getStringExtra("userID");
        eventID = intent.getStringExtra("eventID");
        userName = intent.getStringExtra("userName");

        //register for broadcast from dal
        innerReceiver = new InnerReceiver(EventContext.this);
        registerReceiver(innerReceiver, new IntentFilter(
                getString(R.string.BROADCAST_ACTION_POLL_LISTED)));
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
        pBar.setVisibility(View.VISIBLE);
        pBar.bringToFront();
        registerReceiver(innerReceiver, new IntentFilter(getString(R.string.BROADCAST_ACTION_POLL_LISTED)));
        checkIsUserListedForEvent();

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
        InitEventContext();
        InitButtonsPanel();
    }

    private void InitEventContext(){
        Intent intent = getIntent();
        String eventName = intent.getStringExtra("eventName");
		String eventCreatorID = intent.getStringExtra("eventCreator");
        String eventCreator = intent.getStringExtra("eventCreatorEmail");
        String eventContext = intent.getStringExtra("eventContext");
        String eventLocation = intent.getStringExtra("eventLocation");
        String eventStartDate = intent.getStringExtra("eventStartDate");
        String eventEndDate = intent.getStringExtra("eventEndDate");
        String eventType = intent.getStringExtra("eventType");


        /*editTextContext = (TextView)findViewById(R.id.etxtContext);
        editTextContext.setText(eventContext);
*/
        editTextContext = (TextView)findViewById(R.id.etxrContext2);
        editTextContext.setText(eventContext);

        /*editTextName = (TextView)findViewById(R.id.etxtCreatorName);
        editTextName.setText(eventCreator);*/

        TextView EventNameHeadrer = (TextView)findViewById(R.id.txtEventName);
        EventNameHeadrer.setText(eventName);

        editTextLocation = (TextView)findViewById(R.id.etxtLocation);
        if(eventLocation.length() <= 3){
            eventLocation = "Unspecified";
        }
        editTextLocation.setText(eventLocation);

        editTextSatrtDate = (TextView)findViewById(R.id.etxtEventStartDate);
        editTextSatrtDate.setText(eventStartDate);

        editTextEndDate = (TextView)findViewById(R.id.etxtEventEndDate);
        editTextEndDate.setText(eventEndDate);

        editTextName = (TextView) findViewById(R.id.etxtCreatorName);
        editTextName.setText(userName);

        setIEventImg(eventType);
        /*getUserInfo(eventCreatorID);*/

    }

    private void setIEventImg(String caseS){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutEvent);
        int resID = -1;
        switch (caseS){
            case "sport":
                resID = R.drawable.sportbackground;
                break;
            case "study":
                resID = R.drawable.studybackground;
                break;
            case "local trip":
                resID = R.drawable.trip_camping;
                break;
            case "night entertainment":
                resID = R.drawable.night_life2;
                break;
            case "abroad":
                resID = R.drawable.abroad_passport;
                break;
            default :
                resID = R.drawable.diary_event;

        }
        linearLayout.setBackgroundResource(resID);
    }
    private void InitButtonsPanel(){
        btn_openEventChat = (FloatingActionButton) findViewById(R.id.btnOpenChat);
        btn_openEventChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EventContext.this, ChatRoom.class);
                intent.putExtra("eventID",eventID);
                intent.putExtra("userID",userID);
                intent.putExtra("userName",userName);
                startActivity(intent);
            }
        });
        btn_ListInForEvent= (FloatingActionButton) findViewById(R.id.btnListIn);
        btn_ListInForEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isEventParticipant){
                    fdb.removeEventParticipant(eventID,userID);
                    isEventParticipant = false;
                    btn_ListInForEvent.setImageDrawable
                            (ContextCompat.getDrawable(EventContext.this,R.drawable.star));
                }else {
                    btn_ListInForEvent.setImageDrawable
                            (ContextCompat.getDrawable(EventContext.this,R.drawable.star_blue_outline));

                    fdb.addEventParticipant(eventID,userID);
                    isEventParticipant = true;
                }
            }
        });
        btn_showEventLocation  = (FloatingActionButton) findViewById(R.id.btnShowEventLocation);
        btn_showEventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEventLocation = editTextLocation.getText().toString();
                if(!strEventLocation.equals("Unspecified"))
                {
                    Intent intent = new Intent(EventContext.this, MapsActivity.class);
                    intent.putExtra("eventLocation",strEventLocation);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(EventContext.this, "The location was not specified.", Toast.LENGTH_SHORT).show();
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
            /*isUserInfo = intent.getBooleanExtra("isUserInfo",false);*/

            /*if(isUserInfo){
                updateUserInfo();
                isUserInfo = false;
            }*/
            //update View
            updateView(isListed);
            pBar.setVisibility(View.GONE);


        }


    }
    /*private  void getUserInfo(String eventCreatorID){
        fdb.getUserInfo(eventCreatorID);
    }*/
    /*private void setUserNameView() {
        editTextName = (TextView) findViewById(R.id.etxtCreatorName);
        editTextName.setText(userName);
    }*/

    /*private void updateUserInfo() {
        userName =  fdb.getUserInfo();
        setUserNameView();
    }*/
    private void updateView(boolean isListed) {

        isEventParticipant = isListed;
        if(isListed){
            try{
                /*btn_ListInForEvent.setBackground(getDrawable(R.drawable.star_blue_outline));*/
                btn_ListInForEvent.setImageResource(R.drawable.star_blue_outline);
            }catch (Exception exc){
                Log.d(TAG,exc.getMessage());
                exc.getStackTrace();
            }

        }else{
            try {
                btn_ListInForEvent.setImageResource(R.drawable.star);
                /*btn_ListInForEvent.setBackground(getDrawable(R.drawable.star));*/
            }catch (Exception exc){
                Log.d(TAG,exc.getMessage());
                exc.getStackTrace();
            }

        }

    }

}