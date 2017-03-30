package com.example.gesanidas.unipipmsplishopping;


import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties

public class Product implements Serializable
{
    //a model class for the products in the database
    //it implements serializable in order to be saved in an intent
    private int id;
    private String title;
    private String description;
    private String date;
    private double price;
    private String storeLocation;
    private String photoUrl;
    private double Longitude;
    private double Latitude;


    public Product(){}


    public Product(int id,String title, String description, String date, double price, String photoUrl, double longitude, double latitude) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.price = price;
        this.photoUrl = photoUrl;
        Longitude = longitude;
        Latitude = latitude;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }
}
