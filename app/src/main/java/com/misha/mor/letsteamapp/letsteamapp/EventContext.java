package com.misha.mor.letsteamapp.letsteamapp;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class EventContext extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_context);
        DemoData();
    }

    public void DemoData(){
        EditText editTextContext = (EditText)findViewById(R.id.etxtContext);
        editTextContext.setText("this is a demo data \n this is demo data \n this is demo data", TextView.BufferType.EDITABLE);

        EditText editTextName = (EditText)findViewById(R.id.etxtCreatorName);
        editTextName.setText("Mor and Misha", TextView.BufferType.EDITABLE);

        EditText editTextLocation = (EditText)findViewById(R.id.etxtLocation);
        editTextLocation.setText("Afeka", TextView.BufferType.EDITABLE);
    }
}