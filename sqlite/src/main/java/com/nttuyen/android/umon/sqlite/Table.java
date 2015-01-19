package com.nttuyen.android.umon.sqlite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by nttuyen on 1/17/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
    public String value() default "table";

    /**
     * This field will format like JSON
     * {"oldVersion": 1, "newVersion": 2, "sql": ["ALERT .... or any thing", "other sql statement"]}
     * @return
     */
    public String[] updates() default "";
}
