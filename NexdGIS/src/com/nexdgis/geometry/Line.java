package com.nexdgis.geometry;

public class Line extends Geometry {

	private Point startPoint;
	private Point endPoint;
	
	
	public Line() {
		this(new Point(0, 0), new Point(10, 10));
	}
	
	public Line(float startX, float startY, float endX, float endY) {
		this(new Point(startX, startY), new Point(endX, endY));
	}

	public Line(Point start, Point end) {
		startPoint = start;
		endPoint = end;
	}
	
	public float getLength() {
		float x = startPoint.x - endPoint.x;
		float y = startPoint.y - endPoint.y;
		return (float) Math.sqrt(x * x + y * y);
	}

	public Point getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Point startPoint) {
		this.startPoint = startPoint;
	}

	public Point getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}
}
