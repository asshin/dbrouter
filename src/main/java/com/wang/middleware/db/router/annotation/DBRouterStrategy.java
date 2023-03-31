package com.wang.middleware.db.router.annotation;

import java.lang.annotation.*;

/**
 * @author zsw
 * @create 2023-03-30 16:17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface DBRouterStrategy {
    boolean splitTable() default  false;
}
