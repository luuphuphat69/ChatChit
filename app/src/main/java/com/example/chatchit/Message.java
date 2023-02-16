package com.example.chatchit;

public class Message {
    private String userEmail;
    private String userMessage;
    private String datetime;

    public Message(String userEmail, String userMessage, String datetime) {
        this.userEmail = userEmail;
        this.userMessage = userMessage;
        this.datetime = datetime;
    }
    public Message(){
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
