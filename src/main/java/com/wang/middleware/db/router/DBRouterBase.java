package com.wang.middleware.db.router;

/**
 * @author zsw
 * @create 2023-03-30 20:42
 */
public class DBRouterBase {
    private  String tbIdx;
    public  String getTbIdx(){
        return  DBContextHolder.getTBKey();
    }
}
