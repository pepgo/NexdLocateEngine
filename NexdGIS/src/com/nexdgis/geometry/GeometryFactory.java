package com.nexdgis.geometry;

import com.nexdgis.geometry.Geometry.Type;

public final class GeometryFactory {

	public static Geometry createGeometry(Type type) {
		switch (type) {
		case POLYGON :
			return new Polygon();
		case LINE :
			return new Line();
		case POINT :
			return new Point();
		default :
			return null;
		}
	}
}
