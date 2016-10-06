package com.mark.prosper.invest.config;

import com.mark.prosper.invest.api.util.ApiUtil;

/**
 * Created by NeVeX on 10/1/2015.
 */
public class UserInformation {

    private String userName;
    private String password;
    private String clientKey;
    private String clientSecret;
    private String base64Authentication;

    public UserInformation(String userName, String password) {
        this(userName, password, null, null);
    }

    public UserInformation(String userName, String password, String clientKey, String clientSecret) {
        this.userName = userName;
        this.password = password;
        this.clientKey = clientKey;
        this.clientSecret = clientSecret;
        this.base64Authentication = ApiUtil.encodeToBase64(userName, password);
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getClientKey() {
        return clientKey;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getBase64Authentication() {
        return base64Authentication;
    }

}
