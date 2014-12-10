package com.valterc.utils;

/**
 * Created by Valter on 10/12/2014.
 */
public interface MultipurposeAsyncTaskData<T> {

    T runOnBackground();
    void onComplete(T t);
}
