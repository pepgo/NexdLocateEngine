package com.nexdgis.feature;

import com.nexdgis.geometry.Geometry;
import com.nexdgis.renderer.Renderer;

public abstract class Feature {

	@SuppressWarnings("unused")
	private static final String TAG = "feature/Feature";
	
	public final long id;
	protected Renderer mRenderer;
	protected Geometry mGeometry;
	
	protected Feature(long id, Renderer r, Geometry g) {
		this.id = id;
		this.mRenderer = r;
		this.mGeometry = g;
	}
	
	public Renderer getRenderer() {
		return mRenderer;
	}
	
	public Geometry getGeometry() {
		return mGeometry;
	}
}
