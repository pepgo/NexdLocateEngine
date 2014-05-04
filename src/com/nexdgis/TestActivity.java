package com.nexdgis;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.gy.nexdgis.R;

import com.nexdgis.log.NexdLog;

public class TestActivity extends Activity {
	
	public static float x;
	public static float y;

	ListView listView;
	ArrayList<String> buildingList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	NexdLog.tempDebug("onCreate");		
    	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_activity);
		
		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/map_xml/");
		File[] fileList = file.listFiles();
		buildingList = new ArrayList<String>();
		for (int i = 0; i < fileList.length; i++) {
			buildingList.add(fileList[i].getName());
		}
		
		listView = (ListView) findViewById(R.id.listview);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, buildingList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putString("BuildingIntentName", buildingList.get(arg2));
				Intent intent = new Intent(TestActivity.this, BuildingActivity.class);
				intent.putExtras(bundle);
				TestActivity.this.startActivity(intent);
			}
		});		
		
//		NexdMap nexdMap = (NexdMap)findViewById(R.id.nexd_map);
//		NexdDatabase test = NexdDatabase.getDbInstance();
//		test.syncRenderer(this, "render.xml");
//		test.syncFeature(this, "FengLianGuangChang.FloorF2.xml");
//		
//		nexdMap.setMapFeatureLayer(new MapFeatureLayer(this, true));
//		TopLayer topLayer = new TopLayer(this);
//		Callout callout = new Callout(this);
//		callout.setContent(R.layout.callout);
//		topLayer.setCallout(callout);
//		nexdMap.setTopLayer(topLayer);
//		
//		View view = callout.getContent();
//		final TextView textView = (TextView)view.findViewById(R.id.text_view);
//		final Button button = (Button)view.findViewById(R.id.button);
//		button.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				textView.setText("������������");
//			}
//
//		});
		
//		GraphicLayer gLayer;
//		gLayer = new GraphicLayer(this);
//		gLayer.addGraphic(new MarkerSymbol(new Point(50, 50)));
//		gLayer.addGraphic(new MarkerSymbol(new Point(500, 500)));
//		nexdMap.addGraphicLayer(gLayer);
//		gLayer = new GraphicLayer(this);
//		gLayer.addGraphic(new LineSymbol(new Line(60, 60, 100, 100)));
//		gLayer.addGraphic(new LineSymbol(new Line(70, 60, 150, 60)));
//		nexdMap.addGraphicLayer(gLayer);
//		gLayer = new GraphicLayer(this);
//		Point[] pArray = new Point[4];
//		pArray[0] = new Point(200, 200);
//		pArray[1] = new Point(300, 300);
//		pArray[2] = new Point(300, 200);
//		pArray[3] = new Point(200, 200);
//		gLayer.addGraphic(new FillSymbol(new Polygon(pArray)));
//		pArray[0] = new Point(150, 200);
//		pArray[1] = new Point(50, 300);
//		pArray[2] = new Point(250, 300);
//		pArray[3] = new Point(150, 200);
//		gLayer.addGraphic(new FillSymbol(new Polygon(pArray)));
//		nexdMap.addGraphicLayer(gLayer);
//		nexdMap.updateAllGraphicLayer();
	}
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
