package com.jdaltonlins.rankingtop.db;

import com.jdaltonlins.rankingtop.dao.PointLog;
import com.jdaltonlins.rankingtop.dao.RankingModel;
import com.jdaltonlins.rankingtop.dao.UserModel;

import java.util.Date;
import java.util.List;

public interface Database {

    int createUser(String nickname);

    Result<UserModel> getUser(int userId);

    Result<UserModel> getUser(String nickname);

    boolean deleteUser(int userId);

    boolean deleteUser(String nickname);

    Result<List<RankingModel>> getRanking(int page);

    Result<List<RankingModel>> getRanking(int page, Date startedAt, Date endedAt);

    Result<List<PointLog>> getLogs(int userId, int page);

    int addPointLog(int userId, double price, String reason);

    void resetPoints(int userId);
}
