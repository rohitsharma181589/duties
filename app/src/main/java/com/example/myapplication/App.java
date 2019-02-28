package com.example.myapplication;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class App extends Application {

    private static App instance;
   static RequestQueue requestQueue;

    public App() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        getInstance();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public static App getInstance() {
        return instance == null ? instance = new App() : instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue==null?requestQueue=Volley.newRequestQueue(getApplicationContext()):requestQueue;
    }

}
