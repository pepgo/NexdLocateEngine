package com.nexdgis.renderer;

import android.graphics.Color;

public class PolygonRenderer extends Renderer {
	
	private boolean fillable;
	private int filledColor;
	private int selectedColor;
	
	private boolean strokeable;
	private int strokeWidth;
	private int strokeColor;
	private int strokeType;
	
	private boolean textable;
	private String textFont;
	private int textOffset;
	private int textSize;
	private int textColor;
	private int textBgColor;
	private boolean textBgable;

	
	public PolygonRenderer(int type) {
		super(type);
		fillable = true;
		filledColor = Color.BLACK;
	}

	public boolean isFillable() {
		return fillable;
	}

	public void setFillable(boolean fillable) {
		this.fillable = fillable;
	}

	public int getFilledColor() {
		return filledColor;
	}

	public void setFilledColor(int filledColor) {
		this.filledColor = filledColor;
	}

	public int getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(int selectedColor) {
		this.selectedColor = selectedColor;
	}

	public boolean isStrokeable() {
		return strokeable;
	}

	public void setStrokeable(boolean strokeable) {
		this.strokeable = strokeable;
	}

	public int getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public int getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(int strokeColor) {
		this.strokeColor = strokeColor;
	}

	public int getStrokeType() {
		return strokeType;
	}

	public void setStrokeType(int strokeType) {
		this.strokeType = strokeType;
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

	@Override
	public String toString() {
		String str =  "type : " + type
				+ "\n";
		str += "filled color : "+filledColor+"\n";
		str += "selected color : "+selectedColor+"\n";
		str += "stroke color : "+strokeColor+"\n";
		str += "stroke width : "+strokeWidth+"\n";
		return str;
	}
	
}
