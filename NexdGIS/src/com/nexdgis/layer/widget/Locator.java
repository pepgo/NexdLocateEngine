package com.nexdgis.layer.widget;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.gy.nexdgis.R;
import com.nexdgis.geometry.Point;

public class Locator {

	private final Context mContext;
	private final ViewGroup parent;
	private final ImageView content;
	private int width = 50;
	private int height = 50;
	
	
	public Locator(ViewGroup parent) {
		this.parent = parent;
		mContext = parent.getContext();
		content = new ImageView(mContext);
		content.setImageResource(R.drawable.ic_launcher);
		content.setLayoutParams(new FrameLayout.LayoutParams(width, height));
	}

	public View getContent() {
		return content;
	}
	
	public void show(Point p, float offset) {
		show(p.x, p.y, offset);
	}
	
	public void show(float x, float y, float offset) {
		if (parent == null) {
			return ;
		}
		if (content.getParent() == null) {
			content.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// consumed touch event
				}
				
			});
			parent.addView(content);
		}
		content.setX(x - width/2 + offset);
		content.setY(y - height/2 + offset);
		parent.invalidate();
		
	}
	
	public void hide() {
		if (parent != null) {
			parent.removeView(content);
		}
	}
}
