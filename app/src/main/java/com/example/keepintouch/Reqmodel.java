package com.example.keepintouch;

public class Reqmodel {
    String imgUrl,name;

    public Reqmodel(String imgUrl, String name) {
        this.imgUrl = imgUrl;
        this.name = name;
    }

    public Reqmodel() {
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
