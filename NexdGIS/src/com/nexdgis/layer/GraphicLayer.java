package com.nexdgis.layer;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.PointF;

import com.nexdgis.feature.graphic.Graphic;
import com.nexdgis.geometry.Line;
import com.nexdgis.geometry.Point;
import com.nexdgis.geometry.Polygon;
import com.nexdgis.gesture.IGestureObserver;
import com.nexdgis.renderer.LineRenderer;
import com.nexdgis.renderer.PointRenderer;
import com.nexdgis.renderer.PolygonRenderer;
import com.nexdgis.renderer.Renderer;

public class GraphicLayer extends Layer {

	static {
		System.loadLibrary("Map");
	}

	native void customLayerStart();
	native void customLayerEnd();
	native void customLayerAddPolygon(Polygon polygon,Renderer renderer);
	native void customLayerAddLine(Point startPoint,Point endPoint,Renderer renderer);
	native void customLayerAddPoint(Point point,Renderer renderer);
	private ArrayList<Graphic> graphicList;
	

	public GraphicLayer(Context context) {
		super(null);
		graphicList = new ArrayList<Graphic>();
	}
	
	public void update() {
		// native update
		customLayerStart();
		for (Graphic g:graphicList)
		{
			if (g.getGeometry() instanceof Polygon)
			{
				customLayerAddPolygon((Polygon)g.getGeometry(),(PolygonRenderer)g.getRenderer());				
			}
			else if (g.getGeometry() instanceof Line)
			{
				customLayerAddLine(((Line)g.getGeometry()).getStartPoint(),((Line)g.getGeometry()).getEndPoint(),(LineRenderer)g.getRenderer());	
			}
			else if (g.getGeometry() instanceof Point)
			{
				customLayerAddPoint((Point)g.getGeometry(),(PointRenderer)g.getRenderer());					
			}
		}
		customLayerEnd();
	}
	
	public void addGraphic(Graphic graphic) {
		graphicList.add(graphic);
	}

	public void addGraphic(int index, Graphic graphic) {
		graphicList.add(index, graphic);
	}
	
	public void removeGraphic(Graphic graphic) {
		graphicList.remove(graphic);
	}
	
	public void removeGraphic(int index) {
		if (index >= 0 && index < graphicList.size()) {
			graphicList.remove(index);
		}
	}
	
	public void removeAllGraphics() {
		graphicList.clear();
	}
	
	public Graphic getGraphic(int index) {
		if (index >= 0 && index < graphicList.size()) {
			return graphicList.get(index);
		}
		return null;
	}
	
	@Override
	public void registerObserver(IGestureObserver observer) {
		// TODO Auto-generated method stub

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

}
