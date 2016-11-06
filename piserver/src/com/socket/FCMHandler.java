package com.socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FCMHandler {

	public final static String AUTH_KEY_FCM = "AIzaSyA5vEiSyl9xCoIona6PLgt48BDFFqocGQ8";
	public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

	public static void pushFCMNotification(String userDeviceIdKey) throws Exception {

		String authKey = AUTH_KEY_FCM;
		String FCMurl = API_URL_FCM;

		URL url = new(FCMurl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
	
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "key="+authKey);
		conn.setRequestProperty("Content-Type", "application/json");
	
		JSONObject json = new JSONObject();
		json.put("to", userDeviceIdKey.trim());
		JSONObject info = new JSONObject();
		info.put("title", "Notification Title");	// Notification Title
		info.put("body", "hello");	// Notification Body
		json.put("notification", info);
	
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(json.toString());
		wr.flush();
		conn.getInputStream();	
	}
}
