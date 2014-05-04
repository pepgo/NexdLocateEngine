package com.nexdgis.database;

import android.graphics.Color;

import com.nexdgis.geometry.Geometry;

public final class XmlAdapter {
	
	private XmlAdapter() {}
	
	public static int convertXmlStr2Color(String str) {
		String[] argb = str.split(";");
		int a = Integer.parseInt(argb[3]);
		int r = Integer.parseInt(argb[0]);
		int g = Integer.parseInt(argb[1]);
		int b = Integer.parseInt(argb[2]);
		return Color.argb(a, r, g, b);
	}
	
	public static boolean convertXmlStr2Bool(String str) {
		return str.equals("1");
	}
	
	public static Geometry.Type convertXmlStr2GeometryType(String str) {
		if (str.equals("0") || str.equals("polygon")) {
			return Geometry.Type.POLYGON;
		} else if (str.equals("1") || str.equals("line")) {
			return Geometry.Type.LINE;
		} else if (str.equals("2") || str.equals("point")) {
			return Geometry.Type.POINT;
		} else {
			return null;
		}
	}
}
