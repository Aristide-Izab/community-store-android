package com.example.communitystore.student;

public class StudentChatPreview {

    public final String name;
    public final String lastMessage;
    public final String time;
    public final int unread;

    public StudentChatPreview(String name, String lastMessage, String time, int unread) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
        this.unread = unread;
    }
}
