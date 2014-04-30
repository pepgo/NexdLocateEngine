NexdLocateEngine
================

core engine to scan wifi result and push request to server

##Usage:
####Init
`NexdEngine nexdEngine = NexdEngine.getEngine();`

####Set Engine
	nexdEngine.setEngine(this, buildingIntentName, selectedFloorName, new NexdCallBackListener() {
		public void call(Object object) {
								if (object instanceof android.graphics.Point) {
									int x = ((android.graphics.Point)object).x;
									int y = ((android.graphics.Point)object).y;
									setLocatorTo(x, y);
								return;
								}
								if (object instanceof String) {
								new ToastUtil(getApplicationContext()).showToast("Detected floor:" + (String)object);
								}
								if (object instanceof Integer) {
									switch ((Integer)object) {
									case NexdEngine.LOCATING_FINISHED:
										//Whats LocateActivity????
										LocateActivity.this.locateButton.setEnabled(true);
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
									//NETWORK_TIMEOUT has no definition
									case NexdEngine.NETWORK_TIMEOUT:
										new ToastUtil(getApplicationContext()).showToast("Timeout");
										break;
									//SOCKET_EXCEPTION has no definition
									case NexdEngine.SOCKET_EXCEPTION:
										new ToastUtil(getApplicationContext()).showToast("Socket error");
										break;
									default:
										break;
									}
									return;
								}
							}
		}
	});
The `NexdCallBackListener` is a interface in `NexdCallBackListener.java`

####Start Locating
	
	nexdEngine.startSerialLocate(8, 2);	
	//the number 8 and 2 can be changed to any depends on the requirement
	
####Stop Locating
	
	nexdEngine.stopSerialLocate();
	
####User Permission
Add codes below in the `user-permission` part in AndroidManifest.xml:

	<uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.DISABLE_KEYGUARD" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
	<uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />  