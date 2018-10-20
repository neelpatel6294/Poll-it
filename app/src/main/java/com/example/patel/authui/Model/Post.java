package com.example.patel.authui.Model;

import java.util.Map;

public class Post {

    private User user;
    private String question;
    private String imageUrl1;
    private String imageUrl2;
    private String storage_uri;
    private Object timestamp;


    public Post(User user, String question, Object timestamp,
                String imageUrl1, String imageUrl2, String storage_uri) {
        this.user = user;
        this.question = question;
        this.timestamp = timestamp;
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.storage_uri = storage_uri;
    }


    public Post() {
    }



    public String getImageUrl1() {
        return imageUrl1;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public User getUser() {
        return user;
    }

    public String getQuestion() {
        return question;
    }


    public String getStorage_uri() {
        return storage_uri;
    }
}
