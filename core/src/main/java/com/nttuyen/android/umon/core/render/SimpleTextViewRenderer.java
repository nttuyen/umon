package com.nttuyen.android.umon.core.render;

import android.widget.TextView;
import com.nttuyen.android.umon.core.reflect.ReflectUtil;

/**
 * Created by nttuyen on 8/17/14.
 */
public class SimpleTextViewRenderer implements Renderer<Object, TextView> {
    @Override
    public TextView render(Object model, TextView view) {
        if(!ReflectUtil.isPrimary(model.getClass())) {
            return view;
        }
        view.setText(String.valueOf(model));

        return view;
    }

    public static void register() {
        SimpleTextViewRenderer render = new SimpleTextViewRenderer();
        RenderRegistry registry = RenderRegistry.getInstance();
        registry.register(int.class, TextView.class, render);
        registry.register(Integer.class, TextView.class, render);

        registry.register(long.class, TextView.class, render);
        registry.register(Long.class, TextView.class, render);

        registry.register(float.class, TextView.class, render);
        registry.register(Float.class, TextView.class, render);

        registry.register(double.class, TextView.class, render);
        registry.register(Double.class, TextView.class, render);

        registry.register(boolean.class, TextView.class, render);
        registry.register(Boolean.class, TextView.class, render);

        registry.register(byte.class, TextView.class, render);
        registry.register(Byte.class, TextView.class, render);

        registry.register(boolean.class, TextView.class, render);
        registry.register(Boolean.class, TextView.class, render);

        registry.register(String.class, TextView.class, render);
    }
}
