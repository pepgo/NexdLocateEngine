package com.nexdgis.feature.graphic;

import com.nexdgis.geometry.Point;
import com.nexdgis.renderer.PointRenderer;

public class MarkerSymbol extends Graphic {

	public MarkerSymbol() {
		super(-1, new PointRenderer(-1), new Point());
	}
	
	public MarkerSymbol(Point p) {
		super(-1, new PointRenderer(-1), p);
	}
	
	public MarkerSymbol(PointRenderer pr) {
		super(-1, pr, new Point());
	}
	
	public MarkerSymbol(PointRenderer pr, Point p) {
		super(-1, pr, p);
	}

	public PointRenderer getMarkerRenderer() {
		return (PointRenderer) mRenderer;
	}
	
	public Point getMarkerGeometry() {
		return (Point) mGeometry;
	}
	
}
