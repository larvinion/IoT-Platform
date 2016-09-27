package com.socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonParser {
  //최종 완성될 JSONObject 선언(전체)
  JSONObject jsonObject;
  //person의 JSON정보를 담을 Array 선언
  JSONArray sensorArray;
  //person의 한명 정보가 들어갈 JSONObject 선언
  JSONObject sensorInfo;

  public JsonParser(){
    jsonObject = new JSONObject();
    sensorArray = new JSONArray();
    sensorInfo = new JSONObject();
  }

  public String createSersorDataFormat() {
    sensorInfo.put("id", "1");
    sensorInfo.put("payload", "25");
    //Array에 입력
    sensorArray.add(sensorInfo);

    sensorInfo.put("id", "2");
    sensorInfo.put("payload", "30");
    sensorArray.add(sensorInfo);

    jsonObject.put("SensorValue", sensorArray);
    String jsonInfo = jsonObject.toJSONString();

    return jsonInfo;
  }
}
