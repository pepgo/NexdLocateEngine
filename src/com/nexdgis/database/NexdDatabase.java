package com.nexdgis.database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;

import com.nexdgis.feature.MapFeature;
import com.nexdgis.feature.MapFeatureTable;
import com.nexdgis.geometry.Geometry;
import com.nexdgis.geometry.GeometryFactory;
import com.nexdgis.log.NexdLog;
import com.nexdgis.renderer.LineRenderer;
import com.nexdgis.renderer.PointRenderer;
import com.nexdgis.renderer.PolygonRenderer;
import com.nexdgis.renderer.Renderer;
import com.nexdgis.renderer.RendererFactory;
import com.nexdgis.renderer.RendererTable;

public class NexdDatabase {

	private static final String TAG = "database/NexdDatabase";

	private static NexdDatabase instance;

	private ArrayList<String> featurePathList;
	private ArrayList<String> rendererPathList;
	private MapFeatureTable featureTable;
	private RendererTable rendererTable;

	private NexdDatabase() {
		featureTable = MapFeatureTable.getTableInstance();
		rendererTable = RendererTable.getTableInstance();
		featurePathList = new ArrayList<String>();
		rendererPathList = new ArrayList<String>();
	}

	public static NexdDatabase getDbInstance() {
		if (instance == null) {
			instance = new NexdDatabase();
		}
		return instance;
	}
	
	public static void releaseDatabase() {
		instance = null;
	}

	public void addFeaturePath(String path, boolean syncData) {
		if (!containsFeaturePath(path)) {
			featurePathList.add(path);
		}
		if (syncData) {
			syncFeature(path);
		}
	}

	public void addRendererPath(String path, boolean syncData) {
		if (!containsRendererPath(path)) {
			rendererPathList.add(path);
		}
		if (syncData) {
			syncRenderer(path);
		}
	}
	
	public void syncFeature(Context context, String fileName) {
		InputStream is = null;
		try {
			is = context.getAssets().open(fileName);
		} catch (FileNotFoundException e) {
			NexdLog.tagError(TAG, "opening feature xml file in assets", e);
		} catch (IOException e) {
			NexdLog.tagError(TAG, "opening feature xml file in assets", e);
		}
		if (is == null) {
			NexdLog.tagError(TAG, "failed to open feature file " + fileName
					+ "in assets.");
			return;
		}
		syncFeature(is);
	}

	public void syncFeature(String path) {
		InputStream is = null;
		try {
			is = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			NexdLog.tagError(TAG, "opening feature xml file by path", e);
		}
		if (is == null) {
			NexdLog.tagError(TAG, "failed to open feature file by path " + path);
			return;
		}
		syncFeature(is);
	}

