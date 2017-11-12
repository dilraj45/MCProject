package com.mcproject.aroundme;

/**
 * @author dilraj
 */
public interface Constants {
    String HOST = "172.31.71.134:8000";

    // Constant key values
    String USERNAME = "username";
    String PASSWORD = "password";
    String AUTH_TOKEN = "token";
    String EMAIL = "email";
    String CONTACT = "contact";
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";
    String MESSAGE = "message";

    // Constant URI
    String URI_BUILD_SCHEME = "http";
    String UPDATE_LOCATION_URI = "androidServer/UpdateLocation";
    String SIGN_UP_URI = "androidServer/SignUp";
    String POST_SOS_REQUEST = "androidServer/PostSOSRequest";
    String LOG_IN_URI = "androidServer/AuthenticateCred";

    String PREF_CONSTANT = "com.mcproject.aroundme";
}
