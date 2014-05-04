#include "MapDraw.h"
void Layer::Draw()
{
	for (int i = 0;i<polyList.size();i++)
		polyList[i].LowDraw();
	for (int i = 0;i<lineList.size();i++)
		lineList[i].Draw();
	for (int i = 0;i<pointList.size();i++)
		pointList[i].Draw();
}
void Layer::update()
{
	for (int i = 0;i<polyList.size();i++)
	{
		polyList[i].getIndices();
		polyList[i].copyArray();
	}
	for (int i = 0;i<lineList.size();i++)
		lineList[i].copyArray();
	for (int i = 0;i<pointList.size();i++)
		pointList[i].copyArray();
}
void Layers::update()
{
	for (int i = 0;i<layer.size();i++)
		layer[i].update();
}
void Layers::reset()
{
	for (int i = 0;i<layer.size();i++)
		layer[i].reset();
	layer.clear();
}
void Layer::reset()
{
	for (int i = 0;i<polyList.size();i++)
		polyList[i].reset();
	polyList.clear();
	lineList.clear();
	pointList.clear();
}
void Layers::Draw()
{
	glBindBuffer    (GL_ARRAY_BUFFER, 0);
	glEnableClientState(GL_VERTEX_ARRAY);
	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	for (int i = 0;i<layer.size();i++)
		layer[i].Draw();
	glDisableClientState(GL_VERTEX_ARRAY);
	glDisable(GL_BLEND);
}
void Line::copyArray()
{
	positions[0] = p1.x;
	positions[1] = p1.y;
	positions[2] = p2.x;
	positions[3] = p2.y;
}
void RadiusPoint::copyArray()
{
	positions[0] = p.x;
	positions[1] = p.y;
}
void RadiusPoint::Draw()
{
	glColor4f(c.r,c.g,c.b,c.a);
	glPointSize(radius);
	glVertexPointer(2,GL_FLOAT,0, positions);
	glDrawArrays(GL_POINTS,0, 1);
}
void Line::Draw()
{
	glColor4f(c.r,c.g,c.b,c.a);
	glLineWidth(width);
	glVertexPointer(2,GL_FLOAT,0, positions);
	glDrawArrays(GL_LINES,0, 2);
}
void Polygon::copyArray()
{
	positions = new float[2*indices.size()];
	linePositions = new float[2*pointList.size()];
	for (int i = 0;i<indices.size();i++)
	{
		positions[i*2] = pointList[indices[i]].x;
		positions[i*2+1] = pointList[indices[i]].y;
		LOGI("copyArray %f %f",positions[i*2],positions[i*2+1]);
	}
	for (int i = 0;i<pointList.size();i++)
	{
		linePositions[i*2] = pointList[i].x;
		linePositions[i*2+1] = pointList[i].y;
		LOGI("copyLineArray %f %f",linePositions[i*2],linePositions[i*2+1]);
	}
}

void Polygon::LowDraw()
{

//	LOGI("mmmm%d",pointList.size());
	if (isFillable)
	{
		glColor4f(backColor.r,backColor.g,backColor.b,backColor.a);
		glVertexPointer(2,GL_FLOAT,0, positions);
		glDrawArrays(GL_TRIANGLES,0,indices.size());
	}
	if (isStrokeable)
	{
		glColor4f(lineColor.r,lineColor.g,lineColor.b,lineColor.a);
		glLineWidth(lineWidth);
		glVertexPointer(2,GL_FLOAT,0, linePositions);
		glDrawArrays(GL_LINE_LOOP,0,pointList.size());
	}
}
void Polygon::reset()
{
	if (positions!=0)
	{
		delete [] positions;
		positions = 0;
	}
	if (linePositions!=0)
	{
		delete [] linePositions;
		linePositions = 0;
	}
}
