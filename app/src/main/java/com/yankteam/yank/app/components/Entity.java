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

    private Integer nid;
    private Integer eid;

    public Entity(String name, String desc) {
        this(name, 0.0, 0.0, 0, 0);
    }
    public Entity(String name, double lat, double lng, Integer eid, Integer nid) {
        this.name = name;
        this.lat  = lat;
        this.lng  = lng;
        this.eid = eid;
        this.nid = nid;
    }

    // getters and setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getEid() { return eid; }
    public void setEid(Integer eid) { this.eid = eid; }

    public Integer getNid() { return nid; }
    public void setNid(Integer nid) { this.nid = nid; }

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
