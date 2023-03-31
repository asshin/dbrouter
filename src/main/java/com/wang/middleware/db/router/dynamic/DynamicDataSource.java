package com.wang.middleware.db.router.dynamic;


import com.wang.middleware.db.router.DBContextHolder;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 这些代码中用到一个类DynamicDataSource，
 * 这个类是由我自定义的他主要的作用就是用来数据源的切换。
 * 而这个自定义的类继承自AbstractRoutingDataSource类，
 * 这个是spring.jdbc中提供的一个，
 * 他的作用在于在执行DML操作之前可以根据规则来使用哪一个数据源。
 * 在执行DML之前会回调这个类中的determineCurrentLookupKey方法来切换数据源。

 * @author zsw
 * @create 2023-03-30 20:46
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {

        return  "db"+ DBContextHolder.getDBKey();
    }
}
