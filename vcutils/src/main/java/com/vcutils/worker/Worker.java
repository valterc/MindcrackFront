package com.vcutils.worker;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import com.vcutils.WLog;

import android.os.Handler;

public class Worker implements Runnable {

	
	private static Worker instance;
	
	/**
	 *  Call this before using any BackgroundWork
	 *  (This function must be executed on UI Thread)
	 * 
	 */
	public static void Initialize(){
		if (instance == null)
			instance = new Worker();
	}
	
	public static Boolean isInitialized(){
		return instance != null;
	}
	
	/**
	 *  Creates an instance for a worker. This instance must be managed by the caller.
	 *  (This function must be executed on UI Thread)
	 * 
	 */
	public static Worker createIndependentInstance(){
		return new Worker();
	}
	
	protected static Worker getInstance(){
		if (instance == null)
			throw new RuntimeException("Worker was not intialized! Call Initialize from a UI Thread.");
		return instance;
	}
	
	
	private Handler uiThreadHandler;
	private Thread thread;
	private LinkedList<BackgroundWork<?, ?>> works;
	private LinkedList<BackgroundWork<?, ?>> newWorks;
	private Semaphore semaphore;
	private Boolean exit;
	
	private Worker(){
		this.uiThreadHandler = new Handler();
		this.works = new LinkedList<BackgroundWork<?,?>>();
		this.newWorks = new LinkedList<BackgroundWork<?,?>>();
		this.semaphore = new Semaphore(0);
		this.exit = false;
		
		this.thread = new Thread(this, "Worker - Background worker thread");
		this.thread.start();
	}
	
	
	@Override
	public void run() {
		
		while (true) {
			
			try {
				WLog.logDebug(Worker.class.getSimpleName(), "Waiting for work to do");
				this.semaphore.acquire();
				if (this.exit) {
					WLog.logDebug(Worker.class.getSimpleName(), "Worker exiting");
					return;
				}
				WLog.logDebug(Worker.class.getSimpleName(), "Work is available");
			} catch (InterruptedException e1) {
				continue;
			}
			
			synchronized (newWorks) {
				works.addAll(newWorks);
				newWorks.clear();
			}
			
			BackgroundWork<?, ?> work = works.poll();
			if (work != null){
				WLog.logDebug(Worker.class.getSimpleName(), "Executing work: " + work.toString());
				work.executeAsync(uiThreadHandler);
				WLog.logDebug(Worker.class.getSimpleName(), "Work: " + work.toString() + " Complete");
			}
						
		}
		
	}
	
	
	public void executeWork(BackgroundWork<?, ?> w){
		synchronized (newWorks) {
			newWorks.add(w);
		}
		this.semaphore.release();
	}

	public static void clearAllWorks(){
		if (instance == null)
			return;
		synchronized (instance.newWorks) {
			instance.newWorks.clear();
			instance.works.clear();
		}
		
	}
	
	public static void dispose(){
		if (instance == null)
			return;
		
		synchronized (instance.newWorks) {
			instance.newWorks.clear();
			instance.works.clear();
		}
		
		instance.exit = true;
		instance.semaphore.release();
		instance = null;
	}
	
}

