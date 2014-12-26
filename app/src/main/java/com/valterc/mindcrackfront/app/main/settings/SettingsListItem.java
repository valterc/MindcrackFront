package com.valterc.mindcrackfront.app.main.settings;

/**
 * Created by Valter on 23/12/2014.
 */
public class SettingsListItem<T extends Object> {

    public static final int SETTINGS_TYPE_COUNT = 2;
    public static final int TYPE_BUTTON = 0;
    public static final int TYPE_SWITCH = 1;

    private int type;
    private String title;
    private String subTitle;
    private T value;
    private Boolean enabled;
    private Boolean clickable;
    private SettingsInteractionListener<T> interactionListener;

    public SettingsListItem(SettingsInteractionListener<T> listener, int type, String title, String subTitle){
        this.interactionListener = listener;
        this.setType(type);
        this.setTitle(title);
        this.setSubTitle(subTitle);
        this.setEnabled(true);
        if (type == TYPE_BUTTON){
            setClickable(true);
        } else {
            setClickable(false);
        }
    }

    public SettingsListItem(SettingsInteractionListener<T> listener, int type, String title, String subTitle, T value){
        this.interactionListener = listener;
        this.setType(type);
        this.setTitle(title);
        this.setSubTitle(subTitle);
        this.setValue(value);
        this.setEnabled(true);
        if (type == TYPE_BUTTON){
            setClickable(true);
        } else {
            setClickable(false);
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled){
        this.enabled = enabled;
    }

    public Boolean getClickable() {
        return clickable;
    }

    public void setClickable(Boolean clickable) {
        this.clickable = clickable;
    }

    public void OnClick() {
        if (!getEnabled()){
            return;
        }
        if (interactionListener != null) {
            interactionListener.onClick(this);
        }
    }

    public void OnValueChanged(T newValue) {
        if (interactionListener != null) {
            interactionListener.onInteraction(this, newValue);
        }
    }

    public static interface SettingsInteractionListener<T extends Object> {
        void onInteraction(SettingsListItem item, T newValue);
        void onClick(SettingsListItem item);
    }

}
