package com.nexdgis.renderer;

import android.util.SparseArray;

public class RendererTable {
	
	@SuppressWarnings("unused")
	private static final String TAG = "renderer/RendererTable";
	
	private static RendererTable instance;
	
	private SparseArray<Renderer> table;

	
	private RendererTable() {
		table = new SparseArray<Renderer>();
	}
	
	public static RendererTable getTableInstance() {
		if (instance == null) {
			instance = new RendererTable();
		}
		return instance;
	}
	
	public int getRendererNum() {
		return table.size();
	}
	
	public Renderer findRendererByIndex(int index) {
		if (index < 0 || index >= table.size()) {
			return null;
		}
		return table.valueAt(index);
	}

	public Renderer findRendererByType(int type) {
		return table.get(type);
	}
	
	public void addRenderer(Renderer renderer) {
		table.put(renderer.type, renderer);
	}
	
	public static void releaseTable() {
		if (instance != null) {
			instance.table.clear();
		}
	}
}
