package com.socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonParser {
  JSONObject jsonObject;
  JSONArray sensorArray;
  JSONObject sensorInfo;

  public JsonParser(){
    jsonObject = new JSONObject();
    sensorArray = new JSONArray();
    sensorInfo = new JSONObject();
  }

  public String createSersorDataFormat() {
    sensorInfo.put("id", "1");
    sensorInfo.put("payload", "25");
    //Array
    sensorArray.add(sensorInfo);

    sensorInfo.put("id", "2");
    sensorInfo.put("payload", "30");
    sensorArray.add(sensorInfo);

    jsonObject.put("SensorValue", sensorArray);
    String jsonInfo = jsonObject.toJSONString();

    return jsonInfo;
  }
}
