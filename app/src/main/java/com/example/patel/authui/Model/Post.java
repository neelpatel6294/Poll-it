package com.example.patel.authui.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Post {

    private User user;
    private String question;
    private String imageUrl1;
    private String imageUrl2;
    private String thumb_url1;
    private String thumb_url2;
    private String thumb_storage_uri1;
    private String thumb_storage_uri2;
    private String full_storage_uri1;
    private String full_storage_uri2;
    private Object timestamp;

    private String votes;


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("question", question);
        result.put("imageUrl1", imageUrl1);
        result.put("imageUrl2", imageUrl2);
        result.put("thumb_url1", thumb_url1);
        result.put("thumb_url2", thumb_url2);
        result.put("thumb_storage_uri1", thumb_storage_uri1);
        result.put("thumb_storage_uri2", thumb_storage_uri2);
        result.put("full_storage_uri1", full_storage_uri1);
        result.put("full_storage_uri2", full_storage_uri2);
        result.put("timestamp", timestamp);
        result.put("votes",votes);
        return result;
    }

//    public Post(User user, String question, Object timestamp,
//                String imageUrl1, String imageUrl2, String storage_uri) {
//        this.user = user;
//        this.question = question;
//        this.timestamp = timestamp;
//        this.imageUrl1 = imageUrl1;
//        this.imageUrl2 = imageUrl2;
//        this.storage_uri = storage_uri;
//    }


    public Post(User user, String question, String imageUrl1, String imageUrl2,
                String thumb_url1, String thumb_url2, String thumb_storage_uri1,
                String thumb_storage_uri2, String full_storage_uri1,
                String full_storage_uri2, Object timestamp,String votes) {

        this.user = user;
        this.question = question;
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.thumb_url1 = thumb_url1;
        this.thumb_url2 = thumb_url2;
        this.thumb_storage_uri1 = thumb_storage_uri1;
        this.thumb_storage_uri2 = thumb_storage_uri2;
        this.full_storage_uri1 = full_storage_uri1;
        this.full_storage_uri2 = full_storage_uri2;
        this.timestamp = timestamp;
        this.votes = votes;
    }

    public Post() {
    }


    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        votes = votes;
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

    public String getThumb_url1() {
        return thumb_url1;
    }

    public String getThumb_url2() {
        return thumb_url2;
    }

    public String getThumb_storage_uri1() {
        return thumb_storage_uri1;
    }

    public String getThumb_storage_uri2() {
        return thumb_storage_uri2;
    }

    public String getFull_storage_uri1() {
        return full_storage_uri1;
    }

    public String getFull_storage_uri2() {
        return full_storage_uri2;
    }
}
