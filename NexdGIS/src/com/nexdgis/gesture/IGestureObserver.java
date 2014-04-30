package com.nexdgis.gesture;

import com.nexdgis.layer.Layer;

public interface IGestureObserver {

	void clickToUpdate(Layer layer);
	void dragToUpdate(Layer layer);
	void zoomToUpdate(Layer layer);
}
