package com.nttuyen.android.umon.widget;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;

/**
 * Created by tuyennt on 9/26/14.
 */
public class SlidingMenu extends DrawerLayout {
    private int width;
    private Activity activity = null;
    private DrawerListener listener = null;
    private int drawerImageResource = android.R.drawable.screen_background_dark_transparent;

    private final FrameLayout body;
    private final FrameLayout menu;

    public SlidingMenu(Context context) {
        super(context);
        this.body = new FrameLayout(context);
        this.menu = new FrameLayout(context);
    }

    public void init() {
        if(width == 0) {
            if (activity != null){
                Display display = activity.getWindowManager().getDefaultDisplay();
                int screenWidth = display.getWidth();
                width = screenWidth * 3 / 4;
            } else {
                width = 200;
            }
        }

        DrawerLayout.LayoutParams bodyLayoutParams = new DrawerLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        DrawerLayout.LayoutParams menuLayoutParams = new DrawerLayout.LayoutParams(width, LayoutParams.MATCH_PARENT);
        menuLayoutParams.gravity = Gravity.START;

        this.addView(body, bodyLayoutParams);
        this.addView(menu, menuLayoutParams);
        menu.setClickable(true);

        //TODO: I don't know this is good or bad
        menu.setBackgroundColor(getResources().getColor(android.R.color.white));
    }

    public void attachTo(final Activity activity) {
        if(activity == null) {
            return;
        }
        this.activity = activity;
        this.init();

        TypedArray a = activity.getTheme().obtainStyledAttributes(new int[] {android.R.attr.windowBackground});
        int background = a.getResourceId(0, 0);
        a.recycle();

        ViewGroup contentParent = (ViewGroup)activity.findViewById(android.R.id.content);
        View content = contentParent.getChildAt(0);
        contentParent.removeView(content);
        contentParent.addView(this);
        this.body.addView(content);

        // save people from having transparent backgrounds
        if (content.getBackground() == null) {
            content.setBackgroundResource(background);
        }

        this.setDrawerImageResource(this.drawerImageResource);
    }

    public void setDrawerImageResource(int drawerImageResource) {
        this.drawerImageResource = drawerImageResource;
        if(this.activity != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, this, drawerImageResource, 0, 0);
            this.setDrawerListener(toggle);
        }
    }

    @Override
    public void setDrawerListener(DrawerListener listener) {
        this.listener = listener;
        super.setDrawerListener(listener);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (this.listener instanceof ActionBarDrawerToggle) {
                return ((ActionBarDrawerToggle) listener).onOptionsItemSelected(item);
            }
        } catch (Exception ex){}

        return false;
    }

    public void syncState() {
        try {
            if (this.listener instanceof ActionBarDrawerToggle) {
                ((ActionBarDrawerToggle) listener).syncState();
            }
        } catch (Exception ex){}
    }

    public void toggle() {
        if(this.isDrawerOpen(menu)) {
            this.closeDrawer(menu);
        } else {
            this.openDrawer(menu);
        }
    }

    public void setWidth(int width) {
        this.width = width;
        ViewGroup.LayoutParams params = this.menu.getLayoutParams();
        if(params != null) {
            params.width = width;
        }
    }
    public void setContentView(View view) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.menu.addView(view, params);
    }
    public void setContentView(int layoutId) {
        setContentView(LayoutInflater.from(getContext()).inflate(layoutId, null));
    }
}
