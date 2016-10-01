package com.misha.mor.letsteamapp.letsteamapp;

/**
 * Created by Misha on 9/10/2016.
 */


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.collection.ArraySortedMap;

import java.util.ArrayList;

public class Room {

    String room_DisplayName;
    String room_Owner;

    //unique  id
    String room_ID ;

    //tags for filter
    ArrayList<String> room_Tags ;

    //dummy
    @JsonIgnore
    ArraySortedMap<String,String> ChatMessages;
    //status failed's
    boolean room_isActive ;
    String room_createDate ;
    String room_closeDate ;

    //default constructor in firebase use
    public Room() {
    }

    public Room(String room_DisplayName,String Tag) {
        this.room_DisplayName = room_DisplayName;
        room_Tags = new ArrayList<String>();
        room_Tags.add(Tag);

    }

    public boolean isRoom_isActive() {
        return room_isActive;
    }

    public void setRoom_isActive(boolean room_isActive) {
        this.room_isActive = room_isActive;
    }

    public void setRoom_ID(String room_ID) {
        this.room_ID = room_ID;
    }

    public void setRoom_Tags(ArrayList<String> room_Tags) {
        this.room_Tags = room_Tags;
    }


    public void setRoom_createDate(String room_createDate) {
        this.room_createDate = room_createDate;
    }

    public void setRoom_closeDate(String room_closeDate) {
        this.room_closeDate = room_closeDate;
    }

    public String getRoom_DisplayName() {
        return room_DisplayName;
    }

    public String getRoom_Owner() {
        return room_Owner;
    }

    public String getRoom_ID() {
        return room_ID;
    }

    public ArrayList<String> getRoom_Tags() {
        return room_Tags;
    }


    public String getRoom_createDate() {
        return room_createDate;
    }

    public void setRoom_DisplayName(String room_DisplayName) {
        this.room_DisplayName = room_DisplayName;
    }

    public void setRoom_Owner(String room_Owner) {
        this.room_Owner = room_Owner;
    }

    public String getRoom_closeDate() {
        return room_closeDate;
    }
    @JsonIgnore
    public ArraySortedMap<String, String> getChatMessages() {
        return ChatMessages;
    }
    @JsonIgnore
    public void setChatMessages(ArraySortedMap<String, String> chatMessages) {
        ChatMessages = chatMessages;
    }
}
