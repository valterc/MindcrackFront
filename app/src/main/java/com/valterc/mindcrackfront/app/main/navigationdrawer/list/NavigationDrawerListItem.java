package com.valterc.mindcrackfront.app.main.navigationdrawer.list;

import com.valterc.mindcrackfront.app.data.Mindcracker;

/**
 * Created by Valter on 23/05/2014.
 */
public class NavigationDrawerListItem {

    public static final int TYPE_TITLE = 0;
    public static final int TYPE_EMPTY = 1;
    public static final int TYPE_FAVORITE = 2;
    public static final int TYPE_NORMAL = 3;

    public int type;
    public Mindcracker mindcracker;
    public String title;

    public NavigationDrawerListItem(int type, Mindcracker mindcracker) {
        this.type = type;
        this.mindcracker = mindcracker;
    }

    public NavigationDrawerListItem(int type) {
        this.type = type;
    }

    public NavigationDrawerListItem(int type, String title) {
        this.type = type;
        this.title = title;
    }

}
