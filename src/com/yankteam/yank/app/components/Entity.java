package com.yankteam.yank.app.components;

import android.location.Location;

/*
 * Representation of the entity data structure which we write to and obtain from
 * the RESTful API.
 */
public class Entity {
    private String name;
    private String desc;

    private Double lat;
    private Double lng;

    public Entity(String name, String desc) {
        this(name, desc, 0.0, 0.0);
    }
    public Entity(String name, String desc, double lat, double lng) {
        this.name = name;
        this.lat  = lat;
        this.lng  = lng;
        this.desc = desc;
    }

    // getters and setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDesc(){
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getLat(){
        return lat;
    }
    public double getLng(){
        return lng;
    }
    public void setPos(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}
