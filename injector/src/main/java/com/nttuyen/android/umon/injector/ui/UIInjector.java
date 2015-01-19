package com.nttuyen.android.umon.injector.ui;

import android.util.Log;
import android.view.View;
import com.nttuyen.android.umon.utils.reflect.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by nttuyen on 1/19/15.
 */
public class UIInjector {
    private static final String TAG = "UIInjector";

    public static void inject(Object target) {
        injectContentView(target);
        ViewFinder finder = new ViewFinder(target);
        if(finder.isValid()) {
            injectView(target, finder);
        }
    }
    public static void inject(Object target, Object viewSource) {
        injectContentView(target);
        ViewFinder finder = new ViewFinder(viewSource);
        if(finder.isValid()) {
            injectView(target, finder);
        }
    }

    private static void injectContentView(Object target) {
        if(target == null) {
            return;
        }
        Class clazz = target.getClass();
        ContentView contentView = (ContentView)clazz.getAnnotation(ContentView.class);
        if(contentView == null || contentView.value() <= 0) {
            return;
        }
        Method method = null;
        try {
            method = clazz.getMethod("setContentView", int.class);
        } catch (Exception ex) {

        }
        if(method != null) {
            int id = contentView.value();
            method.setAccessible(true);
            try {
                method.invoke(target, id);
            } catch (Exception ex) {
                Log.e(TAG, "InvokeException", ex);
            }
        }
    }
    private static void injectView(Object object, ViewFinder viewFinder) {
        if(object == null) {
            return;
        }
        Class clazz = object.getClass();
        Field[] fields = ReflectUtil.getFieldsByAnnotation(clazz, InjectView.class);
        for(Field f : fields) {
            InjectView inject = f.getAnnotation(InjectView.class);
            int id = inject.value();
            if (id > 0) {
                View v = viewFinder.findViewById(id);
                if(v != null) {
                    if(f.getType().isAssignableFrom(v.getClass())) {
                        f.setAccessible(true);
                        try {
                            f.set(object, v);
                        } catch (Exception ex) {
                            Log.e(TAG, "Inject exception", ex);
                        }
                    }
                }
            }
        }
    }
    private static class ViewFinder {
        private final Object source;
        private final Method method;
        private ViewFinder(Object source) {
            Method m;
            if (source != null) {
                Class clazz = source.getClass();
                try {
                    m = clazz.getMethod("findViewById", int.class);
                } catch (Exception ex) {
                    m = null;
                }
            } else {
                m = null;
            }
            this.source = source;
            this.method = m;
        }
        public View findViewById(int id) {
            try {
                if (this.method != null && this.source != null) {
                    return (View)this.method.invoke(source, id);
                }
            } catch (Exception ex) {
                return null;
            }
            return null;
        }
        public boolean isValid() {
            return this.method != null;
        }
    }
}
