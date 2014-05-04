package com.nexdgis.layer.widget;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.nexdgis.geometry.Point;
import com.nexdgis.log.NexdLog;

public class Callout {
	
	private static final String TAG = "layer/widget/Callout";

	private int width = 250;
	private int height = 150;
	private int padding = 8;
	private int arrowSize = 15;
	private int borderWidth = 2;
	private int borderColor = Color.GRAY;
	private int cornerRadius = 6;

	private final Context mContext;
	private final ViewGroup parent;
	private final FrameLayout calloutContainer;
	private final FrameLayout contentContainer;
	private final View border;
	private View content;
	

	public Callout(ViewGroup parent) {
		this.parent = parent;
		mContext = parent.getContext();
		// initialize callout container
		calloutContainer = new FrameLayout(mContext);
		LayoutParams lp = new LayoutParams(width, height);
		calloutContainer.setLayoutParams(lp);

		// initialize border
		border = new View(mContext) {

			private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			private RectF borderRect = new RectF();
			private Path arrow = new Path();

			@Override
			protected void onDraw(Canvas canvas) {
				paint.setStrokeWidth(borderWidth);
				paint.setStrokeCap(Cap.ROUND);

				borderRect.top = borderRect.left = borderWidth / 2;
				borderRect.right = this.getWidth() - borderWidth / 2;
				borderRect.bottom = this.getHeight() - borderWidth / 2
						- arrowSize;
				paint.setStyle(Style.FILL);
				paint.setColor(Color.WHITE);
				canvas.drawRoundRect(borderRect, cornerRadius,
						cornerRadius, paint);
				paint.setStyle(Style.STROKE);
				paint.setColor(borderColor);
				// paint.setShadowLayer(4, 0, 0, Color.BLACK);
				canvas.drawRoundRect(borderRect, cornerRadius,
						cornerRadius, paint);

				arrow.moveTo(getWidth() / 2, getHeight() - 1);
				arrow.lineTo(getWidth() / 2 + arrowSize / 2, borderRect.bottom
						- borderWidth);
				arrow.lineTo(getWidth() / 2 - arrowSize / 2, borderRect.bottom
						- borderWidth);
				arrow.close();
				paint.setStyle(Style.FILL);
				paint.setColor(Color.WHITE);
				canvas.drawPath(arrow, paint);
				paint.setStyle(Style.STROKE);
				paint.setColor(borderColor);
				canvas.drawLine(getWidth() / 2, getHeight() - 1, getWidth() / 2
						+ arrowSize / 2, borderRect.bottom, paint);
				canvas.drawLine(getWidth() / 2, getHeight() - 1, getWidth() / 2
						- arrowSize / 2, borderRect.bottom, paint);
			}
		};
		// border.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		calloutContainer.addView(border);

		// initialize content container
		contentContainer = new FrameLayout(mContext);
		int w = width - 2 * padding;
		int h = height - 2 * padding - arrowSize;
		lp = new LayoutParams(w, h);
		lp.setMargins(padding, padding, padding, padding + arrowSize);
		contentContainer.setLayoutParams(lp);
		calloutContainer.addView(contentContainer);

		// add a view by default
		content = new View(mContext);
		content.setBackgroundColor(Color.CYAN);
		contentContainer.addView(content);
	}

	public void setContent(View view) {
		content = view;
		contentContainer.removeAllViews();
		if (content != null) {
			contentContainer.addView(content);
		}
	}

