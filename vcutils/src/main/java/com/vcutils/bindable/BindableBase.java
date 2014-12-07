package com.vcutils.bindable;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Valter on 23/05/2014.
 */
public class BindableBase {

    public HashMap<String, ArrayList<WeakReference<BindableListener>>> listeners = new HashMap<String, ArrayList<WeakReference<BindableListener>>>();

    public void RegisterBinder(String name, BindableListener listener){

        if (listeners.containsKey(name)){
            listeners.get(name).add(new WeakReference<BindableListener>(listener));
        } else {
            listeners.put(name, new ArrayList<WeakReference<BindableListener>>());
            listeners.get(name).add(new WeakReference<BindableListener>(listener));
        }

    }

    @Deprecated
    public void UnregisterBinder(String name, BindableListener listener){
        if (listeners.containsKey(name)) {
            listeners.get(name).remove(name);
        }
    }

    protected void NotifyChange(String name, Object t){
        if (listeners.containsKey(name)) {
            for (int i = 0; i < listeners.get(name).size(); i++) {
                WeakReference<BindableListener> weakref =  listeners.get(name).get(i);
                BindableListener listener = weakref.get();
                if (listener != null) {
                    listener.onChange(name, t, null);
                } else {
                    listeners.get(name).remove(i--);
                }
            }
        }
    }

    protected void NotifyChange(String name, Object t, Object extra){
        if (listeners.containsKey(name)) {
            for (int i = 0; i < listeners.get(name).size(); i++) {
                WeakReference<BindableListener> weakref =  listeners.get(name).get(i);
                BindableListener listener = weakref.get();
                if (listener != null) {
                    listener.onChange(name, t, extra);
                } else {
                    listeners.get(name).remove(i--);
                }
            }
        }
    }


}
