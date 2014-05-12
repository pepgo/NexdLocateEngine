package com.nexdgis;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gy.nexdgis.R;
import com.nexdgis.database.NexdDatabase;
import com.nexdgis.feature.Feature;
import com.nexdgis.feature.MapFeatureTable;
import com.nexdgis.geometry.Point;
import com.nexdgis.layer.TopLayer;
import com.nexdgis.layer.opengl.NexdGLRenderer;
import com.nexdgis.layer.widget.OnFeatureSelectedListener;
import com.nexdgis.layer.widget.OnUserClickListener;
import com.nexdgis.log.NexdLog;
import com.nexdgis.remote.*;

public class BuildingActivity extends Activity {

	String buildingIntentName;
	ArrayList<String> floorList;
	ListView listView;
	String currentFloor;
	NexdDatabase test;
	String floorFilePath;
	Button locatorButton;
	NexdEngine nexdEngine;
	SensorManager mSensorManager;
	Sensor mSensorMagnet,mSensorAccel;
	Point locatePos;
	Point currentPos;

	public void onCreate(Bundle savedInstanceStateBundle) {
		super.onCreate(savedInstanceStateBundle);
		setContentView(R.layout.building_activity);

		Bundle bundle = this.getIntent().getExtras();
		buildingIntentName = bundle.getString("BuildingIntentName");

		File building = new File(Environment.getExternalStorageDirectory().getPath() + "/map_xml/" + buildingIntentName);
		File[] floors = building.listFiles();
		floorList = new ArrayList<String>();

		for (int i = 0; i < floors.length; i++) {
			floorList.add(floors[i].getName());
		}

		test = NexdDatabase.getDbInstance();

		currentFloor = floorList.get(0);
		floorFilePath = Environment.getExternalStorageDirectory().getPath() + "/map_xml/" + buildingIntentName + "/" + currentFloor;
		locatorButton = (Button) findViewById(R.id.button1);
		initViews();
		initSensor();
	}

	Handler handler = new Handler();

	Runnable runnable = new Runnable() {
		public void run() {
			System.out.println("change floor to:" + currentFloor);
			BuildingActivity.this.setContentView(R.layout.building_activity);

			test = NexdDatabase.getDbInstance();
			initViews();
			initSensorPos();
		}
	};
	NexdMap nexdMap;
	Handler updateHandler;
	void initViews() {
		MapFeatureTable.releaseTable();
		NexdGLRenderer.initialized = false;
		nexdMap = (NexdMap)findViewById(R.id.nexd_map);
		updateHandler = new Handler(){
			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				if (msg.what == 1)
				{
					if (currentPos!=null && currentPos.x>-998 && currentPos.y>-998)
						nexdMap.updateLocator();
				}

			}
		};
		test.syncRenderer(this, "render.xml");

		//		test.syncFeature(this, "FengLianGuangChang.FloorF2.xml");
		test.syncFeature(floorFilePath);

		//		nexdMap.setMapFeatureLayer(new MapFeatureLayer(this, true));
		TopLayer topLayer = nexdMap.getTopLayer();
		topLayer.enableLocator();
		topLayer.enableCallout();
		topLayer.getCallout().setContent(R.layout.callout);
		topLayer.getCallout().setHeight(200);
		//		nexdMap.setTopLayer(topLayer);

		View view = topLayer.getCallout().getContent();
		final TextView textView = (TextView)view.findViewById(R.id.text_view);
		final Button button = (Button)view.findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				textView.setText(R.string.clicked);
			}

		});
		nexdMap.setOnFeatureSelectedListener(new OnFeatureSelectedListener() {

			@Override
			public void onSelected(Feature feature) {
				textView.setText("FeatureID : "+feature.id);
			}

		});
		nexdMap.setOnUserClickListener(new OnUserClickListener(){

			@Override
			public void onClick(float x, float y) {
				// TODO Auto-generated method stub
				NexdLog.tagInfo("clickMapPos",+x+"    "+y);
				
			}
			
		});


		listView = (ListView) findViewById(R.id.floorlistview);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, floorList);
		listView.setAdapter(arrayAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String selectedFloor = floorList.get(arg2);
				if (selectedFloor.equals(currentFloor)) {
					//					do nothing
				} else {
					currentFloor = selectedFloor;
					floorFilePath = Environment.getExternalStorageDirectory().getPath() + "/map_xml/" + buildingIntentName + "/" + currentFloor;
					//					BuildingActivity.this.runOnUiThread(runnable);
					nexdMap.addView(new View(nexdMap.getContext()) {
						Paint paint = new Paint();
						@Override
						protected void onDraw(Canvas canvas) {
							paint.setAntiAlias(true);
							paint.setTextSize(50);
							paint.setColor(Color.BLUE);
							canvas.drawColor(Color.WHITE);
							canvas.drawText("Loading...", getWidth()/2, getHeight()/2, paint);
						}
					});
					handler.post(runnable);
				}
			}
		});
		nexdEngine = NexdEngine.getEngine();
