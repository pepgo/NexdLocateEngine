package com.nexd.tecenttest.CoreEngine;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;



import android.content.Context;
import android.graphics.Point;
import android.net.wifi.ScanResult;

//	All locate related functions could only be invoked by an instance of NexdEngine



public class NexdEngine {
	protected static final String BASE_URL = "http://115.28.46.208:23333";
//	protected static final String BASE_URL = "http://192.168.166.2:8889";
	WifiScanner wifiScanner;
	Context context;
	int singleSMP;
	int singleGap;
	int singleCount;
	ArrayList<List<ScanResult>> singleResults;
	StringBuilder singleContent;
	int serialSMP;
	int serialGap;
	int serialCount;
	ArrayList<List<ScanResult>> serialResults;
	StringBuilder serialContent;
	Point locationPoint;
	String buildingName;
	String floorName;
	String userID;
	StringBuilder wifiContent;
	NexdCallBackListener listener;

	public static final int LOCATING_FINISHED = 0;
	public static final int LOCATING_ERROR = -1;
	public static final int LOCATING_FAILED = -2;
	public static final int LOCATING_EXCEPTION = -3;
	
	private ExecutorService pool = Executors.newSingleThreadExecutor();
	private ExecutorService requestPool = Executors.newSingleThreadExecutor();
	private HttpClient httpClient = new DefaultHttpClient();

	private static NexdEngine engine = new NexdEngine();
	
	private NexdEngine(){}
	
	public static NexdEngine getEngine() {
		return engine;
	}
	
	public void setEngine(Context cxt, String bdn, String fln, NexdCallBackListener lst) {
		this.context = cxt;
		this.buildingName = bdn;
		this.floorName = fln;
		this.listener = lst;
		this.locationPoint = new Point();
		this.wifiContent = new StringBuilder();
		this.wifiScanner = new WifiScanner(context);
		this.userID = wifiScanner.getMacAddress();
		this.singleResults = new ArrayList<List<ScanResult>>();
		this.serialResults = new ArrayList<List<ScanResult>>();
		this.singleContent = new StringBuilder();
		this.serialContent = new StringBuilder();
	}
	
	public void startSingleLocate(int smp, int gap) {
		singleSMP = smp;
		singleGap = gap;
		singleResults.clear();
		singleCount = 0;
		singleContent.setLength(0);
		wifiScanner.setNexdCallBackListener(new NexdCallBackListener() {
			
			@SuppressWarnings("unchecked")
			public void call(Object object) {
				// TODO Auto-generated method stub
				if (object instanceof List<?>) {
					if (singleCount < singleSMP) {
						singleResults.add((ArrayList<ScanResult>)object);
						saveSingleResults();
						singleCount++;
						if (singleCount % singleGap == 0 && singleCount != singleSMP) {
//							send locate data
							sendSingleLocateRequest();
						}
					} 
					if (singleCount == singleSMP) {
						sendSingleLocateRequest();
						wifiScanner.stopScan();
						listener.call(LOCATING_FINISHED);
					}
				}
			}
		});
		wifiScanner.startWifiScan();
	}
	
