package com.nttuyen.android.umon.core;

/**
 * Created by nttuyen on 8/14/14.
 */
public abstract class TriCallback<T, U, V> implements Callback {
    @Override
    public void execute(Object... params) {
        T t = null;
        U u = null;
        V v = null;

        if(params.length > 0) {
            t = (T)params[0];
        }
        if(params.length > 1) {
            u = (U)params[1];
        }
        if(params.length > 2) {
            v = (V)params[2];
        }

        this.execute(t, u, v);
    }
    public abstract void execute(T t, U u, V v);
}
