package com.nexdgis.layer;

import com.nexdgis.gesture.IGestureActor;

import android.view.View;

public abstract class Layer implements IGestureActor {

	@SuppressWarnings("unused")
	/** just for debugging */
	private static final String TAG = "layer/NexdLayer";
	
	protected final View mContentView;
	protected boolean mVisible;
	
	
	protected Layer(View view) {
		mContentView = view;
	}
	
	public boolean isVisible() {
		return mVisible;
	}
	
	public void setVisible(boolean visible) {
		mVisible = visible;
		if (mVisible) {
			mContentView.setVisibility(View.VISIBLE);
		} else {
			mContentView.setVisibility(View.INVISIBLE);
		}
		mContentView.invalidate();
	}
	
	public View getContentView() {
		return mContentView;
	}

}
