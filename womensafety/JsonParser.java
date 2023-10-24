package com.example.womensafety;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private HashMap<String,String> parseJsonObject(JSONObject object){
        HashMap<String,String> hashMap=new HashMap<>();

        try {
            String name=object.getString("name");
            String latitude=object.getJSONObject("geometry").getJSONObject("location").getString("lat");
            String longitude=object.getJSONObject("geometry").getJSONObject("location").getString("lng");

            hashMap.put("name",name);
            hashMap.put("lat",latitude);
            hashMap.put("lng",longitude);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  hashMap;
    }
    private List<HashMap<String,String>> parseJsonArrray(JSONArray jsonArray) {
        List<HashMap<String,String>> hashMap1 = new ArrayList<>();

        for(int i=0;i<jsonArray.length();i++){

            try {
                HashMap<String,String> data=parseJsonObject((JSONObject)jsonArray.get(i));
                hashMap1.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return hashMap1;
    }

    public List<HashMap<String,String>> parseResult(JSONObject object){

        JSONArray jsonArray=null;
        try {
            jsonArray=object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parseJsonArrray(jsonArray);
    }


}
