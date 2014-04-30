package com.nexdgis.feature.graphic;

import com.nexdgis.geometry.Line;
import com.nexdgis.renderer.LineRenderer;

public class LineSymbol extends Graphic {

	public LineSymbol() {
		super(-1, new LineRenderer(-1), new Line());
	}
	
	public LineSymbol(Line l) {
		super(-1, new LineRenderer(-1), l);
	}
	
	public LineSymbol(LineRenderer lr) {
		super(-1, lr, new Line());
	}
	
	public LineSymbol(LineRenderer lr, Line l) {
		super(-1, lr, l);
	}

	public LineRenderer getLineRenderer() {
		return (LineRenderer) mRenderer;
	}
	
	public Line getLineGeometry() {
		return (Line) mGeometry;
	}
	
}
