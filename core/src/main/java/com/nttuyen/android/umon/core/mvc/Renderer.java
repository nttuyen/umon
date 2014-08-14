package com.nttuyen.android.umon.core.mvc;

import android.view.View;

/**
 * Created by nttuyen on 8/14/14.
 */
public interface Renderer<M, V extends View> {
    public void render(M model, V view);
}
