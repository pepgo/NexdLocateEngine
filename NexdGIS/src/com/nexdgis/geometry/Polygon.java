package com.nexdgis.geometry;

public class Polygon extends Geometry {

	/**
	 * The points buffer capacity.
	 */
	private static final int BUFFER_CAPACITY = 4;

	/**
	 * The number of Polygon vertices.
	 */
	private int pointNum;

	/**
	 * The array of the vertices.
	 */
	private Point[] pointArray;

	/**
	 * Instantiates a new empty polygon.
	 */
	public Polygon() {
		pointArray = new Point[BUFFER_CAPACITY];
	}

	/**
	 * Instantiates a new polygon with the specified number of vertices.
	 *
	 * @param pointArray
	 *            the array of vertices.
	 */
	public Polygon(Point[] pointArray) {

		this.pointNum = pointArray.length;
		this.pointArray = new Point[pointNum];
		System.arraycopy(pointArray, 0, this.pointArray, 0, pointNum);
	}

	/**
	 * Resets the current Polygon to an empty Polygon. More precisely, the
	 * number of Polygon vertices is set to zero, but vertex array
	 * is not affected.
	 */
	public void reset() {
		pointNum = 0;
	}

	/**
	 * Adds the point to the Polygon.
	 *
	 * @param point
	 *            the point of the added vertex.
	 */
	public void addPoint(Point point) {
		if (pointNum == pointArray.length) {
			Point[] tmp;
			
			tmp = new Point[pointNum + BUFFER_CAPACITY];
			System.arraycopy(pointArray, 0, tmp, 0, pointNum);
			pointArray = tmp;
		}

		pointArray[pointNum] = point;
		pointNum++;
	}

	/**
	 * Adds the point to the Polygon.
	 *
	 * @param px
	 *            the X coordinate of the added vertex.
	 * @param py
	 *            the Y coordinate of the added vertex.
	 */
	public void addPoint(float px, float py) {
		addPoint(new Point(px, py));
	}
	
	public int getPointNum() {
		return pointNum;
	}
	
	public Point[] getPointArray() {
		return pointArray;
	}
	
	/**
	 * Translates all vertices of Polygon the specified distances along X, Y
	 * axis.
	 *
	 * @param mx
	 *            the distance to translate horizontally.
	 * @param my
	 *            the distance to translate vertically.
	 */
	public void translate(float mx, float my) {

		for (int i = 0; i < pointNum; i++) {
			pointArray[i].x += mx;
			pointArray[i].y += my;
		}
	}
	
	/**
	 * Checks whether or not the point with specified float coordinates lies
	 * inside the Polygon.
	 *
	 * @param x
	 *            the X coordinate of the point to check.
	 * @param y
	 *            the Y coordinate of the point to check.
	 * @return true, if the point given by the float coordinates lies inside
	 *         the Polygon, false otherwise.
	 */
	public boolean contains(float x, float y) {
		
		return false;
	}

	public String toString() {
		String str = "";
		for (int i = 0; i < pointNum; i++) {
			str += i + " : " + pointArray[i] + "\n";
		}
		return str;
	}
}
