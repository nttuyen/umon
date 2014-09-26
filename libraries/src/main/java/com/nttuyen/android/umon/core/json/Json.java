package com.nttuyen.android.umon.core.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author nttuyen266@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Json {
    //Json field name
    public String name();

    /**
     * is Collection: only support list
     * @return
     */
    public boolean isCollection() default false;

    /**
     * Type of field in collection
     * @return
     */
    public Class<?> type() default Object.class;
}
