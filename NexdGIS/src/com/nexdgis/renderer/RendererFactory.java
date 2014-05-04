package com.nexdgis.renderer;

import com.nexdgis.geometry.Geometry.Type;

public final class RendererFactory {

	public static Renderer createRenderer(int rendererType, Type geometryType) {
		switch (geometryType) {
		case POLYGON :
			return new PolygonRenderer(rendererType);
		case LINE :
			return new LineRenderer(rendererType);
		case POINT :
			return new PointRenderer(rendererType);
		default :
			return null;
		}
	}

	
}
