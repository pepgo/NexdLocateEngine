/*
 * MapDraw.cpp
 *
 *  Created on: Mar 7, 2014
 *      Author: YanXiao
 */


#include <android/log.h>
#define LOG_TAG "MYJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

#include "MapDraw.h"
#include <cmath>
void Map::initialPaint()
{
	if (!start)
	{
		start = true;
		LOGI("start");
		//生成两个缓存区对象
		vlen = ilen = llen = 0;
		glGenBuffers (3, vbo);
	//	for (int j = 0;j<100;j++)
		for (int j = 0; j < polyList.size(); j++)
		{
		//	Polygon poly1;
//			Polygon poly1 = polyList[j];
		/*	for (int i = 0;i<10;i++)
			{
				poly1.addPoint(Point(1*cos((float)3.1415926*2/10*i),1*sin((float)3.1415926*2/10*i)));
			}*/
			polyList[j].setSelected(false);
			polyList[j].getIndices();
			polyList[j].getBoundingBox();
		//	poly1.preDraw(vbo);
			polyList[j].startVertexIndex = vlen;
			polyList[j].startIndicesIndex = ilen;
			polyList[j].startLineIndex = vlen;
			vlen += polyList[j].pointList.size();
			ilen += polyList[j].indices.size();
			polyList[j].endVertexIndex = vlen;
			polyList[j].endIndicesIndex = ilen;
			polyList[j].endLineIndex = vlen;
//			polyList[j].setColor(Color(0,1,0,1));
//			polyList[j].setLineWidth(2);
//			polyList[j].setLineColor(Color(1,0,0,1));
		//	polyList.push_back(poly1);
		}

		LOGI("preDrawOk");
		ttlen = vlen;
		lineIndexes = new short[vlen];

		for (int j = 0;j< textPolyList.size();j++)
		{
			textPolyList[j].getIndices();
			textPolyList[j].startVertexIndex = vlen;
			textPolyList[j].startIndicesIndex = ilen;
			textPolyList[j].startTextIndex = ttlen;
			vlen += textPolyList[j].pointList.size();
			ilen += textPolyList[j].indices.size();
			ttlen += textPolyList[j].pointList.size();
		}
		textureIndexes = new float[2*ttlen];
		positions = new float[2*vlen];
		indexes = new short[ilen];
		for (int i = 0;i<polyList.size();i++)
		{
			polyList[i].copyData(positions,indexes,lineIndexes);
		}
		for (int i = 0;i<textPolyList.size();i++)
		{
			textPolyList[i].copyData(positions,indexes,textureIndexes);
		}
	}
	LOGI("preDrawOk");
//	glEnable(GL_MULTISAMPLE);
//	glDisable(GL_MULTISAMPLE);
//	glEnable(GL_DEPTH_TEST);
//	glShadeModel(GL_FLAT);
//	glActiveTexture(GL_TEXTURE1);
    glEnable(GL_TEXTURE_2D);
    glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
    //为该ID指定一块指定大小的存储区域（区域的位置大抵由末参数影响),  传输数据
    glBufferData(GL_ARRAY_BUFFER, sizeof(float)*2*ttlen, textureIndexes, GL_STATIC_DRAW);
	glTexCoordPointer(2, GL_FLOAT, 0, 0);
//	glBindTexture(GL_TEXTURE_2D,textPolyList[0].textures);
    glDisable(GL_TEXTURE_2D);
    // 禁用深度测试
    glDisable(GL_DEPTH_TEST);

    // 启用混合
	glBindBuffer    (GL_ARRAY_BUFFER, vbo[0]);
	glBufferData (GL_ARRAY_BUFFER, 4*2*vlen, positions, GL_STATIC_DRAW);
	glVertexPointer (2, GL_FLOAT, 0, 0);

	//定义顶点坐标
	glBindBuffer    (GL_ELEMENT_ARRAY_BUFFER, vbo[1]);
//	glColor4f(backColor.r,backColor.g,backColor.b,backColor.a);
	glBufferData (GL_ELEMENT_ARRAY_BUFFER, 2*ilen, indexes, GL_STATIC_DRAW);
//	glBufferData (GL_ARRAY_BUFFER, 4*2*vlen, positions, GL_STATIC_DRAW);
//	glVertexPointer (2, GL_FLOAT, 0, 0);
//	glBufferData (GL_ELEMENT_ARRAY_BUFFER, 2*vlen, indexes, GL_STATIC_DRAW);
/*	Polygon poly;
	poly.addPoint(Point(1,1));
	poly.addPoint(Point(0.5,-0.5));
	poly.addPoint(Point(-1,-1));
	poly.addPoint(Point(1,-1));
	poly.getIndices();
	poly.preDraw(vbo);
	poly.setColor(Color(0,1,0,0.5));
	poly.setLineWidth(3);
	poly.setLineColor(Color(1,0,0,1));
	polyList.push_back(poly);
*/	LOGI("getIndices");

}
void Map::restart()
{

    glEnable(GL_TEXTURE_2D);
    glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
    //为该ID指定一块指定大小的存储区域（区域的位置大抵由末参数影响),  传输数据
    glBufferData(GL_ARRAY_BUFFER, sizeof(float)*2*ttlen, textureIndexes, GL_STATIC_DRAW);
	glTexCoordPointer(2, GL_FLOAT, 0, 0);
//	glBindTexture(GL_TEXTURE_2D,textPolyList[0].textures);
    glDisable(GL_TEXTURE_2D);
    // 禁用深度测试
    glDisable(GL_DEPTH_TEST);

    // 启用混合
	glBindBuffer    (GL_ARRAY_BUFFER, vbo[0]);
	glBufferData (GL_ARRAY_BUFFER, 4*2*vlen, positions, GL_STATIC_DRAW);
	glVertexPointer (2, GL_FLOAT, 0, 0);

	//定义顶点坐标
	glBindBuffer    (GL_ELEMENT_ARRAY_BUFFER, vbo[1]);
//	glColor4f(backColor.r,backColor.g,backColor.b,backColor.a);
	glBufferData (GL_ELEMENT_ARRAY_BUFFER, 2*ilen, indexes, GL_STATIC_DRAW);
}
void Map::rescaleText(float scale)
{
	LOGI("rescale%f",scale);
	for (int i = 0;i< textPolyList.size();i++)
	{
		textPolyList[i].copyTextPosition(scale,positions,&polyList[textPolyList[i].polyID]);
	}
	glBindBuffer    (GL_ARRAY_BUFFER, vbo[0]);
	glBufferData (GL_ARRAY_BUFFER, 4*2*vlen, positions, GL_STATIC_DRAW);
	glVertexPointer (2, GL_FLOAT, 0, 0);
	LOGI("endrescale%f",scale);
}
void Map::render()
{
//	LOGI("render");

	glEnableClientState (GL_VERTEX_ARRAY);
	glBindBuffer    (GL_ARRAY_BUFFER, vbo[0]);
	glVertexPointer (2, GL_FLOAT, 0, 0);
	//清屏
	glDisable(GL_TEXTURE_2D);
	for (int i = polyList.size()-1;i>=0;i--)
	{
//		glBufferData (GL_ARRAY_BUFFER, 4*2*10, positions, GL_STATIC_DRAW);
//		glVertexPointer (2, GL_FLOAT, 0, 0);
		//定义顶点坐标
	//	glColor4f(backColor.r,backColor.g,backColor.b,backColor.a);
//		glBufferData (GL_ELEMENT_ARRAY_BUFFER, 2*24, indexes, GL_STATIC_DRAW);
		//按照参数给定的值绘制图形
//		glDrawElements  (GL_TRIANGLES,24, GL_UNSIGNED_SHORT, 0);

		//设置顶点和纹理的位置、类型
//		LOGI("%d",i);
		polyList[i].Draw();
	}
	glEnable(GL_TEXTURE_2D);
	glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	//绘图
	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	for (int i = 0;i<textPolyList.size();i++)
	{
//	    glBindTexture(GL_TEXTURE_2D, textPolyList[i].textures);
	    glBindTexture(GL_TEXTURE_2D, textPolyList[i].textures);
		textPolyList[i].Draw();
//	glDrawArrays(GL_TRIANGLES, 0,6);
	}
//	glBufferData (GL_ARRAY_BUFFER, 4*2*vlen, positions, GL_STATIC_DRAW);
//	glVertexPointer (2, GL_FLOAT, 0, 0);
	//定义顶点坐标
//	glColor4f(backColor.r,backColor.g,backColor.b,backColor.a);
//	glBufferData (GL_ELEMENT_ARRAY_BUFFER, 2*ilen, indexes, GL_STATIC_DRAW);
	//按照参数给定的值绘制图形
//	glDrawElements  (GL_TRIANGLES,ilen, GL_UNSIGNED_SHORT, 0);
	//关闭顶点设置功能
	glDisableClientState(GL_TEXTURE_COORD_ARRAY);
	glDisableClientState(GL_VERTEX_ARRAY);
	glDisable(GL_BLEND);
	glDisable(GL_TEXTURE_2D);
}

