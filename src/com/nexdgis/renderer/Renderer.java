package com.nexdgis.renderer;

public abstract class Renderer {
	
	@SuppressWarnings("unused")
	private static final String TAG = "renderer/Renderer";

	public final int type;
	
	
	protected Renderer(int type) {
		this.type = type;
	}

}
