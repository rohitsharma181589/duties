package com.example.myapplication.interfacesPck;

import org.json.JSONObject;

public interface NetWorkResponse {

   void onSuccess(JSONObject jsonObject);
   void onError(String errorMsg);
}
