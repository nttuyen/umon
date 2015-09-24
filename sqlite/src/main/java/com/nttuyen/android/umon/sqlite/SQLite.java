package com.nttuyen.android.umon.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.nttuyen.android.umon.sqlite.condition.Query;
import com.nttuyen.android.umon.utils.reflect.ReflectUtil;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nttuyen on 1/17/15.
 */
public class SQLite extends SQLiteOpenHelper {
    static final String TAG = "SQLite";

    private static final Map<Class, EntityInfo> mappings = new ConcurrentHashMap<Class, EntityInfo>();

    private static final Map<Class, String> typeMaps = new HashMap<Class, String>();

    private static final Map<Class, DataProcessor<?>> contentProcessor = new ConcurrentHashMap<Class, DataProcessor<?>>();

    static {
        registerTypeMapping("INTEGER", Byte.TYPE, Byte.class, Integer.TYPE, Integer.class, Long.TYPE, Long.class, Boolean.TYPE, Boolean.class, Date.class);
        registerTypeMapping("REAL", Float.TYPE, Float.class, Double.TYPE, Double.class);
        registerTypeMapping("TEXT", String.class, JSONObject.class, JSONArray.class);

        //
        registerProcessor(new DataProcessor<Byte>() {
            @Override
            public void set(ContentValues values, String fieldName, Byte value) {
                values.put(fieldName, value);
            }

            @Override
            public Byte get(Cursor cursor, int index) {
                return (byte)cursor.getInt(index);
            }
        }, Byte.TYPE, Byte.class);

        //
        registerProcessor(new DataProcessor<Integer>() {
            @Override
            public void set(ContentValues values, String fieldName, Integer value) {
                values.put(fieldName, value);
            }

            @Override
            public Integer get(Cursor cursor, int index) {
                return cursor.getInt(index);
            }
        }, Integer.TYPE, Integer.class);

        //
        registerProcessor(new DataProcessor<Long>() {
            @Override
            public void set(ContentValues values, String fieldName, Long value) {
                values.put(fieldName, value);
            }

            @Override
            public Long get(Cursor cursor, int index) {
                return cursor.getLong(index);
            }
        }, Long.TYPE, Long.class);

        //
        registerProcessor(new DataProcessor<Boolean>() {
            @Override
            public void set(ContentValues values, String fieldName, Boolean value) {
                values.put(fieldName, value ? 1 : 0);
            }

            @Override
            public Boolean get(Cursor cursor, int index) {
                return cursor.getInt(index) >= 1 ? Boolean.TRUE : Boolean.FALSE;
            }
        }, Boolean.TYPE, Boolean.class);

        registerProcessor(new DataProcessor<Float>() {
            @Override
            public void set(ContentValues values, String fieldName, Float value) {
                values.put(fieldName, value);
            }

            @Override
            public Float get(Cursor cursor, int index) {
                return cursor.getFloat(index);
            }
        }, Float.TYPE, Float.class);

        registerProcessor(new DataProcessor<Double>() {
            @Override
            public void set(ContentValues values, String fieldName, Double value) {
                values.put(fieldName, value);
            }

            @Override
            public Double get(Cursor cursor, int index) {
                return cursor.getDouble(index);
            }
        }, Double.TYPE, Double.class);

        registerProcessor(new DataProcessor<String>() {
            @Override
            public void set(ContentValues values, String fieldName, String value) {
                values.put(fieldName, value);
            }

            @Override
            public String get(Cursor cursor, int index) {
                return cursor.getString(index);
            }
        }, String.class);

        registerProcessor(new DataProcessor<Date>() {
            @Override
            public void set(ContentValues values, String fieldName, Date value) {
                values.put(fieldName, value.getTime());
            }

            @Override
            public Date get(Cursor cursor, int index) {
                return new Date(cursor.getLong(index));
            }
        }, Date.class);

        registerProcessor(new DataProcessor<JSONObject>() {
            @Override
            public void set(ContentValues values, String fieldName, JSONObject value) {
                values.put(fieldName, value.toString());
            }

            @Override
            public JSONObject get(Cursor cursor, int index) {
                try {
                    return new JSONObject(cursor.getString(index));
                } catch (JSONException ex) {
                    return new JSONObject();
                }
            }
        }, JSONObject.class);

        registerProcessor(new DataProcessor<JSONArray>() {
            @Override
            public void set(ContentValues values, String fieldName, JSONArray value) {
                values.put(fieldName, value.toString());
            }

            @Override
            public JSONArray get(Cursor cursor, int index) {
                try {
                    return new JSONArray(cursor.getString(index));
                } catch (JSONException ex) {
                    return new JSONArray();
                }
            }
        }, JSONObject.class);
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
                DataProcessor processor = contentProcessor.get(column.javaType);
                processor.set(values, column.name, column.getValue(entity));
            }
        }

        long id = db.insert(mapping.table, null, values);
        db.close();
        if(cid != null && ("INTEGER".equalsIgnoreCase(cid.sqlType))) {
            cid.setValue(entity, id);
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
            } else {
                DataProcessor processor = contentProcessor.get(column.javaType);
                processor.set(values, column.name, column.getValue(entity));
            }
        }

        if (cid != null) {
            SQLiteDatabase db = this.getWritableDatabase();
            int count = db.update(mapping.table, values, cid.name + " = ?", new String[]{idVal});
            db.close();
            return count;
        }
        return 0;
    }

    //TODO: is this method needed?
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

    //TODO: we will need a paging and query method
    private static final Pattern PATTERN = Pattern.compile("\\{([a-zA-Z_0-9]+)\\}");
    public <T> List<T> select(Class<T> clazz, Query query) {
        if(clazz == null || !mappings.containsKey(clazz)) {
            return Collections.emptyList();
        }
        EntityInfo info = mappings.get(clazz);
        List<T> entities = new LinkedList<T>();

        try {
            StringBuffer sb = new StringBuffer();

            Matcher matcher = PATTERN.matcher(query.selection);
            while(matcher.find()) {
                String field = matcher.group(1);
                EntityInfo.Column c = info.columns.get(field);
                if (c == null) {
                    throw new IllegalArgumentException("Field with name '" + field + "' does not exist");
                }
                matcher.appendReplacement(sb, c.name);
            }
            matcher.appendTail(sb);

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(info.table, null,
                    sb.toString(), query.getArgs(),
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

    public <T> void delete(Class<T> clazz, Object id) {
        if (!mappings.containsKey(clazz)) return;
        EntityInfo mapping = mappings.get(clazz);

        EntityInfo.Column cid = null;
        for (EntityInfo.Column c : mapping.columns.values()) {
            if (c.isId) {
                cid = c;
                break;
            }
        }

        if (cid == null) {
            return;
        }

        SQLiteDatabase db = getWritableDatabase();
        db.delete(mapping.table, cid.name + " = ? ", new String[] {String.valueOf(id)});
        db.close();
        if (mapping.getCount() > 0) {
            mapping.decreaseCount();
        }
    }

    private <T> void inject(Cursor cursor, T instance) {
        EntityInfo info = mappings.get(instance.getClass());
        for(EntityInfo.Column column : info.columns.values()) {
            int columnIndex = cursor.getColumnIndex(column.name);
            DataProcessor processor = contentProcessor.get(column.javaType);
            column.setValue(instance, processor.get(cursor, columnIndex));
        }
    }

    public static interface DataProcessor<T> {
        public void set(ContentValues values, String fieldName, T value);
        public T get(Cursor cursor, int index);
    }
    public static <T> void registerProcessor(DataProcessor<T> processor, Class... clazz) {
        for (Class c : clazz) {
            contentProcessor.put(c, processor);
        }
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
        if (tableName.isEmpty()) {
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

    public static <T> void registerTypeMapping(String sqlType, Class... javaType) {
        for(Class c : javaType) {
            typeMaps.put(c, sqlType);
        }
    }

}
