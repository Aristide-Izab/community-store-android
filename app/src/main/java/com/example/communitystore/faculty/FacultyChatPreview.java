package com.example.communitystore.faculty;

public class FacultyChatPreview {

    public final String name;
    public final String lastMessage;
    public final String time;
    public final int unread;

    public FacultyChatPreview(String name, String lastMessage, String time, int unread) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
        this.unread = unread;
    }
}
