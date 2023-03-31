package com.wang.middleware.db.router;

import com.wang.middleware.db.router.annotation.DBRouter;
import com.wang.middleware.db.router.strategy.IDBRouterStrategy;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * @author zsw
 * @create 2023-03-30 17:14
 */
@Aspect
public class DBRouterJoinPoint {
    Logger logger =LoggerFactory.getLogger(DBRouterJoinPoint.class);
    private  DBRouterConfig dbRouterConfig;
    private IDBRouterStrategy dbRouterStrategy;

    public DBRouterJoinPoint(DBRouterConfig dbRouterConfig, IDBRouterStrategy dbRouterStrategy) {
        this.dbRouterConfig = dbRouterConfig;
        this.dbRouterStrategy = dbRouterStrategy;
    }
    @Pointcut("@annotation(com.wang.middleware.db.router.annotation.DBRouter)")
    public  void aopPoint(){

    }

    @Pointcut("aopPoint()&&@annotation(dbRouter)")
    public Object doRouter(ProceedingJoinPoint jp,DBRouter dbRouter) throws  Throwable{
        String dbkey = dbRouter.key();
        if (StringUtils.isBlank(dbkey)&&StringUtils.isBlank(dbRouterConfig.getRouteKey() )){
            throw new RuntimeException("annotation DBRouter key is null !");
        }
        dbkey=StringUtils.isBlank(dbkey) ? dbRouterConfig.getRouteKey():dbkey;
        //路由属性
        Object[] args = jp.getArgs();
        String attr = getAttr(args, dbkey);
        //路由策略
        dbRouterStrategy.dbRouter(attr);
        try {
            return jp.proceed();
        } finally {
            dbRouterStrategy.clear();
        }
    }

    private String getAttr(Object[] args,String dbkey) {
        String res=null;
        for (int i = 0; i < args.length; i++) {
            if (args.length==1){
                if( args[0] instanceof  String){
                 return  (args[0].toString());
                }
            }
            if (StringUtils.isNotBlank(res)){
                break;
            }
            try {
                res = BeanUtils.getProperty(args[i], dbkey);
            } catch (Exception e) {
               logger.info("获取路由属性值失败 attr：{}",dbkey,e);
            }
        }
        return  res;
    }
}
