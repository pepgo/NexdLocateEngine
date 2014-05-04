package com.nexdgis.layer.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Handler;

import com.nexdgis.feature.Feature;
import com.nexdgis.feature.MapFeature;
import com.nexdgis.feature.MapFeatureTable;
import com.nexdgis.geometry.Point;
import com.nexdgis.geometry.Polygon;
import com.nexdgis.log.NexdLog;
import com.nexdgis.renderer.PolygonRenderer;
import com.nexdgis.renderer.Renderer;

public class NexdGLRenderer implements GLSurfaceView.Renderer{

	/** just for debugging */
	private static final String TAG = "layer/opengl/NexdGLRenderer";

	static private int[] TextureString;
	static public boolean initialized = false;
	static int featurenum = 0;
	static Bitmap[] bitmap; 
	static Paint p; 
	static Canvas[] canvas;
	static float[][] param;


	static {
		System.loadLibrary("Map");
	}
	private Handler parent;

	private native void nativeInit();
	private native void nativeRender();
	private native void nativeResize(int w, int h);
	private native void nativeAddPolygon(Polygon polygon,Renderer renderer,Point center);
	private native void Initialize();
	private native void Restart();
	private native void RenderwithoutText();
	private native void DrawText(int texture,int xsize,int ysize, float textureminx,float textureminy,float texturemaxx,float texturemaxy,int polyID);
	private native void DrawText1(int texture,int polyID);
	
	native void startDrag(float x, float y);
	native void startZoom(float x, float y);
	native void clearStatus(float scale);
	//click:if return -1 no polygon chosen
	native int doClick(float x, float y);
	native void moveDrag(float x, float y);
	native void moveZoom(float scale,float totalScale);
	//[output]get view Rectangle top_left is the topleft corner under map coor(the view coor is0,0)
  	//[output]get view Rectangle top_left is the bottomright corner under map coor(the view coor is w,h(real screen size))
  	native void getViewRect(Point top_left,Point bottom_right);
  	//getPoint view
  	//[input]mapPoint is the point under map coor
  	//[output]viewPoint is the mapPoint under view coor region is (0,0)~(w,h)
  	//x<0 or x>w or y<0 or y>h means out of sight
  	native void getPointView(Point mapPoint, Point viewPoint);
  	native void getPointMap(Point viewPoint, Point mapPoint);
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		NexdLog.tagInfo(TAG, "onSurfaceCreated");

		if (!initialized)
		{
			Initialize();
			MapFeatureTable fTable = MapFeatureTable.getTableInstance();
			Feature feature;
			featurenum = fTable.getMapFeatureNum();
			NexdLog.tagInfo(TAG, "onSurfaceCreated "+fTable.getMapFeatureNum());
			for (int i = 0; i < fTable.getMapFeatureNum(); i++) {
				feature = fTable.findMapFeatureByIndex(i);
				NexdLog.tempDebug("center "+i+" "+((MapFeature)feature).getCenter().y+" "+((MapFeature)feature).getCenter().x);
				nativeAddPolygon((Polygon)feature.getGeometry(), feature.getRenderer(),((MapFeature)feature).getCenter());
			}
		}
		//		canvas.drawBitmap(bitmap,0,0,p);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		NexdLog.tagInfo(TAG, "onSurfaceChanged");		

		nativeResize(w, h);
		if (!initialized)
		{
			TextureString = new int[featurenum];
			gl.glGenTextures(featurenum, TextureString, 0);  
			NexdLog.tempDebug("shit");
			bitmap = new Bitmap[featurenum];
			canvas = new Canvas[featurenum];
			p= new Paint(Paint.ANTI_ALIAS_FLAG);  
			String familyName = "宋体";  
			Typeface font = Typeface.create(familyName, Typeface.NORMAL);  
			p.setColor(Color.BLACK);  
			p.setTypeface(font);  
			p.setTextSize(30); 
			p.setTextAlign(Paint.Align.CENTER);
			FontMetricsInt fontMetrics = p.getFontMetricsInt();
			param = new float[featurenum][6];
			String s = "NexdGIS";
			MapFeatureTable mfTable = MapFeatureTable.getTableInstance();
			for (int i = 0;i<featurenum;i++)
			{
				NexdLog.tempDebug("center "+i+" "+featurenum);		
//				s = String.valueOf(i);
				MapFeature mf = mfTable.findMapFeatureByIndex(i);
				s = mf.getName();
				p.setTypeface(Typeface.create(((PolygonRenderer)mf.getRenderer()).getTextFont(), Typeface.NORMAL));
				p.setColor(((PolygonRenderer)mf.getRenderer()).getTextColor());
				p.setTextSize(((PolygonRenderer)mf.getRenderer()).getTextSize());

				float textwidth = p.measureText(s);
//				int bitmapYSize = 256;	
				int bitmapYSize = 64;	
				while (textwidth>=bitmapYSize)
					bitmapYSize *=2;
				int bitmapXSize = bitmapYSize;
				int baseline = (bitmapXSize - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
				bitmap[i] = Bitmap.createBitmap(bitmapXSize, bitmapYSize, Bitmap.Config.ARGB_8888);
				canvas[i] = new Canvas(bitmap[i]);
				canvas[i].drawColor(Color.TRANSPARENT); 
				canvas[i].drawText(s, bitmapXSize/2, baseline, p);
				gl.glBindTexture(GL10.GL_TEXTURE_2D, TextureString[i]);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR);
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap[i], 0);
				//	  gl.glBindTexture(GL10.GL_TEXTURE_2D, TextureString[1]);
				DrawText(TextureString[i],bitmapXSize,bitmapYSize,(bitmapYSize-textwidth)/2/(float)bitmapYSize,(baseline+fontMetrics.top)/(float)bitmapXSize,(bitmapYSize+textwidth)/2/(float)bitmapYSize,(baseline+fontMetrics.bottom)/(float)bitmapXSize,i);  

				param[i][0] = bitmapXSize;
				param[i][1] = bitmapYSize;
				param[i][2] = (bitmapYSize-textwidth)/2/(float)bitmapYSize;
				param[i][3] = (baseline+fontMetrics.top)/(float)bitmapXSize;
				param[i][4] = (bitmapYSize+textwidth)/2/(float)bitmapYSize;
				param[i][5] = (baseline+fontMetrics.bottom)/(float)bitmapXSize;
			}
			nativeInit();
//			initialized = true;
		}
		else 
		{	
			Restart();  
			for (int i = 0;i<featurenum;i++)
			{
				//  	gl.glGenTextures(1, TextureString, 0);  
				gl.glBindTexture(GL10.GL_TEXTURE_2D, TextureString[i]);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR);
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap[i], 0);
				//	    	gl.glBindTexture(GL10.GL_TEXTURE_2D, TextureString[0]);
				//  	RenderwithoutText();
				//	    	DrawText1(TextureString[i],i);  
			}
		}
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		nativeRender();
		if (!initialized) {
			initialized = true;
			NexdLog.tagInfo(TAG, "onSurfaceDraw");
			parent.sendEmptyMessage(0);
		}
	}
	public void setParent(Handler handler) {
		this.parent = handler;
	}
}

