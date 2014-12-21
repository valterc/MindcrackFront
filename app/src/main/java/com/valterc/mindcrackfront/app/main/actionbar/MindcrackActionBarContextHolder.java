package com.valterc.mindcrackfront.app.main.actionbar;

/**
 * Created by Valter on 21/12/2014.
 */
public interface MindcrackActionBarContextHolder {

    void restoreContext(MindcrackActionBarFragment actionBarFragment);
    void contextLost(MindcrackActionBarFragment actionBarFragment);

}
