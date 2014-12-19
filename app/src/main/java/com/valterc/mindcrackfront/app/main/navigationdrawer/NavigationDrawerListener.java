package com.valterc.mindcrackfront.app.main.navigationdrawer;


import com.valterc.mindcrackfront.app.data.Mindcracker;
import com.valterc.mindcrackfront.app.main.navigationdrawer.list.NavigationDrawerListItem;

/**
 * Created by Valter on 17/05/2014.
 */
public interface NavigationDrawerListener {

    public void onNavigationDrawerItemSelected(Mindcracker m);
    public void onNavigationDrawerItemSelected(NavigationDrawerListItem item);

}