	public void setContent(int layoutResID) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(layoutResID, null);
		setContent(view);
	}

	public View getContent() {
		return content;
	}

	public View getCalloutView() {
		return calloutContainer;
	}
	
	public void setStyle(int width, int height, int padding, int arrowSize,
			int borderWidth, int borderColor, int cornerRadius) {
		this.setWidth(width);
		this.setHeight(height);
		this.setPadding(padding);
		this.setArrowSize(arrowSize);
		this.setBorderWidth(borderWidth);
		this.setBorderColor(borderColor);
		this.setCornerRadius(cornerRadius);
	}

	public void setStyle(int resID) {
		XmlPullParser parser = mContext.getResources().getXml(resID);

		try {
			boolean isFound = false;
			int eventType = parser.getEventType();
			while (!isFound && eventType != XmlPullParser.END_DOCUMENT) {  
				switch (eventType) {  
				case XmlPullParser.START_DOCUMENT:  
					break;  
				case XmlPullParser.START_TAG:  
					if (parser.getName().equals("callout_style")) {
						for (int i = 0; i < parser.getAttributeCount(); i++) {
							if (parser.getAttributeName(i).equals("width")) {
								this.setWidth(Integer.parseInt(parser.getAttributeValue(i)));
							} else if (parser.getAttributeName(i).equals("height")) {
								this.setHeight(Integer.parseInt(parser.getAttributeValue(i)));
							} else if (parser.getAttributeName(i).equals("padding")) {
								this.setPadding(Integer.parseInt(parser.getAttributeValue(i)));
							} else if (parser.getAttributeName(i).equals("arrow_size")) {
								this.setArrowSize(Integer.parseInt(parser.getAttributeValue(i)));
							} else if (parser.getAttributeName(i).equals("border_width")) {
								this.setBorderWidth(Integer.parseInt(parser.getAttributeValue(i)));
							} else if (parser.getAttributeName(i).equals("border_color")) {
								this.setBorderColor(Color.parseColor(parser.getAttributeValue(i)));
							} else if (parser.getAttributeName(i).equals("corner_radius")) {
								this.setCornerRadius(Integer.parseInt(parser.getAttributeValue(i)));
							}
						}
						isFound = true;
					}
					break;  
				case XmlPullParser.END_TAG:
				default :
					break;	
				}  
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			NexdLog.tagError(TAG, "getting callout style xml parser event", e);
		} catch (IOException e) {
			NexdLog.tagError(TAG, "getting callout style xml parser event", e);
		}
	}
	
	private Point p;
	
	public void show(Point p, float offset) {
		if (this.parent == null) {
			return ;
		}
		if (calloutContainer.getParent() != null) {
//			hide();
			this.p = p;
			calloutContainer.setX(p.x - width / 2);
			calloutContainer.setY(p.y - height);
			this.parent.invalidate();
			return ;
		}
		parent.addView(calloutContainer);
		this.p = p;
		calloutContainer.setX(p.x - width / 2);
		calloutContainer.setY(p.y - height);
		content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// consumed touch event
			}
			
		});
	}
	
	public void show(float x, float y, float offset) {
		show(new Point(x, y), offset);
	}
	
	public void hide() {
		if (parent != null) {
			parent.removeView(calloutContainer);
//			parent = null;
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		calloutContainer.getLayoutParams().width = width;
		contentContainer.getLayoutParams().width = width - 2 * padding;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		calloutContainer.getLayoutParams().height = height;
		contentContainer.getLayoutParams().height = height - 2 * padding - arrowSize;
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
		LayoutParams lp = (LayoutParams) contentContainer.getLayoutParams();
		lp.width = width - 2 * padding;
		lp.height = height - 2 * padding - arrowSize;
		lp.setMargins(padding, padding, padding, padding + arrowSize);
	}

	public int getArrowSize() {
		return arrowSize;
	}

	public void setArrowSize(int arrowSize) {
		this.arrowSize = arrowSize;
		border.postInvalidate();
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
		border.postInvalidate();
	}

	public int getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(int borderColor) {
		this.borderColor = borderColor;
		border.postInvalidate();
	}

	public int getCornerRadius() {
		return cornerRadius;
	}

	public void setCornerRadius(int cornerRadius) {
		this.cornerRadius = cornerRadius;
		border.postInvalidate();
	}

	public void move(float endX, float endY) {
		if (this.parent != null) {
		calloutContainer.setX(p.x + endX - width / 2);
		calloutContainer.setY(p.y + endY - height);
		parent.invalidate();
		}
	}

}
