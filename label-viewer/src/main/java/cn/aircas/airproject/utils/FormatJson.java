package cn.aircas.airproject.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public class FormatJson {

    public static String vifToJson(JSONObject jsonObject) {
        JSONObject object = jsonObject.getJSONObject("判读标注");
        JSONArray childs = new JSONArray();

        if ((jsonObject.getJSONObject("判读标注")).getJSONObject("Children").get("Child") instanceof JSONObject){
            childs.put((jsonObject.getJSONObject("判读标注")).getJSONObject("Children").get("Child"));
        }
        else {
            childs = (jsonObject.getJSONObject("判读标注")).getJSONObject("Children").getJSONArray("Child");
        }

        List<JSONObject> childList = new ArrayList<>();
        for (Object child : childs) {
            JSONObject childTemp = new JSONObject(child.toString());
            JSONObject points = new JSONObject(child.toString());
            List<String> jsonList = new ArrayList<>();
            if (!new JSONObject(child.toString()).getJSONObject("GeoShape").toMap().containsKey("GeoShapePoint")) {
                JSONObject pointTemp = points.getJSONObject("GeoShape");
                jsonList.add(pointTemp.getJSONObject("lt").getDouble("x") + "," + pointTemp.getJSONObject("lt").getDouble("y"));
                jsonList.add(pointTemp.getJSONObject("rt").getDouble("x") + "," + pointTemp.getJSONObject("rt").getDouble("y"));
                jsonList.add(pointTemp.getJSONObject("rb").getDouble("x") + "," + pointTemp.getJSONObject("rb").getDouble("y"));
                jsonList.add(pointTemp.getJSONObject("lb").getDouble("x") + "," + pointTemp.getJSONObject("lb").getDouble("y"));
                pointTemp.remove("rt");
                pointTemp.remove("ld");
                pointTemp.remove("rd");
            } else {
                for (Object obj : new JSONObject(child.toString()).getJSONObject("GeoShape").getJSONArray("GeoShapePoint")) {
                    jsonList.add(new JSONObject(obj.toString()).getDouble("x") + "," + new JSONObject(obj.toString()).getDouble("y"));
                }
            }

            List<JSONObject> possibleresult = new ArrayList<>();
            JSONObject possible = new JSONObject();
            possible.put("name", new JSONObject(child.toString()).get("name"));
            possible.put("probability", 1);
            possibleresult.add(possible);
            childTemp.put("points", jsonList).put("possibleresult", possibleresult).put("description", "经纬度坐标").put("coordinate", "geodegree").remove("GeoShape");
            childList.add(childTemp);
        }
        object.put("objects", childList).remove("Children");
        JSONObject source = new JSONObject();
        source.put("filename", object.getString("imageSource").substring(jsonObject.getJSONObject("判读标注").getString("imageSource").lastIndexOf("\\") + 1));
        object.put("source", source);
        object.remove("imageSource");
        jsonObject.put("annotation", object).remove("判读标注");

        return jsonObject.toString();
    }


    public String updateJson(JSONObject jsonObject){

        JSONObject object = new JSONObject();
        JSONArray jsonArray = (jsonObject.getJSONObject("ImageInfo")).getJSONObject("result").getJSONArray("DetectResult");
        for (int i = 0; i< jsonArray.length(); i++){
            JSONObject jsonObj = (JSONObject) jsonArray.get(i);
            jsonObj.put("description","经纬度坐标");
            jsonObj.put("coordinate","geodegree");
            //jsonObj.discard("Location").discard("CenterLonLat").discard("Length").discard("Width").discard("Area").discard("Angle").discard("Probability").discard("ResultImagePath").discard("ValidationName");
        }
        object.put("objects",jsonArray);
        object.put("source",jsonObject.getJSONObject("ImageInfo").getJSONObject("BaseInfo"));
        JSONObject newJsonObj = new JSONObject();
        newJsonObj.put("ImageInfo",object);

        Map<String,String> keyMap = new HashMap<>();
        keyMap.put("ImageInfo","annotation");
        keyMap.put("BaseInfo","source");
        keyMap.put("@ID","id");
        keyMap.put("@description","filename");
        keyMap.put("@name","origin");
        keyMap.put("DetectResult","objects");
        keyMap.put("ResultID","id");
        keyMap.put("Shape","points");
        keyMap.put("PossibleResults","possibleresult");
        keyMap.put("Type","name");
        keyMap.put("Reliability","probability");

        JSONObject newJsonObject = changeJsonObj(newJsonObj,keyMap);
        return newJsonObject.toString();
    }

    public static JSONObject changeJsonObj(JSONObject jsonObject, Map<String, String> keyMap){
        JSONObject readJson = new JSONObject();
        Set<String> keySet = jsonObject.keySet();
        for (String key : keySet){
            String finalKey = keyMap.get(key) == null ? key : keyMap.get(key);
            try {
                JSONObject jsonObject1 = jsonObject.getJSONObject(key);
                readJson.put(finalKey,changeJsonObj(jsonObject1,keyMap));
            }catch (Exception e){
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray(key);
                    readJson.put(finalKey,changeJsonArr(jsonArray,keyMap));
                }catch (Exception x){
                    readJson.put(finalKey,jsonObject.get(key));
                }
            }

        }
        return readJson;
    }

    public static JSONArray changeJsonArr(JSONArray jsonArray, Map<String, String> keyMap){
        JSONArray readJson = new JSONArray();
        for (int i = 0; i < jsonArray.length() ; i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            readJson.put(changeJsonObj(jsonObject,keyMap));
        }
        return readJson;
    }

/*
    //json小改动
    public String possibleResultToArray(String jsonStr){
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray("objects");
        //JSONArray jsonArray = ((jsonObject.getJSONObject("annotation")).getJSONObject("objects")).getJSONArray("object");
        for (int i = 0; i< jsonArray.size(); i++){
            JSONObject object = (JSONObject) jsonArray.get(i);
            JSONArray jsonArr = new JSONArray();
            jsonArr.add(object.getJSONObject("possibleresult"));
            object.put("possibleresult",jsonArr);
        }
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("objects",jsonArray);
        jsonObj.put("source",jsonObject.get("source"));
        jsonObject.put("annotation",jsonObj);
        return jsonObject.toString();
    }*/


}
