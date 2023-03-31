package com.wang.middleware.db.router.config;

import com.wang.middleware.db.router.DBRouterConfig;

import com.wang.middleware.db.router.DBRouterJoinPoint;
import com.wang.middleware.db.router.dynamic.DynamicDataSource;
import com.wang.middleware.db.router.dynamic.DynamicMybatisPlugin;
import com.wang.middleware.db.router.strategy.IDBRouterStrategy;
import com.wang.middleware.db.router.strategy.impl.DBRouterStrategyHashCode;
import com.wang.middleware.db.router.util.PropertyUtil;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zsw
 * @create 2023-03-31 9:51
 */
@Configuration
public class DataSourceAutoConfig implements EnvironmentAware {

    /*   数据源配置组*/

    private  Map<String, Map<String ,Object>> dataSourceMap =new HashMap<>();
    /*默认数据源配置*/
    private  Map<String,Object> defalutDataSourceConfig;
    /*分库数量*/
    private  int dbCount;
    /*分表数量*/
    private  int dtCount;
    /*数据路由字段*/
    private  String routerKey;



    @Bean(name = "db-router-point")
    @ConditionalOnMissingBean
    public DBRouterJoinPoint point(DBRouterConfig dbRouterConfig, IDBRouterStrategy dbRouterStrategy) {
        return new DBRouterJoinPoint(dbRouterConfig, dbRouterStrategy);
    }


    @Bean public  DBRouterConfig dbRouterConfig(){

        return new DBRouterConfig(dbCount,dtCount,routerKey);
    }
    @Bean
    public Interceptor plugin() {
        return new DynamicMybatisPlugin();
    }
    @Bean
    public DataSource dataSource(){
        //创建数据源
        HashMap<Object, Object> targetDataSource = new HashMap<>();
        for (String dbInfo : dataSourceMap.keySet()) {
            Map<String, Object> ojMap = dataSourceMap.get(dbInfo);
            targetDataSource.put(dbInfo,new DriverManagerDataSource(ojMap.get("url").toString(),ojMap.get("username").toString(),ojMap.get("password").toString()));

        }
        //设置数据源
        DynamicDataSource dynamicDataSource=new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSource);
        dynamicDataSource.setDefaultTargetDataSource(new DriverManagerDataSource(defalutDataSourceConfig.get("url").toString(),defalutDataSourceConfig.get("username").toString(),defalutDataSourceConfig.get("password").toString()));
        return  dynamicDataSource;


    }
    @Bean
    public IDBRouterStrategy dbRouterStrategy(DBRouterConfig dbRouterConfig){
        return  new DBRouterStrategyHashCode(dbRouterConfig);
    }
    @Bean
    public TransactionTemplate transactionTemplate(DataSource dataSource){
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);

        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(dataSourceTransactionManager);
        transactionTemplate.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return  transactionTemplate;
    }

    @Override
    public void setEnvironment(Environment environment) {
        String prefix = "mini-db-router.jdbc.datasource.";
        dbCount=Integer.valueOf(environment.getProperty(prefix+"dbCount"));
        dtCount=Integer.valueOf(environment.getProperty(prefix+"dtCount"));
        routerKey=environment.getProperty(prefix+"routerKey");
        //分库分表数据源
        String list = environment.getProperty(prefix + "list");
        assert  list !=null;
        String[] dbinfos = list.split(",");
 
        for (String dbInfo : dbinfos) {
            Map<String,Object> dataSourceProps = PropertyUtil.handle(environment, prefix + dbInfo, Map.class);
            dataSourceMap.put(dbInfo,dataSourceProps);
        }
        //默认数据源
        String defaultData=environment.getProperty(prefix+"default");
        defalutDataSourceConfig=PropertyUtil.handle(environment,prefix+defaultData,Map.class);

    }
}
