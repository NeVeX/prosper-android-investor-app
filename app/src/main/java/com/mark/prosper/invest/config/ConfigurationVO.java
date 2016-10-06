package com.mark.prosper.invest.config;

import java.util.Map;

/**
 * Created by NeVeX on 9/30/2015.
 */
public class ConfigurationVO {

    private String serverBaseEndpoint;
    private String environment;
    private Map<String, UserInformation> userInformationMap;
    private ApiType apiType;
    private boolean inDebugMode;
    private boolean useMockData;

    public ConfigurationVO()
    {

    }

    public String getServerBaseEndpoint() {
        return serverBaseEndpoint;
    }

    public String getEnvironment() {
        return environment;
    }

    public Map<String, UserInformation> getUserInformationMap() {
        return userInformationMap;
    }

    public ApiType getApiType() {
        return apiType;
    }

    public boolean isInDebugMode() {
        return inDebugMode;
    }

    public void setInDebugMode(boolean inDebugMode) {
        this.inDebugMode = inDebugMode;
    }

    public void setServerBaseEndpoint(String serverBaseEndpoint) {
        this.serverBaseEndpoint = serverBaseEndpoint;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public void setUserInformationMap(Map<String, UserInformation> userInformationMap) {
        this.userInformationMap = userInformationMap;
    }

    public void setApiType(ApiType apiType) {
        this.apiType = apiType;
    }

    public boolean isUseMockData() {
        return useMockData;
    }

    public void setUseMockData(boolean useMockData) {
        this.useMockData = useMockData;
    }
}
