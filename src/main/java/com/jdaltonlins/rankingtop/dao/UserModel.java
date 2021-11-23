package com.jdaltonlins.rankingtop.dao;

import java.util.Date;

public class UserModel {

    private int id;
    private String name;
    private double points, avgPoints;
    private Date createdAt;

    public UserModel(int id, String name,double points, double avgPoints, Date createdAt) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.avgPoints = avgPoints;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPoints() {
        return points;
    }

    public double getAvgPoints() {
        return avgPoints;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
