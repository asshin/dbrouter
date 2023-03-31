package com.wang.middleware.test;


import com.wang.middleware.db.router.annotation.DBRouter;
import com.wang.middleware.db.router.annotation.DBRouterStrategy;

/**
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
@DBRouterStrategy(splitTable = true)
public interface IUserDao {

    @DBRouter(key = "userId")
    void insertUser(String req);

}
