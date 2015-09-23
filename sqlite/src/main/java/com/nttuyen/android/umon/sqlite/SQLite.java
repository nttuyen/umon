package com.nttuyen.android.umon.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.nttuyen.android.umon.utils.reflect.ReflectUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nttuyen on 1/17/15.
 */
public class SQLite extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";

    private static final Map<Class, EntityInfo> mappings = new ConcurrentHashMap<Class, EntityInfo>();

    private static final Map<Class, String> typeMaps = new HashMap<Class, String>();
    private static final Map<Class, CursorGetter> cursorGetterMap = new HashMap<Class, CursorGetter>();
    private static final Map<Class, ContentValuesSetter> contentValuesSetterMap = new HashMap<Class, ContentValuesSetter>();

    static {
        typeMaps.put(Integer.TYPE, "INTEGER");
        typeMaps.put(Integer.class, "INTEGER");
        typeMaps.put(Long.TYPE, "INTEGER");
        typeMaps.put(Long.class, "INTEGER");
        typeMaps.put(Boolean.TYPE, "INTEGER");
        typeMaps.put(Boolean.class, "INTEGER");
        typeMaps.put(Byte.TYPE, "INTEGER");
        typeMaps.put(Byte.class, "INTEGER");
        typeMaps.put(Date.class, "INTEGER");

        typeMaps.put(Float.TYPE, "REAL");
        typeMaps.put(Float.class, "REAL");
        typeMaps.put(Double.TYPE, "REAL");
        typeMaps.put(Double.class, "REAL");

        typeMaps.put(String.class, "TEXT");
        typeMaps.put(JSONObject.class, "TEXT");
        typeMaps.put(JSONArray.class, "TEXT");

        cursorGetterMap.put(Integer.TYPE, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                return cursor.getInt(index);
            }
        });
        cursorGetterMap.put(Integer.class, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                return cursor.getInt(index);
            }
        });
        cursorGetterMap.put(Long.TYPE, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                return cursor.getInt(index);
            }
        });
        cursorGetterMap.put(Long.class, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                return cursor.getInt(index);
            }
        });
        cursorGetterMap.put(Boolean.TYPE, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                int val = cursor.getInt(index);
                if(val == 1) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        cursorGetterMap.put(Boolean.class, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                int val = cursor.getInt(index);
                if (val == 1) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        cursorGetterMap.put(Byte.TYPE, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                return (byte)cursor.getInt(index);
            }
        });
        cursorGetterMap.put(Byte.class, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                return (byte)cursor.getInt(index);
            }
        });
        cursorGetterMap.put(Date.class, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                return new Date(cursor.getInt(index));
            }
        });

        cursorGetterMap.put(Float.TYPE, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                return cursor.getFloat(index);
            }
        });
        cursorGetterMap.put(Float.class, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                return cursor.getFloat(index);
            }
        });
        cursorGetterMap.put(Double.TYPE, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                return cursor.getDouble(index);
            }
        });
        cursorGetterMap.put(Double.class, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                return cursor.getDouble(index);
            }
        });

        cursorGetterMap.put(String.class, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                return cursor.getString(index);
            }
        });
        cursorGetterMap.put(JSONObject.class, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                try {
                    return new JSONObject(cursor.getString(index));
                } catch (Exception ex) {
                    return null;
                }
            }
        });
        cursorGetterMap.put(JSONArray.class, new CursorGetter() {
            @Override
            public Object getValue(Cursor cursor, int index) {
                try {
                    return new JSONArray(cursor.getString(index));
                } catch (Exception ex) {
                    return null;
                }
            }
        });

        //. Content value setter map
        contentValuesSetterMap.put(Integer.TYPE, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                values.put(name, field.getInt(target));
            }
        });
        contentValuesSetterMap.put(Integer.class, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                values.put(name, field.getInt(target));
            }
        });
        contentValuesSetterMap.put(Long.TYPE, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                values.put(name, field.getLong(target));
            }
        });
        contentValuesSetterMap.put(Long.class, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                values.put(name, field.getLong(target));
            }
        });
        contentValuesSetterMap.put(Boolean.TYPE, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                values.put(name, field.getBoolean(target) ? 1 : 0);
            }
        });
        contentValuesSetterMap.put(Boolean.class, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                values.put(name, field.getBoolean(target) ? 1 : 0);
            }
        });
        contentValuesSetterMap.put(Byte.TYPE, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                values.put(name, field.getByte(target));
            }
        });
        contentValuesSetterMap.put(Byte.class, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                values.put(name, field.getByte(target));
            }
        });
        contentValuesSetterMap.put(Date.class, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                Date date = (Date)field.get(target);
                values.put(name, date.getTime());
            }
        });

        contentValuesSetterMap.put(Float.TYPE, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                values.put(name, field.getFloat(target));
            }
        });
        contentValuesSetterMap.put(Float.class, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                values.put(name, field.getFloat(target));
            }
        });
        contentValuesSetterMap.put(Double.TYPE, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                values.put(name, field.getDouble(target));
            }
        });
        contentValuesSetterMap.put(Double.class, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                values.put(name, field.getDouble(target));
            }
        });

        contentValuesSetterMap.put(String.class, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                values.put(name, (String)field.get(target));
            }
        });
        contentValuesSetterMap.put(JSONObject.class, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                JSONObject val = (JSONObject)field.get(target);
                values.put(name, val.toString());
            }
        });
        contentValuesSetterMap.put(JSONArray.class, new ContentValuesSetter() {
            @Override
            public void set(ContentValues values, String name, Field field, Object target) throws Exception {
                JSONArray val = (JSONArray)field.get(target);
                values.put(name, val.toString());
            }
        });
    }

    public SQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(Class clazz : mappings.keySet()) {
            EntityInfo mapping = mappings.get(clazz);
            StringBuilder sql = new StringBuilder("CREATE TABLE ").append(mapping.table).append(" (");
            for(EntityInfo.Column column : mapping.columns.values()) {
                String name = column.name;
                String type = column.sqlType;
                if(name != null && !"".equals(name) && type != null && !"".equals(type)) {
                    sql.append(name).append(" ").append(type).append(" ");
                    if(column.isId) {
                        sql.append(" PRIMARY KEY ");
                        if("INTEGER".equalsIgnoreCase(type)) {
                            sql.append("autoincrement ");
                        }
                    }
                    sql.append(",");
                }
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(" )");

            db.execSQL(sql.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(Class clazz : mappings.keySet()) {
            Table table = (Table) clazz.getAnnotation(Table.class);
            if (table == null) {
                continue;
            }
            String[] updates = table.updates();
            if(updates != null && updates.length > 0) {
                for(String update : updates) {
                    try {
                        JSONObject json = new JSONObject(update);
                        int oldV = json.getInt("oldVersion");
                        int newV = json.getInt("newVersion");
                        if(oldV == oldVersion && newV == newVersion) {
                            JSONArray sql = json.getJSONArray("sql");
                            if(sql != null && sql.length() > 0) {
                                for(int i = 0; i < sql.length(); i++) {
                                    String s = sql.getString(i);
                                    db.execSQL(s);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "Exception while update database", ex);
                    }
                }
            }
        }
    }

    public long insert(Object entity) {
        if(entity == null) {
            return -1;
        }
        Class clazz = entity.getClass();
        if(!mappings.containsKey(clazz)) {
            return -1;
        }
        EntityInfo mapping = mappings.get(clazz);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        EntityInfo.Column cid = null;
        for(EntityInfo.Column column : mapping.columns.values()) {
            if (column.isId) {
                cid = column;
            }
            if (column.isId && "INTEGER".equalsIgnoreCase(column.sqlType)) {
                //. Ignore
            } else {
                try {
                    ContentValuesSetter setter = contentValuesSetterMap.get(column.javaType);
                    column.field.setAccessible(true);
                    setter.set(values, column.name, column.field, entity);
                } catch (Exception ex) {
                    Log.e(TAG, "Exception", ex);
                }
            }
        }

        long id = db.insert(mapping.table, null, values);
        db.close();
        if(cid != null && ("INTEGER".equalsIgnoreCase(cid.sqlType))) {
            try {
                if (cid.setterMethod != null) {
                    cid.setterMethod.invoke(entity, id);
                } else {
                    cid.field.setAccessible(true);
                    cid.field.setLong(entity, id);
                }
            } catch (Exception ex) {
                Log.e(TAG, "Exception while update ID to entity after insert", ex);
            }
        }
        if (id > 0 && mapping.getCount() > 0) {
            mapping.increaseCount();
        }

        return id;
    }

    public int update(Object entity) {
        if(entity == null) {
            return 0;
        }
        Class clazz = entity.getClass();
        if(!mappings.containsKey(clazz)) {
            return 0;
        }
        EntityInfo mapping = mappings.get(clazz);

        ContentValues values = new ContentValues();
        EntityInfo.Column cid = null;
        String idVal = "";
        for(EntityInfo.Column column : mapping.columns.values()) {
            if (column.isId) {
                cid = column;
                try {
                    idVal = String.valueOf(column.getterMethod.invoke(entity));
                } catch (Exception ex) {
                    Log.e(TAG, "Invoke getter method exception", ex);
                }
            }
            try {
                ContentValuesSetter setter = contentValuesSetterMap.get(column.javaType);
                setter.set(values, column.name, column.field, entity);
            } catch (Exception ex) {}
        }

        if (cid != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            int count = db.update(mapping.table, values, cid.name + " = ?", new String[]{idVal});
            db.close();
            return count;
        }
        return 0;
    }

    public SQLite save(Object entity) {
        if(entity == null) {
            return this;
        }
        Class clazz = entity.getClass();
        if(!mappings.containsKey(clazz)) {
            return this;
        }
        EntityInfo mapping = mappings.get(clazz);

        boolean isInsert = true;
        for(EntityInfo.Column column : mapping.columns.values()) {
            if (!column.isId) continue;

            try {
                long val = (Long)column.getterMethod.invoke(entity);
                if(val > 0) {
                    isInsert = false;
                }
            } catch (Exception ex) {
                Log.e(TAG, "Exception while invoke getter", ex);
            }
        }

        if(isInsert) {
            this.insert(entity);
        } else {
            this.update(entity);
        }

        return this;
    }

    public <T> Long count(Class<T> clazz) {
        if (clazz == null || !mappings.containsKey(clazz)) {
            return 0L;
        }
        EntityInfo entityInfo = mappings.get(clazz);
        long count = entityInfo.getCount();
        if (count > 0) {
            return count;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        count = DatabaseUtils.queryNumEntries(db, mappings.get(clazz).table);
        entityInfo.setCount(count);
        db.close();
        return count;
    }

    public <T> List<T> select(Class<T> clazz) {
        if(clazz == null || !mappings.containsKey(clazz)) {
            return Collections.emptyList();
        }
        EntityInfo info = mappings.get(clazz);
        List<T> entities = new LinkedList<T>();

        List<String> columns = new ArrayList<String>(info.columns.size());
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            for(EntityInfo.Column column : info.columns.values()) {
                columns.add(column.name);
            }

            Cursor cursor = db.query(info.table, columns.toArray(new String[columns.size()]),
                    null, null,
                    null, null, null, null);
            if(cursor == null) {
                return null;
            }
            if(cursor.moveToFirst()) {
                do {
                    T instance = clazz.newInstance();
                    this.inject(cursor, instance);
                    entities.add(instance);
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            Log.e(TAG, "Exception while select entity object by ID", ex);
            return null;
        }

        return entities;
    }
    public <T> T selectById(Class<T> clazz, Object id) {
        if(clazz == null || !mappings.containsKey(clazz) || id == null) {
            return null;
        }
        EntityInfo info = mappings.get(clazz);
        try {
            List<String> columns = new ArrayList<String>();
            SQLiteDatabase db = this.getReadableDatabase();
            String idColumn = "id";
            for(EntityInfo.Column column : info.columns.values()) {
                columns.add(column.name);
                if(column.isId) {
                    idColumn = column.name;
                }
            }

            Cursor cursor = db.query(info.table, columns.toArray(new String[columns.size()]),
                    idColumn + " = ?", new String[]{String.valueOf(id)},
                    null, null, null, null);
            if(cursor == null) {
                return null;
            }
            T instance = null;
            if(cursor.moveToFirst()) {
                instance = clazz.newInstance();
                this.inject(cursor, instance);
            }

            return instance;
        } catch (Exception ex) {
            Log.e(TAG, "Exception while select entity object by ID", ex);
            return null;
        }
    }

    private <T> void inject(Cursor cursor, T instance) {
        EntityInfo info = mappings.get(instance.getClass());
        for(EntityInfo.Column column : info.columns.values()) {
            int columnIndex = cursor.getColumnIndex(column.name);
            CursorGetter getter = cursorGetterMap.get(column.javaType);
            try {
                Object val = getter.getValue(cursor, columnIndex);
                if (column.setterMethod != null) {
                    column.setterMethod.invoke(instance, val);
                } else {
                    column.field.setAccessible(true);
                    column.field.set(instance, val);
                }
            } catch (Exception ex) {
                Log.e(TAG, "Exception while inject data to entity", ex);
            }
        }
    }

    public static interface CursorGetter<T> {
        public T getValue(Cursor cursor, int index);
    }
    public static interface ContentValuesSetter {
        //TODO: this method is need to improve
        public void set(ContentValues values, String name, Field field, Object target) throws Exception;
    }

    public static void addEntityClass(Class... clazz) {
        for (Class c : clazz) {
            if (c == null) {
                continue;
            }
            if (c.getAnnotation(Table.class) == null) {
                continue;
            }
            processEntityClass(c);
        }
    }

    private static void processEntityClass(Class clazz) {
        Table table = (Table)clazz.getAnnotation(Table.class);
        String tableName = table.value();
        if (tableName == null || tableName.isEmpty()) {
            tableName = clazz.getSimpleName();
        }
        EntityInfo mapping = new EntityInfo(tableName);

        Field[] fields = ReflectUtil.getAllFields(clazz);
        for(Field f : fields) {
            Transient trans = f.getAnnotation(Transient.class);
            if(trans != null) {
                continue;
            }
            Id id = f.getAnnotation(Id.class);
            Column column = f.getAnnotation(Column.class);
            String columnName = column != null ? column.value() : f.getName();

            boolean isId = false;
            if (id != null || columnName.equalsIgnoreCase("id")) {
                isId = true;
            }
            String sqlType;
            if(column != null && column.type() != null && !"".equals(column.type())) {
                sqlType = column.type();
            } else {
                sqlType = typeMaps.get(f.getType());
            }

            Method getter = ReflectUtil.getterMethod(f, clazz);
            Method setter = ReflectUtil.setterMethod(f, clazz);
            mapping.addColumn(f.getName(), new EntityInfo.Column(columnName, sqlType, f.getType(), isId, f, getter, setter));
        }
        mappings.put(clazz, mapping);
    }

    public static <T> void registerGetter(Class<T> clazz, CursorGetter<T> getter) {
        if (getter == null || clazz == null) {
            return;
        }
        cursorGetterMap.put(clazz, getter);
    }
    public static <T> void registerSetter(Class<T> clazz, ContentValuesSetter setter) {
        if (clazz == null || setter == null) {
            return;
        }
        contentValuesSetterMap.put(clazz, setter);
    }
    public static <T> void registerType(Class<T> clazz, String type) {
        if (clazz == null || type == null) {
            return;
        }
        typeMaps.put(clazz, type);
    }
}
