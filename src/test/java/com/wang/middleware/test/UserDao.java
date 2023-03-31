package com.wang.middleware.test;

import com.wang.middleware.db.router.annotation.DBRouter;
import com.wang.middleware.db.router.annotation.DBRouterStrategy;
import org.springframework.stereotype.Component;

/**
 * @author zsw
 * @create 2023-03-31 22:27
 */
@Component
@DBRouterStrategy(splitTable = true)
public class UserDao {
    @DBRouter(key = "userId")
    public void insertUser(String req) {
        System.out.println("charu");
    }
}
