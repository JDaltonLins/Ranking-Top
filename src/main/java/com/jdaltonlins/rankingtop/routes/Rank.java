package com.jdaltonlins.rankingtop.routes;

import com.jdaltonlins.rankingtop.RankingTop;
import com.jdaltonlins.rankingtop.dao.RankingModel;
import com.jdaltonlins.rankingtop.db.Result;
import com.jdaltonlins.simplerest.Router;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

import static com.jdaltonlins.simplerest.HttpMethod.GET;
import static com.jdaltonlins.simplerest.HttpMethod.POST;

public class Rank extends RouterBase {

    public Rank(RankingTop instance) {
        super(instance);
    }

    @Router(methods = {GET, POST})
    public Result<List<RankingModel>> list(@Nullable Integer page, @Nullable Date startedAt, @Nullable Date endedAt) {
        return startedAt != null && endedAt != null ?
                getDb().getRanking(page == null ? 1 : page, startedAt, endedAt) :
                getDb().getRanking(page == null ? 1 : page);
    }

}
