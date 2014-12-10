package com.valterc.utils;

import android.os.AsyncTask;

/**
 * Created by Valter on 10/12/2014.
 */
public class MultipurposeAsyncTask<T> extends AsyncTask<Void, Void, T> {

    private MultiporpuseAsyncTaskData<T> data;

    public MultipurposeAsyncTask(MultiporpuseAsyncTaskData<T> data){
        this.data = data;
    }

    @Override
    protected T doInBackground(Void... params) {
        return data.runOnBackground();
    }

    @Override
    protected void onPostExecute(T t) {
        super.onPostExecute(t);
        data.onComplete(t);
    }
}
