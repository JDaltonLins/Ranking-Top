package com.jdaltonlins.rankingtop.routes;

import com.jdaltonlins.rankingtop.RankingTop;
import com.jdaltonlins.rankingtop.dao.UserModel;
import com.jdaltonlins.rankingtop.db.Result;
import com.jdaltonlins.simplerest.Router;
import com.jdaltonlins.simplerest.server.Request;
import com.jdaltonlins.simplerest.server.Response;

import static com.jdaltonlins.simplerest.HttpMethod.GET;
import static com.jdaltonlins.simplerest.HttpMethod.POST;

public class Player extends RouterBase {

    public Player(RankingTop instance) {
        super(instance);
    }

    @Router(methods = {POST})
    public Response create(Request request, String nickname) {
        if (getDb().createUser(nickname) == -1) return request.status(403);
        else return request.status(200);
    }

    @Router(methods = {GET})
    public Result<UserModel> get(String nickname) {
        return getDb().getUser(nickname);
    }

    @Router(methods = {GET})
    public Result<UserModel> get(int user_id) {
        return getDb().getUser(user_id);
    }

}