//		nexdEngine.setEngine(this, buildingIntentName, currentFloor, new NexdCallBackListener() {
//			public void call(Object object) {
//				if (object instanceof android.graphics.Point) {
//					int x = ((android.graphics.Point)object).x;
//					int y = ((android.graphics.Point)object).y;
//					//		                                setLocatorTo(x, y);
//					nexdMap.setLocator(x, y);
//					return;
//				}
//				if (object instanceof String) {
//					new ToastUtil(getApplicationContext()).showToast("Detected floor:" + (String)object);
//				}
//				if (object instanceof Integer) {
//					switch ((Integer)object) {
//					case NexdEngine.LOCATING_FINISHED:
//						BuildingActivity.this.locatorButton.setEnabled(true);
//						break;
//					case NexdEngine.LOCATING_ERROR:
//						new ToastUtil(getApplicationContext()).showToast("Generating info data");
//						break;
//					case NexdEngine.LOCATING_FAILED:
//						new ToastUtil(getApplicationContext()).showToast("Locating failed");
//						break;
//					case NexdEngine.LOCATING_EXCEPTION:
//						new ToastUtil(getApplicationContext()).showToast("Result exception");
//						break;
//					case NexdEngine.NETWORK_TIMEOUT:
//						new ToastUtil(getApplicationContext()).showToast("Timeout");
//						break;
//					case NexdEngine.SOCKET_EXCEPTION:
//						new ToastUtil(getApplicationContext()).showToast("Socket error");
//						break;
//					default:
//						break;
//					}
//					return;
//				}
//			}
//		});
		locatorButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				nexdEngine.setEngine(BuildingActivity.this, buildingIntentName, currentFloor, new NexdCallBackListener() {
					public void call(Object object) {
						if (object instanceof android.graphics.Point) {
							int x = ((android.graphics.Point)object).x;
							int y = ((android.graphics.Point)object).y;
							//				                                setLocatorTo(x, y);
							NexdLog.tagInfo("log", x+" "+y);
							locatePos.x = x;
							locatePos.y = y;
			//				nexdMap.setLocator(x, y);
							//				                                nexdMap.updateLocator();
							return;
						}
						if (object instanceof String) {
							new ToastUtil(getApplicationContext()).showToast("Detected floor:" + (String)object);
						}
						if (object instanceof Integer) {
							switch ((Integer)object) {
							case NexdEngine.LOCATING_FINISHED:
								BuildingActivity.this.locatorButton.setEnabled(true);
								break;
							case NexdEngine.LOCATING_ERROR:
								new ToastUtil(getApplicationContext()).showToast("Generating info data");
								break;
							case NexdEngine.LOCATING_FAILED:
								new ToastUtil(getApplicationContext()).showToast("Locating failed");
								break;
							case NexdEngine.LOCATING_EXCEPTION:
								new ToastUtil(getApplicationContext()).showToast("Result exception");
								break;
							case NexdEngine.NETWORK_TIMEOUT:
								new ToastUtil(getApplicationContext()).showToast("Timeout");
								break;
							case NexdEngine.SOCKET_EXCEPTION:
								new ToastUtil(getApplicationContext()).showToast("Socket error");
								break;
							default:
								break;
							}
							return;
						}
					}
				});
				locatorButton.setEnabled(false);
				nexdEngine.startSerialLocate(2,1);
			}
		});
	}
	float lowpass = 0.0f;
	private float low_pass (float input)  
    {  
    	if (Math.abs(input - lowpass) > 3) {
			lowpass = input;
		} else {
			lowpass = input * 0.1f + lowpass * 0.9f;  
		}
        return lowpass;  
    }  
	
	private void location_low_pass (float input_x, float input_y) {
		currentPos.x = input_x * 0.1f + currentPos.x * 0.9f;
		currentPos.y = input_y * 0.1f + currentPos.y * 0.9f;
	}
    
	double rotationRate;
	float[] accelerometerValues = new float[3];
	float[] magneticFieldValues = new float[3];
//	float north = 0.0;
	private void calculate() {
		float[] values = new float[3];
		float[] R = new float[9];
		SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
		SensorManager.getOrientation(R, values);
//		if the location is already recived from server
		rotationRate = low_pass(values[0]);
		location_low_pass(locatePos.x, locatePos.y);
		nexdMap.setLocator(currentPos.x, currentPos.y);
		nexdMap.updateLocator();
//		Message msg = new Message();
//		msg.what = 1;
//		updateHandler.sendMessage(msg);
//		rotateLocator(low_pass(values[0]) - north);// - 3.1415926 / 2.0));
	}
	private void initSensor()
	{
		final SensorEventListener magEventListener = new SensorEventListener() {
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)  
					magneticFieldValues = event.values;  
				if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)  
					accelerometerValues = event.values;
				if (locatePos.x > -998.0 && locatePos.y > -998.0) {
					calculate();
				}
			}
				
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
			}
		};
		locatePos = new Point (-999,-999);
		currentPos = new Point(-999,-999);
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
			if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
				mSensorMagnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
				mSensorAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				mSensorManager.registerListener(magEventListener, mSensorMagnet, 70000);
				mSensorManager.registerListener(magEventListener, mSensorAccel, 70000);
			} else {
			}
	}
	private void initSensorPos()
	{
		locatePos = new Point (-999,-999);
		currentPos = new Point(-999,-999);
		
	}
}
