package com.jdaltonlins.rankingtop.routes;

import com.jdaltonlins.rankingtop.RankingTop;
import com.jdaltonlins.rankingtop.dao.PointLog;
import com.jdaltonlins.rankingtop.db.Result;
import com.jdaltonlins.simplerest.Router;
import com.jdaltonlins.simplerest.server.Request;
import com.jdaltonlins.simplerest.server.Response;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.jdaltonlins.simplerest.HttpMethod.GET;
import static com.jdaltonlins.simplerest.HttpMethod.POST;

public class Points extends RouterBase {
    public Points(RankingTop instance) {
        super(instance);
    }

    public Response modify(Request request, int user_id, double points, @NotNull String reason, boolean pos) {
        if (points <= 0) request.status(405).json("message", "`points` must be more than 0");
        if (reason.length() < 10) request.status(405).json("message", "`points` must be more or equal than 10");
        if (getDb().addPointLog(user_id, pos ? points : -points, reason) == -1) return request.status(404);
        return request.status(200);
    }

    @Router(methods = {POST})
    public Response add(Request request, int user_id, double points, @NotNull String reason) {
        return modify(request, user_id, points, reason, true);
    }

    @Router(methods = {POST})
    public Response rem(Request request, int user_id, double points, @NotNull String reason) {
        return modify(request, user_id, points, reason, false);
    }

    @Router(methods = {GET})
    public Result<List<PointLog>> logs(int user_id, int page) {
        return getDb().getLogs(user_id, page);
    }
}
