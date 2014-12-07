package com.vcutils;

import android.util.Log;

public class WLog {

	public WLog() {
	
	}
	
	public static void logException(String tag, String text, Throwable t){
		Log.e(tag, text, t);
		
	}
	
	public static void logInfo(String tag, String text){
		Log.i(tag, text);
		
	}
	
	public static void logDebug(String tag, String text){
		Log.d(tag, text);
		
	}
	
	
	
}
