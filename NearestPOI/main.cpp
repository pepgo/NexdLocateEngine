#include "POI.h"
int main()
{
	XMLAdapter xmlAdapter("../map_xml");
	xmlAdapter.adaptXML("BeiJingYiDiGang","FloorF1");
	xmlAdapter.createQuestList();
	printf("preProcess Completed\n");
	while(true){
	Point p;
	printf("Point:\n");
	scanf("%f %f",&p.x,&p.y);
	pair<int,int> index = xmlAdapter.askIndex(p);
	int* result;
	result = xmlAdapter.askResult(index);
	if (result == NULL)
	{
		printf("out of index\n");
	//	return 0;
		continue;
	}
	for (int i = 0;i<3;i++)
		printf("%s %f%s\n",xmlAdapter.polyList[result[i]].id.c_str()
			,xmlAdapter.polyList[result[i]].getDis(p),xmlAdapter.polyList[result[i]].inside(p)?"(inside)":"(outside)");
	}
	return 0;
}

