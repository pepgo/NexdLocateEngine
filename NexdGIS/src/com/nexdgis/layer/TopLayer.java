package com.nexdgis.layer;

import android.content.Context;
import android.graphics.PointF;
import android.widget.FrameLayout;

import com.nexdgis.geometry.Point;
import com.nexdgis.gesture.IGestureObserver;
import com.nexdgis.layer.widget.Callout;
import com.nexdgis.layer.widget.Locator;

public class TopLayer extends Layer {

	@SuppressWarnings("unused")
	private static final String TAG = "layer/TopLayer";

	private final FrameLayout frameLayout;
	private Callout mCallout;
	private Locator mLocator;


	public TopLayer(Context context) {
		super(new FrameLayout(context));
		frameLayout = (FrameLayout)mContentView;
	}

	public void enableCallout() {
		if (mCallout == null) {
			mCallout = new Callout(frameLayout);
		}
	}
	
	public void disableCallout() {
		mCallout = null;
	}
	
	public void enableLocator() {
		if (mLocator == null) {
			mLocator = new Locator(frameLayout);
		}
	}
	
	public void disableLocator() {
		mLocator = null;
	}
	
	public Callout getCallout() {
		return mCallout;
	}

	public void showCallout(Point p) {
		if (mCallout != null) {
			mCallout.show(p, 0);
		}
	}

	public void hideCallout() {
		if (mCallout != null) {
			mCallout.hide();
		}
	}

	public void showLocator(Point p) {
		if (mLocator != null) {
			mLocator.show(p, 0);
		}
	}

	public void hideLocator() {
		if (mLocator != null) {
			mLocator.hide();
		}
	}

	@Override
	public void doClick(float x, float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doDrag(float endX, float endY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doZoom(float scale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStartPoint(PointF p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPivot(PointF p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTotalScale(float totalScale) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerObserver(IGestureObserver observer) {
		// TODO Auto-generated method stub
		
	}

}
