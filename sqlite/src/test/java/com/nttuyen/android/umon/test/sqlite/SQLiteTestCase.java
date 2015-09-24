package com.nttuyen.android.umon.test.sqlite;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.nttuyen.android.umon.sqlite.SQLite;
import com.nttuyen.android.umon.sqlite.condition.Conditions;
import com.nttuyen.android.umon.sqlite.condition.Query;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.List;

/**
 * Created by nttuyen on 9/24/15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class SQLiteTestCase {
    private SQLite persistence;
    @BeforeClass
    public static void beforeClass() {
        SQLite.addEntityClass(MyEntity.class);
    }

    @Before
    public void before() {
        Context c = new Activity();
        persistence = new SQLite(c, "TEST_DB", null, 1);
    }

    @After
    public void after() {
        List<MyEntity> entities = persistence.select(MyEntity.class);
        for (MyEntity e : entities) {
            persistence.delete(MyEntity.class, e.getId());
        }
        persistence.close();
    }

    @Test
    public void testInsert() {
        MyEntity entity = new MyEntity();
        Date created = new Date();
        entity.setName("Test insert");
        entity.setDescription("This is to test the insert");
        entity.setCreated(created);

        persistence.insert(entity);

        Assert.assertTrue("Entity ID must be greater than 0", entity.getId() > 0);

        long count = persistence.count(MyEntity.class);
        Assert.assertEquals(1, count);
        List<MyEntity> entities = persistence.select(MyEntity.class);
        Assert.assertEquals(1, entities.size());

        entity = entities.get(0);
        Assert.assertEquals("Test insert", entity.getName());
        Assert.assertEquals("This is to test the insert", entity.getDescription());
        Assert.assertEquals(created, entity.getCreated());
    }

    @Test
    public void testGetById() {
        MyEntity entity = new MyEntity();
        Date created = new Date();
        entity.setName("Test insert");
        entity.setDescription("This is to test the insert");
        entity.setCreated(created);

        persistence.insert(entity);

        Assert.assertTrue("Entity ID must be greater than 0", entity.getId() > 0);

        MyEntity e = persistence.selectById(MyEntity.class, entity.getId());

        Assert.assertEquals("Test insert", e.getName());
        Assert.assertEquals("This is to test the insert", e.getDescription());
        Assert.assertEquals(created, e.getCreated());
    }

    @Test
    public void testUpdate() {
        MyEntity entity = new MyEntity();
        Date created = new Date();
        entity.setName("Test update");
        entity.setDescription("This is to test the update");
        entity.setCreated(created);

        persistence.insert(entity);

        Assert.assertTrue("Entity ID must be greater than 0", entity.getId() > 0);

        entity = persistence.selectById(MyEntity.class, entity.getId());
        Assert.assertEquals("Test update", entity.getName());

        entity.setName("Test update ok");
        persistence.update(entity);

        MyEntity e = persistence.selectById(MyEntity.class, entity.getId());
        Assert.assertEquals("Test update ok", e.getName());
    }

    @Test
    public void testDelete() {
        MyEntity entity = new MyEntity();
        Date created = new Date();
        entity.setName("Test delete");
        entity.setDescription("This is to test the delete");
        entity.setCreated(created);

        persistence.insert(entity);

        Assert.assertTrue("Entity ID must be greater than 0", entity.getId() > 0);

        MyEntity e = persistence.selectById(MyEntity.class, entity.getId());
        Assert.assertNotNull(e);

        persistence.delete(MyEntity.class, e.getId());

        e = persistence.selectById(MyEntity.class, entity.getId());
        Assert.assertNull(e);
    }

    @Test
    public void testCount() {
        MyEntity entity = new MyEntity();
        Date created = new Date();
        entity.setName("Test delete");
        entity.setDescription("This is to test the delete");
        entity.setCreated(created);

        persistence.insert(entity);
        Assert.assertEquals(new Long(1L), (Long)persistence.count(MyEntity.class));

        entity.setId(0);
        persistence.insert(entity);
        Assert.assertEquals(new Long(2L), (Long)persistence.count(MyEntity.class));

        persistence.delete(MyEntity.class, entity.getId());
        Assert.assertEquals(new Long(1L), (Long)persistence.count(MyEntity.class));
    }

    @Test
    public void testSelectByQuery() {
        MyEntity entity = new MyEntity();
        Date created = new Date();
        entity.setName("Test update");
        entity.setDescription("This is to test the update");
        entity.setCreated(created);

        persistence.insert(entity);

        Query query = Conditions.eq("name", "Test update").toQuery();
        List<MyEntity> entities = persistence.select(MyEntity.class, query);

        Assert.assertTrue(entities.size() >= 1);
    }
}
