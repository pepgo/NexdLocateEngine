/*
 * MapDraw.cpp
 *
 *  Created on: Mar 7, 2014
 *      Author: YanXiao
 */

#include "MapDraw.h"
#include <android/log.h>
#include <cmath>
#define LOG_TAG "MYJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
void Polygon::preDraw(unsigned int vbo[])
{
	positions = new float[2*pointList.size()];
	indexes = new short[indices.size()];
	lineIndexes = new short[pointList.size()];
	for (int i = 0;i<pointList.size();i++)
	{
		positions[i*2] = pointList[i].x;
		positions[i*2+1] = pointList[i].y;
		lineIndexes[i] = i;
	}
	for (int i = 0;i<indices.size();i++)
		indexes[i] = (short)indices[i];
	LOGI("preDrawOk");
}
void Polygon::copyData(float* mapPos, short* mapIndex,short* mapLineIndex)
{
	for (int i = 0;i<pointList.size();i++)
	{
		mapPos[startVertexIndex*2+i*2] = pointList[i].x;
		mapPos[startVertexIndex*2+i*2+1] = pointList[i].y;
		mapLineIndex[startLineIndex+i] = i;
	}
	for (int i = 0;i<indices.size();i++)
		mapIndex[startIndicesIndex+i] = startVertexIndex+(short)indices[i];
	positions = mapPos+startVertexIndex*2;
	indexes = mapIndex+startIndicesIndex;
	lineIndexes = mapLineIndex + startLineIndex;

}
void TextPolygon::copyData(float* mapPos, short* mapIndex,float* textureIndex)
{
	for (int i = 0;i<pointList.size();i++)
	{
		mapPos[startVertexIndex*2+i*2] = pointList[i].x;
		mapPos[startVertexIndex*2+i*2+1] = pointList[i].y;
		textureIndex[startTextIndex*2 + i * 2] = textPointList[i].x;
		textureIndex[startTextIndex*2 + i * 2 + 1] = textPointList[i].y;
	}
	for (int i = 0;i<indices.size();i++)
	{
		mapIndex[startIndicesIndex+i] = startVertexIndex+(short)indices[i];
	}
	positions = mapPos+startVertexIndex*2;
	indexes = mapIndex+startIndicesIndex;
	textIndexes = textureIndex + startTextIndex*2;
}
void TextPolygon::copyTextPosition(float scale,float* mapPos,Polygon* pp)
{
	visible = true;
	Point p;
	for (int i = 0;i<pointList.size();i++)
	{
		p.x = mapPos[startVertexIndex*2+i*2] = center.x+(pointList[i].x-center.x)*scale;
		p.y = mapPos[startVertexIndex*2+i*2+1] = center.y+(pointList[i].y-center.y)*scale;
		if (visible && !pp->insidePoly(p))
			visible = false;
	}
	if (!visible)
	{
		visible = true;
		for (int i = 0;i<pointList.size();i++)
		{
			p = counterClockwise45(pointList[i],center,scale);
			mapPos[startVertexIndex*2+i*2] = p.x;
			mapPos[startVertexIndex*2+i*2+1] = p.y;
			if (visible && !pp->insidePoly(p))
				visible = false;
		}
	}
	if (!visible)
	{
		visible = true;
		for (int i = 0;i<pointList.size();i++)
		{
			p = clockwise45(pointList[i],center,scale);
			mapPos[startVertexIndex*2+i*2] = p.x;
			mapPos[startVertexIndex*2+i*2+1] = p.y;
			if (visible && !pp->insidePoly(p))
				visible = false;
		}
	}
	if (!visible)
	{
		visible = true;
		for (int i = 0;i<pointList.size();i++)
		{
			p.x = mapPos[startVertexIndex*2+i*2] = center.x+(pointList[i].y-center.y)*scale;
			p.y = mapPos[startVertexIndex*2+i*2+1] = center.y-(pointList[i].x-center.x)*scale;
			if (visible && !pp->insidePoly(p))
				visible = false;
		}
	}
}
Point TextPolygon::counterClockwise45(Point p,Point center,float scale)
{
	float pi4 = sqrt(2.0f)/2;
	Point newP = (p-center)*scale;
	return Point(pi4*(newP.x-newP.y)+center.x,pi4*(newP.x+newP.y)+center.y);
}
Point TextPolygon::clockwise45(Point p,Point center,float scale)
{
	float pi4 = sqrt(2.0f)/2;
	Point newP = (p-center)*scale;
	return Point(pi4*(newP.x+newP.y)+center.x,pi4*(newP.y-newP.x)+center.y);

}
void Polygon::Draw()
{
//	glBufferData (GL_ARRAY_BUFFER, 4*2*pointList.size(), positions, GL_STATIC_DRAW);
//	glVertexPointer (2, GL_FLOAT, 0, 0);
	//定义顶点坐标
//	LOGI("%f %f %f",backColor.r,backColor.g,backColor.b);
//	LOGI("%d %d",isFillable,isStrokeable);
	if (isFillable)
	{
		if (selected)
			glColor4f(selectColor.r,selectColor.g,selectColor.b,selectColor.a);
		else
			glColor4f(backColor.r,backColor.g,backColor.b,backColor.a);

		glDrawElements  (GL_TRIANGLES,indices.size(), GL_UNSIGNED_SHORT, (GLvoid*)(sizeof(GLushort) * startIndicesIndex));
	}
	if (isStrokeable)
	{
		glColor4f(lineColor.r,lineColor.g,lineColor.b,lineColor.a);
		glLineWidth(lineWidth);
		glDrawArrays(GL_LINE_LOOP,startLineIndex,pointList.size());
	}
	//	glBufferData (GL_ELEMENT_ARRAY_BUFFER, 2*indices.size(), indexes, GL_STATIC_DRAW);
	//按照参数给定的值绘制图形
//	glDrawElements  (GL_TRIANGLES,indices.size(), GL_UNSIGNED_SHORT, 0);
//	glColor4f(lineColor.r,lineColor.g,lineColor.b,lineColor.a);
//	glLineWidth(lineWidth);
//	glBufferData (GL_ELEMENT_ARRAY_BUFFER, 2*pointList.size(), lineIndexes, GL_STATIC_DRAW);
//	glDrawElements  (GL_LINE_LOOP,pointList.size(), GL_UNSIGNED_SHORT, 0);

}
void Polygon::cleanup()
{

	pointList.clear();
	indices.clear();
}
void TextPolygon::Draw()
{
//	glBufferData (GL_ARRAY_BUFFER, 4*2*pointList.size(), positions, GL_STATIC_DRAW);
//	glVertexPointer (2, GL_FLOAT, 0, 0);
	//定义顶点坐标
//	LOGI("%f %f %f",backColor.r,backColor.g,backColor.b);
//	LOGI("%d %d",isFillable,isStrokeable);
//	glBindTexture(GL_TEXTURE_2D, textures);
	if (visible)
	glDrawElements  (GL_TRIANGLES,indices.size(), GL_UNSIGNED_SHORT, (GLvoid*)(sizeof(GLushort) * startIndicesIndex));
	//	glBufferData (GL_ELEMENT_ARRAY_BUFFER, 2*indices.size(), indexes, GL_STATIC_DRAW);
	//按照参数给定的值绘制图形
//	glDrawElements  (GL_TRIANGLES,indices.size(), GL_UNSIGNED_SHORT, 0);
//	glColor4f(lineColor.r,lineColor.g,lineColor.b,lineColor.a);
//	glLineWidth(lineWidth);
//	glBufferData (GL_ELEMENT_ARRAY_BUFFER, 2*pointList.size(), lineIndexes, GL_STATIC_DRAW);
//	glDrawElements  (GL_LINE_LOOP,pointList.size(), GL_UNSIGNED_SHORT, 0);

}
void Polygon::addPoint(Point p)
{
	pointList.push_back(p);
}
void Polygon::setColor(Color c)
{
	backColor = c;
}
void Polygon::setSelectColor(Color c)
{
	selectColor = c;
}
void Polygon::setLineColor(Color c)
{
	lineColor = c;
}
void Polygon::setLineWidth(float width)
{
	lineWidth = width;
}
void Polygon::setFillable(bool fillable)
{
	isFillable = fillable;
}
void Polygon::setStrokeable(bool strokeable)
{
	isStrokeable = strokeable;
}
void Polygon::setCenter(Point p)
{
	center = p;
}
void Polygon::getIndices()
{
	isConvex = true;
	for (int i = 0;i<pointList.size();i++)
		if (!clockwise(pointList[i],pointList[nextPointIndex(i)],pointList[nextPointIndex(i,2)]))
		{
			isConvex = false;
			break;
		}
	if (isConvex)
	{
		LOGI("isCONvex");
		for (int i = 1;i<pointList.size()-1;i++)
		{
			indices.push_back(0);
			indices.push_back(i);
			indices.push_back(i+1);
		}
	}
	else
	{
		LOGI("notConvex");
		vector<int> next(pointList.size());
		int now = 0;
		for (int i = 0;i<pointList.size();i++)
			next[i] = nextPointIndex(i);
		for (int i = 0;i<pointList.size()-2;i++)
		{
			int old_now = now;
			while (true)
			{
				int index1 = now,index2 = next[now],index3 = next[index2];
				if (!clockwise(pointList[index1],pointList[index2],pointList[index3]))
				{
					now = next[now];
					if (now == old_now)
					{
						LOGI("error,cannot find valid vertex %f %f",center.x,center.y);
						break;
					}
					continue;
				}
				int otherPoint = next[index3];
				bool findInner = false;
				while (index1!=otherPoint)
				{
					if (innerPoint(pointList[otherPoint],pointList[index1],pointList[index2],pointList[index3]))
					{
						findInner = true;
						break;
					}
					otherPoint = next[otherPoint];
				}
				if (findInner == false)
				{
					indices.push_back(index1);
					indices.push_back(index2);
					indices.push_back(index3);
					next[index1] = index3;
					break;
				}
				now = next[now];
				if (now == old_now)
				{
					LOGI("error,cannot find valid vertex1");
					break;
				}
			}
		}
		next.clear();
	}
}
void Polygon::getBoundingBox()
{
	minPoint = Point(MAX_FLOAT,MAX_FLOAT);
	maxPoint = Point(MIN_FLOAT,MIN_FLOAT);
	for (int i = 0;i<pointList.size();i++)
	{
		if (pointList[i].x>maxPoint.x) maxPoint.x = pointList[i].x;
		if (pointList[i].y>maxPoint.y) maxPoint.y = pointList[i].y;
		if (pointList[i].x<minPoint.x) minPoint.x = pointList[i].x;
		if (pointList[i].y<minPoint.y) minPoint.y = pointList[i].y;
	}
}
bool Polygon::insidePoly(Point p)
{
	if (p.x<minPoint.x-0.001 || p.x>maxPoint.x+0.001 || p.y<minPoint.y-0.001 || p.y>maxPoint.y+0.001)
		return false;
	int sum = 0;
	Point p1 = pointList[0] - p;
	int t1 = p1.x>=0 ?(p1.y>=0?0:3) :(p1.y>=0?1:2);
	if (equalZero(p1.x) && equalZero(p1.y)) return true;
	int i;
	for (i = 0;i<pointList.size();i++)
	{
		Point p2 = pointList[nextPointIndex(i)] - p;
		if(equalZero(p2.x) && equalZero(p2.y)) break;
		float f = p2.y * p1.x - p2.x * p1.y;
		if(equalZero(f) && p1.x*p2.x <= 0 && p1.y*p2.y <= 0) break;
		int t2 = p2.x>=0 ?(p2.y>=0?0:3) :(p2.y>=0?1:2);
		if(t2 ==(t1 + 1) % 4) sum += 1;
		else if(t2 ==(t1 + 3) % 4) sum -= 1;
		else if(t2 ==(t1 + 2) % 4)
		{
			if(f > 0) sum += 2;
		    else sum -= 2;
		}
		t1 = t2;
		p1 = p2;
	}
	if(i<pointList.size() || sum)
		return true;
	return false;
}

void Polygon::setSelected(bool b)
{
	selected = b;
}
void TextPolygon::cleanupText()
{

	textPointList.clear();
}
