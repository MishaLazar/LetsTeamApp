package com.misha.mor.letsteamapp.letsteamapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreateEventActivity extends AppCompatActivity {

    //views
    EditText editTextContext;
    EditText editTextDisplayName;
    EditText editTextLocation;

    Button btn_creatEvent;
    Button btn_setEventLocation;

    //db
    FireBaseDAL fdb; //DAL

    //vars
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");


        //initialize data DAL
        fdb = FireBaseDAL.getFireBaseDALInstance();


        initViews();

    }

    public void initViews(){

        editTextContext = (EditText)findViewById(R.id.EventContext);
        editTextContext.setText("this is a demo data \n this is demo data \n this is demo data", TextView.BufferType.EDITABLE);

        editTextDisplayName = (EditText)findViewById(R.id.event_displayName);
        editTextDisplayName.setText("Mor and Misha", TextView.BufferType.EDITABLE);

        editTextLocation = (EditText)findViewById(R.id.eventLocation);
        editTextLocation.setText("My New Event", TextView.BufferType.EDITABLE);

        btn_creatEvent = (Button)findViewById(R.id.btnCreateEvent);
        if(btn_creatEvent != null){

            btn_creatEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO make tag generic
                    String tag = "Tag";
                    String event_displayName = editTextDisplayName.getText().toString();
                    String event_location = editTextLocation.getText().toString();
                    String event_context = editTextContext.getText().toString();

                    Event newEvent = new Event(event_displayName,event_context,tag,event_location);
                    newEvent.setEvent_Owner(userID);

                    fdb.registerEvent(newEvent);


                }
            });


        }



    }
}