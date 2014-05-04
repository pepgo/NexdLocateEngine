package com.nexdgis.feature;

import android.support.v4.util.LongSparseArray;


public class MapFeatureTable {
	
	@SuppressWarnings("unused")
	private static final String TAG = "feature/FeatureTable";

	private static MapFeatureTable instance;
	
	private LongSparseArray<MapFeature> table;
	
	
	private MapFeatureTable() {
		table = new LongSparseArray<MapFeature>();
	}
	
	public static MapFeatureTable getTableInstance() {
		if (instance == null) {
			instance = new MapFeatureTable();
		}
		return instance;
	}
	
	public int getMapFeatureNum() {
		return table.size();
	}

	public MapFeature findMapFeatureByIndex(int index) {
		if (index < 0 || index >= table.size()) {
			return null;
		}
		return table.valueAt(index);
	}

	public MapFeature findMapFeatureById(long id) {
		return table.get(id);
	}
	
	public void addMapFeature(MapFeature feature) {
		table.put(feature.id, feature);
	}
	
	public static void releaseTable() {
		if (instance != null) {
			instance.table.clear();
		}
	}
}
