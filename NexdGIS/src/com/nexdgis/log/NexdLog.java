package com.nexdgis.log;

import android.util.Log;

public class NexdLog {

	private static final String TAG_PREFIX = "nexd-";
	private static final String DEBUG_TAG = TAG_PREFIX + "debug";
	private static int verboseLevel = 21;

	private final String mTag;

	public NexdLog(String tag) {
		mTag = TAG_PREFIX + tag;
	}
	
	public void info(String info) {
		if (verboseLevel >= 10) {
			Log.i(mTag, info);
		}
	}

	public void info(byte[] info) {
		if (verboseLevel >= 10) {
			Log.i(mTag, new String(info));
		}
	}

	public void error(String error) {
		if (verboseLevel >= 0) {
			Log.e(mTag, "Error : "+error);
		}
	}

	public void error(String situation, Exception e) {
		if (verboseLevel >= 0) {
			Log.e(mTag, "Error in : "+situation, e);
		}
	}

	/**
	 * just for temporary debugging output
	 * 
	 * @param tag
	 * @param info
	 */
	public static void tempDebug(String info) {
		if (verboseLevel > 20) {
			Log.d(DEBUG_TAG, info);
		}
	}
	
	public static void tagInfo(String tag, String info) {
		if (verboseLevel >= 10) {
			Log.i(TAG_PREFIX + tag, info);
		}
	}

	public static void tagInfo(String tag, byte[] info) {
		if (verboseLevel >= 10) {
			Log.i(TAG_PREFIX + tag, new String(info));
		}
	}

	public static void tagError(String tag, String info) {
		if (verboseLevel > 0) {
			Log.e(TAG_PREFIX + tag, info);
		}
	}

	public static void tagError(String tag, String when, Throwable tr) {
		if (verboseLevel > 0) {
			Log.e(TAG_PREFIX + tag, "Error in " + when + " : " + tr.getMessage());
			tr.printStackTrace();
		}
	}
}
