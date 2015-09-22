package com.nttuyen.android.umon.bus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import com.nttuyen.android.umon.utils.reflect.ReflectUtil;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * Created by nttuyen on 2/19/15.
 */
public class Bus {
    static Context defaultContext = null;

    public static Bus getInstance(Context context) {
        return new Bus(context);
    }
    public static Bus getInstance() {
        return new Bus(defaultContext);
    }

    private static final String EVENT_DATA_KEY = "event-data";


    private final Context context;
    private final LocalBroadcastManager broadcastManager;

    public Bus(Context context) {
        if(context == null) {
            throw new IllegalArgumentException("Context must not be null!");
        }
        this.context = context;
        this.broadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public void post(Context context, Object... params) {
        this.post("*", new EventData(params), context, false);
    }
    public void post(Object... params) {
        this.post("*", new EventData(params), this.context, false);
    }
    public void post(String name, Context context, Object... params) {
        this.post(name, new EventData(params), context, false);
    }
    public void post(String name, Object... params) {
        this.post(name, new EventData(params), this.context, false);
    }

    public void postSync(Context context, Object... params) {
        this.post("*", new EventData(params), context, true);
    }
    public void postSync(Object... params) {
        this.post("*", new EventData(params), this.context, true);
    }

    public void postSync(String name, Context context, Object... params) {
        this.post(name, new EventData(params), context, true);
    }
    public void postSync(String name, Object... params) {
        this.post(name, new EventData(params), this.context, true);
    }

    public Bus post(String name, EventData eventData, Context context, boolean isSync) {
        Intent intent = new Intent(name);
        intent.putExtra(EVENT_DATA_KEY, eventData);
        Intent starIntent = new Intent("*");
        starIntent.putExtra(EVENT_DATA_KEY, eventData);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        if(isSync) {
            manager.sendBroadcastSync(intent);
            manager.getInstance(context).sendBroadcastSync(starIntent);
        } else {
            manager.sendBroadcast(intent);
            manager.sendBroadcast(starIntent);
        }

        return this;
    }

    public Bus register(Object subscriber) {
        if(subscriber == null) {
            return this;
        }
        Method[] methods = ReflectUtil.getMethods(subscriber.getClass(), Subscribe.class);
        for(Method m : methods) {
            Subscribe listener = m.getAnnotation(Subscribe.class);
            String[] eventNames = listener.value();
            String[] names = new String[eventNames.length + 1];
            names[0] = "*";
            if (eventNames.length > 0) {
                System.arraycopy(eventNames, 0, names, 1, eventNames.length);
            }
            for(String event : eventNames) {
                EventBroadcastReceiver receiver = new EventBroadcastReceiver(subscriber, m, this.broadcastManager);
                this.broadcastManager.registerReceiver(receiver, new IntentFilter(event));
            }
        }

        return this;
    }

    private static class EventData implements Serializable {
        public final Object[] data;

        public EventData(Object... data) {
            this.data = data;
        }
    }

    private static class EventBroadcastReceiver extends BroadcastReceiver {
        private final WeakReference<Object> target;
        private final Method method;
        private final LocalBroadcastManager broadcastManager;

        public EventBroadcastReceiver(Object target, Method method, LocalBroadcastManager broadcastManager) {
            if(target == null) {
                this.target = null;
            } else {
                this.target = new WeakReference<Object>(target);
            }
            this.method = method;
            this.broadcastManager = broadcastManager;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(this.method == null) {
                this.broadcastManager.unregisterReceiver(this);
                return;
            }
            if(this.target != null && this.target.get() == null) {
                this.broadcastManager.unregisterReceiver(this);
                return;
            }
            EventData event;
            try {
                event = (EventData) intent.getSerializableExtra(EVENT_DATA_KEY);
            } catch (Exception ex) {
                event = new EventData();
            }

            //. Build params
            Class[] paramTypes = this.method.getParameterTypes();
            Object[] params = new Object[paramTypes.length];

            boolean valid = true;
            int index = 0;
            for(int i = 0; i < params.length; i++) {
                if(paramTypes[i].equals(Context.class)) {
                    params[i] = context;
                } else {
                    if(index >= event.data.length) {
                        valid = false;
                        break;
                    }
                    if(event.data[index] == null) {
                        params[i] = null;

                    } else if(paramTypes[i].equals(String.class) && (event.data[index].getClass() == Integer.TYPE || event.data[index].getClass() == Integer.class)) {
                        int resourceId = (Integer)event.data[index];
                        String value;
                        try {
                            value = context.getString(resourceId);
                        } catch (Throwable ex) {
                            value = null;
                        }
                        if(value == null) {
                            valid = false;
                            break;
                        }
                    } else {
                        try {
                            params[i] = paramTypes[i].cast(event.data[index]);
                        } catch (Throwable ex) {
                            valid = false;
                            break;
                        }
                    }
                    index++;
                }
            }

            if(valid) {
                try {
                    method.invoke(this.target.get(), params);
                } catch (Throwable ex) {

                }
            }
        }
    }
}