	private void syncFeature(InputStream is) {
		// create an XmlPullParser instance
		XmlPullParser parser = Xml.newPullParser();

		// set input encoding type, may be changed in the future for robust
		try {
			parser.setInput(is, "UTF-8");
		} catch (XmlPullParserException e) {
			NexdLog.tagError(TAG, "setting feature xml input encoding", e);
		}

		MapFeature curFeature = null;

		try {
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (parser.getName().equals("feature")) {

						long id = 0;
						Geometry geometry = null;
						Renderer renderer = null;
						String name = "";

						for (int i = 0; i < parser.getAttributeCount(); i++) {
							if (parser.getAttributeName(i).equals("id")) {
								id = Long.parseLong(parser.getAttributeValue(i));
							} else if (parser.getAttributeName(i).equals("shape")) {
								geometry = GeometryFactory.createGeometry(XmlAdapter
										.convertXmlStr2GeometryType(parser
												.getAttributeValue(i)));
							} else if (parser.getAttributeName(i).equals("type")) {
								renderer = rendererTable
										.findRendererByType(Integer
												.parseInt(parser
														.getAttributeValue(i)));
							} else if (parser.getAttributeName(i).equals("name")) {
								name = parser.getAttributeValue(i);
							}
						}

						curFeature = new MapFeature(id, renderer, geometry, name);
					}

					if (curFeature != null) {
						if (parser.getName().equals("nd")) {
							float x = Float.parseFloat(parser
									.getAttributeValue(0));
							float y = Float.parseFloat(parser
									.getAttributeValue(1));
							curFeature.addPoint(x, y);
						} else if (parser.getName().equals("center")) {
							float x = Float.parseFloat(parser
									.getAttributeValue(0));
							float y = Float.parseFloat(parser
									.getAttributeValue(1));
							curFeature.setCenter(x, y);
						} else if (parser.getName().equals("info")) {
							String url = parser.getAttributeValue(0);
							String text = parser.getAttributeValue(1);
							curFeature.setMapFeatureInfo(url, text);
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if (parser.getName().equals("feature")) {
						if (curFeature != null) {
							featureTable.addMapFeature(curFeature);
							curFeature = null;
						}
					}
					break;
				default:
					break;
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			NexdLog.tagError(TAG, "getting feature xml parser event", e);
		} catch (IOException e) {
			NexdLog.tagError(TAG, "getting feature xml parser event", e);
		}
	}

	public void syncRenderer(Context context, String fileName) {
		InputStream is = null;
		try {
			is = context.getAssets().open(fileName);
		} catch (FileNotFoundException e) {
			NexdLog.tagError(TAG, "opening renderer xml file in assets", e);
		} catch (IOException e) {
			NexdLog.tagError(TAG, "opening renderer xml file in assets", e);
		}
		if (is == null) {
			NexdLog.tagError(TAG, "failed to open renderer file " + fileName
					+ "in assets.");
			return;
		}
		syncRenderer(is);
	}

	public void syncRenderer(String path) {
		InputStream is = null;
		try {
			is = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			NexdLog.tagError(TAG, "opening renderer xml file by path", e);
		}
		if (is == null) {
			NexdLog.tagError(TAG, "failed to open renderer file by path "
					+ path);
			return;
		}
		syncRenderer(is);
	}

	private void syncRenderer(InputStream is) {
		// create an XmlPullParser instance
		XmlPullParser parser = Xml.newPullParser();

		// set input encoding type, may be changed in the future for robust
		try {
			parser.setInput(is, "UTF-8");
		} catch (XmlPullParserException e) {
			NexdLog.tagError(TAG, "setting renderer xml input encoding", e);
		}

		Renderer curRenderer = null;

		try {
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (parser.getName().equals("renderer")) {

						int rendererType = Integer
								.parseInt(parser.getAttributeValue(0));
						Geometry.Type geometryType = XmlAdapter.convertXmlStr2GeometryType(parser
								.getAttributeValue(1));

						curRenderer = RendererFactory.createRenderer(rendererType, geometryType);
					}

					if (curRenderer != null) {
						if (parser.getName().equals("fill")) {
							if (curRenderer instanceof PolygonRenderer) {								
								((PolygonRenderer)curRenderer).setFillable(XmlAdapter
										.convertXmlStr2Bool(parser.getAttributeValue(0)));
								((PolygonRenderer)curRenderer).setFilledColor(XmlAdapter
										.convertXmlStr2Color(parser.getAttributeValue(1)));
								((PolygonRenderer)curRenderer).setSelectedColor(XmlAdapter
										.convertXmlStr2Color(parser.getAttributeValue(2)));
							}
						} else if (parser.getName().equals("stroke")) {
							if (curRenderer instanceof PolygonRenderer) {
								((PolygonRenderer)curRenderer).setStrokeable(XmlAdapter
										.convertXmlStr2Bool(parser.getAttributeValue(0)));
								((PolygonRenderer)curRenderer).setStrokeColor(XmlAdapter
										.convertXmlStr2Color(parser.getAttributeValue(1)));
								((PolygonRenderer)curRenderer).setStrokeWidth(Integer
										.parseInt(parser.getAttributeValue(2)));
								((PolygonRenderer)curRenderer).setStrokeType(Integer
										.parseInt(parser.getAttributeValue(3)));
							} else if (curRenderer instanceof LineRenderer) {
								((LineRenderer)curRenderer).setLineColor(XmlAdapter
										.convertXmlStr2Color(parser.getAttributeValue(1)));
								((LineRenderer)curRenderer).setLineWidth(Integer
										.parseInt(parser.getAttributeValue(2)));
								((LineRenderer)curRenderer).setLineType(Integer
										.parseInt(parser.getAttributeValue(3)));
							}
						} else if (parser.getName().equals("text")) {
							if (curRenderer instanceof PolygonRenderer) {
								((PolygonRenderer)curRenderer).setTextable(XmlAdapter
										.convertXmlStr2Bool(parser.getAttributeValue(0)));
								((PolygonRenderer)curRenderer).setTextFont(parser
										.getAttributeValue(1));
								((PolygonRenderer)curRenderer).setTextColor(XmlAdapter
										.convertXmlStr2Color(parser.getAttributeValue(2)));
								((PolygonRenderer)curRenderer).setTextSize(Integer
										.parseInt(parser.getAttributeValue(3)));
								((PolygonRenderer)curRenderer).setTextBgable(XmlAdapter
										.convertXmlStr2Bool(parser.getAttributeValue(4)));
								((PolygonRenderer)curRenderer).setTextBgColor(XmlAdapter
										.convertXmlStr2Color(parser.getAttributeValue(5)));
							} else if (curRenderer instanceof LineRenderer) {
								((LineRenderer)curRenderer).setTextable(XmlAdapter
										.convertXmlStr2Bool(parser.getAttributeValue(0)));
								((LineRenderer)curRenderer).setTextFont(parser
										.getAttributeValue(1));
								((LineRenderer)curRenderer).setTextColor(XmlAdapter
										.convertXmlStr2Color(parser.getAttributeValue(2)));
								((LineRenderer)curRenderer).setTextSize(Integer
										.parseInt(parser.getAttributeValue(3)));
								((LineRenderer)curRenderer).setTextBgable(XmlAdapter
										.convertXmlStr2Bool(parser.getAttributeValue(4)));
								((LineRenderer)curRenderer).setTextBgColor(XmlAdapter
										.convertXmlStr2Color(parser.getAttributeValue(5)));
							} else if (curRenderer instanceof PointRenderer) {
								((PointRenderer)curRenderer).setTextable(XmlAdapter
										.convertXmlStr2Bool(parser.getAttributeValue(0)));
								((PointRenderer)curRenderer).setTextFont(parser
										.getAttributeValue(1));
								((PointRenderer)curRenderer).setTextColor(XmlAdapter
										.convertXmlStr2Color(parser.getAttributeValue(2)));
								((PointRenderer)curRenderer).setTextSize(Integer
										.parseInt(parser.getAttributeValue(3)));
								((PointRenderer)curRenderer).setTextBgable(XmlAdapter
										.convertXmlStr2Bool(parser.getAttributeValue(4)));
								((PointRenderer)curRenderer).setTextBgColor(XmlAdapter
										.convertXmlStr2Color(parser.getAttributeValue(5)));
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if (parser.getName().equals("renderer")) {
						if (curRenderer != null) {
							rendererTable.addRenderer(curRenderer);
							curRenderer = null;
						}
					}
					break;
				default:
					break;
				}
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			NexdLog.tagError(TAG, "getting renderer xml parser event", e);
		} catch (IOException e) {
			NexdLog.tagError(TAG, "getting renderer xml parser event", e);
		}
	}

	public void syncAllData() {
		syncAllRenderers();
		syncAllFeatures();
	}

	public void syncAllRenderers() {
		for (String rPath : rendererPathList) {
			syncRenderer(rPath);
		}
	}

	public void syncAllFeatures() {
		for (String fPath : featurePathList) {
			syncFeature(fPath);
		}
	}

	private boolean containsFeaturePath(String path) {
		for (String fPath : featurePathList) {
			if (fPath.equals(path)) {
				return true;
			}
		}
		return false;
	}

	private boolean containsRendererPath(String path) {
		for (String rPath : rendererPathList) {
			if (rPath.equals(path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * release using resources
	 */
	public void dispose() {

	}

}
