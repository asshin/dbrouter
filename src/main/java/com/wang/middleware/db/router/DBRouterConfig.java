package com.wang.middleware.db.router;

/**
 * @author zsw
 * @create 2023-03-30 17:17
 */
public class DBRouterConfig {
    /*分库数量*/
    private  int dbCount;
    /*分表数量*/
    private  int dtCount;
    /*路由字段*/
    private  String  routeKey;

    public int getDbCount() {
        return dbCount;
    }

    public void setDbCount(int dbCount) {
        this.dbCount = dbCount;
    }

    public int getDtCount() {
        return dtCount;
    }

    public void setDtCount(int dtCount) {
        this.dtCount = dtCount;
    }

    public String getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }

    public DBRouterConfig(int dbCount, int dtCount, String routeKey) {
        this.dbCount = dbCount;
        this.dtCount = dtCount;
        this.routeKey = routeKey;
    }

    public DBRouterConfig() {
    }
}
