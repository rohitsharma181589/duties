package com.example.myapplication;

public class Constants {

    static String BASE_URL = "http://52.221.213.45:5000";

    public static String GET_LIST_OF_DUTIES = BASE_URL + "/api/v1/app/dutyid/list";
    public static String GET_DETAILS_OF_DUTY = BASE_URL + "/api/v1/app/duty/";
    public static String UPDATE_DUTY_STATUS = BASE_URL + "/api/v1/app/update/duty/";
    public static String UPDATE_DUTY_STATUS_WITHOUT_BASE_URL = "/api/v1/app/update/duty/";
    public static final int LOCATION_REQUEST = 1000;
    public static final int GPS_REQUEST = 1001;
}
