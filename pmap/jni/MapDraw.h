#include <GLES/gl.h>
#include <vector>
using namespace std;
#define MAX_FLOAT 1e10
#define MIN_FLOAT -1e10
#define equalZero(x) (fabs(x)<1e-3)
#include <android/log.h>
#define LOG_TAG "MYJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
class Point
{
public:
	Point(float x1,float y1):x(x1),y(y1){}
	Point():x(0),y(0){}
	float x,y;
	Point operator+(Point p)
	{
		return Point(x+p.x,y+p.y);
	}
	Point operator-(Point p)
	{
		return Point(x-p.x,y-p.y);
	}
	Point operator*(float scale)
	{
		return Point(x*scale,y*scale);
	}
};
class Color
{
public:
	float r,g,b,a;
	Color(float r1,float g1,float b1):r(r1),g(g1),b(b1),a(1){}
	Color(float r1,float g1,float b1,float a1):r(r1),g(g1),b(b1),a(a1){}
	Color():r(0),g(0),b(0),a(1){}
};
class Polygon
{
protected:
	Color backColor;
	Color selectColor;
	float lineWidth;
	Color lineColor;
	bool isConvex;
	float* positions;
	float* linePositions;
	short* indexes;
	short* lineIndexes;
	bool isFillable;
	bool isStrokeable;
	Point minPoint,maxPoint;
	bool selected;
	inline int nextPointIndex(int i,int next = 1)
	{
		return (i+next)%pointList.size();
	}
	bool clockwise(Point p1,Point p2,Point p3)
	{
		return double(p2.x-p1.x)*double(p3.y-p1.y)-double(p3.x-p1.x)*double(p2.y-p1.y)>=-(1e-10);
	}
	double area(Point p1,Point p2,Point p3)
	{
		return abs((double)(p1.x - p3.x)*(double)(p2.y - p3.y)-(double)(p2.x - p3.x)*(double)(p1.y - p3.y));
	}
	bool innerPoint(Point p0,Point p1,Point p2,Point p3)
	{
		/*int sum = 0;
		Point pointList[3] = {pt1,pt2,pt3};
			Point p1 = pointList[0] - p;
			int t1 = p1.x>=0 ?(p1.y>=0?0:3) :(p1.y>=0?1:2);
			if (equalZero(p1.x) && equalZero(p1.y)) return true;
			int i;
			for (i = 0;i<3;i++)
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
			if(i<3 || sum)
				return true;
			return false;
			*/
//		LOGI("innerPoint:%f",fabs(area(p1,p2,p3)-area(p0,p2,p3)-area(p1,p0,p3)-area(p1,p2,p0)));
		double t = area(p1,p2,p3),t1 = area(p0,p2,p3),t3 = area(p1,p0,p3),t2 = area(p1,p2,p0);
		if (t1<1e-10||t2<1e-10||t3<1e-10) return false;
		if(abs(area(p1,p2,p3)-area(p0,p2,p3)-area(p1,p0,p3)-area(p1,p2,p0))<1e-30)
			return true;
		return false;
	}
public:
	Point center;
	vector<Point> pointList;
	vector<int> indices;
	int startVertexIndex,endVertexIndex;
	int startIndicesIndex,endIndicesIndex;
	int startLineIndex,endLineIndex;
	Polygon()
	{
		pointList.clear();
		indices.clear();
		float LineWidth = 0;
		positions = 0;
		linePositions = 0;
	}
	void Draw();
	void preDraw(unsigned int vob[]);
	void getIndices();
	void getBoundingBox();
	void addPoint(Point p);
	void setColor(Color c);
	void setSelectColor(Color c);
	void setLineColor(Color c);
	void setLineWidth(float width);
	void setFillable(bool fillable);
	bool getFillable(){return isFillable;}
	void setStrokeable(bool strokeable);
	void copyData(float* mapPos, short* mapIndex,short* mapLineIndex);
	bool insidePoly(Point p);
	void setSelected(bool b);
	void setCenter(Point p);
	void cleanup();
	void LowDraw();
	void copyArray();
	void reset();
};
class TextPolygon:public Polygon
{
private:
	float* textIndexes;
public:
	int startTextIndex,endTextIndex;
	GLuint textures;
	bool visible;
	int polyID;
	TextPolygon(){Polygon();}
	vector<Point> textPointList;
	void copyData(float* mapPos, short* mapIndex,float* textureIndex);
	void copyTextPosition(float scale,float* mapPos,Polygon* pp);
	void Draw();
	void cleanupText();
	Point counterClockwise45(Point p,Point center,float scale);
	Point clockwise45(Point p,Point center,float scale);
};
class Map
{
private:
	bool start;
	unsigned int vbo[2];
	float* positions;
	short* indexes;
	short* lineIndexes;
	int selectedId;
	float* textureIndexes;
public:
	void rescaleText(float scale);
	Map():start(false),selectedId(0),positions(0),indexes(0),lineIndexes(0),textureIndexes(0){}
	int vlen,ilen,llen;
	int tvlen,tilen,ttlen;
	vector<Polygon> polyList;
	vector<TextPolygon> textPolyList;
	void restart();
	int findSelectedPolygon(Point p);
	void initialPaint();
	void render();
	void cleanup();
	void renderwithoutText();
};
class RadiusPoint
{
public:
	Point p;
	float radius;
	float positions[2];
	Color c;
	void Draw();
	void copyArray();
};
class Line
{
public:
	Point p1,p2;
	float width;
	float positions[4];
	Color c;
	void Draw();
	void copyArray();
};
class Layer
{
private:
	vector<Polygon> polyList;
	vector<RadiusPoint> pointList;
	vector<Line> lineList;
public:
	Layer(){polyList.clear();pointList.clear();lineList.clear();}
	void addPoly(Polygon p){polyList.push_back(p);}
	void addLine(Line l){lineList.push_back(l);}
	void addPoint(RadiusPoint p){pointList.push_back(p);}
	void Draw();
	void update();
	void reset();
};
class Layers
{
private:
//	float* points;
public:
	vector<Layer> layer;
	Layers(){layer.clear();}
	void addLayer(Layer l){layer.push_back(l);}
	void Draw();
	void update();
	void reset();
};
