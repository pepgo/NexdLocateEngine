package com.nexdgis.feature;

import com.nexdgis.geometry.Geometry;
import com.nexdgis.geometry.Point;
import com.nexdgis.geometry.Polygon;
import com.nexdgis.renderer.Renderer;

public class MapFeature extends Feature {

	private String name;
	private Point center;
	private MapFeatureInfo info;

	public MapFeature(long id, Renderer renderer, Geometry geometry, String name) {
		super(id, renderer, geometry);
		this.name = name;
	}

	public void addPoint(float x, float y) {
		((Polygon)mGeometry).addPoint(x, y);
	}

	public void addPoint(Point point) {
		((Polygon)mGeometry).addPoint(point);
	}

	public void setCenter(float x, float y) {
		center = new Point(x, y);
	}

	public void setCenter(Point point) {
		center = point;
	}

	public void setMapFeatureInfo(String url, String text) {
		info = new MapFeatureInfo(url, text);
	}

	public void setMapFeatureInfo(MapFeatureInfo info) {
		this.info = info;
	}
	
	public String getName() {
		return name;
	}

	public Point getCenter() {
		return center;
	}

	public MapFeatureInfo getInfo() {
		return info;
	}

	@Override
	public String toString() {
		String str =  "id : " + id
				+ ", name : " + name
				+ "\n";
		str += mGeometry.toString();
		str += center.toString() + "\n";
		str += info.toString() + "\n";
		return str;
	}

	public class MapFeatureInfo {
		public final String url;
		public final String text;

		public MapFeatureInfo(String url, String text) {
			this.url = url;
			this.text = text;
		}

		@Override
		public String toString() {
			return "url : "+url+", text : "+text;
		}
	}
}
