package com.nttuyen.android.umon.widget;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by tuyennt on 9/26/14.
 */
public class SlidingMenu extends DrawerLayout {
    private int width;
    private Activity activity = null;
    private final FrameLayout body;
    private final FrameLayout menu;

    public SlidingMenu(Context context) {
        super(context);
        this.body = new FrameLayout(context);
        this.menu = new FrameLayout(context);
    }

    public void init() {
        this.addView(body);
        this.addView(menu);
        //TODO: implement this please
    }

    public void attachTo(Activity activity) {
        if(activity == null) {
            return;
        }
        this.activity = activity;

        //TODO: please implement this
    }

    public void setWidth(int width) {
        this.width = width;
        ViewGroup.LayoutParams params = this.menu.getLayoutParams();
        if(params != null) {
            params.width = width;
        }
    }
}
