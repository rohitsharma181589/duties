package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.interfacesPck.ItemClick;
import com.example.myapplication.interfacesPck.NetWorkResponse;
import com.example.myapplication.utilz.GpsUtils;
import com.example.myapplication.utilz.Util;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.com.joinersa.oooalertdialog.Animation;
import br.com.joinersa.oooalertdialog.OnClickListener;
import br.com.joinersa.oooalertdialog.OoOAlertDialog;


/**
 * BCrypt link https://github.com/benjholla/Android-Applications/blob/master/Android%20Applications/Secrets/Secrets/src/org/mindrot/jbcrypt/BCrypt.java
 * Activity to perform all user actions for the required actions of the app.
 * This is an single activity application, all the operations are performed with this activity.
 */
public class MainActivity extends AppCompatActivity implements ItemClick {

    private final String TAG = MainActivity.class.getSimpleName();
    private DutiesAdapter dutiesAdapter;
    private ArrayList<Integer> dutiesItems = new ArrayList<>(0);
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;
    private String curentState = "", userId = "";
    private double wayLatitude = 0.0, wayLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        RecyclerView recyclerView = findViewById(R.id.rv_list);
        dutiesAdapter = new DutiesAdapter(dutiesItems, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(dutiesAdapter);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds


        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        mFusedLocationClient.removeLocationUpdates(locationCallback);
                        getListOfDuties();
                    }
                }
            }
        };

        if (!isGPS) {
            Util.getInstance().showLongToast("Please on GPS of Device", getApplicationContext());
            return;
        }

        // GPS is on ask for permission and proceed.
        getLocation();

    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.LOCATION_REQUEST);

        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        getListOfDuties();

                    } else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();
                                getListOfDuties();
                            } else {
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
                getLocation();
            }
        }
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

    private void showDialog(String title, String message, String positiveButton, final boolean dialogForLocation) {
        new OoOAlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setAnimation(Animation.POP)
                .setPositiveButton(positiveButton, new OnClickListener() {
                    @Override
                    public void onClick() {
                        if (!dialogForLocation) {
                            Util.getInstance().checkAndShowNetworkConnectionToast(getApplicationContext());
                            changeDutyStatus();
                        } else {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("", null)
                .build();
    }


    private void changeDutyStatus() {
        switch (curentState) {
            case "PLANNED":
                Util.getInstance().updateState("START", String.valueOf(wayLatitude), String.valueOf(wayLongitude), userId, getApplicationContext());
                break;
            case "IN_PROGRESS":
                Util.getInstance().updateState("COMPLETE", "23.333", "25.332", userId, getApplicationContext());
                break;
            case "COMPLETED":
                Util.getInstance().updateState("START", "23.333", "25.332", userId, getApplicationContext());
                break;
        }
    }


    @Override
    public void onItemClick(String item) {

        //bluPriv@8,/api/v1/app/duty/4356

        Util.getInstance().checkAndShowNetworkConnectionToast(this);
        Util.getInstance().showLongToast("Getting details, Please wait.....", getApplicationContext());

        String stringtToHash;
        userId = item;
        stringtToHash = "bluPriv@8,/api/v1/app/duty/" + item;
        String generatedSecuredPasswordHash = com.example.myapplication.BCrypt.hashpw(stringtToHash, com.example.myapplication.BCrypt.gensalt(12));
        NetWorking.getInstance().getDutyDetail(item, generatedSecuredPasswordHash, new NetWorkResponse() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Log.w(TAG, "");
                String assigned = jsonObject.optString("assigned", "");
                int id = jsonObject.optInt("id", 0);
                String state = curentState = jsonObject.optString("state", "");
                String type = jsonObject.optString("type", "");

                String msg = "Assigned To:" +
                        assigned +
                        "\n" +
                        "Driving State:" +
                        state +
                        "\n" +
                        "Driving type:" +
                        type;
                showDialog("Details", msg, "Update State", false);
            }

            @Override
            public void onError(String errorMsg) {
                Log.e(TAG, errorMsg);
                Util.getInstance().showLongToast("Please try again", getApplicationContext());
            }
        });
    }
}
