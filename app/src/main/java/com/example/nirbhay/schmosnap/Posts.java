package com.example.nirbhay.schmosnap;

//Module Class foe all posts for Recycler Adapter
//Here we retrieve data from database of posts

public class Posts
{
    public String uid;
    public String time;
    public String date;
    public String postimage;
    public String profileimage;
    public String description;
    public String fullname;

    public Posts()
    {}

    public Posts(String uid, String time, String date, String postimage, String profileimage, String description, String fullname)
    {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postimage = postimage;
        this.profileimage = profileimage;
        this.description = description;
        this.fullname = fullname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
