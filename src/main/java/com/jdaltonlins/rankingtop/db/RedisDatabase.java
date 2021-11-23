package com.jdaltonlins.rankingtop.db;

import com.jdaltonlins.rankingtop.dao.PointLog;
import com.jdaltonlins.rankingtop.dao.RankingModel;
import com.jdaltonlins.rankingtop.dao.UserModel;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RedisDatabase implements Database {

    private Database original;
    private SetParams timeCache;
    private JedisPool pool;

    @Override
    public int createUser(String nickname) {
        return original.createUser(nickname);
    }

    @Override
    public Result<UserModel> getUser(int userId) {
        try (Jedis jedis = pool.getResource()) {
            String rs = jedis.get("user:" + userId);
            if (rs == null) {
                Result<UserModel> modelResult = original.getUser(userId);
                if (modelResult != null) {
                    UserModel userModel = modelResult.getValue();
                    jedis.set("user:" + userId, modelResult.toString(), timeCache);
                    jedis.set("user2id:" + userModel.getName(), String.valueOf(userId), timeCache);
                    jedis.set("user2name:" + userModel.getId(), userModel.getName(), timeCache);
                }
                return modelResult;
            }
            return Result.ofRaw(rs);
        }
    }

    @Override
    public Result<UserModel> getUser(String nickname) {
        try (Jedis jedis = pool.getResource()) {
            String rs = jedis.get("user2id:" + nickname);
            if (rs != null)
                rs = jedis.get("user:" + rs);
            if (rs == null) {
                Result<UserModel> modelResult = original.getUser(nickname);
                if (modelResult != null) {
                    UserModel userModel = modelResult.getValue();
                    jedis.set("user:" + userModel.getId(), modelResult.toString(), timeCache);
                    jedis.set("user2id:" + userModel.getName(), String.valueOf(userModel.getId()), timeCache);
                    jedis.set("user2name:" + userModel.getId(), userModel.getName(), timeCache);
                }
                return modelResult;
            }
            return Result.ofRaw(rs);
        }
    }

    @Override
    public boolean deleteUser(int userId) {
        if (original.deleteUser(userId)) {
            try (Jedis jedis = pool.getResource()) {
                String name = jedis.get("user2name:" + userId);
                if (name != null)
                    jedis.del("user2name:" + userId, "user2id:" + name, "user:" + userId);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteUser(String nickname) {
        if (original.deleteUser(nickname)) {
            try (Jedis jedis = pool.getResource()) {
                String userId = jedis.get("user2id:" + nickname);
                if (userId != null)
                    jedis.del("user2name:" + userId, "user2id:" + nickname, "user:" + userId);
                return true;
            }
        }
        return false;
    }

    @Override
    public Result<List<RankingModel>> getRanking(int page) {
        try (Jedis jedis = pool.getResource()) {
            String ranking = jedis.get("ranking:" + page);
            if (ranking == null) {
                Result<List<RankingModel>> rs = original.getRanking(page);
                List<RankingModel> value = rs.getValue();
                if (value.isEmpty()) return rs;
                jedis.set("ranking:" + page, rs.toString(), timeCache);
            }
            return Result.ofRaw(ranking);
        }
    }

    @Override
    public Result<List<RankingModel>> getRanking(int page, Date startedAt, Date endedAt) {
        String key = "ranking:" + page;
        int hash = startedAt != null || endedAt != null ? Objects.hash(startedAt, endedAt) : 0;
        if (hash != 0) key += ":" + hash;

        try (Jedis jedis = pool.getResource()) {
            String ranking = jedis.get(key);
            if (ranking == null) {
                Result<List<RankingModel>> rs = original.getRanking(page, startedAt, endedAt);
                List<RankingModel> value = rs.getValue();
                if (value.isEmpty()) return rs;
                jedis.set(key, rs.toString(), timeCache);
            }
            return Result.ofRaw(ranking);
        }
    }

    @Override
    public Result<List<PointLog>> getLogs(int userId, int page) {
        try (Jedis jedis = pool.getResource()) {
            String ranking = jedis.get("logs:" + userId + ":" + page);
            if (ranking == null) {
                Result<List<PointLog>> rs = original.getLogs(userId, page);
                List<PointLog> value = rs.getValue();
                if (value.isEmpty()) return rs;
                jedis.set("logs:" + userId + ":" + page, rs.toString(), timeCache);
            }
            return Result.ofRaw(ranking);
        }
    }

    @Override
    public int addPointLog(int userId, double points, String reason) {
        return original.addPointLog(userId, points, reason);
    }

    @Override
    public void resetPoints(int userId) {
        original.resetPoints(userId);
    }
}
