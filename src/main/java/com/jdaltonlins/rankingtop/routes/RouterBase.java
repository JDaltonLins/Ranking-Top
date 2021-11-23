package com.jdaltonlins.rankingtop.routes;

import com.jdaltonlins.rankingtop.RankingTop;
import com.jdaltonlins.rankingtop.db.Database;

public class RouterBase {

    private RankingTop instance;

    public RouterBase(RankingTop instance) {
        this.instance = instance;
    }

    public Database getDb() {
        return instance.getDatabase();
    }

}

