package com.nexdgis.layer.opengl;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Handler;

import com.nexdgis.feature.MapFeature;
import com.nexdgis.feature.MapFeatureTable;
import com.nexdgis.geometry.Point;

public class NexdGLSurfaceView extends GLSurfaceView {

    private NexdGLRenderer mRenderer; 
    private PointF gestureStartPoint;
    private float mTotalScale = 1;

    public NexdGLSurfaceView(Context context) {
        super(context);
        setEGLConfigChooser(new EGLConfigChooser() {
        	
        	@Override
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				int[] attrList = new int[] {
			            EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT,
			                    EGL10.EGL_RED_SIZE, 8,
			                    EGL10.EGL_GREEN_SIZE, 8,
			                    EGL10.EGL_BLUE_SIZE, 8,
			                    EGL10.EGL_DEPTH_SIZE, 16,
			                    EGL10.EGL_SAMPLE_BUFFERS, 1,
			                    EGL10.EGL_SAMPLES, 2,
			                    EGL10.EGL_NONE
			            };
				EGLConfig[] configOut = new EGLConfig[1];
		        int[] configNumOut = new int[1];
				egl.eglChooseConfig(display, attrList, configOut, 1, configNumOut);
				return configOut[0];
        	}
        	
		});
        mRenderer = new NexdGLRenderer();
        setRenderer(mRenderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

	public MapFeature doClick(float x, float y) {
		MapFeatureTable fTable = MapFeatureTable.getTableInstance();
		int fIndex = fTable.getMapFeatureNum() - 1 - mRenderer.doClick(x, y);
		requestRender();
		return fTable.findMapFeatureByIndex(fIndex);
	}

	public void doDrag(float endX, float endY) {
		mRenderer.moveDrag(endX-gestureStartPoint.x, endY-gestureStartPoint.y);
		requestRender();
	}

	public void doZoom(float scale) {
		mRenderer.moveZoom(scale, mTotalScale);
		requestRender();
	}

	public void setStartPoint(PointF p) {
		mRenderer.startDrag(p.x, p.y);
		gestureStartPoint = p;
	}

	public void setPivot(PointF p) {
		mRenderer.startZoom(p.x, p.y);
	}

	public void setTotalScale(float totalScale) {
		mRenderer.clearStatus(totalScale);
		requestRender();
		mTotalScale = totalScale;
	}

	public Point mapPoint2ScreenPoint(Point p) {
		Point sp = new Point();
		mRenderer.getPointView(p, sp);
		sp.y = this.getHeight() - sp.y;
		return sp;
	}
	public Point screenPoint2MapPoint(Point p) {
		Point tmpP = new Point();
		tmpP.set(p.x,p.y);
		tmpP.y = this.getHeight() - tmpP.y;
		Point sp = new Point();
		mRenderer.getPointMap(tmpP, sp);
		return sp;
	}

	public void setParent(Handler handler) {
		mRenderer.setParent(handler);
	}

    
    
}
