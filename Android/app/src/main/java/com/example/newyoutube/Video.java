package com.example.newyoutube;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Video implements Serializable {
    private String id;
    private String title;
    private String channel;

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", channel='" + channel + '\'' +
                ", path='" + path + '\'' +
                ", image='" + image + '\'' +
                ", views=" + views +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", comments=" + comments +
                ", details='" + details + '\'' +
                ", date='" + date + '\'' +
                ", createdByUser=" + createdByUser +
                '}';
    }

    private String path;
    private String image;
    private int views;
    private int likes;
    private int dislikes;
    private List<Comment> comments;
    private String details;
    private String date;


    public boolean isCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(boolean createdByUser) {
        this.createdByUser = createdByUser;
    }

    private boolean createdByUser;

    public Video(JSONObject jsonObject) {
        this.id = jsonObject.optString("id");
        this.title = jsonObject.optString("title");
        this.channel = jsonObject.optString("channel");
        this.path = jsonObject.optString("path");
        this.image = jsonObject.optString("image");
        this.views = jsonObject.optInt("views");
        this.likes = jsonObject.optInt("likes");
        this.dislikes = jsonObject.optInt("dislikes");
        this.details = jsonObject.optString("details");
        this.date = jsonObject.optString("date");
        this.createdByUser = jsonObject.optBoolean("createdByUser", false);

        // If comments is a JSON array, you can parse it like this:
        this.comments = new ArrayList<>();
        JSONArray commentsArray = jsonObject.optJSONArray("comments");
        if (commentsArray != null) {
            for (int i = 0; i < commentsArray.length(); i++) {
                JSONObject commentObject = commentsArray.optJSONObject(i);
                if (commentObject != null) {
                    Comment comment = new Comment(commentObject); // Assuming Comment has a similar constructor
                    this.comments.add(comment);
                }
            }
        }
    }


    public Video() {
        this.createdByUser = false;
    }

    public Video(String id, String title, String channel, String path, String image, int views, int likes, int dislikes, List<Comment> comments, String details, String date) {
        this.id = id;
        this.title = title;
        this.channel = channel;
        this.path = path;
        this.image = image;
        this.views = views;
        this.likes = likes;
        this.dislikes = dislikes;
        this.comments = comments;
        this.details = details;
        this.date = date;
        this.createdByUser = false;
    }


    // Getters and setters for all fields

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
