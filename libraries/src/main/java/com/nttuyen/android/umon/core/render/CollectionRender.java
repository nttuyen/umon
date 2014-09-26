package com.nttuyen.android.umon.core.render;

import android.view.View;
import android.widget.LinearLayout;

import java.util.Collection;

/**
 * Created by nttuyen on 8/15/14.
 */
public abstract class CollectionRender<M> implements Renderer<Collection<M>, LinearLayout> {
    private final Renderer<M, ?> itemRender;

    public CollectionRender(Renderer<M, ?> itemRender) {
        this.itemRender = itemRender;
    }

    @Override
    public LinearLayout render(Collection<M> model, LinearLayout view) {
        for(M m : model) {
            View v = this.createItemView();
            ((Renderer<M, View>)itemRender).render(m, v);
            view.addView(v);
        }
        return view;
    }

    public abstract View createItemView();
}
