package com.example.douyin.bean;

import com.google.gson.annotations.SerializedName;

public class GetVideoResponse {
    // TODO-C1 (1) Implement your Cat Bean here according to the response json
    @SerializedName("id") private String id;
    @SerializedName("url") private String url;
    @SerializedName("width") private int width;
    @SerializedName("height") private int height;

    public static class Value {

    }

    // public List<String> getBreeds(){ return breeds; }
    //public void setBreeds(List<String> breeds){ this.breeds = breeds; }

    public String getId(){ return id; }
    public void setId(String id){ this.id = id; }

    public String getUrl(){ return url; }
    public void setUrl(String url){ this.url = url; }

    public int getWidth(){ return width; }
    public void setWidth(int width){ this.width = width; }

    public int getHeight(){ return height; }
    public void setHeight(int height){ this.height = height; }
}
