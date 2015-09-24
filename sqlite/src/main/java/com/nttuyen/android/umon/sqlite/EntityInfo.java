package com.nttuyen.android.umon.sqlite;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by nttuyen on 9/23/15.
 */
class EntityInfo {
    public final String table;
    public final Map<String, Column> columns = new HashMap<String, Column>();
    public final AtomicLong count = new AtomicLong(-1);

    EntityInfo(String table) {
        this.table = table;
    }

    void addColumn(String fieldName, Column column) {
        columns.put(fieldName, column);
    }

    public void setCount(long c) {
        count.compareAndSet(-1, c);
    }
    public long getCount() {
        return count.get();
    }
    public void increaseCount() {
        count.addAndGet(1);
    }
    public void decreaseCount() {
        count.addAndGet(-1);
    }

    static class Column {
        public final String name;
        public final String sqlType;
        public final Class javaType;
        public final boolean isId;
        public final Field field;
        public final Method getterMethod;
        public final Method setterMethod;

        Column(String name, String sqlType, Class javaType, boolean isId, Field f, Method getter, Method setter) {
            this.name = name;
            this.sqlType = sqlType;
            this.javaType = javaType;
            this.isId = isId;
            this.field = f;
            this.getterMethod = getter;
            this.setterMethod = setter;
        }

        public <T> T getValue(Object entity) {
            if (entity == null) {
                throw new IllegalArgumentException("Entity must not null");
            }
            try {
                if (getterMethod != null) {
                    return (T) getterMethod.invoke(entity);
                } else {
                    field.setAccessible(true);
                    return (T)field.get(entity);
                }
            } catch (IllegalAccessException ex) {
                Log.e(SQLite.TAG, "Exception while fetch field value", ex);
            } catch (InvocationTargetException ex) {
                Log.e(SQLite.TAG, "Exception while fetch field value", ex);
            }
            return null;
        }
        public <T> void setValue(Object entity, T value) {
            if (entity == null) {
                throw new IllegalArgumentException("Entity must not null");
            }
            try {
                if (setterMethod != null) {
                    setterMethod.invoke(entity, value);
                } else {
                    field.setAccessible(true);
                    field.set(entity, value);
                }
            } catch (IllegalAccessException ex) {
                Log.e(SQLite.TAG, "Exception while fetch field value", ex);
            } catch (InvocationTargetException ex) {
                Log.e(SQLite.TAG, "Exception while fetch field value", ex);
            }
        }
    }
}
