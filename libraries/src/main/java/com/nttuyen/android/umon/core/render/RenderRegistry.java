package com.nttuyen.android.umon.core.render;

import android.opengl.GLSurfaceView;
import android.view.View;
import com.nttuyen.android.umon.core.reflect.ReflectUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nttuyen on 8/15/14.
 */
public class RenderRegistry {
    private static RenderRegistry instance;
    public static RenderRegistry getInstance() {
        if(instance == null) {
            synchronized (RenderRegistry.class) {
                if(instance == null) {
                    instance = new RenderRegistry();
                }
            }
        }
        return instance;
    }

    private final Map<Class, Map<Class, Renderer>> registry;

    private RenderRegistry() {
        registry = new HashMap<Class, Map<Class, Renderer>>();
    }

    public Renderer register(Class modelClass, Class viewClass, Renderer render) {
        Map<Class, Renderer> map = registry.get(modelClass);
        if(map == null) {
            map = new HashMap<Class, Renderer>();
        }
        Renderer<?, ? extends View> r = map.put(viewClass, render);
        registry.put(modelClass, map);

        return r;
    }

    public <M, V extends View> Renderer<M, V> findRender(Class modelClass, Class viewClass) {
        Renderer render;
        Map<Class, Renderer> map = registry.get(modelClass);
        if(map != null) {
            render = map.get(viewClass);
            if(render != null) {
                return render;
            }
        }

        if(!viewClass.equals(View.class)) {
            viewClass = viewClass.getSuperclass();
        } else if(!modelClass.equals(Object.class) && !modelClass.isPrimitive()) {
            modelClass = modelClass.getSuperclass();
        } else {
            return null;
        }

        return findRender(modelClass, viewClass);
    }
}
