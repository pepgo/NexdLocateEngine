#include <iostream>
#include <vector>
#include <cmath>
#include <libxml/parser.h>
#include <libxml/tree.h>
#include <cstdio>
#include <string.h>
#include <libxml/xpath.h>
using namespace std;
#include <utility>
#include <algorithm>
#define EPSILON 1e-5
#define MAXIMUM 1e7
#define MINIMUM -1e7
class Point
{
public:
	Point(float x1, float y1)
	{
		x = x1;
		y = y1;
	}
	Point()
	{
		x = y = 0;
	}	
	float x,y;
	Point operator+(Point p)
	{
		return Point(p.x+x,p.y+y);
	}
	Point operator-(Point p)
	{
		return Point(x-p.x,y-p.y);
	}
};

inline float disPoint(Point p1,Point p2)
{
	return sqrt((p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y));
}
float disLinePoint(Point p1,Point p2,Point p);
class Polygon
{
private:
	vector<Point> pointList;
public:
	Point minP,maxP;
	string id,name;
	Polygon()
	{
		minP = Point(MAXIMUM,MAXIMUM);
		maxP = Point(MINIMUM,MINIMUM);
		name = "";
		id = "";
		pointList.clear();
	}
	float minPointBound(Point p)
	{
		return min(min(disPoint(p,minP),disPoint(p,maxP)),min(disPoint(p,Point(minP.x,maxP.y)),disPoint(p,Point(maxP.x,minP.y))));
	}
	void addPoint(Point p)
	{
		pointList.push_back(p);
		minP.x = min(p.x,minP.x);
		minP.y = min(p.y,minP.y);
		maxP.x = max(p.x,maxP.x);
		maxP.y = max(p.y,maxP.y);		
	}
	int nextIndex(int i)
	{
		return (i+1)%pointList.size();
	}
	float getDis(Point p);
	bool inside(Point p);
};
inline bool equalZero(float x)
{
	return (fabs(x)<=EPSILON);
	
}
inline bool equalZero(Point p)
{
	return equalZero(p.x) && equalZero(p.y);
}
inline float dot3(Point p1,Point p2,Point p)
{
	return (p1.x-p.x)*(p2.x-p.x)+(p1.y-p.y)*(p2.y-p.y);
}
inline float cross(Point p1,Point p2,Point p)
{
	return (p1.x-p.x)*(p2.y-p.y)-(p1.y-p.y)*(p2.x-p.x);
}
class SortPair
{
public:
	SortPair()
	{
	}
	SortPair(float f,int i)
	{
		first = f;
		second = i;
	}
	bool operator<(const SortPair& sp) const
	{
		return first<sp.first;
	}
	float first;
	int second;
};	

class XMLAdapter
{
private:
	int scale;
	string xmlPath,buildingName,floorName;
	int minx,miny,maxx,maxy;
	vector<SortPair> polyMap;
	int xsize,ysize;
	int*** matrix;
public:
	vector<Polygon> polyList;
	XMLAdapter(string s)
	{
		matrix = NULL;
		polyMap.clear();
		polyList.clear();
		scale = 1;
		xmlPath = s;
		minx = miny = int(MAXIMUM);
		maxx = maxy = int(MINIMUM);
		xsize = ysize = 0;
	}
	void adaptXML(string buildingName,string floorName);
	void createQuestList();
	inline pair<int,int> askIndex(Point p)
	{
		return pair<int,int>(int((p.x-minx)/scale),int((p.y-miny)/scale));
	}
	inline int* askResult(pair<int,int> p)
	{
		printf("Index: %d %d\n",p.first,p.second);
		if (p.first<0||p.first>=xsize||p.second<0||p.second>=ysize)
			return NULL;
		return matrix[p.first][p.second];
	}	
	~XMLAdapter()
	{
		int i,j;
		for (i = 0;i<xsize;i++)
		{
			for (j = 0;j<ysize;j++)
				delete [] matrix[i][j];
			delete [] matrix[i];
		}
		delete [] matrix;
	}
};
