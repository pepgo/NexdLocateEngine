package com.nexdgis.feature.graphic;

import com.nexdgis.geometry.Polygon;
import com.nexdgis.renderer.PolygonRenderer;

public class FillSymbol extends Graphic {

	public FillSymbol() {
		super(-1, new PolygonRenderer(-1), new Polygon());
	}
	
	public FillSymbol(Polygon p) {
		super(-1, new PolygonRenderer(-1), p);
	}
	
	public FillSymbol(PolygonRenderer pr) {
		super(-1, pr, new Polygon());
	}
	
	public FillSymbol(PolygonRenderer pr, Polygon p) {
		super(-1, pr, p);
	}

	public PolygonRenderer getFillRenderer() {
		return (PolygonRenderer) mRenderer;
	}
	
	public Polygon getFillGeometry() {
		return (Polygon) mGeometry;
	}

}
