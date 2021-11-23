package com.jdaltonlins.rankingtop.dao;

public class PointLog {

    private int userId;
    private String username;
    private double points;
    private String reason;

    public PointLog(int userId, String username, double points, String reason) {
        this.userId = userId;
        this.username = username;
        this.points = points;
        this.reason = reason;
    }

    public int getUserId() {
        return userId;
    }

    public double getPoints() {
        return points;
    }

    public String getReason() {
        return reason;
    }
}
