package com.example.aksha.newb;

import android.graphics.Bitmap;

public class MarkerInfoSearch {
    ArrayList<String> bitmapUrl;
    String cost;
    String description;
    String longitude;
    String latitude;
    String id;
    String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getBitmapUrl() {
        return bitmapUrl;
    }

    public void setBitmapUrl(ArrayList<String> bitmapUrl) {
        this.bitmapUrl = bitmapUrl;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
