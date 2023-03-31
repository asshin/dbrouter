package com.wang.middleware.db.router.dynamic;


import com.wang.middleware.db.router.DBContextHolder;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author zsw
 * @create 2023-03-30 20:46
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {

        return  "db"+ DBContextHolder.getDBKey();
    }
}
