package com.nexdgis.gesture;

import android.graphics.PointF;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.nexdgis.log.NexdLog;

public class GestureListener implements OnTouchListener {
	
	@SuppressWarnings("unused")
	private static final String TAG = "gesture/GestureListener";

	private final IGestureActor mActor;
	
	private GestureEvent mode = GestureEvent.NONE;

	// for click check
	private int moveStep;
	private Handler clickHandler = new Handler();
	
	// for drag and zoom
	private PointF firstDownPoint;
	private PointF secondDownPoint;
	
	// for zoom
	private float startFingerDist;
	private float curScale;
	private float totalScale = 1;
    
    
    /**
     * must call in UI thread
     * @param actor
     */
    public GestureListener(IGestureActor actor) {
    	mActor = actor;
    	firstDownPoint = new PointF();
    	secondDownPoint = new PointF();
    	clickHandler = new Handler();
    }
    
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			firstDownPoint.set(event.getX(), event.getY());
			mActor.setStartPoint(firstDownPoint);
			mode = GestureEvent.CLICK;
			
			// for determine whether this is a click gesture;
			moveStep = 0;
			clickHandler.postDelayed(new CheckForClick(), 200);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			secondDownPoint.set(event.getX(), event.getY());
			startFingerDist = fingerDistance(event);
			if (startFingerDist > 10f) {
				mActor.setPivot(fingerMidPoint(event));
				mode = GestureEvent.ZOOM;
				clickHandler.removeCallbacksAndMessages(null);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == GestureEvent.CLICK) {
				if (moveStep < 2) {
					moveStep++;
				} else {
					mode = GestureEvent.DRAG;
					clickHandler.removeCallbacksAndMessages(null);
				}
			} else if (mode == GestureEvent.DRAG) {
				mActor.doDrag(event.getX(), event.getY());
			} else if (mode == GestureEvent.ZOOM) {
				float endFingerDist = fingerDistance(event);
				if (endFingerDist > 10f) {
					curScale = endFingerDist / startFingerDist;
					mActor.doZoom(curScale);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mode == GestureEvent.CLICK) {
				clickHandler.removeCallbacksAndMessages(null);
				NexdLog.tempDebug("doClick");
				mActor.doClick(event.getX(),event.getY());
//				MapFeatureTable fTable = MapFeatureTable.getTableInstance();
//				int fIndex = fTable.getFeatureNum() - 1 - doClick(firstDownPoint.x,firstDownPoint.y);
//				NexdLog.tempDebug("click:"+fIndex);
//				view.requestRender();
//				if (fIndex >= 0) {
//					NexdLog.tempDebug("id : "+fTable.findFeatureByIndex(fIndex).id);
//					center = ((MapFeature)(fTable.findFeatureByIndex(fIndex))).getCenter();
//					NexdLog.tempDebug("center : "+center.toString());
//					Point viewPoint = new Point();
//					getPointView(center, viewPoint);
//					NexdLog.tempDebug("view : "+viewPoint.toString());
//					TestActivity.x = viewPoint.x;
//					TestActivity.y = viewPoint.y;
//					((ViewGroup)v).getChildAt(1).invalidate();
//				}
			}
		case MotionEvent.ACTION_POINTER_UP:
			if (mode == GestureEvent.ZOOM) {
				totalScale *= curScale;
				mActor.setTotalScale(totalScale);
			}
			mode = GestureEvent.NONE;
			break;
		default :
			break;
		}
		
		// consume this event
		return true;
	}

	/**
	 * find out the distance between first two touching fingers
	 * 
	 * @param event
	 * @return distance
	 */
	private float fingerDistance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * find out the middle point between first two touching fingers
	 * 
	 * @param event
	 * @return middle point
	 */
	private PointF fingerMidPoint(MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		return new PointF(x/2, y/2);
	}

	private class CheckForClick implements Runnable {

		@Override
		public void run() {
			if (mode == GestureEvent.CLICK) {
				mode = GestureEvent.DRAG;
			}
		}
		
	}
	
}
