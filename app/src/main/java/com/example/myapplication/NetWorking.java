package com.example.myapplication;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.myapplication.interfacesPck.NetWorkResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetWorking {


    private static final NetWorking ourInstance = new NetWorking();

    public static NetWorking getInstance() {
        return ourInstance;
    }

    private NetWorking() {
    }


    void getListOfDuties(final NetWorkResponse netWorkResponse) {

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.GET_LIST_OF_DUTIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("", "" + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("array", jsonArray);
                            netWorkResponse.onSuccess(jsonObject);
                        } catch (JSONException e) {
                            netWorkResponse.onError("JSON Exception");
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                netWorkResponse.onError("VolleyError");

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>(2);
                headers.put("Content-Type", "application/json");
                headers.put("checksum", "$2y$12$JX5SwWv0SXWAqLn5bUVNmuOJPnzRR4Pnwf8M0nfrFBtNKG.0g.gw2");
                return headers;
            }
        };

        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy());
        App.getInstance().getRequestQueue().add(stringRequest);
    }

    void getDutyDetail(String userId, final String checksum, final NetWorkResponse netWorkResponse) {

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.GET_DETAILS_OF_DUTY + userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w("", "" + response);
                        try {
                            netWorkResponse.onSuccess(new JSONObject(response));
                        } catch (JSONException e) {
                            netWorkResponse.onError("JSON Exception");
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                netWorkResponse.onError("VolleyError");

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>(2);
                headers.put("Content-Type", "application/json");
                headers.put("checksum", checksum);
                return headers;
            }
        };

        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy());
        App.getInstance().getRequestQueue().add(stringRequest);

    }

    public void updateDutyStatus(String userId, final String checksum, JSONObject body, final NetWorkResponse netWorkResponse) {

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, Constants.UPDATE_DUTY_STATUS + userId,
                body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                netWorkResponse.onSuccess(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                netWorkResponse.onError(error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>(2);
                headers.put("Content-Type", "application/json");
                headers.put("checksum", checksum);
                return headers;
            }
        };

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy());
        App.getInstance().getRequestQueue().add(jsonObjectRequest);

    }


}
