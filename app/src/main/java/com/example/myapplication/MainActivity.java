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

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.joinersa.oooalertdialog.Animation;
import br.com.joinersa.oooalertdialog.OnClickListener;
import br.com.joinersa.oooalertdialog.OoOAlertDialog;

public class MainActivity extends AppCompatActivity implements ItemClick {

    private final String TAG = MainActivity.class.getSimpleName();

    RecyclerView recyclerView;
    DutiesAdapter dutiesAdapter;
    ArrayList<Integer> dutiesItems = new ArrayList<>(0);

    private String curentState = "";

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
                        changeDutyStatus();
                    }
                })
                .setNegativeButton(negativeText, null)
                .build();
    }

    private void changeDutyStatus() {
        switch (curentState) {
            case "START":
                //TODO: update the currentState tp In progress
                updateState("IN_PROGRESS");
                break;
            case "IN_PROGRESS":
                //TODO: update the currentState tp Completed
                updateState("COMPLETED");
                break;
            case "COMPLETED":
                //TODO: update the currentState tp START
                updateState("START");
                break;
        }

        Toast.makeText(this, "Click Event", Toast.LENGTH_LONG).show();

    }

    private void updateState(String action){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("action",action);
            jsonObject.put("assigned","BLU-SMART");
            jsonObject.put("timestamp",action);
            jsonObject.put("user","puneet");
            jsonObject.put("latitude",action);
            jsonObject.put("longitude",action);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(String item) {

        //bluPriv@8,/api/v1/app/duty/4356

        Toast.makeText(this, "Getting details, Please wait.....", Toast.LENGTH_LONG).show();

        String stringtToHash;

        stringtToHash = "bluPriv@8,/api/v1/app/duty/" + item;

        String generatedSecuredPasswordHash = BCrypt.withDefaults().hashToString(12, stringtToHash.toCharArray());

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
                Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
            }
        });


    }
}
