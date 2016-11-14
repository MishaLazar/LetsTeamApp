package com.misha.mor.letsteamapp.letsteamapp;

/**
 * Created by Misha on 9/22/2016.
 */
public class RoomButton {

    String roomButtonID;
    Event chatRoo;


    public RoomButton() {
    }

    public String getRoomButtonID() {
        return roomButtonID;
    }

    public void setRoomButtonID(String roomButtonID) {
        this.roomButtonID = roomButtonID;
    }

    public Event getChatRoo() {
        return chatRoo;
    }

    public void setChatRoo(Event chatRoo) {
        this.chatRoo = chatRoo;
    }
}
