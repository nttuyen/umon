package com.nttuyen.android.umon.test.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.nttuyen.android.umon.sqlite.SQLite;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

/**
 * Created by nttuyen on 9/24/15.
 */
@Implements(SQLite.class)
public class ShadowSQLiteOpenHelper {
    @RealObject private SQLite realHelper;
    private static SQLiteDatabase database;

    private static Context previousContext;
    private String name;

    public void __constructor__(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        this.name = name;
        if (previousContext == null) {
            previousContext = context;
        } else {
            if(previousContext == context) {
                return;
            } else {
                previousContext = context;
            }
        }
        if (database != null) {
            database.close();
        }
        database = null;
    }

    @Implementation
    public synchronized void close() {
        if(previousContext != null)
            return;
        if (database != null) {
            database.close();
        }
        database = null;
    }

    @Implementation
    public synchronized SQLiteDatabase getReadableDatabase() {
        if (database == null) {
            database = SQLiteDatabase.openDatabase("path", null, 0);
            realHelper.onCreate(database);
        }

        realHelper.onOpen(database);
        return database;
    }

    @Implementation
    public synchronized SQLiteDatabase getWritableDatabase() {
        if (database == null) {
            database = SQLiteDatabase.openDatabase("path", null, 0);
            realHelper.onCreate(database);
        }

        realHelper.onOpen(database);
        return database;
    }

    @Implementation
    public String getDatabaseName() {
        return name;
    }
}
