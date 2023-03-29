package com.example.chatchit.message;

public class Message {
    private String userName;
    private String userMessage;
    private String datetime;
    private String senderId;
    private String receiverId;
    private String contentWebView;
    private int isShown;

    public Message(String userName, String userMessage, String datetime, String contentWebView, String senderId, String receiverId, int isShown) {
        this.userName = userName;
        this.userMessage = userMessage;
        this.datetime = datetime;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.contentWebView = contentWebView;
        this.isShown = isShown;
    }
    public Message(){
    }

    public int getIsShown() {
        return isShown;
    }

    public void setIsShown( int isShown ) {
        this.isShown = isShown;
    }

    public String getContentWebView() {
        return contentWebView;
    }

    public void setContentWebView( String contentWebView ) {
        this.contentWebView = contentWebView;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
