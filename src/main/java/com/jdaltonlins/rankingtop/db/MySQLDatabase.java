package com.jdaltonlins.rankingtop.db;

import com.jdaltonlins.rankingtop.RankingTop;
import com.jdaltonlins.rankingtop.dao.PointLog;
import com.jdaltonlins.rankingtop.dao.RankingModel;
import com.jdaltonlins.rankingtop.dao.UserModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySQLDatabase implements Database {

    private RankingTop rankingTop;
    private Connection con;

    public int createUser(String nickname) {
        try (PreparedStatement stmt = con.prepareStatement("INSERT INTO `players` (`nickname`) VALUES (?);")) {
            stmt.setString(1, nickname);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Result<UserModel> getUser(int userId) {
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT `players`.`id`, `players`.`nickname`, SUM(`logs`.`points`) points, AVG(`logs`.`points`) averagePoints  FROM `players`, `logs` WHERE `players`.`id` = `logs`.`userId` AND `players`.`id`=" + userId + ";")) {
            return rs.next() ? Result.of(new UserModel(rs.getInt(1), rs.getString(1), rs.getDouble(2), rs.getDouble(3), rs.getDate(4))) : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Result<UserModel> getUser(String nickname) {
        try (PreparedStatement stmt = con.prepareStatement("SELECT `players`.`id`, `players`.`nickname`, SUM(`logs`.`points`) points, AVG(`logs`.`points`) averagePoints  FROM `players`, `logs` WHERE `players`.`id` = `logs`.`userId` AND `players`.`nickname`=?;"); ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? Result.of(new UserModel(rs.getInt(1), rs.getString(1), rs.getDouble(2), rs.getDouble(3), rs.getDate(4))) : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteUser(int userId) {
        try (Statement stmt = con.createStatement()) {
            return stmt.executeUpdate("DELETE `players` WHERE `players`.`id`=" + userId + ";") > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String nickname) {
        try (PreparedStatement stmt = con.prepareStatement("DELETE `players` WHERE `players`.`nickname`=?;")) {
            stmt.setString(1, nickname);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Result<List<RankingModel>> getRanking(int page) {
        try (PreparedStatement stmt = con.prepareStatement("SELECT calc.*, (@curRank := @curRank + 1) as `rank` FROM (SELECT `players`.`userId`, `players`.`nickname`, SUM(`logs`.`points`) points, AVG(`logs`.`points`) avg FROM `logs`, `players`) as `calc` ORDER BY `calc`.`points` LIMIT ?, ?;")) {
            int size = this.rankingTop.getConfig().getPageSize(), end = page * size, start = end - page;
            stmt.setInt(1, start);
            stmt.setInt(2, end);
            try (ResultSet rs = stmt.executeQuery()) {
                List<RankingModel> ranks = new ArrayList<>();
                while (rs.next()) {
                    ranks.add(new RankingModel(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5)));
                }
                return Result.of(ranks);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Result<List<RankingModel>> getRanking(int page, Date startedAt, Date endedAt) {
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM (SELECT calc.*, (@curRank := @curRank + 1) as `rank` FROM (SELECT `players`.`userId`, `players`.`nickname`, SUM(`logs`.`points`) points, AVG(`logs`.`points`) avg, `logs`.`createdAt` FROM `logs`, `players` WHERE ) as `calc` ORDER BY `calc`.`points`) LIMIT ?, ?;")) {
            int size = this.rankingTop.getConfig().getPageSize(), end = page * size, start = end - page, i = 1;
            if ((startedAt == null && endedAt != null) || (endedAt == null && startedAt != null)) {
                stmt.setDate(i++, new java.sql.Date((startedAt != null ? startedAt : endedAt).getTime()));
            } else {
                stmt.setDate(i++, new java.sql.Date(startedAt.getTime()));
                stmt.setDate(i++, new java.sql.Date(endedAt.getTime()));
            }
            stmt.setInt(i++, start);
            stmt.setInt(i, end);

            try (ResultSet rs = stmt.executeQuery()) {
                List<RankingModel> ranks = new ArrayList<>();
                while (rs.next()) {
                    ranks.add(new RankingModel(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(6)));
                }
                return Result.of(ranks);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Result<List<PointLog>> getLogs(int userId, int page) {
        try (PreparedStatement stmt = con.prepareStatement("SELECT `nickname`, `points`, `reason` FROM `logs`, `players` WHERE `logs`.`userId`=? AND `players`.`id`=`logs`.`userId`;")) {
            int size = this.rankingTop.getConfig().getPageSize(), end = page * size, start = page - size;
            stmt.setInt(1, userId);
            stmt.setInt(2, end);
            stmt.setInt(2, start);
            try (ResultSet rs = stmt.executeQuery())  {
                List<PointLog> list = new ArrayList<>();
                while(rs.next()) {
                    list.add(new PointLog(userId, rs.getString("nickname"), rs.getDouble("points"), rs.getString("reason")));
                }
                return Result.of(list);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addPointLog(int userId, double points, String reason) {
        try (PreparedStatement stmt = con.prepareStatement("INSERT INTO `logs` (userId, points, reason) VALUES (?, ?, ?)")) {
            stmt.setInt(1, userId);
            stmt.setDouble(2, points);
            stmt.setString(3, reason);
            if (stmt.executeUpdate() > 0)
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    return rs.next() ? rs.getInt(1) : -1;
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void resetPoints(int userId) {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate("DELETE `logs` WHERE `userId`=" + userId + ";");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
