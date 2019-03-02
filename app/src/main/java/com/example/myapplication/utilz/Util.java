package com.example.myapplication.utilz;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.myapplication.Constants;
import com.example.myapplication.NetWorking;
import com.example.myapplication.interfacesPck.NetWorkResponse;

import org.json.JSONException;
import org.json.JSONObject;

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

    public  void checkAndShowNetworkConnectionToast(Context context){
        if (!checkInternetConnection(context))
            showLongToast("Internet Connection required to perform this action",context);
    }

    public   void showLongToast(String msg, Context context){

        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    public void showShortToast(String msg, Context context){

        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    //Checksum pattern: //bluPriv@8,START,BLU-SMART,23.333,25.332,1496982995000,/api/v1/app/update/duty/4359,puneet
    public  void updateState(String action, String latitute, String longitude, String userId, final Context context) {
        JSONObject jsonObject = new JSONObject();
        try {

            String timeStamp = String.valueOf(System.currentTimeMillis());
            String uri = Constants.UPDATE_DUTY_STATUS_WITHOUT_BASE_URL + userId;
            jsonObject.put("action", action);
            jsonObject.put("assigned", "BLU-SMART");
            jsonObject.put("latitude", latitute);
            jsonObject.put("longitude", longitude);
            jsonObject.put("timestamp", timeStamp);
            jsonObject.put("uri", uri);
            jsonObject.put("user", "puneet");

            String checksum = "bluPriv@8," + action + ",BLU-SMART," + latitute + "," + longitude + "," + timeStamp + ",/api/v1/app/update/duty/" + userId + ",puneet";
            String generatedSecuredPasswordHash = com.example.myapplication.BCrypt.hashpw(checksum, com.example.myapplication.BCrypt.gensalt(12));

            NetWorking.getInstance().updateDutyStatus(userId, generatedSecuredPasswordHash, jsonObject, new NetWorkResponse() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
//                    showLongToast(jsonObject.toString(),context);
                    showShortToast("Updated Successfully",context);
                }

                @Override
                public void onError(String errorMsg) {
                    showShortToast(errorMsg,context);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            showShortToast("Some error happened! Please try later",context);
        }
    }
}
