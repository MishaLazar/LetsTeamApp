package com.misha.mor.letsteamapp.letsteamapp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by Misha on 9/11/2016.
 */

public class ChatMessage implements Comparable <ChatMessage>{

    String id;
    String message;
    String userId;
    String dateTime;
    String roomID;
    @JsonIgnore
    String dateOnly;
    @JsonIgnore
    String timeOnly;


    public ChatMessage() {
    }

    public ChatMessage(String message,String roomID,String userId) {
        this.roomID = roomID;
        this.message = message;
        this.userId = userId;
        this.dateTime = UtilMethods.getDateTimeSimple();
        this.dateOnly = UtilMethods.getDateSimple();
        this.timeOnly = UtilMethods.getTimeSimple();

    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    //@JsonIgnore
    public String getDate() {
        return dateTime;
    }
    @JsonIgnore
    public String getTimeOnly() {
        return timeOnly;
    }
    @JsonIgnore
    public String getDateOnly() {
        return dateOnly;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }


    @Override
    public int compareTo(ChatMessage message) {
        return Comparators.MessageOrder.compare(this, message);
    }
}