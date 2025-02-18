package com.example.newyoutube;

import org.json.JSONObject;

import java.io.Serializable;

// Comment.java
public class Comment implements Serializable {
    private String author;

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String text;

    public Comment() {}
    public Comment(JSONObject jsonObject) {
        this.author = jsonObject.optString("author");
        this.text = jsonObject.optString("text");
    }

    public Comment(String author, String text) {
        this.author = author;
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }
}
