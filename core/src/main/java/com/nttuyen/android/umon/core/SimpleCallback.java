package com.nttuyen.android.umon.core;

/**
 * Created by nttuyen on 8/14/14.
 */
public abstract class SimpleCallback<T> implements Callback {
    @Override
    public void execute(Object... params) {
        T t = null;
        if(params.length > 0) {
            t = (T)params[0];
        }
        this.execute(t);
    }

    public abstract void execute(T t);
}