	private void saveSingleResults() {
		pool.execute(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					synchronized (singleResults) {
						if (singleResults.size() > 0) {
							for (ScanResult result : singleResults.get(singleResults.size() - 1)) {
								singleContent.append(result.SSID + "\t" + result.BSSID + "\t" + result.frequency + "\t" + result.level + "\t" + result.capabilities);
								singleContent.append("\n");
							}
						}
						singleContent.append("--------------------------------------\n");
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void sendSingleLocateRequest() {
		pool.execute(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				String urlServerString = BASE_URL + "/IKAServer/locating?user=" + userID + "&floor=" + floorName + "&building=" + buildingName;
				httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				final HttpPost httpPost = new HttpPost(urlServerString);
				try {
					System.out.println(singleContent.toString() + "\n********************************");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					GZIPOutputStream gzip = new GZIPOutputStream(baos);
					gzip.write(singleContent.toString().getBytes("utf-8"));
					gzip.finish();
					httpPost.setEntity(new ByteArrayEntity(baos.toByteArray()));
					gzip.close();
					requestPool.execute(new Runnable() {
						
						public void run() {
							// TODO Auto-generated method stub
							try {
								HttpResponse response = httpClient.execute(httpPost);
								String resString;
								if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
									resString = EntityUtils.toString(response.getEntity());
									JSONObject object = new JSONObject(resString);
									int status = object.getInt("status");
									System.out.println("Get a response with json object:" + object);
									if (status == 0x10) {
										locationPoint.set(object.getInt("x") * 6, object.getInt("y") * 6);
										listener.call(locationPoint);
									} else if (status == 0x01) {
										listener.call(LOCATING_ERROR);
									} else {
										listener.call(LOCATING_FAILED);
									}
								}
							}catch (Exception e ){
								e.printStackTrace();
								listener.call(LOCATING_EXCEPTION);
							}
						}
					});
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
	}
	
	public void startSerialLocate(int smp, int gap) {
		serialSMP = smp;
		serialGap = gap;
		serialCount = 0;
		serialResults.clear();
		serialContent.setLength(0);
		wifiScanner.setNexdCallBackListener(new NexdCallBackListener() {
			
			@SuppressWarnings("unchecked")
			public void call(Object object) {
				// TODO Auto-generated method stub
				if (object instanceof List<?>) {
					if (serialResults.size() < serialSMP) {
						serialResults.add((ArrayList<ScanResult>)object);
						if (serialResults.size() == serialSMP) {
//							send locate data
							saveSerialResults();
							sendSerialLocateRequest();
						}
					} else {
						serialResults.remove(0);
						serialResults.add((ArrayList<ScanResult>)object);
						serialCount++;
						if (serialCount == serialGap) {
							serialCount = 0;
//							send locate data
							saveSerialResults();
							sendSerialLocateRequest();
						}
					}
				}
			}
		});
		wifiScanner.startWifiScan();
	}
	
	public void stopSerialLocate() {
		wifiScanner.stopScan();
	}
	
	private void saveSerialResults() {
		
		pool.execute(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				synchronized (serialResults) {
					serialContent.setLength(0);
					if (serialResults.size() > 0) {
						for (int i = 0 ; i < serialResults.size() ; i++) {
							for (ScanResult result : serialResults.get(i)) {
								serialContent.append(result.SSID + "\t" + result.BSSID + "\t" + result.frequency + "\t" + result.level + "\t" + result.capabilities);
								serialContent.append("\n");
							}
							serialContent.append("--------------------------------------\n");
						}
					}
				}
			}
		});
	}
	
	private void sendSerialLocateRequest() {
		pool.execute(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				String urlServerString = BASE_URL + "/IKAServer/locating?user=" + userID + "&floor=" + floorName + "&building=" + buildingName;
				httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				final HttpPost httpPost = new HttpPost(urlServerString);
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					GZIPOutputStream gzip = new GZIPOutputStream(baos);
					gzip.write(serialContent.toString().getBytes("utf-8"));
					System.out.println(serialContent.toString());
					System.out.println("***************************");
					gzip.finish();
					httpPost.setEntity(new ByteArrayEntity(baos.toByteArray()));
					gzip.close();
					requestPool.execute(new Runnable() {
						public void run() {
							try {
								HttpResponse response = httpClient.execute(httpPost);
								String resString;
								if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
									resString = EntityUtils.toString(response.getEntity());
									JSONObject object = new JSONObject(resString);
									int status = object.getInt("status");
									System.out.println("Get a response with json object:" + object);
									if (status == 0x10) {
										locationPoint.set(object.getInt("x") * 6, object.getInt("y") * 6);
										listener.call(locationPoint);
									} else if (status == 0x01) {
										listener.call(LOCATING_ERROR);
									} else {
										listener.call(LOCATING_FAILED);
									}
								}
							}catch (Exception e ){
								e.printStackTrace();
								listener.call(LOCATING_EXCEPTION);
							}
						}
					});
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
	}

	public void startPOILocate(int smp) {
		singleSMP = smp;
		singleResults.clear();
		singleCount = 0;
		singleContent.setLength(0);
		wifiScanner.setNexdCallBackListener(new NexdCallBackListener() {
			
			@SuppressWarnings("unchecked")
			public void call(Object object) {
				// TODO Auto-generated method stub
				if (object instanceof List<?>) {
					if (singleCount < singleSMP) {
						singleResults.add((ArrayList<ScanResult>)object);
						saveSingleResults();
						singleCount++;
					}
					if (singleCount == singleSMP) {
						sendPOIRequest();
						wifiScanner.stopScan();
						listener.call(LOCATING_FINISHED);
					}
				}
			}
		});
		wifiScanner.startWifiScan();
	}
	
	private void sendPOIRequest() {
		pool.execute(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				String urlServerString = BASE_URL + "/IKAServer/locating?user=" + userID + "&floor=" + floorName + "&building=" + buildingName;
				httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				final HttpPost httpPost = new HttpPost(urlServerString);
				try {
					System.out.println(singleContent.toString() + "\n********************************");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					GZIPOutputStream gzip = new GZIPOutputStream(baos);
					gzip.write(singleContent.toString().getBytes("utf-8"));
					gzip.finish();
					httpPost.setEntity(new ByteArrayEntity(baos.toByteArray()));
					gzip.close();
					requestPool.execute(new Runnable() {
						
						public void run() {
							// TODO Auto-generated method stub
							try {
								HttpResponse response = httpClient.execute(httpPost);
								String resString;
								if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
									resString = EntityUtils.toString(response.getEntity());
									JSONObject object = new JSONObject(resString);
									int status = object.getInt("status");
									System.out.println("Get a response with json object:" + object);
									if (status == 0x10) {
//										listener should call an ArrayList<String>
									} else if (status == 0x01) {
										listener.call(LOCATING_ERROR);
									} else {
//										for test
										ArrayList<POI> arrayList = new ArrayList<POI>();
										arrayList.add(new POI("Lancome", 1, 12.4f, "Feel Young"));
										arrayList.add(new POI("Chanel", 2, 22.7f, "Miss CoCo"));
										arrayList.add(new POI("Hermes", 3, 34.4f, "Only Hermes"));
										arrayList.add(new POI("Lamer", 4, 36.3f, "From Ocean"));
										arrayList.add(new POI("Fancl", 5, 61.9f, "You Know Me"));
										listener.call(arrayList);
										listener.call(LOCATING_FAILED);
									}
								}
							}catch (Exception e ){
								e.printStackTrace();
								listener.call(LOCATING_EXCEPTION);
							}
						}
					});
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
	}
	
	public String getUserID() {
		return userID;
	}

	public void releasePool() {
		pool.shutdownNow();
		requestPool.shutdownNow();
	}
}