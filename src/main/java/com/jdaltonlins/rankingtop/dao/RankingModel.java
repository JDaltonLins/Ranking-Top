package com.jdaltonlins.rankingtop.dao;

public class RankingModel {

    private int userId;
    private String nome;
    private double points, avgPoints;
    private int rank;

    public RankingModel(int userId, String nome, double points, double avgPoints, int rank) {
        this.userId = userId;
        this.nome = nome;
        this.points = points;
        this.avgPoints = avgPoints;
        this.rank = rank;
    }

    public int getUserId() {
        return userId;
    }

    public String getNome() {
        return nome;
    }

    public double getPoints() {
        return points;
    }

    public double getAvgPoints() {
        return avgPoints;
    }

    public int getRank() {
        return rank;
    }
}
