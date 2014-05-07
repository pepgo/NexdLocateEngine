package com.nexdgis.layer;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;

import com.nexdgis.feature.MapFeature;
import com.nexdgis.geometry.Point;
import com.nexdgis.gesture.GestureListener;
import com.nexdgis.gesture.IGestureObserver;
import com.nexdgis.layer.opengl.NexdGLSurfaceView;

public class MapFeatureLayer extends Layer {
	
	@SuppressWarnings("unused")
	private static final String TAG = "layer/NexdMapFeatureLayer";

	private final NexdGLSurfaceView glSurfaceView;
	private IGestureObserver gestureObserver;
	private MapFeature clickedMapFeature;
	private Point clickedScreenPoint = new Point();
//	private Point clickedMapPoint;
	private Point locatorPoint = new Point();
	private boolean isListeningSelected = false;

	public MapFeatureLayer(Context context) {
		this(context, true);
	}
	
	public MapFeatureLayer(Context context, boolean enableGesture) {
		super(new NexdGLSurfaceView(context));
		glSurfaceView = (NexdGLSurfaceView) mContentView;
		if (enableGesture) {
			enableGesture();
		}
	}
	
	public MapFeature getClickedMapFeature() {
		return clickedMapFeature;
	}

	public Point getClickedMapFeatureLocation() {
		if (clickedMapFeature == null) {
			return null;
		}
		return glSurfaceView.mapPoint2ScreenPoint(clickedMapFeature.getCenter());
	}

	public void enableGesture() {
		glSurfaceView.setOnTouchListener(new GestureListener(this));
	}
	
	public void disableGesture() {
		glSurfaceView.setOnTouchListener(null);
	}

	public void enableListeningSelected()
	{
		isListeningSelected = true;
	}
	@Override
	public void doClick(float x, float y) {
		if (isListeningSelected)
			clickedMapFeature = glSurfaceView.doClick(x, y);
		clickedScreenPoint.set(x, y);
		// native method 
//		clickedMapPoint = glSurfaceView.screenPoint2MapPoint(clickedScreenPoint);
		if (gestureObserver != null) {
			gestureObserver.clickToUpdate(this);
		}
	}

	@Override
	public void doDrag(float endX, float endY) {
		glSurfaceView.doDrag(endX, endY);
		if (gestureObserver != null) {
			gestureObserver.dragToUpdate(this);
		}
	}

	@Override
	public void doZoom(float scale) {
		glSurfaceView.doZoom(scale);
		if (gestureObserver != null) {
			gestureObserver.zoomToUpdate(this);
		}
	}

	@Override
	public void setStartPoint(PointF p) {
		glSurfaceView.setStartPoint(p);
	}

	@Override
	public void setPivot(PointF p) {
		glSurfaceView.setPivot(p);
	}

	@Override
	public void setTotalScale(float totalScale) {
		glSurfaceView.setTotalScale(totalScale);
	}

	@Override
	public void registerObserver(IGestureObserver observer) {
		gestureObserver = observer;
	}
	public Point getClickedScreenPoint() {
		// return glSurfaceView.mapPoint2ScreenPoint(clickedMapPoint);
		return clickedScreenPoint;
	}
	public void setLocator(float x, float y) {
		locatorPoint = new Point(x,y);
	}
	public Point getLocatorScreenPoint(){
		if (locatorPoint!=null)
		return glSurfaceView.mapPoint2ScreenPoint(locatorPoint);
		else return null;
	}
//	public Point getClickedMapPoint() {
//		// return glSurfaceView.mapPoint2ScreenPoint(clickedMapPoint);
//		return clickedMapPoint;
//	}

	public void setLoadingParent(Handler handler) {
		glSurfaceView.setParent(handler);
	}

}
