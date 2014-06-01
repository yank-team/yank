package com.yankteam.yank.app.components;

import android.location.Location;

/*
 * Representation of the entity data structure which we write to and obtain from
 * the RESTful API.
 */
public class Entity {

    private int id;
    private String name;

    private Double lat;
    private Double lng;

    public Entity(int _id, String _name) {
        this(_id, _name, 0.0, 0.0);
    }
    public Entity(int _id, String _name, double _lat, double _lng) {
        id   = _id;
        name = _name;
        lat  = _lat;
        lng  = _lng;
    }

    // getters and setters
    public int getId(){
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getLat(){
        return lat;
    }
    public double getLng(){
        return lng;
    }
    public void setPos(double _lat, double _lng) {
        lat = _lat;
        lng = _lng;
    }
}