void Map::renderwithoutText()
{
//	LOGI("render");

	glEnableClientState (GL_VERTEX_ARRAY);
	//清屏
	glDisable(GL_TEXTURE_2D);
	for (int i = polyList.size()-1;i>=0;i--)
	{
//		glBufferData (GL_ARRAY_BUFFER, 4*2*10, positions, GL_STATIC_DRAW);
//		glVertexPointer (2, GL_FLOAT, 0, 0);
		//定义顶点坐标
	//	glColor4f(backColor.r,backColor.g,backColor.b,backColor.a);
//		glBufferData (GL_ELEMENT_ARRAY_BUFFER, 2*24, indexes, GL_STATIC_DRAW);
		//按照参数给定的值绘制图形
//		glDrawElements  (GL_TRIANGLES,24, GL_UNSIGNED_SHORT, 0);

		//设置顶点和纹理的位置、类型
//		LOGI("%d",i);
		polyList[i].Draw();
	}
}

int Map::findSelectedPolygon(Point p)
{
	for (int i = 0;i<polyList.size();i++)
	if (polyList[i].getFillable())
	{
		if (polyList[i].insidePoly(p))
		{
			polyList[selectedId].setSelected(false);
			polyList[i].setSelected(true);
			selectedId = i;
			return polyList.size()-i-1;
		}
	}
	polyList[selectedId].setSelected(false);
	selectedId = 0;
	return -1;
}
void Map::cleanup()
{
/*	for (int i = 0;i<polyList.size();i++)
		polyList[i].cleanup();
	for (int i = 0;i<textPolyList.size();i++)
		textPolyList[i].cleanupText();
*/	LOGI("CLEANUP");
	polyList.clear();
	textPolyList.clear();
	if (positions!=0)
		delete [] positions;
	if (indexes!=0)
		delete [] indexes;
	if (lineIndexes!=0)
		delete [] lineIndexes;
	if (textureIndexes!=0)
		delete [] textureIndexes;
	start = false;
	selectedId = 0;
	positions = 0;
	indexes = 0;
	lineIndexes = 0;
	textureIndexes = 0;
}
