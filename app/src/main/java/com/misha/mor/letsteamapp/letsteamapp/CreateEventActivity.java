package com.misha.mor.letsteamapp.letsteamapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreateEventActivity extends AppCompatActivity {

    //views
    EditText editTextContext;
    EditText editTextDisplayName;
    EditText editTextCity;
    EditText editTextStreet;
    EditText editTextHouseNumber;
    EditText editTextStartDate;
    EditText editTextEndDate;
    EditText editTextType;
    AutoCompleteTextView textView;
    ArrayAdapter<String> arrayAdapter;
    Button btn_creatEvent;

    //db
    FireBaseDAL fdb; //DAL


    //vars
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MyApp.setLocaleEn(CreateEventActivity.this);
        setContentView(R.layout.activity_create_event);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");


        //initialize data DAL
        fdb = FireBaseDAL.getFireBaseDALInstance();


        initViews();

    }

    public void initViews(){
        editTextContext = (EditText)findViewById(R.id.etxtEventContext);
        editTextDisplayName = (EditText)findViewById(R.id.event_displayName);
        editTextCity = (EditText)findViewById(R.id.etxtEventCity);
        editTextStreet = (EditText)findViewById(R.id.etxtEventStreet);
        editTextHouseNumber = (EditText)findViewById(R.id.etxtEventHouseNum);
        editTextType = textView;
        editTextEndDate = (EditText)findViewById(R.id.etxtStartDate);
        editTextStartDate = (EditText)findViewById(R.id.etxtEndDate);

        btn_creatEvent = (Button)findViewById(R.id.btnCreateEvent);
        if(btn_creatEvent != null){

            btn_creatEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO make tag generic
                    String event_displayName = editTextDisplayName.getText().toString();
                    String event_context = editTextContext.getText().toString();
                    String event_city = editTextCity.getText().toString();
                    String event_street = editTextStreet.getText().toString();
                    String event_HouseNumber = editTextHouseNumber.getText().toString();
                    String event_StartDate = editTextStartDate.getText().toString();
                    String event_EndDate = editTextEndDate.getText().toString();
                    String event_Type = editTextType.getText().toString();

                    String Location = event_street + " " + event_HouseNumber + ", " + event_city;
                    Event newEvent = new Event(event_displayName,Location,event_context,event_Type,event_StartDate,event_EndDate);
                    newEvent.setEvent_Owner(userID);

                    fdb.registerEvent(newEvent);


                }
            });


        }
        //TODO: maybe switch to expending txt layout in phone looks bad
        arrayAdapter = new ArrayAdapter<>(
        this, android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.array_test));

        textView = (AutoCompleteTextView) findViewById(R.id.cbType);
        textView.setAdapter(arrayAdapter);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                textView.showDropDown();
            }
        });



    }
}