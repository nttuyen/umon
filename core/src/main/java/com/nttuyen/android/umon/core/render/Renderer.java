package com.nttuyen.android.umon.core.render;

import android.view.View;

/**
 * Created by nttuyen on 8/14/14.
 */
public interface Renderer<M, V extends View> {
    public V render(M model, V view);
}
