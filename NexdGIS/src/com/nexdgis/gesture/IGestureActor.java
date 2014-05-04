package com.nexdgis.gesture;

import android.graphics.PointF;

public interface IGestureActor {
	
	void registerObserver(IGestureObserver observer);

	void doClick(float x, float y);
	void doDrag(float endX, float endY);
	void doZoom(float scale);
	
	void setStartPoint(PointF p);
	void setPivot(PointF p);
	void setTotalScale(float totalScale);
}
