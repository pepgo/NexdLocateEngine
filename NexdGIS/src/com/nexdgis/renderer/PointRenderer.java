package com.nexdgis.renderer;

import android.graphics.Color;

public class PointRenderer extends Renderer {
	
	private int pointColor;
	private int pointRadius;
	
	private boolean textable;
	private String textFont;
	private int textOffset;
	private int textSize;
	private int textColor;
	private int textBgColor;
	private boolean textBgable;
	
	private boolean isImage;
	private String imageURL;
//	private Frame imageFrame;

	
	public PointRenderer(int type) {
		super(type);
		pointColor = Color.BLACK;
		pointRadius = 6;
	}

	public int getPointColor() {
		return pointColor;
	}

	public void setPointColor(int pointColor) {
		this.pointColor = pointColor;
	}

	public int getPointRadius() {
		return pointRadius;
	}

	public void setPointRadius(int pointRadius) {
		this.pointRadius = pointRadius;
	}

	public boolean isTextable() {
		return textable;
	}

	public void setTextable(boolean textable) {
		this.textable = textable;
	}

	public String getTextFont() {
		return textFont;
	}

	public void setTextFont(String textFont) {
		this.textFont = textFont;
	}

	public int getTextOffset() {
		return textOffset;
	}

	public void setTextOffset(int textOffset) {
		this.textOffset = textOffset;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getTextBgColor() {
		return textBgColor;
	}

	public void setTextBgColor(int textBgColor) {
		this.textBgColor = textBgColor;
	}

	public boolean isTextBgable() {
		return textBgable;
	}

	public void setTextBgable(boolean textBgable) {
		this.textBgable = textBgable;
	}

	public boolean isImage() {
		return isImage;
	}

	public void setImage(boolean isImage) {
		this.isImage = isImage;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

}
