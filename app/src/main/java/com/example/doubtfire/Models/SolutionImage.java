package com.example.doubtfire.Models;

import com.google.firebase.database.Exclude;

public class SolutionImage {
    public String key;
    public String magnifierkey;
    public String userId;
    public String downurl;
    public String description;

    // these properties will not be saved to the database
    @Exclude
    public UserModel user;


    public SolutionImage() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public SolutionImage(String key, String userId, String downurl, String description,String magnifierkey) {
        this.key = key;
        this.userId = userId;
        this.downurl = downurl;
        this.description = description;
        this.magnifierkey = magnifierkey;
    }
}
