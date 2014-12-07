package com.vcutils.worker;

import android.os.Handler;

public abstract class BackgroundWork<T, R> implements Runnable {

	private Boolean cancelled;
	private Boolean error;
	private R result;
	private T[] params;

	public BackgroundWork(T... params) {
		this.params = params;
	}

	public void execute(){
		Worker worker = Worker.getInstance();
		worker.executeWork(this);
	}
	
	protected void executeAsync(Handler h) {

		error = false;
		try {
			this.result = doWork(params);
		} catch (Exception e) {
			e.printStackTrace();
			error = true;
		}

		for (int i = 0; i < params.length; i++) {
			params[i] = null;
		}
		params = null;

		if (h == null) {
			run();
		} else {
			h.post(this);
		}
	}

	protected abstract R doWork(T... params);

	protected abstract void onPostWork(R result);

	protected abstract void onPostWorkError(R result);

	@Override
	public void run() {
		try {
			if (error)
				onPostWorkError(this.result);
			else
				onPostWork(this.result);
			this.result = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cancel() {
		this.cancelled = true;
	}

	public Boolean getCancelled() {
		return cancelled;
	}

}
