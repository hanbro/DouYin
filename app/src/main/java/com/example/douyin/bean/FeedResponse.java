package com.example.douyin.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class FeedResponse {
    @SerializedName("feeds")private List<Feed> feeds;
    @SerializedName("success")private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Feed> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Feed> feeds) {
        this.feeds = feeds;
    }
}

