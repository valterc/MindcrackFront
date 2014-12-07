package com.valterc.utils;

import android.os.AsyncTask;
import android.os.Build;

import java.util.concurrent.Executor;

import static com.mopub.common.util.Reflection.MethodBuilder;

public class AsyncTasks {
    /**
     * Starting with ICS, default AsyncTask#execute behavior runs the tasks serially. This method
     * attempts to force these AsyncTasks to run in parallel with a ThreadPoolExecutor, if possible.
     */
    public static <P> void safeExecuteOnExecutor(AsyncTask<P, ?, ?> asyncTask, P... params) {
        if (asyncTask == null) {
            throw new IllegalArgumentException("Unable to execute null AsyncTask.");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                Executor threadPoolExecutor = (Executor) AsyncTask.class.getField("THREAD_POOL_EXECUTOR").get(AsyncTask.class);

                new MethodBuilder(asyncTask, "executeOnExecutor")
                        .addParam(Executor.class, threadPoolExecutor)
                        .addParam(Object[].class, params)
                        .execute();
            } catch (Exception e) {
                asyncTask.execute(params);
            }
        } else {
            asyncTask.execute(params);
        }
    }
}
