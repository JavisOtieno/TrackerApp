package com.scg.tracker.models;

public class Locationmodel {

    public int id;
    public String lat;
    public String lon;
    public String status;
    public String accuracy;
    public String distance;
    public Integer tripId;
    public String type;
    public String name;



    public Locationmodel(int id,
                         Integer tripId,
                         String lat,
                         String lon,
                         String accuracy,
                         String distance,
                         String status,
                         String type,
                         String name
    ) {

        this.id = id;
        this.tripId = tripId;
        this.lat = lat;
        this.lon = lon;
        this.status = status;
        this.accuracy = accuracy;
        this.distance = distance;
        this.type = type;
        this.name = name;

    }

}
