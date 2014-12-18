package com.vcutils;

import android.os.Handler;

public class VTimer {

    private Handler mHandler;
    private TimerExecutor mExecutor;
    private long mInterval;
    private Boolean mRunning;

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {
                mExecutor.execute(VTimer.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mHandler != null) {
                mHandler.postDelayed(this, mInterval);
            }
        }
    };

    public VTimer(TimerExecutor executor, long interval) {
        this.mHandler = new Handler();
        this.mExecutor = executor;
        this.mInterval = interval;
        this.mRunning = false;
    }

    public VTimer(Handler h, TimerExecutor executor, long interval) {
        this.mHandler = h;
        this.mExecutor = executor;
        this.mInterval = interval;
        this.mRunning = false;
    }

    public void start() {
        if (!mRunning) {
            mHandler.postDelayed(mUpdateTimeTask, mInterval);
            mRunning = true;
        }
    }

    public void stop() {
        mRunning = false;
        if (mHandler != null && mUpdateTimeTask != null)
            mHandler.removeCallbacks(mUpdateTimeTask);
    }

    public void dispose() {
        stop();
        mHandler = null;
    }

    public long getInterval() {
        return mInterval;
    }

    public void setInterval(long interval) {
        this.mInterval = interval;
    }

    public interface TimerExecutor {
        public void execute(VTimer vTimer);
    }

}
