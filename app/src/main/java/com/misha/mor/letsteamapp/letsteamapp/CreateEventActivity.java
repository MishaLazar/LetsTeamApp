package com.misha.mor.letsteamapp.letsteamapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class CreateEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        DemoData();
    }

    public void DemoData(){
        EditText editTextContext = (EditText)findViewById(R.id.etxtEventContext);
        editTextContext.setText("this is a demo data \n this is demo data \n this is demo data", TextView.BufferType.EDITABLE);

        EditText editTextName = (EditText)findViewById(R.id.etxtCreatorName);
        editTextName.setText("Mor and Misha", TextView.BufferType.EDITABLE);

        EditText editTextLocation = (EditText)findViewById(R.id.etxtEventName);
        editTextLocation.setText("My New Event", TextView.BufferType.EDITABLE);
    }
}