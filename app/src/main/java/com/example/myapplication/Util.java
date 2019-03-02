package com.example.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Util {
    private static final Util ourInstance = new Util();

    public static Util getInstance() {
        return ourInstance;
    }

    private Util() {
    }


    private boolean checkInternetConnection(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void checkAndShowNetworkConnectionToast(Context context){
        if (!checkInternetConnection(context))
            showLongToast("Internet Connection required to perform this action",context);
    }

    public void showLongToast(String msg,Context context){

        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    public void showShortToast(String msg,Context context){

        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
