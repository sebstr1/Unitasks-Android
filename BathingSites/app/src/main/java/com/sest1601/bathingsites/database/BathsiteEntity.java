package com.sest1601.bathingsites.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index(value = {"lng", "lat"},
        unique = true)})

public class BathsiteEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "lng")
    private String lng;

    @ColumnInfo(name = "lat")
    private String lat;

    @ColumnInfo(name = "rating")
    private float rating;

    @ColumnInfo(name = "watertemp")
    private String watertemp;

    @ColumnInfo(name = "watertempdate")
    private String watertempdate;

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getLng() {
        return lng;
    }

    public String getLat() {
        return lat;
    }

    public float getRating() {
        return rating;
    }

    public String getWatertemp() {
        return watertemp;
    }

    public String getWatertempdate() {
        return watertempdate;
    }

    // Setters


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setWatertemp(String watertemp) {
        this.watertemp = watertemp;
    }

    public void setWatertempdate(String watertempdate) {
        this.watertempdate = watertempdate;
    }

}
