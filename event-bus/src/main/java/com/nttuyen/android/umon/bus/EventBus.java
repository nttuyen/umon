package com.nttuyen.android.umon.bus;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import com.nttuyen.android.umon.utils.reflect.ReflectUtil;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by nttuyen on 1/17/15.
 */
public class EventBus {
    private static final String TAG = "EventBus";
    private final Set<Subscriber> subscribers;
    private final ExecutorService executor;

    public EventBus() {
        subscribers = new HashSet<Subscriber>();
        this.executor = Executors.newCachedThreadPool();
    }

    private static EventBus instance = null;
    public static EventBus getDefault() {
        if(instance == null) {
            synchronized (EventBus.class) {
                if(instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    public List<Future> post(String eventName, final Object... eventParams) {
        List<Future> futures = new LinkedList<Future>();
        Class[] classes = new Class[eventParams.length];
        for(int i = 0; i < eventParams.length; i++) {
            classes[i] = eventParams[i] != null ? eventParams[i].getClass() : null;
        }
        for(final Subscriber subscriber : this.findSubscribers(eventName, classes)) {
            Future f = this.executor.submit(new Callable() {
                @Override
                public Object call() throws Exception {
                    return subscriber.handle(eventParams);
                }
            });
            futures.add(f);
        }
        return futures;
    }

    public List<Future> post(Object... eventParams) {
        return this.post(null, eventParams);
    }

    public EventBus register(Object subscriber) {
        if(subscriber == null) {
            return this;
        }
        Method[] methods = ReflectUtil.getMethods(subscriber.getClass(), Subscribe.class);
        for(Method m : methods) {
            Subscribe listener = m.getAnnotation(Subscribe.class);
            String[] eventNames = listener.value();
            Subscriber eventHandler = new Subscriber(eventNames, subscriber, m);
            if(!subscribers.contains(eventHandler)) {
                subscribers.add(eventHandler);
            }
        }

        return this;
    }
    public EventBus unregister(Object subscriber) {
        if(subscriber == null) {
            return this;
        }
        Set<Subscriber> needRemoved = new HashSet<Subscriber>();
        for(Subscriber sub : this.subscribers) {
            if(sub.obj == subscriber || subscriber.equals(sub.obj)) {
                needRemoved.add(sub);
            }
        }
        this.subscribers.removeAll(needRemoved);
        return this;
    }

    public EventBus clear() {
        this.subscribers.clear();
        return this;
    }

    private List<Subscriber> findSubscribers(String name, Class... paramType) {
        List<Subscriber> handlers = new ArrayList<Subscriber>();
        for(Subscriber handler : this.subscribers) {
            boolean ok = true;
            // Compare eventName
            if(name != null && !"".equals(name) && handler.names.length > 0) {
                ok = false;
                for(String n : handler.names) {
                    if(name.equals(n)) {
                        ok = true;
                        break;
                    }
                }
            }
            if(!ok) continue;

            Class[] params = handler.method.getParameterTypes();
            if(params.length != paramType.length) {
                ok = false;
            } else {
                for (int i = 0; i < paramType.length; i++) {
                    if (paramType[i] != null
                            && paramType[i] != params[i]
                            && !paramType[i].equals(params[i])
                            && !params[i].isAssignableFrom(paramType[i])) {
                        ok = false;
                    }
                }
            }

            if(ok) {
                handlers.add(handler);
            }
        }
        return handlers;
    }

    private static final class Subscriber {
        public final String[] names;
        public final Object obj;
        public final Method method;

        private Subscriber(String[] names, Object obj, Method method) {
            this.names = names;
            this.obj = obj;
            this.method = method;
        }

        private Object handle(final Object... params) {
            if(method!= null) {
                method.setAccessible(true);
                Handler handler = null;
                if(obj != null) {
                    try {
                        if (obj instanceof View) {
                            View v = (View) this.obj;
                            handler = v.getHandler();
                        } else if (obj instanceof Activity) {
                            Activity activity = (Activity) this.obj;
                            handler = activity.getWindow().getDecorView().getHandler();
                        } else if (obj instanceof Fragment) {
                            handler = ((Fragment)obj).getActivity().getWindow().getDecorView().getHandler();
                        }
                    } catch (Throwable ex) {
                        Log.d(TAG, "Exception", ex);
                    }
                }
                final AtomicReference ref = new AtomicReference(null);
                if(handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ref.compareAndSet(null, method.invoke(obj, params));
                            } catch (Exception ex) {
                                Log.e(TAG, "Exception while invoke event handler", ex);
                            }
                        }
                    });
                } else {
                    try {
                        ref.compareAndSet(null, method.invoke(obj, params));
                    } catch (Exception ex) {
                        Log.e(TAG, "Exception while invoke event handler", ex);
                    }
                }
                return ref.get();
            }
            return null;
        }

        @Override
        public int hashCode() {
            int h = 0;
            for(String n : this.names) {
                h = 31 * h + n.hashCode();
            }
            h = 31 * h + obj.hashCode();
            h = 31 * h + method.hashCode();

            return h;
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof Subscriber)) {
                return false;
            }
            Subscriber handler = (Subscriber)obj;

            if(handler.names.length != this.names.length) {
                return false;
            }
            for(int i = 0; i < this.names.length; i++) {
                if(!this.names[i].equals(handler.names[i])) {
                    return false;
                }
            }

            return (this.obj == handler.obj || this.obj.equals(handler.obj)) &&
                    (this.method == handler.method || this.method.equals(handler.method));
        }
    }
}
