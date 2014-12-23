package com.valterc.mindcrackfront.app.main.settings;

/**
 * Created by Valter on 23/12/2014.
 */
public class SettingsListItem {

    private int type;
    private String title;
    private String subTitle;
    private Object value;
    private SettingsInteractionListener interactionListener;

    public SettingsListItem(SettingsInteractionListener listener, int type, String title, String subTitle){
        this.interactionListener = listener;
        this.type = type;
        this.title = title;
        this.subTitle = subTitle;
    }

    public SettingsListItem(SettingsInteractionListener listener, int type, String title, String subTitle, Object value){
        this.interactionListener = listener;
        this.type = type;
        this.title = title;
        this.subTitle = subTitle;
        this.value = value;
    }

    public static interface SettingsInteractionListener {
        void onInteraction(SettingsListItem item, Object newValue);
    }

}
