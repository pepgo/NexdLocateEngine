package com.nexdgis;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.nexdgis.geometry.Point;
import com.nexdgis.gesture.IGestureObserver;
import com.nexdgis.layer.GraphicLayer;
import com.nexdgis.layer.Layer;
import com.nexdgis.layer.MapFeatureLayer;
import com.nexdgis.layer.TopLayer;
import com.nexdgis.layer.widget.OnFeatureSelectedListener;
import com.nexdgis.log.NexdLog;

public class NexdMap extends FrameLayout {

	private MapFeatureLayer mMapFeatureLayer;
	private TopLayer mTopLayer;
	private final ArrayList<GraphicLayer> mGraphicLayerList = new ArrayList<GraphicLayer>();
	private OnFeatureSelectedListener mFeatureSelectedListener;
	private final NexdMapHandler mHandler = new NexdMapHandler(this);
	private boolean listened = false;

	static {
		System.loadLibrary("Map");
	}

	native void customLayerReset();
	native void customLayerUpdate();


	public NexdMap(Context context) {
		super(context);
		init();
	}

	public NexdMap(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NexdMap(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		setTopLayer(new TopLayer(getContext()));
		setMapFeatureLayer(new MapFeatureLayer(getContext(), true));
		addView(new View(getContext()) {
			Paint paint = new Paint();
			@Override
			protected void onDraw(Canvas canvas) {
				paint.setAntiAlias(true);
				paint.setTextSize(50);
				paint.setColor(Color.BLUE);
				canvas.drawColor(Color.WHITE);
				canvas.drawText("Loading...", getWidth()/2, getHeight()/2, paint);
			}
		});
		mMapFeatureLayer.setLoadingParent(mHandler);
	}

	private void setTopLayer(TopLayer layer) {
		mTopLayer = layer;
		if (mTopLayer != null) {
			addView(mTopLayer.getContentView());
		}
	}
	
	public TopLayer getTopLayer() {
		return mTopLayer;
	}

	private void setMapFeatureLayer(MapFeatureLayer layer) {
		mMapFeatureLayer = layer;
		if (mMapFeatureLayer != null) {
			addView(mMapFeatureLayer.getContentView(), 0);
			mMapFeatureLayer.registerObserver(new IGestureObserver() {

				@Override
				public void clickToUpdate(Layer layer) {
//					resetLocator();
//					updateLocator();
					updateCallout();
					updateSelectedFeature();
				}

				@Override
				public void dragToUpdate(Layer layer) {
					updateLocator();
					updateCallout();
				}

				@Override
				public void zoomToUpdate(Layer layer) {
					updateLocator();
					updateCallout();
				}

			});
		}
	}
	
	public MapFeatureLayer getMapFeatureLayer() {
		return mMapFeatureLayer;
	}

	private void updateCallout() {
		if (mMapFeatureLayer != null) {
			Point p = mMapFeatureLayer.getClickedMapFeatureLocation();
			if (mTopLayer != null) {
				if (p != null) {
					mTopLayer.showCallout(p);
				} else {
					mTopLayer.hideCallout();
				}
			}
		}
	}

//	private void resetLocator() {
//	}
	public void setLocator(float x, float y)
	{
		if (mMapFeatureLayer != null) {
			mMapFeatureLayer.setLocator(x,y);
		}
	}

	public void updateLocator() {
		if (mMapFeatureLayer != null) {
			Point p = mMapFeatureLayer.getLocatorScreenPoint();
			if (mTopLayer != null) {
				if (p != null) {
					NexdLog.tagInfo("MYJNI",p.x+" "+p.y);
					mTopLayer.showLocator(p);
				} else {
					mTopLayer.hideLocator();
				}
			}
		}
	}

	private void updateSelectedFeature() {
		if (mMapFeatureLayer != null) {
			if (mFeatureSelectedListener != null) {
				if (mMapFeatureLayer.getClickedMapFeature() != null) {
					mFeatureSelectedListener.onSelected(mMapFeatureLayer.getClickedMapFeature());
				}
			}
		}
	}

	public void addGraphicLayer(GraphicLayer graphicLayer) {
		mGraphicLayerList.add(graphicLayer);
	}

	public void addGraphicLayer(int index, GraphicLayer graphicLayer) {
		mGraphicLayerList.add(index, graphicLayer);
	}

	public void removeGraphicLayer(GraphicLayer graphicLayer) {
		mGraphicLayerList.remove(graphicLayer);
	}

	public void removeGraphicLayer(int index) {
		if (index >= 0 && index < mGraphicLayerList.size()) {
			mGraphicLayerList.remove(index);
		}
	}

	public void removeAllGraphicLayers() {
		mGraphicLayerList.clear();
	}

	public GraphicLayer getGraphicLayer(int index) {
		if (index >= 0 && index < mGraphicLayerList.size()) {
			return mGraphicLayerList.get(index);
		}
		return null;
	}

	public void updateAllGraphicLayer() {
		customLayerReset();
		for (GraphicLayer gLayer : mGraphicLayerList) {
			gLayer.update();
		}
		customLayerUpdate();
	}

	public void setOnFeatureSelectedListener(OnFeatureSelectedListener listener) {
		mFeatureSelectedListener = listener;
		listened = true;
		if (listener!=null)
		{
			mMapFeatureLayer.enableListeningSelected();
		}
	}

	public void removeOnFeatureSelectedListener() {
		mFeatureSelectedListener = null;
	}

	private static class NexdMapHandler extends Handler {
		
		final WeakReference<NexdMap> nexdMapReference;
		
		NexdMapHandler(NexdMap nexdMap) {
			nexdMapReference = new WeakReference<NexdMap>(nexdMap);
		}
		
		@Override
		public void handleMessage(Message msg) {
			NexdMap nexdMap = nexdMapReference.get();
			if (nexdMap != null) {
				nexdMap.removeViewAt(2);
			}
		}
	}
	
}