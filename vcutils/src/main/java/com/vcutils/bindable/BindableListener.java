package com.vcutils.bindable;

/**
 * Created by Valter on 23/05/2014.
 */
public interface BindableListener<T> {

    public void onChange(String property, T value, Object extra);

}
