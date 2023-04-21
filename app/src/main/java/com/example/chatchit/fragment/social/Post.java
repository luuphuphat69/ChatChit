package com.example.chatchit.fragment.social;

public class Post {
    private String postId, userId;
    private String postContent, postContentImage, postContentVideo, userName, postDate;
    private String postURLTitle,  postURL;
    private int amountLike;

    public Post() {
    }

    public Post( String postId, String postContent, String postContentImage, String postContentVideo, String postURL, String postURLTitle, String userName, String userId, String postDate, int amountLike) {
        this.postId = postId;
        this.postContent = postContent;
        this.userName = userName;
        this.postDate = postDate;
        this.postContentImage = postContentImage;
        this.postContentVideo = postContentVideo;
        this.postURL = postURL;
        this.postURLTitle = postURLTitle;
        this.userId = userId;
        this.amountLike = amountLike;
    }

    public String getPostURLTitle() {
        return postURLTitle;
    }

    public void setPostURLTitle( String postURLTitle ) {
        this.postURLTitle = postURLTitle;
    }

    public String getPostURL() {
        return postURL;
    }

    public void setPostURL( String postURL ) {
        this.postURL = postURL;
    }

    public String getPostContentVideo() {
        return postContentVideo;
    }

    public void setPostContentVideo( String postContentVideo ) {
        this.postContentVideo = postContentVideo;
    }

    public int getAmountLike() {
        return amountLike;
    }

    public void setAmountLike( int amountLike ) {
        this.amountLike = amountLike;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId( String postId ) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId( String userId ) {
        this.userId = userId;
    }

    public String getPostContentImage() {
        return postContentImage;
    }

    public void setPostContentImage( String postContentImage ) {
        this.postContentImage = postContentImage;
    }


    public String getPostContent() {
        return postContent;
    }

    public void setPostContent( String postContent ) {
        this.postContent = postContent;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName( String userName ) {
        this.userName = userName;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate( String postDate ) {
        this.postDate = postDate;
    }
}
