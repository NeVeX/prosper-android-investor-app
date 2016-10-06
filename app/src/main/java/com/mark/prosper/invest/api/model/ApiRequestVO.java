package com.mark.prosper.invest.api.model;

import com.mark.prosper.invest.api.ApiResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NeVeX on 9/24/2015.
 */
public class ApiRequestVO<T extends ApiResponse> {

    private String resource;
    private Map<String, String> headers;
    private Class<T> responseClass;
    private boolean isResponseAList;
    private String queryParameterString;
    private int offsetAmount;
    private int resultFetchAmount;
    private String requestBody;
    private int connectionTimeoutMS = 15000;
    private int socketTimeoutMS = 15000;


    public ApiRequestVO(String resource, String queryParameterString, Map<String, String> headers, String requestBody, Class<T> responseClass, int offsetAmount, int resultFetchAmount) {
        this(resource, queryParameterString, headers, requestBody, responseClass, offsetAmount, resultFetchAmount, false);
    }

    public ApiRequestVO(String resource, String queryParameterString, Map<String, String> headers, String requestBody, Class<T> responseClass, int offsetAmount, int resultFetchAmount, boolean isResponseAList) {
        if (resource == null || resource.length() == 0) {
            throw new RuntimeException("Cannot create API Request VO with null/empty resource");
        }
        if (responseClass == null) {
            throw new RuntimeException("Cannot create API Request VO with null response class");
        }

        this.resource = resource;
        this.headers = headers;
        this.responseClass = responseClass;
        this.isResponseAList = isResponseAList;
        this.queryParameterString = queryParameterString;
        this.offsetAmount = offsetAmount;
        this.resultFetchAmount = resultFetchAmount;
        this.requestBody = requestBody;
        if ( this.headers == null)
        {
            this.headers = new HashMap<>();
        }
    }

    public String getResource() {
        return resource;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Class<T> getResponseClass() {
        return responseClass;
    }

    public boolean isResponseAList() {
        return isResponseAList;
    }

    public String getQueryParameterString() {
        return queryParameterString;
    }

    public int getOffsetAmount() {
        return offsetAmount;
    }

    public int getResultFetchAmount() {
        return resultFetchAmount;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public int getConnectionTimeoutMS() {
        return connectionTimeoutMS;
    }

    public void setConnectionTimeoutMS(int connectionTimeoutMS) {
        this.connectionTimeoutMS = connectionTimeoutMS;
    }

    public int getSocketTimeoutMS() {
        return socketTimeoutMS;
    }

    public void setSocketTimeoutMS(int socketTimeoutMS) {
        this.socketTimeoutMS = socketTimeoutMS;
    }
}

