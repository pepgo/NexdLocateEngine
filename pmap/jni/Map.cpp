#include <jni.h>
#include <GLES/gl.h>
#include "MapDraw.h"
#include <ctime>
#include <android/log.h>
#define LOG_TAG "MYJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
extern "C" {
Map map;
Layers layers;
Color I2C(int c)
{
	int r = (c >> 16) & 0x000000ff;
	int g = (c >> 8) & 0x000000ff;
	int b = c & 0x000000ff;
	int a = (c >> 24) & 0x000000ff;
	return Color(r/255.0, g/255.0, b/255.0, a/255.0);
}
float maxX=-1000,maxY=-1000,minX=1000,minY=1000;
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_nativeAddPolygon (JNIEnv* env, jobject obj, jobject polygon,jobject renderer,jobject center)
{
	jclass polygonClass = (env)->GetObjectClass(polygon);
	jclass rendererClass = (env)->GetObjectClass(renderer);
	jclass centerClass = (env)->GetObjectClass(center);
    if( polygonClass){ 
        jboolean iscopy = JNI_FALSE;
        jmethodID pointArrayMid = (env)->GetMethodID(polygonClass, "getPointArray", "()[Lcom/nexdgis/geometry/Point;");
		jmethodID sizeMid = (env)->GetMethodID(polygonClass, "getPointNum", "()I");
		jobjectArray pointArray = (jobjectArray)(env)->CallObjectMethod(polygon, pointArrayMid);
		int size = (int)(env)->CallIntMethod(polygon, sizeMid);

		Polygon polygonInCpp;
		jobject point;
		jclass pointClass;
		float x0,y0,xn,yn;
		for (int i = size-1; i > 0; i--) {
			point = env->GetObjectArrayElement(pointArray, i);
			pointClass = (env)->GetObjectClass(point);
    		if (pointClass) {
				jfieldID xId = (env)->GetFieldID(pointClass, "x", "F");
				jfieldID yId = (env)->GetFieldID(pointClass, "y", "F");
				float x = (float)(env)->GetFloatField(point, xId);
				float y = (float)(env)->GetFloatField(point, yId);
				if (i == 0)
				{
					x0 = x; y0 = y;
				}
				else if (i == size -1)
				{
					xn = x;yn = y;
				}
				if (i!=0 || fabs(x0-xn)>1e-3||fabs(y0-yn)>1e-3)
					polygonInCpp.addPoint(Point(x, y));
	    		if (x+10>maxX) maxX = x+10;
	    		if (x-10<minX) minX = x-10;
	    		if (y+10>maxY) maxY = y+10;
	    		if (y-10<minY) minY = y-10;
			}
    		env->DeleteLocalRef(point);
    		env->DeleteLocalRef(pointClass);
		}
		/*
		jfieldID fid;
		bool resultb;
		int resulti;
		if (rendererClass)
		{
			fid = (env)->GetFieldID(rendererClass,"fillable","Z");
			resultb = (bool)((env)->GetBooleanField(rendererClass,fid));
			polygonInCpp.setFillable(resultb);
			LOGI("%d",resultb);
			if (bool(resultb))
			{
				fid = (env)->GetFieldID(rendererClass, "selectedColor", "I");
				resulti = (int)((env)->GetIntField(rendererClass,fid));
				LOGI("%d",resulti);
				polygonInCpp.setSelectColor(I2C((int)resulti));
				fid = (env)->GetFieldID(rendererClass, "filledColor", "I");
				resulti = (int)(env)->GetIntField(rendererClass,fid);
				polygonInCpp.setColor(I2C((int)resulti));
				LOGI("%d",resulti);
			}
			fid = (env)->GetFieldID(rendererClass,"strokeable","Z");
			resultb = (bool)((env)->GetBooleanField(rendererClass,fid));
			polygonInCpp.setStrokeable(resultb);
			LOGI("%d",resultb);
			if (bool(resultb))
			{
				fid = (env)->GetFieldID(rendererClass, "strokeWidth", "I");
				resulti = (int)((env)->GetIntField(rendererClass,fid));
				LOGI("%d",resulti);
				polygonInCpp.setLineWidth((int)resulti);
				fid = (env)->GetFieldID(rendererClass, "strokeColor", "I");
				resulti = (int)(env)->GetIntField(rendererClass,fid);
				polygonInCpp.setLineColor(I2C((int)resulti));
				LOGI("%d",resulti);
			}
		}*/
		/*
		jmethodID mid;
		bool resultb;
		int resulti;
		if (rendererClass)
		{
			mid = (env)->GetMethodID(rendererClass, "isFillable", "()Z");
			resultb = (bool)((env)->CallBooleanMethod(rendererClass,mid));
			polygonInCpp.setFillable(resultb);
			LOGI("%d",resultb);
			if (bool(resultb))
			{
				mid = (env)->GetMethodID(rendererClass, "getSelectedColor", "()I");
				resulti = (int)((env)->CallIntMethod(rendererClass,mid));
				LOGI("%d",resulti);
				polygonInCpp.setSelectColor(I2C((int)resulti));
				mid = (env)->GetMethodID(rendererClass, "getFilledColor", "()I");
				resulti = (int)(env)->CallIntMethod(rendererClass,mid);
				polygonInCpp.setColor(I2C((int)resulti));
				LOGI("%d",resulti);
			}
			mid = (env)->GetMethodID(rendererClass, "isStrokeable", "()Z");
			resultb = (bool)(env)->CallBooleanMethod(rendererClass,mid);
			polygonInCpp.setStrokeable(resultb);
			LOGI("%d",resultb);
			if (bool(resultb))
			{
				mid = (env)->GetMethodID(rendererClass, "getStrokeWidth", "()I");
				resulti = (int)(env)->CallIntMethod(rendererClass,mid);
				polygonInCpp.setLineWidth((int)resulti);
				LOGI("%d",resulti);
				mid = (env)->GetMethodID(rendererClass, "getStrokeColor", "()I");
				resulti = (int)(env)->CallIntMethod(rendererClass,mid);
				polygonInCpp.setLineColor(I2C((int)resulti));
				LOGI("%d",resulti);
			}
		}
        LOGI("size = %d", size);
		map.polyList.push_back(polygonInCpp);
		*/
		jmethodID mid;
		jint resulti;
		bool resultb;
		if (rendererClass)
		{
			mid = (env)->GetMethodID(rendererClass, "isFillable", "()Z");
			resultb = (bool)((env)->CallBooleanMethod(renderer, mid));
			polygonInCpp.setFillable(resultb);
			if (resultb)
			{
				mid = (env)->GetMethodID(rendererClass, "getSelectedColor", "()I");
				resulti = ((env)->CallIntMethod(renderer, mid));
				polygonInCpp.setSelectColor(I2C((int)resulti));
				mid = (env)->GetMethodID(rendererClass, "getFilledColor", "()I");
				resulti = (env)->CallIntMethod(renderer, mid);
				polygonInCpp.setColor(I2C((int)resulti));
			}
			mid = (env)->GetMethodID(rendererClass, "isStrokeable", "()Z");
			resultb = (bool)(env)->CallBooleanMethod(renderer, mid);
			polygonInCpp.setStrokeable(resultb);
			if (bool(resultb))
			{
				mid = (env)->GetMethodID(rendererClass, "getStrokeWidth", "()I");
				resulti = (env)->CallIntMethod(renderer, mid);
				polygonInCpp.setLineWidth(resulti);
				mid = (env)->GetMethodID(rendererClass, "getStrokeColor", "()I");
				resulti = (env)->CallIntMethod(renderer, mid);
				polygonInCpp.setLineColor(I2C((int)resulti));
			}
		}
		jfieldID fid;
		float centerx,centery;
		if (centerClass)
		{
			fid = (env)->GetFieldID(centerClass, "x", "F");
			centerx=(jfloat)(env)->GetFloatField(center, fid);
			fid = (env)->GetFieldID(centerClass, "y", "F");
			centery=(jfloat)(env)->GetFloatField(center, fid);
			polygonInCpp.setCenter(Point(centerx,centery));
		}
		map.polyList.push_back(polygonInCpp);
    } 
}
Point mid;
float min_height,min_width;
Point zoomCenter;
Point startPoint;
Point oldMid;
Point oldMin;
const int NONE = 0;
const int DRAG = 1;
const int ZOOM = 2;
float realHeight,realWidth;
int Type = NONE;
float tmpsize = 1;
int allwidth,allheight;
bool recalcText = true;
float scaleNow = 1;
float scaleOld = 1;
bool initialed = false;
int currentLayer = -1;
JNIEXPORT void JNICALL Java_com_nexdgis_layer_GraphicLayer_customLayerStart (JNIEnv* env, jobject obj)
{
	LOGI("layerstart");
	Layer layer;
	layers.addLayer(layer);
	currentLayer = layers.layer.size()-1;
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_GraphicLayer_customLayerEnd (JNIEnv* env, jobject obj)
{
	LOGI("layerend");
	currentLayer = -1;
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_GraphicLayer_customLayerAddPolygon (JNIEnv* env, jobject obj, jobject polygon,jobject renderer)
{
	LOGI("addPolygon");
//	LOGI("xixi");
	jclass polygonClass = (env)->GetObjectClass(polygon);
	jclass rendererClass = (env)->GetObjectClass(renderer);
    if( polygonClass){
        jboolean iscopy = JNI_FALSE;
        jmethodID pointArrayMid = (env)->GetMethodID(polygonClass, "getPointArray", "()[Lcom/nexdgis/geometry/Point;");
		jmethodID sizeMid = (env)->GetMethodID(polygonClass, "getPointNum", "()I");
		jobjectArray pointArray = (jobjectArray)(env)->CallObjectMethod(polygon, pointArrayMid);
		int size = (int)(env)->CallIntMethod(polygon, sizeMid);

		Polygon polygonInCpp;
		jobject point;
		jclass pointClass;
		float x0,y0,xn,yn;
		for (int i = size-1; i > 0; i--) {
			point = env->GetObjectArrayElement(pointArray, i);
			pointClass = (env)->GetObjectClass(point);
    		if (pointClass) {
				jfieldID xId = (env)->GetFieldID(pointClass, "x", "F");
				jfieldID yId = (env)->GetFieldID(pointClass, "y", "F");
				float x = (float)(env)->GetFloatField(point, xId);
				float y = (float)(env)->GetFloatField(point, yId);
				if (i == 0)
				{
					x0 = x; y0 = y;
				}
				else if (i == size -1)
				{
					xn = x;yn = y;
				}
				LOGI("x = %f, y = %f", x, y);
				if (i!=0 || fabs(x0-xn)>1e-3||fabs(y0-yn)>1e-3)
					polygonInCpp.addPoint(Point(x, y));
			}
    		env->DeleteLocalRef(point);
    		env->DeleteLocalRef(pointClass);
		}
		jmethodID mid;
		jint resulti;
		bool resultb;
		if (rendererClass)
		{
			mid = (env)->GetMethodID(rendererClass, "isFillable", "()Z");
			resultb = (bool)((env)->CallBooleanMethod(renderer, mid));
			polygonInCpp.setFillable(resultb);
			if (resultb)
			{
				mid = (env)->GetMethodID(rendererClass, "getSelectedColor", "()I");
				resulti = ((env)->CallIntMethod(renderer, mid));
				polygonInCpp.setSelectColor(I2C((int)resulti));
				mid = (env)->GetMethodID(rendererClass, "getFilledColor", "()I");
				resulti = (env)->CallIntMethod(renderer, mid);
				polygonInCpp.setColor(I2C((int)resulti));
			}
			mid = (env)->GetMethodID(rendererClass, "isStrokeable", "()Z");
			resultb = (bool)(env)->CallBooleanMethod(renderer, mid);
			polygonInCpp.setStrokeable(resultb);
			if (bool(resultb))
			{
				mid = (env)->GetMethodID(rendererClass, "getStrokeWidth", "()I");
				resulti = (env)->CallIntMethod(renderer, mid);
				polygonInCpp.setLineWidth(resulti);
				mid = (env)->GetMethodID(rendererClass, "getStrokeColor", "()I");
				resulti = (env)->CallIntMethod(renderer, mid);
				polygonInCpp.setLineColor(I2C((int)resulti));
			}
		}
		if (currentLayer!=-1)
			layers.layer[currentLayer].addPoly(polygonInCpp);
    }
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_GraphicLayer_customLayerAddLine (JNIEnv* env, jobject obj, jobject startPoint,jobject endPoint,jobject renderer)
{
	LOGI("addline");
//	LOGI("xixi");
	jfieldID fid;
	Point p1,p2;
	jclass pointClass = (env)->GetObjectClass(startPoint);
	fid = (env)->GetFieldID(pointClass, "x", "F");
	p1.x=(jfloat)(env)->GetFloatField(startPoint, fid);
	fid = (env)->GetFieldID(pointClass, "y", "F");
	p1.y=(jfloat)(env)->GetFloatField(startPoint, fid);
	pointClass = (env)->GetObjectClass(endPoint);
	fid = (env)->GetFieldID(pointClass, "x", "F");
	p2.x=(jfloat)(env)->GetFloatField(endPoint, fid);
	fid = (env)->GetFieldID(pointClass, "y", "F");
	p2.y=(jfloat)(env)->GetFloatField(endPoint, fid);
	Line line;
	line.p1 = p1;
	line.p2 = p2;
	jclass rendererClass = (env)->GetObjectClass(renderer);
	jmethodID mid;
	float width;
	if (rendererClass)
	{
		mid = (env)->GetMethodID(rendererClass, "getLineWidth", "()I");
		line.width = (int)((env)->CallIntMethod(renderer, mid));
		mid = (env)->GetMethodID(rendererClass, "getLineColor", "()I");
		line.c = I2C((int)((env)->CallIntMethod(renderer, mid)));
	}
	if (currentLayer!=-1)
		layers.layer[currentLayer].addLine(line);
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_GraphicLayer_customLayerAddPoint (JNIEnv* env, jobject obj, jobject point,jobject renderer)
{
	jfieldID fid;
	RadiusPoint p;
	jclass pointClass = (env)->GetObjectClass(point);
	fid = (env)->GetFieldID(pointClass, "x", "F");
	p.p.x=(jfloat)(env)->GetFloatField(point, fid);
	fid = (env)->GetFieldID(pointClass, "y", "F");
	p.p.y=(jfloat)(env)->GetFloatField(point, fid);
	jclass rendererClass = (env)->GetObjectClass(renderer);
	jmethodID mid;
	float width;
	if (rendererClass)
	{
		mid = (env)->GetMethodID(rendererClass, "getPointRadius", "()I");
		p.radius = (int)((env)->CallIntMethod(renderer, mid));
		mid = (env)->GetMethodID(rendererClass, "getPointColor", "()I");
		p.c = I2C((int)((env)->CallIntMethod(renderer, mid)));
	}
	if (currentLayer!=-1)
		layers.layer[currentLayer].addPoint(p);
}
JNIEXPORT void JNICALL Java_com_nexdgis_NexdMap_customLayerUpdate (JNIEnv* env, jobject obj)
{
	LOGI("update");
	layers.update();
}
JNIEXPORT void JNICALL Java_com_nexdgis_NexdMap_customLayerReset (JNIEnv* env, jobject obj)
{
	LOGI("reset");
	layers.reset();
	currentLayer = -1;
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_startDrag (JNIEnv* env, jobject obj,jfloat x, jfloat y)
{
	startPoint = Point(x,y);
	oldMid.x = mid.x;
	oldMid.y = mid.y;
	Type = DRAG;
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_startZoom (JNIEnv* env, jobject obj,jfloat x, jfloat y)
{
	zoomCenter.x = mid.x - realWidth/2+x/allwidth*realWidth;
	zoomCenter.y = mid.y + realHeight/2-y/allheight*realHeight;
	oldMid = mid;
	Type = ZOOM;
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_endZoom (JNIEnv* env, jobject obj,jfloat x, jfloat y)
{
//	zoomCenter = Point(x,y);
	Type = NONE;
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_endDrag (JNIEnv* env, jobject obj,jfloat x, jfloat y)
{
//	zoomCenter = Point(x,y);
	Type = NONE;
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_clearStatus (JNIEnv* env, jobject obj,jfloat scale)
{
//	zoomCenter = Point(x,y);
	if (Type == ZOOM)
	{
		scaleNow = scale;
		recalcText = true;
	}
	Type = NONE;
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_moveDrag (JNIEnv* env, jobject obj,jfloat x, jfloat y)
{
//	zoomCenter = Point(x,y);
	if (Type == DRAG)
	{
		mid.x = oldMid.x - x/allwidth*realWidth;
		mid.y = oldMid.y + y/allheight*realHeight;
	}
//	Type = NONE;
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_moveZoom (JNIEnv* env, jobject obj,jfloat scale,jfloat totalScale)
{
//	zoomCenter = Point(x,y);
	if (Type == ZOOM)
	{
		min_width = oldMin.x/scale/totalScale;
		min_height = oldMin.y/scale/totalScale;
		mid.x = zoomCenter.x+(oldMid.x-zoomCenter.x)/scale;
		mid.y = zoomCenter.y+(oldMid.y-zoomCenter.y)/scale;
		if (allwidth/min_width <= allheight/min_height)
		{
			realWidth = min_width;
			realHeight = min_height*(GLfloat)allheight/(GLfloat)allwidth;
		}
		else
		{
			realWidth = min_height*(GLfloat)allwidth/(GLfloat)allheight;
			realHeight = min_height;
		}
//		scaleNow = scaleOld*scale;
//		recalcText = true;
	}
//	Type = NONE;
}
JNIEXPORT jint JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_doClick (JNIEnv* env, jobject obj,jfloat x, jfloat y)
{
//	zoomCenter = Point(x,y);
	Point p;
	p.x = mid.x - realWidth/2+x/allwidth*realWidth;
	p.y = mid.y + realHeight/2-y/allheight*realHeight;
	Type = NONE;
	return (jint)map.findSelectedPolygon(p);
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_nativeInit  (JNIEnv* env, jobject obj)
{
	glClearColor(1,1,1, 1.0);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	map.initialPaint();
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_DrawText (JNIEnv* env, jobject obj,jint texture,jint xsize,jint ysize, jfloat textureminx,jfloat textureminy,jfloat texturemaxx,jfloat texturemaxy,jint polyID)
{
	TextPolygon textPoly;
	Polygon *pp = &map.polyList[polyID];
	textPoly.addPoint(Point(-xsize/2*(texturemaxx-textureminx)*realWidth/allwidth+pp->center.x,-ysize/2*(texturemaxy-textureminy)*realHeight/allheight+pp->center.y));
	textPoly.addPoint(Point(xsize/2*(texturemaxx-textureminx)*realWidth/allwidth+pp->center.x,-ysize/2*(texturemaxy-textureminy)*realHeight/allheight+pp->center.y));
	textPoly.addPoint(Point(xsize/2*(texturemaxx-textureminx)*realWidth/allwidth+pp->center.x,ysize/2*(texturemaxy-textureminy)*realHeight/allheight+pp->center.y));
	textPoly.addPoint(Point(-xsize/2*(texturemaxx-textureminx)*realWidth/allwidth+pp->center.x,ysize/2*(texturemaxy-textureminy)*realHeight/allheight+pp->center.y));

	textPoly.visible = true;
	for (int i = 0;i<textPoly.pointList.size();i++)
		if (!pp->insidePoly(textPoly.pointList[i]))
		{
			textPoly.visible = false;
			break;
		}

	textPoly.textPointList.push_back(Point(textureminx,texturemaxy));
	textPoly.textPointList.push_back(Point(texturemaxx,texturemaxy));
	textPoly.textPointList.push_back(Point(texturemaxx,textureminy));
	textPoly.textPointList.push_back(Point(textureminx,textureminy));
	textPoly.center = pp->center;
	textPoly.textures = (GLuint)texture;
	textPoly.polyID = polyID;
//		LOGI("texture:%d",textPoly.textures);
	map.textPolyList.push_back(textPoly);
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_DrawText1 (JNIEnv* env, jobject obj,jint texture,jint polyID)
{
	for (int i = 0;i<map.textPolyList.size();i++)
		if (map.textPolyList[i].polyID == polyID)
		{
			map.textPolyList[i].textures = (GLuint)texture;
			break;
		};
}

JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_nativeResize(JNIEnv* env, jobject obj, jint width, jint height)
{
	glClearColor(1,1,1, 1.0);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	if (!initialed)
	{
		mid = Point((maxX+minX)/2,(maxY+minY)/2);
		min_height = maxY-minY;
		min_width = maxX-minX;
		if (min_height>min_width) min_width = min_height;
		else min_height = min_width;
		initialed = true;
		oldMin.x = min_width;
		oldMin.y = min_height;
	}
	allwidth = width;
	allheight = height;
	//图形最终显示到屏幕的区域的位置、长和宽
	glViewport (0,0,width,height);
	//指定矩阵
	glMatrixMode   (GL_PROJECTION);
	//将当前的矩阵设置为glMatrixMode指定的矩阵
	glLoadIdentity ();
	if (width/min_width <= height/min_height)
	{
		tmpsize = min_width/2;
		glOrthof(mid.x-min_width/2, mid.x+min_width/2, mid.y-tmpsize*(GLfloat)height/(GLfloat)width, mid.y+tmpsize*(GLfloat)height/(GLfloat)width, -10, 300);
		realWidth = min_width;
		realHeight = min_height*(GLfloat)height/(GLfloat)width;
	}
	else
	{
		tmpsize = min_height/2;
		glOrthof( mid.x-tmpsize*(GLfloat)width/(GLfloat)height, mid.x+tmpsize*(GLfloat)width/(GLfloat)height,mid.y-tmpsize, mid.y+tmpsize, -10, 300);
		realWidth = min_height*(GLfloat)width/(GLfloat)height;
		realHeight = min_height;
	}
}
double t;
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_nativeRender (JNIEnv* env, jobject obj)
{
	if (recalcText)
	{
		map.rescaleText(1.0/scaleNow);
		recalcText = false;
	}
/*	if (!back)
	tmpsize = tmpsize+0.5;
	else tmpsize = tmpsize-0.5;
	if (tmpsize>10) back = true;
	if (tmpsize<1) back = false;
	LOGI("tmpsize%f",tmpsize);
*/	//图形最终显示到屏幕的区域的位置、长和宽
/*	glViewport (0,0,allwidth,allheight);
	//指定矩阵
	glMatrixMode   (GL_PROJECTION);
	//将当前的矩阵设置为glMatrixMode指定的矩阵
	glLoadIdentity ();
	if (allwidth/min_width <= allheight/min_height)
	{
		tmpsize = min_width/2;
		glOrthof(mid.x-tmpsize, mid.x+tmpsize, mid.y-tmpsize*(GLfloat)allheight/(GLfloat)allwidth, mid.y+tmpsize*(GLfloat)allheight/(GLfloat)allwidth, -1, 1);
	}
	else
	{
		tmpsize = min_height/2;
		glOrthof( mid.x-tmpsize*(GLfloat)allwidth/(GLfloat)allheight, mid.x+tmpsize*(GLfloat)allwidth/(GLfloat)allheight,mid.y-tmpsize, mid.y+tmpsize, -1, 1);
	}
	*/
    glClearColor(1,1,1, 1.0);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	double tmptime = clock() - t;
	t = clock();
//	LOGI("FPS:%lf",tmptime);
	glViewport (0,0,allwidth,allheight);
	//指定矩阵
	glMatrixMode   (GL_PROJECTION);
	//将当前的矩阵设置为glMatrixMode指定的矩阵
	glLoadIdentity ();
	if (allwidth/min_width <= allheight/min_height)
	{
		tmpsize = min_width/2;
		glOrthof(mid.x-min_width/2, mid.x+min_width/2, mid.y-tmpsize*(GLfloat)allheight/(GLfloat)allwidth, mid.y+tmpsize*(GLfloat)allheight/(GLfloat)allwidth, -10, 300);
		realWidth = min_width;
		realHeight = min_height*(GLfloat)allheight/(GLfloat)allwidth;
	}
	else
	{
		tmpsize = min_height/2;
		glOrthof( mid.x-tmpsize*(GLfloat)allwidth/(GLfloat)allheight, mid.x+tmpsize*(GLfloat)allwidth/(GLfloat)allheight,mid.y-tmpsize, mid.y+tmpsize, -10, 300);
		realWidth = min_height*(GLfloat)allwidth/(GLfloat)allheight;
		realHeight = min_height;
	}
	//启用顶点设置功能，之后必须要关闭功能
	map.render();
	layers.Draw();
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_getViewRect (JNIEnv* env, jobject obj, jobject topleft, jobject bottomright)
{
	jclass topleftClass = (env)->GetObjectClass(topleft);
	jclass bottomrightClass = (env)->GetObjectClass(bottomright);
	jmethodID methodid;
	jfloat topleftx=mid.x-realWidth/2,
			toplefty=mid.y-realHeight/2;
	jfloat bottomrightx=mid.x+realWidth/2,
			bottomrighty=mid.y+realHeight/2;
	if (topleftClass && bottomrightClass)
	{
		methodid = (env)->GetMethodID(topleftClass, "set", "(FF)V");
		(env)->CallVoidMethod(topleft, methodid,topleftx,toplefty);
		methodid = (env)->GetMethodID(bottomrightClass, "set", "(FF)V");
		(env)->CallVoidMethod(bottomright, methodid,bottomrightx,bottomrighty);
	}
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_getPointView(JNIEnv* env, jobject obj, jobject mapPoint, jobject viewPoint)
{
	jclass mapPointClass = (env)->GetObjectClass(mapPoint);
	jclass viewPointClass = (env)->GetObjectClass(viewPoint);
	jmethodID methodid;
	jfieldID fid;
	jfloat mapPointx,mapPointy,viewPointx,viewPointy;
	if (mapPointClass && viewPointClass)
	{
		fid = (env)->GetFieldID(mapPointClass, "x", "F");
		viewPointx=(jfloat)(env)->GetFloatField(mapPoint, fid);
		fid = (env)->GetFieldID(mapPointClass, "y", "F");
		viewPointy=(jfloat)(env)->GetFloatField(mapPoint, fid);
		viewPointx = ((viewPointx-mid.x)/(realWidth/2)+1)*allwidth/2;
		viewPointy = ((viewPointy-mid.y)/(realHeight/2)+1)*allheight/2;
		methodid = (env)->GetMethodID(viewPointClass, "set", "(FF)V");
		(env)->CallVoidMethod(viewPoint, methodid,viewPointx,viewPointy);
	}
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_getPointMap(JNIEnv* env, jobject obj, jobject viewPoint, jobject mapPoint)
{
	jclass mapPointClass = (env)->GetObjectClass(mapPoint);
	jclass viewPointClass = (env)->GetObjectClass(viewPoint);
	jmethodID methodid;
	jfieldID fid;
	jfloat mapPointx,mapPointy,viewPointx,viewPointy;
	if (mapPointClass && viewPointClass)
	{
		fid = (env)->GetFieldID(viewPointClass, "x", "F");
		viewPointx=(jfloat)(env)->GetFloatField(viewPoint, fid);
		fid = (env)->GetFieldID(viewPointClass, "y", "F");
		viewPointy=(jfloat)(env)->GetFloatField(viewPoint, fid);
		viewPointx = (viewPointx/allwidth*2-1)*(realWidth/2)+mid.x;
		viewPointy = (viewPointy/allheight*2-1)*(realHeight/2)+mid.y;
		methodid = (env)->GetMethodID(viewPointClass, "set", "(FF)V");
		(env)->CallVoidMethod(mapPoint, methodid,viewPointx,viewPointy);
	}
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_Initialize (JNIEnv* env, jobject obj)
{
	maxX=-1000;
	maxY=-1000;
	minX=1000;
	minY=1000;
	Type = NONE;
	tmpsize = 1;
	recalcText = true;
	scaleNow = 1;
	scaleOld = 1;
	initialed = false;
	map.cleanup();
	layers.reset();
}
JNIEXPORT void JNICALL  Java_com_nexdgis_layer_opengl_NexdGLRenderer_Restart(JNIEnv* env, jobject obj)
{
	map.restart();
}
JNIEXPORT void JNICALL Java_com_nexdgis_layer_opengl_NexdGLRenderer_RenderwithoutText (JNIEnv* env, jobject obj)
{
    glClearColor(1,1,1, 1.0);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	double tmptime = clock() - t;
	t = clock();
//	LOGI("FPS:%lf",tmptime);
	glViewport (0,0,allwidth,allheight);
	//指定矩阵
	glMatrixMode   (GL_PROJECTION);
	//将当前的矩阵设置为glMatrixMode指定的矩阵
	glLoadIdentity ();
	if (allwidth/min_width <= allheight/min_height)
	{
		tmpsize = min_width/2;
		glOrthof(mid.x-min_width/2, mid.x+min_width/2, mid.y-tmpsize*(GLfloat)allheight/(GLfloat)allwidth, mid.y+tmpsize*(GLfloat)allheight/(GLfloat)allwidth, -10, 300);
		realWidth = min_width;
		realHeight = min_height*(GLfloat)allheight/(GLfloat)allwidth;
	}
	else
	{
		tmpsize = min_height/2;
		glOrthof( mid.x-tmpsize*(GLfloat)allwidth/(GLfloat)allheight, mid.x+tmpsize*(GLfloat)allwidth/(GLfloat)allheight,mid.y-tmpsize, mid.y+tmpsize, -10, 300);
		realWidth = min_height*(GLfloat)allwidth/(GLfloat)allheight;
		realHeight = min_height;
	}
	map.renderwithoutText();
}
}
