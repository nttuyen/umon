package com.nttuyen.android.umon.core;

/**
 * Created by nttuyen on 8/14/14.
 */
public abstract class BiCallback<U, V> implements Callback {
    @Override
    public void execute(Object... params) {
        U u = null;
        V v = null;
        if(params.length > 0) {
            u = (U)params[0];
        }
        if(params.length > 1) {
            v = (V)params[1];
        }
        this.execute(u, v);
    }

    public abstract void execute(U u, V v);
}
