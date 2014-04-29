package com.nexd.tecenttest.CoreEngine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiScanner {
	String macAddress;
	WifiManager manager;
	Context context;
	boolean started = false;
	WifiReceiver receiver;
	NexdCallBackListener nexdListener;
	
	private final class WifiReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (started == false || nexdListener == null) {
				return;
			} else if (started && nexdListener != null) {
				List<ScanResult> results = manager.getScanResults();
				nexdListener.call(results);
				scan();
				return;
			}
		}
	}
	
	public WifiScanner(Context context) {
		super();
		this.context = context;
		this.manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		context.registerReceiver(receiver = new WifiReceiver(), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		WifiInfo info = (null == this.manager ? null : this.manager.getConnectionInfo());
		if (null != info) {
			macAddress = info.getMacAddress();
		} else {
			macAddress = "UNKNOWN";
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (context != null) {
			context.unregisterReceiver(receiver);
		}
	}
	
	public String getMacAddress() {
		return macAddress;
	}
	
	public void setNexdCallBackListener(NexdCallBackListener listener) {
		this.nexdListener = listener;
	}
	
	public void startWifiScan() {
		started = true;
		this.scan();
	}
	
	public void stopScan() {
		started = false;
		nexdListener = null;
	}

	public boolean isStarted() {
		return started;
	}
	
	public void scan() {
		manager.startScan();
	}
	
	public void newScan() throws NoSuchMethodException {
		Method startScanActiveMethod = WifiManager.class.getMethod("startScanActive");
		try {
			startScanActiveMethod.invoke(manager);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
