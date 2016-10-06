package com.mark.prosper.invest.api.util;

import android.util.Base64;

import com.mark.prosper.invest.config.ApplicationState;
import com.mark.prosper.invest.config.Configuration;
import com.mark.prosper.invest.config.UserInformation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeVeX on 9/25/2015.
 */
public class ApiUtil {

    public static Map<String, String> createDotNetApiHeaders()
    {
        Map<String, String> headers = new HashMap<>();
        UserInformation userInformation = ApplicationState.getState().getCurrentUser();
        if ( userInformation != null ) {
            headers.put("Authorization", "Basic " + encodeToBase64(userInformation.getUserName(), userInformation.getPassword()));
        }
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        return headers;
    }

    public static String encodeToBase64(String username, String password)
    {
        String authorizationString = username + ":" + password;
        return Base64.encodeToString(authorizationString.getBytes(), Base64.NO_WRAP);
    }
}
