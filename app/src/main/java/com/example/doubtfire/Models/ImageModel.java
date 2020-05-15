package com.example.doubtfire.Models;

import com.google.firebase.database.Exclude;

public class ImageModel {
    public String key;
    public String userId;
    public String isSolved;
    public String downurl;
    public String subject;
    public String description;
    public int solutions;

    // these properties will not be saved to the database
    @Exclude
    public UserModel user;

    @Exclude
    public int likes = 0;

    @Exclude
    public boolean hasLiked = false;

    @Exclude
    public String userLike;

    public ImageModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public ImageModel(String key, String userId, String downurl, String description, String subject) {
        this.key = key;
        this.userId = userId;
        this.downurl = downurl;
        this.description = description;
        this.subject = subject;
        this.isSolved = "unsolved";
        this.solutions = 0;
    }

    public void addedSolution()
    {
        solutions++;
    }

}
