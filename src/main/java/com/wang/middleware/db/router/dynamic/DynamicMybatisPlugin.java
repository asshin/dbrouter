package com.wang.middleware.db.router.dynamic;


import com.wang.middleware.db.router.DBContextHolder;
import com.wang.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.javassist.tools.reflect.Metaobject;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zsw
 * @create 2023-03-30 20:49
 */
@Intercepts({@Signature(type= StatementHandler.class,method = "prepare",args = {Connection.class,Integer.class})})
public class DynamicMybatisPlugin implements Interceptor {
    private Pattern pattern =Pattern.compile("(from|into|update)[\\s]{1,}(\\w{1,})",Pattern.CASE_INSENSITIVE);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        //获取StatementHandler
        StatementHandler statementHandler= (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        //获取自定义注解就判断是否进行分表操作
        String id = mappedStatement.getId();
        String className = id.substring(0, id.lastIndexOf("."));
        Class<?> clazz = Class.forName(className);
        DBRouterStrategy dbRouterStrategy = clazz.getAnnotation(DBRouterStrategy.class);
        if (dbRouterStrategy==null||!dbRouterStrategy.splitTable()){
            return  invocation.proceed();
        }
        //获取sql
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        //替换sql表名
        Matcher matcher = pattern.matcher(sql);
        String tableName=null;
        if (matcher.find()){
            tableName=matcher.group().trim();
        }
        assert  tableName!=null;

        String replaceSql = matcher.replaceAll(tableName + "_" + DBContextHolder.getDBKey());

        //通过反射修改sql
        Field field=boundSql.getClass().getDeclaredField("sql");
        field.setAccessible(true);
        field.set(boundSql,replaceSql);
        field.setAccessible(false);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return null;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
