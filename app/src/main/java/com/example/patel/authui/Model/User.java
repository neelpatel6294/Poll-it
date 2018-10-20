package com.example.patel.authui.Model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String id;
    public String username;
    public String profile_picture;

//    public User(String id, String name, String profile_picture) {
//        // Default constructor required for calls to DataSnapshot.getValue(User.class)
//    }


    public User() {
    }

    public User(String username, String profile_picture, String id) {
        this.username = username;
        this.id = id;
        this.profile_picture = profile_picture;
    }

    public String getUserId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    //    @Exclude
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("author", username);
//        result.put("title", profile_picture);
//
//        return result;
//    }
}