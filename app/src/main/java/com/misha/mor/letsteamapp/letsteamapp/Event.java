package com.misha.mor.letsteamapp.letsteamapp;

/**
 * Created by Misha on 9/10/2016.
 */


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.collection.ArraySortedMap;

import java.util.ArrayList;

public class Event {

    //metadata
    String event_DisplayName;
    String event_Owner;
    String event_Context;
    String event_location;
    String event_Type;
    String event_Start;
    String event_End;
    String event_Owner_email;




    long ParticipantCount;
    //unique  id
    String event_ID;


    //tags for filter
    ArrayList<String> event_Tags;

    //dummy
    @JsonIgnore
    ArraySortedMap<String,String> ChatMessages;

    //status failed's
    boolean event_isActive;
    String event_createDate;
    String event_closeDate;

    //default constructor in firebase use
    public Event() {
    }

    public Event(String event_DisplayName, String Tag) {
        this.event_DisplayName = event_DisplayName;
        event_Tags = new ArrayList<String>();
        event_Tags.add(Tag);

    }


    public String getEvent_Owner_email() {
        return event_Owner_email;
    }

    public void setEvent_Owner_email(String event_Owner_email) {
        this.event_Owner_email = event_Owner_email;
    }


    public Event(String event_DisplayName, String event_location, String event_Context, String Type, String StartDate, String EndDate, String OwnerEmail) {
        this.event_DisplayName = event_DisplayName;
        this.event_location = event_location;
        this.event_Context = event_Context;
        this.event_Type = Type;
        this.event_Start = StartDate;
        this.event_End = EndDate;
        this.event_Owner_email = OwnerEmail;

    }

    public String getEvent_Type() {
        return event_Type;
    }

    public void setEvent_Type(String event_Type) {
        this.event_Type = event_Type;
    }

    public String getEvent_Start() {
        return event_Start;
    }

    public void setEvent_Start(String event_Start) {
        this.event_Start = event_Start;
    }

    public String getEvent_End() {
        return event_End;
    }

    public void setEvent_End(String event_End) {
        this.event_End = event_End;
    }
    public boolean isEvent_isActive() {
        return event_isActive;
    }

    public void setEvent_isActive(boolean event_isActive) {
        this.event_isActive = event_isActive;
    }

    public void setEvent_ID(String event_ID) {
        this.event_ID = event_ID;
    }

    public void setEvent_Tags(ArrayList<String> event_Tags) {
        this.event_Tags = event_Tags;
    }


    public void setEvent_createDate(String event_createDate) {
        this.event_createDate = event_createDate;
    }

    public void setEvent_closeDate(String event_closeDate) {
        this.event_closeDate = event_closeDate;
    }

    public String getEvent_DisplayName() {
        return event_DisplayName;
    }

    public String getEvent_Owner() {
        return event_Owner;
    }

    public String getEvent_ID() {
        return event_ID;
    }

    public ArrayList<String> getEvent_Tags() {
        return event_Tags;
    }


    public String getEvent_createDate() {
        return event_createDate;
    }

    public void setEvent_DisplayName(String event_DisplayName) {
        this.event_DisplayName = event_DisplayName;
    }

    public String getEvent_Context() {
        return event_Context;
    }

    public void setEvent_Context(String event_Context) {
        this.event_Context = event_Context;
    }

    public String getEvent_location() {
        return event_location;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
    }

    public void setEvent_Owner(String event_Owner) {
        this.event_Owner = event_Owner;
    }

    public String getEvent_closeDate() {
        return event_closeDate;
    }
    @JsonIgnore
    public ArraySortedMap<String, String> getChatMessages() {
        return ChatMessages;
    }
    @JsonIgnore
    public void setChatMessages(ArraySortedMap<String, String> chatMessages) {
        ChatMessages = chatMessages;
    }
    @JsonIgnore
    public long getParticipantCount() {
        return ParticipantCount;
    }

    public void setParticipantCount(long participantCount) {
        ParticipantCount = participantCount;
    }
}
