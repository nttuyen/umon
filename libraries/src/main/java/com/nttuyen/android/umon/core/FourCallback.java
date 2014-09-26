package com.nttuyen.android.umon.core;

/**
 * Created by nttuyen on 8/14/14.
 */
public abstract class FourCallback<T, U, V, W> implements Callback {
    @Override
    public void execute(Object... params) {
        T t = null; U u = null; V v = null; W w = null;
        if(params.length > 0) {
            t = (T)params[0];
        }
        if(params.length > 1) {
            u = (U)params[1];
        }
        if(params.length > 2) {
            v = (V)params[2];
        }
        if(params.length > 3) {
            w = (W)params[3];
        }
        this.execute(t, u, v, w);
    }
    public abstract void execute(T t, U u, V v, W w);
}
