package com.wang.middleware.db.router.strategy.impl;

import com.wang.middleware.db.router.DBContextHolder;
import com.wang.middleware.db.router.DBRouterConfig;
import com.wang.middleware.db.router.DBRouterJoinPoint;
import com.wang.middleware.db.router.annotation.DBRouter;
import com.wang.middleware.db.router.strategy.IDBRouterStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zsw
 * @create 2023-03-30 16:28
 */
public class DBRouterStrategyHashCode implements IDBRouterStrategy {
    Logger logger=LoggerFactory.getLogger(DBRouterJoinPoint.class);
    private DBRouterConfig dbRouterConfig;

    public DBRouterStrategyHashCode(DBRouterConfig dbRouterConfig) {
        this.dbRouterConfig = dbRouterConfig;
    }

    @Override
    public void dbRouter(String dbKeyAttr) {
        int size= dbRouterConfig.getDbCount()*dbRouterConfig.getDtCount();
        //扰动函数
        int idx= ((dbKeyAttr.hashCode()>>>16)^dbKeyAttr.hashCode())&(size-1);
        //库表索引
        int dbIdx=idx/dbRouterConfig.getDtCount()+1;
        int dtIdx=idx-(dbIdx-1)*dbRouterConfig.getDtCount();
        //设置到ThreadLocal
        DBContextHolder.setDBKey(String.format("%02d",dbIdx));
        DBContextHolder.setTBKey(String.format("%03d",dtIdx));
        logger.debug("数据库路由dbkey:{},dtkey：{}",dbIdx,dtIdx);


    }

    @Override
    public void clear() {
        DBContextHolder.clearDBKey();
        DBContextHolder.clearTBKey();
    }
}
