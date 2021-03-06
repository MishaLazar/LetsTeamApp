package com.misha.mor.letsteamapp.letsteamapp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.ServerValue;

import java.util.Date;
import java.util.Map;

/**
 * Created by Misha on 9/11/2016.
 */

public class ChatMessage implements Comparable <ChatMessage>{
    
    String id;
    String message;
    String userId;
    String dateTime;
    String roomID;
    String ownerName;
    String dateOnly;
    String timeOnly;
    String bitmapStringUserPic;
    long idCounter;


    public ChatMessage() {
    }

    public ChatMessage(String message,String roomID,String userId,String ownerName) {
        this.roomID = roomID;
        this.message = message;
        this.userId = userId;
        this.ownerName = ownerName;
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
  //  @JsonIgnore
    public String getTimeOnly() {
        return timeOnly;
    }
//    @JsonIgnore
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

    public String getOwnerName() {
        return ownerName;
    }

    public long getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(long idCounter) {
        this.idCounter = idCounter;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getBitmapStringUserPic() {
        return bitmapStringUserPic;
    }

    public void setBitmapStringUserPic(String bitmapStringUserPic) {
        this.bitmapStringUserPic = bitmapStringUserPic;
    }
}