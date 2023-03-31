package com.wang.middleware.db.router.strategy;

/**
 * @author zsw
 * @create 2023-03-30 16:27
 */
public interface IDBRouterStrategy {
    void dbRouter(String dbKeyAttr);
    void clear();
}
