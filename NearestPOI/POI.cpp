#include "POI.h"
#include <map>
#include <sstream>
bool Polygon::inside(Point p)
{
	if (p.x<minP.x||p.y<minP.y||p.x>maxP.x||p.y>maxP.y)
		return false;
	int sum = 0;
	Point p1 = pointList[0] - p;
	int t1 = (p1.x>=0?(p1.y>=0?0:3):(p1.y>=0?1:2));
	int t2;
	if (equalZero(p1.x) && equalZero(p1.y))
		return true;
	for (int i = 0;i<pointList.size();i++)
	{
		Point p2 = pointList[nextIndex(i)]-p;
		if (equalZero(p2-p1))
			continue;
		if (equalZero(p2))
			return true;
		float f = p2.y*p1.x-p2.x*p1.y;
		if (equalZero(f) && (p1.x*p2.x<=0) &&(p1.y*p2.y<=0))
			return true;
		t2 = (p2.x>=0?(p2.y>=0?0:3):(p2.y>=0?1:2));
		if (t2 == (t1 + 1)%4)
			sum++;
		else if (t2 == (t1+3)%4)
			sum--;
		else if (t2 == (t1+2)%4)
		{
			if (f>=0)
				sum +=2;
			else sum -= 2;
		}
		t1 = t2;
		p1 = p2;
	}
	if (!equalZero(sum))
		return true;
	return false;
}
float Polygon::getDis(Point p)
{
//	if (inside(p))
//		return 0;
	float  minDis = MAXIMUM;
	for (int i = 0;i<pointList.size();i++)
	{
		minDis = min(minDis,disLinePoint(pointList[i],pointList[nextIndex(i)],p));
	}
	return minDis;
}
float disLinePoint(Point p1,Point p2,Point p)
{
	if (disPoint(p1,p2)<EPSILON)
		return disPoint(p1,p);
	else 	if (dot3(p,p2,p1)>=0 && dot3(p,p1,p2)>=0)
			return fabs(cross(p,p2,p1))/disPoint(p1,p2);
		else
			return min(disPoint(p1,p),disPoint(p2,p));
}
float s2f(string s)
{
	stringstream ss(s);
	float tmp;
	ss>>tmp;
	return tmp;
}
void XMLAdapter::adaptXML(string buildingName,string floorName)
{
	xmlDocPtr doc;
	xmlNodePtr root,cur;
	string fileName = xmlPath+"/"+buildingName+"/"+floorName+"/"+buildingName+"."+floorName+".xml";
	xmlKeepBlanksDefault(0);
	doc = xmlReadFile(fileName.c_str(),"UTF-8",XML_PARSE_NOBLANKS);
	if (doc == NULL)
	{
		printf("Error: cannot open %s",fileName.c_str());
		return ;
	}
	root = xmlDocGetRootElement(doc);
	cur = root->xmlChildrenNode;
	xmlNodePtr nd;
	while (cur!=NULL)
	{
		Polygon poly;
		if (!xmlStrcmp(cur->name,BAD_CAST "feature"))
		{
			if (xmlHasProp(cur,BAD_CAST "shape"))
			{
				xmlChar* szAttr = xmlGetProp(cur,BAD_CAST "shape");
				if (xmlStrcmp(szAttr,BAD_CAST "0"))
				{
					cur = cur->next;
					continue;
				}
			}
			if (xmlHasProp(cur,BAD_CAST "type"))
			{
				xmlChar* szAttr = xmlGetProp(cur,BAD_CAST "type");
				if (!xmlStrcmp(szAttr,BAD_CAST "0"))
				{
					cur = cur->next;
					continue;
				}
			}
			if (xmlHasProp(cur,BAD_CAST "id"))
			{
				xmlChar* szAttr = xmlGetProp(cur,BAD_CAST "id");
				poly.id = (char*)szAttr;
			}
			if (xmlHasProp(cur,BAD_CAST "name"))
			{
				xmlChar* szAttr = xmlGetProp(cur,BAD_CAST "name");
				poly.name = string((char*)szAttr);
			}
		}
		nd = cur->xmlChildrenNode;
		while (nd!=NULL)
		{
			if (!xmlStrcmp(nd->name,BAD_CAST "nd"))
			{
				Point p;
				if (xmlHasProp(nd,BAD_CAST "x"))
				{
					xmlChar* szAttr = xmlGetProp(nd,BAD_CAST "x");
					p.x = s2f(string((char*)szAttr));
					minx = min(minx,int(p.x));
					maxx = max(maxx,int(p.x+0.5));
				}
				if (xmlHasProp(nd,BAD_CAST "y"))
				{
					xmlChar* szAttr = xmlGetProp(nd,BAD_CAST "y");
					p.y = s2f(string((char*)szAttr));
					miny = min(miny,int(p.y));
					maxy = max(maxy,int(p.y+0.5));
				}
				poly.addPoint(p);
			}
			nd = nd->next;
		}
		polyList.push_back(poly);
		cur = cur->next;
	}
	xmlFree(doc);
}
void rshift(int x,int* p,int size)
{
	for (int i = size-1;i>=1;i--)
		p[i] = p[i-1];
	p[0] = x;
}
void rshift(float x,float* p,int size)
{
	for (int i = size-1;i>=1;i--)
		p[i] = p[i-1];
	p[0] = x;
}
void XMLAdapter::createQuestList()
{
	int i,j,k;
	minx -= scale;
	maxx = ((maxx-minx)/scale+1)*scale+minx;
	xsize = int(int(maxx-minx)/scale);
	miny -= scale;
	maxy = ((maxy-miny)/scale+1)*scale+miny;
	ysize = int(int(maxy-miny)/scale);
	matrix = new int**[xsize];
	for (i = 0;i<xsize;i++)
	{
		matrix[i] = new int*[ysize];
		for (j = 0;j<ysize;j++)
		{
			matrix[i][j] = new int[3];
			for (k = 0;k<3;k++)
				matrix[i][j][k] = -1;
		}
	}
	for (i = 0;i<xsize;i++)
		for(j = 0;j<ysize;j++)
			matrix[i][j] = new int[3];
	for (i = 0;i<xsize;i++)
		for(j = 0;j<ysize;j++)
		{
			
			float minX[3] = {MAXIMUM,MAXIMUM,MAXIMUM};
			Point p(i*scale+minx/*+scale/2.0f*/,j*scale+miny/*+scale/2.0f*/);
			if (i == 89 && j == 82)
				cout<<polyList[28].inside(p);
			for (k = 0;k<polyList.size();k++)
			if (polyList[k].inside(p)&&minX[2]>0)
			{
				rshift(0.0f,minX,3);
				rshift(k,matrix[i][j],3);
			}
			else if(polyList[k].minPointBound(p)<minX[2])
			{
				float tmp = polyList[k].getDis(p);
				if (tmp<minX[0])
				{
					rshift(tmp,minX,3);
					rshift(k,matrix[i][j],3);
				}
				else if (tmp<minX[1])
				{
					rshift(tmp,minX+1,2);
					rshift(k,matrix[i][j]+1,2);
				}
				else if (tmp<minX[2])
				{
					rshift(tmp,minX+2,1);
					rshift(k,matrix[i][j]+2,1);
				}
				
			}
		}
}
