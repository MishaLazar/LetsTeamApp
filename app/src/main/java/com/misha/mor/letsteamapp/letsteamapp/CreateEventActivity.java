package com.misha.mor.letsteamapp.letsteamapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends Activity {

    //views
    EditText editTextContext;
    EditText editTextDisplayName;
    EditText editTextCity;
    EditText editTextStreet;
    EditText editTextHouseNumber;
    TextView pickerTextStartDate;
    TextView pickerTextEndDate;
    Button btnTextStartTime;
    Button btnTextEndTime;
    AutoCompleteTextView editTextType;
    ArrayAdapter<String> arrayAdapter;
    Button btn_creatEvent;

    //db
    FireBaseDAL fdb; //DAL

    InnerReceiver innerReceiver;

    //vars
    String userID;
    Date dateValue;
    Calendar cal;
    String date;
    String timeS;
    Validator Validator = new Validator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        //register to receive notification
        innerReceiver = new InnerReceiver(CreateEventActivity.this);
        registerReceiver(innerReceiver, new IntentFilter(
                getString(R.string.BROADCAST_ACTION_POLL_EVENT_TAGS)));

        //initialize data DAL
        fdb = FireBaseDAL.getFireBaseDALInstance();


        initViews();
        fdb.getEventTags();

    }
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(innerReceiver, new IntentFilter(getString(R.string.BROADCAST_ACTION_POLL_EVENT_TAGS)));


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

    public void initViews(){
        editTextContext = (EditText)findViewById(R.id.etxtEventContext);
        editTextDisplayName = (EditText)findViewById(R.id.event_displayName);
        editTextCity = (EditText)findViewById(R.id.etxtEventCity);
        editTextStreet = (EditText)findViewById(R.id.etxtEventStreet);
        editTextHouseNumber = (EditText)findViewById(R.id.etxtEventHouseNum);
        pickerTextEndDate = (TextView) findViewById(R.id.pickerEndDateTime);
        pickerTextStartDate = (TextView) findViewById(R.id.pickerStartDateTime);
        btnTextEndTime = (Button)findViewById(R.id.btnEventEndDate);
        btnTextEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerFunction(pickerTextEndDate);

            }
        });
        btnTextStartTime = (Button)findViewById(R.id.btnEventStartDate);
        btnTextStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerFunction(pickerTextStartDate);
            }
        });

        btn_creatEvent = (Button)findViewById(R.id.btnCreateEvent);
        if(btn_creatEvent != null){

            btn_creatEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String event_displayName = editTextDisplayName.getText().toString();
                    String event_context = editTextContext.getText().toString();
                    String event_city = editTextCity.getText().toString();
                    String event_street = editTextStreet.getText().toString();
                    String event_HouseNumber = editTextHouseNumber.getText().toString();
                    String event_EndDate = pickerTextEndDate.getText().toString();
                    String event_StartDate = pickerTextStartDate.getText().toString();
                    String event_Type = editTextType.getText().toString();

                    String Location = event_street + " " + event_HouseNumber + ", " + event_city;
                    Intent intent = getIntent();
                    String userEmail = intent.getStringExtra(getString(R.string.userEmail));
                    if(Validator.isValidDisplayName(event_displayName)){
                        Event newEvent = new Event(event_displayName
                                ,Location,event_context
                                ,event_Type
                                ,event_StartDate
                                ,event_EndDate
                                ,userEmail);
                        newEvent.setEvent_Owner(userID);

                        fdb.registerEvent(newEvent);
                        onBackPressed();
                        finish();
                        Toast.makeText(getApplicationContext(),"The event was created.",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"The event name can't be empty",Toast.LENGTH_SHORT).show();
                    }


                }
            });


        }



    }
    public void datePickerFunction(final TextView dateText){
        final DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        dateValue = new Date();
        cal = Calendar.getInstance();
        cal.setTime(dateValue);
        new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override public void onDateSet(DatePicker view,
                                                    int y, int m, int d) {
                        cal.set(Calendar.YEAR, y);
                        cal.set(Calendar.MONTH, m);
                        cal.set(Calendar.DAY_OF_MONTH, d);
                        date = d+"/"+m+"/"+y;

                        // now show the time picker
                        new TimePickerDialog(CreateEventActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override public void onTimeSet(TimePicker view,
                                                                    int h, int min) {
                                        cal.set(Calendar.HOUR_OF_DAY, h);
                                        cal.set(Calendar.MINUTE, min);
                                        dateValue = cal.getTime();



                                        dateText.setText(df.format(dateValue));
                                    }
                                }, cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE), true).show();
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();

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



            Log.e("innerReceiver", "MyReceiver: broadcast received , tags");


            //update View
            updateTagPecker();//pecker
        }
    }

    private void updateTagPecker(){

        ArrayList<String> tagsArray = fdb.getTags();

        arrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line,
                tagsArray);

        editTextType = (AutoCompleteTextView) findViewById(R.id.cbType);
        editTextType.setAdapter(arrayAdapter);
        editTextType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                editTextType.showDropDown();
            }
        });
    }

}