package com.nexdgis.renderer;

import android.graphics.Color;

public class LineRenderer extends Renderer {
	
	private int lineColor;
	private int lineWidth;
	private int lineType;
	
	private boolean textable;
	private String textFont;
	private int textOffset;
	private int textSize;
	private int textColor;
	private int textBgColor;
	private boolean textBgable;

	
	public LineRenderer(int type) {
		super(type);
		lineColor =Color.BLACK;
		lineWidth = 2;
	}

	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	public int getLineType() {
		return lineType;
	}

	public void setLineType(int lineType) {
		this.lineType = lineType;
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
	
}
