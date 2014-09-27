package com.nttuyen.android.umon.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;

/**
 * Created by tuyennt on 9/26/14.
 */
public class SlidingMenu extends DrawerLayout {
    private static final String TAG = "UMON:SlidingMenu";

    private int width;
    private Activity activity = null;
    private DrawerListener listener = null;
    private int drawerImageResource = android.R.drawable.screen_background_dark_transparent;
    private int openDrawerContentDescRes = 0;
    private int closeDrawerContentDescRes = 0;

    private final FrameLayout body;
    private final FrameLayout menu;

    public SlidingMenu(Context context) {
        super(context);
        this.body = new FrameLayout(context);
        this.menu = new FrameLayout(context);
    }

    private void init() {
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

    public SlidingMenu attachTo(final Activity activity) {
        if(activity == null) {
            return this;
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

        return this;
    }

    public SlidingMenu setDrawerImageResource(int drawerImageResource) {
        this.drawerImageResource = drawerImageResource;
        if(this.activity != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, this, drawerImageResource, this.openDrawerContentDescRes, this.closeDrawerContentDescRes){
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    if (openDrawerContentDescRes != 0) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                activity.getActionBar().setTitle(openDrawerContentDescRes);
                            } else {
                                if (activity instanceof ActionBarActivity) {
                                    ((ActionBarActivity) activity).getSupportActionBar().setTitle(openDrawerContentDescRes);
                                }
                            }
                        } catch (Throwable ex){
                            Log.w(TAG, "Throwable", ex);
                        }
                    }
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    if (closeDrawerContentDescRes != 0) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                activity.getActionBar().setTitle(closeDrawerContentDescRes);
                            } else if (activity instanceof ActionBarActivity) {
                                ((ActionBarActivity) activity).getSupportActionBar().setTitle(closeDrawerContentDescRes);
                            }
                        } catch (Throwable ex){
                            Log.w(TAG, "Throwable", ex);
                        }
                    }

                }
            };
            this.setDrawerListener(toggle);

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    activity.getActionBar().setDisplayHomeAsUpEnabled(true);
                    activity.getActionBar().setHomeButtonEnabled(true);
                } else {
                    if (activity instanceof ActionBarActivity) {
                        ((ActionBarActivity)activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        ((ActionBarActivity)activity).getSupportActionBar().setHomeButtonEnabled(true);
                    }
                }
            } catch (Throwable ex) {
                Log.w(TAG, "Throwable", ex);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SlidingMenu.this.syncState();
                }
            }, 1);
        }
        return this;
    }

    public SlidingMenu setOpenDrawerContentDescRes(int resourceId) {
        this.openDrawerContentDescRes = resourceId;
        return this;
    }

    public SlidingMenu setCloseDrawerContentDescRes(int resourceId) {
        this.closeDrawerContentDescRes = resourceId;
        return this;
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
        } catch (Throwable ex){
            Log.w(TAG, "Throwable", ex);
        }

        return false;
    }

    public SlidingMenu syncState() {
        try {
            if (this.listener instanceof ActionBarDrawerToggle) {
                ((ActionBarDrawerToggle) listener).syncState();
            }
        } catch (Throwable ex){
            Log.w(TAG, "Throwable", ex);
        }

        return this;
    }

    public SlidingMenu toggle() {
        if(this.isDrawerOpen(menu)) {
            this.closeDrawer(menu);
        } else {
            this.openDrawer(menu);
        }
        return this;
    }

    public SlidingMenu setWidth(int width) {
        this.width = width;
        ViewGroup.LayoutParams params = this.menu.getLayoutParams();
        if(params != null) {
            params.width = width;
        }
        return this;
    }
    public SlidingMenu setContentView(View view) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.menu.addView(view, params);

        return this;
    }
    public SlidingMenu setContentView(int layoutId) {
        return setContentView(LayoutInflater.from(getContext()).inflate(layoutId, null));
    }
}
