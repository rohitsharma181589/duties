package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.interfacesPck.ItemClick;
import com.example.myapplication.interfacesPck.NetWorkResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.com.joinersa.oooalertdialog.Animation;
import br.com.joinersa.oooalertdialog.OnClickListener;
import br.com.joinersa.oooalertdialog.OoOAlertDialog;

import static com.example.myapplication.Constants.UPDATE_DUTY_STATUS_WITHOUT_BASE_URL;


/**
 * BCrypt link https://github.com/benjholla/Android-Applications/blob/master/Android%20Applications/Secrets/Secrets/src/org/mindrot/jbcrypt/BCrypt.java
 * Activity to perform all user actions for the required actions of the app.
 * This is an single activity application, all the operations are performed with this activity.
 */
public class MainActivity extends AppCompatActivity implements ItemClick {

    private final String TAG = MainActivity.class.getSimpleName();

    RecyclerView recyclerView;
    DutiesAdapter dutiesAdapter;
    ArrayList<Integer> dutiesItems = new ArrayList<>(0);

    private String curentState = "", userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_list);
        dutiesAdapter = new DutiesAdapter(dutiesItems, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(dutiesAdapter);

        getListOfDuties();
    }

    private void getListOfDuties() {
        Util.getInstance().checkAndShowNetworkConnectionToast(this);
        NetWorking.getInstance().getListOfDuties(new NetWorkResponse() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Log.w(TAG, "MSg");
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("array");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        dutiesItems.add(jsonArray.getInt(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dutiesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMsg) {
                Log.e(TAG, errorMsg);
            }
        });
    }

    private void showDialog(String title, String message, String positiveText, String negativeText) {
        new OoOAlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setAnimation(Animation.POP)
                .setPositiveButton(positiveText, new OnClickListener() {
                    @Override
                    public void onClick() {
                        Util.getInstance().checkAndShowNetworkConnectionToast(getApplicationContext());
                        changeDutyStatus();
                    }
                })
                .setNegativeButton(negativeText, null)
                .build();
    }


    private void changeDutyStatus() {
        switch (curentState) {
            case "PLANNED":
                //TODO: update the currentState tp In progress
                updateState("START", "23.333", "25.332", userId);
                break;
            case "IN_PROGRESS":
                //TODO: update the currentState tp Completed
                updateState("COMPLETE", "23.333", "25.332", userId);
                break;
            case "COMPLETED":
                //TODO: update the currentState tp START
                updateState("START", "23.333", "25.332", userId);
                break;
        }

        Toast.makeText(this, "Click Event", Toast.LENGTH_LONG).show();

    }


    //Checksum pattern: //bluPriv@8,START,BLU-SMART,23.333,25.332,1496982995000,/api/v1/app/update/duty/4359,puneet
    private void updateState(String action, String latitute, String longitude, String userId) {
        JSONObject jsonObject = new JSONObject();
        try {

            String timeStamp = String.valueOf(System.currentTimeMillis());
            String uri = UPDATE_DUTY_STATUS_WITHOUT_BASE_URL + userId;
            jsonObject.put("action", action);
            jsonObject.put("assigned", "BLU-SMART");
            jsonObject.put("latitude", latitute);
            jsonObject.put("longitude", longitude);
            jsonObject.put("timestamp", timeStamp);
            jsonObject.put("uri", uri);
            jsonObject.put("user", "puneet");

            String checksum = "bluPriv@8," + action + ",BLU-SMART," + latitute + "," + longitude + "," + timeStamp + ",/api/v1/app/update/duty/" + userId + ",puneet";

//            String generatedSecuredPasswordHash = BCrypt.withDefaults().hashToString(12, checksum.toCharArray());

//            String generatedSecuredPasswordHash  = BCrypt.with(LongPasswordStrategies.hashSha512()).hashToString(12, checksum.toCharArray());
            String generatedSecuredPasswordHash = com.example.myapplication.BCrypt.hashpw(checksum, com.example.myapplication.BCrypt.gensalt(12));


            NetWorking.getInstance().updateDutyStatus(userId, generatedSecuredPasswordHash, jsonObject, new NetWorkResponse() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(String errorMsg) {
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClick(String item) {

        //bluPriv@8,/api/v1/app/duty/4356

        Util.getInstance().checkAndShowNetworkConnectionToast(this);
        Util.getInstance().showLongToast("Getting details, Please wait.....",getApplicationContext());
//        Toast.makeText(this, "Getting details, Please wait.....", Toast.LENGTH_LONG).show();

        String stringtToHash;
        userId = item;

        stringtToHash = "bluPriv@8,/api/v1/app/duty/" + item;

//        String generatedSecuredPasswordHash = BCrypt.withDefaults().hashToString(12, stringtToHash.toCharArray());
        String generatedSecuredPasswordHash = com.example.myapplication.BCrypt.hashpw(stringtToHash, com.example.myapplication.BCrypt.gensalt(12));
        NetWorking.getInstance().getDutyDetail(item, generatedSecuredPasswordHash, new NetWorkResponse() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Log.w(TAG, "");
                String assigned = jsonObject.optString("assigned", "");
                int id = jsonObject.optInt("id", 0);
                String state = curentState = jsonObject.optString("state", "");
                String type = jsonObject.optString("type", "");

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Assigned To:");
                stringBuilder.append(assigned);
                stringBuilder.append("\n");
                stringBuilder.append("Driving State:");
                stringBuilder.append(state);
                stringBuilder.append("\n");
                stringBuilder.append("Driving type:");
                stringBuilder.append(type);

                showDialog("Details", stringBuilder.toString(), "Update State", "");
            }

            @Override
            public void onError(String errorMsg) {
                Log.e(TAG, errorMsg);
//                Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
                Util.getInstance().showLongToast("Please try again",getApplicationContext());
            }
        });


    }
}
