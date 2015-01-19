package com.nttuyen.android.umon.test.bus;

import android.app.Activity;
import com.nttuyen.android.umon.bus.EventBus;
import com.nttuyen.android.umon.bus.Subscribe;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by nttuyen on 1/18/15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class TestEventBus {
    private EventBus bus;
    private int count;

    @Before
    public void before() {
        this.bus = new EventBus();
        this.count = 0;
    }
    @After
    public void after() {
        this.bus = null;
    }

    @Test
    public void testSimpleEventBusWithName() {
        List<Future> futures = new LinkedList<Future>();
        this.bus.register(new Object(){
            @Subscribe({"test"})
            public String simpleEvent() {
                count++;
                return "Simple test";
            }
        });
        System.out.println("1+");
        futures.addAll(this.bus.post("test"));
        System.out.println("2+");
        this.bus.register(new TestActivity());
        System.out.println("3+");
        futures.addAll(this.bus.post("test1"));
        System.out.println("4+");
        for(Future f : futures) {
            try {
                Object val = f.get();
                System.out.println("ok: " + val);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("5+");
        Assert.assertEquals(2, count);
    }

    public final class TestActivity extends Activity {
        public TestActivity() {
        }

        @Subscribe({"test1"})
        public int simpleEvent() {
            return count++;
        }
    }
}
