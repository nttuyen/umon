package com.nttuyen.android.umon.core.render;

import android.view.View;
import com.nttuyen.android.umon.core.reflect.ReflectUtil;
import com.nttuyen.android.umon.core.ui.UIView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by nttuyen on 8/17/14.
 */
public class GenericRenderer implements Renderer<Object, View> {
    @Override
    public View render(Object model, View view) {
        if(model == null || view == null) {
            return view;
        }

        Class clazz = model.getClass();
        Set<Field> fields = ReflectUtil.getAllField(clazz);
        for(Field field : fields) {
            Method getter = ReflectUtil.getterMethod(field, clazz);
            if(getter == null) {
                continue;
            }
            getter.setAccessible(true);
            Object val;
            try {
                val = getter.invoke(model);
            } catch (Exception ex) {
                val = null;
            }

            if(val == null) {
                continue;
            }

            UIView ui = getter.getAnnotation(UIView.class);
            if(ui == null || ui.views().length == 0) {
                continue;
            }
            for(int id : ui.views()) {
                View v = view.findViewById(id);
                if(v != null) {
                    Renderer render = RenderRegistry.getInstance().findRender(val.getClass(), v.getClass());
                    if(render != null) {
                        render.render(val, v);
                    }
                }
            }
        }

        return view;
    }

    public static void register() {
        RenderRegistry.getInstance().register(Object.class, View.class, new GenericRenderer());
    }
}
