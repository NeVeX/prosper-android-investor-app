package com.mark.prosper.invest.config;

import android.content.Context;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by NeVeX on 9/30/2015.
 */
public class Configuration {

    private static Configuration instance;
    private Map<String, ConfigurationVO> configurationEnvironmentMap = new LinkedHashMap<>();
    private ConfigurationVO currentConfig;
    private Context context;

    private Configuration(){}

    public static ConfigurationVO getConfiguration()
    {
        if ( instance == null)
        {
            throw new RuntimeException("The configuration is not setup - invoke the constructor");
        }
        return instance.currentConfig;
    }

    public static void initialize(Context context)
    {
        if ( instance != null )
        {
            return; // don't reinitialize
        }
        Configuration configuration = new Configuration();
        configuration.context = context;
        instance = configuration;
        configuration.setup();
    }

    private void setup() {
        InputStream appInputStream = null;
        try {
            appInputStream = context.getAssets().open("app.properties");
            Properties props = new Properties();
            props.load(appInputStream);
            ConfigurationVO configurationVO = new ConfigurationVO();
            configurationVO.setInDebugMode(Boolean.valueOf(props.getProperty("app.debug.mode.enabled")));
            configurationVO.setServerBaseEndpoint(props.getProperty("app.api.dotnet.base.endpoint"));
            configurationVO.setApiType(ApiType.valueOf(props.getProperty("app.api.type")));
            configurationVO.setUseMockData(Boolean.valueOf(props.getProperty("app.debug.mock.data")));
            configurationVO.setEnvironment("LIVE");
            instance.configurationEnvironmentMap.put(configurationVO.getEnvironment(), configurationVO);
            instance.currentConfig = configurationVO;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not load the app.properties file");
        }
        finally {
            if ( appInputStream != null ) { try { appInputStream.close(); } catch (Exception e) {}}
        }

        if ( getConfiguration().isInDebugMode()) {
            InputStream testInputStream = null;
            try {
                testInputStream = context.getAssets().open("test.properties");
                Properties testProps = new Properties();
                testProps.load(testInputStream);
                addStgDotNetConfiguration(testProps);
                addProdDotNetConfiguration(testProps);
                addProdJavaConfiguration(testProps);
            } catch (Exception e) {
                throw new RuntimeException("Could not load the test.properties file");
            }
            finally {
                if ( testInputStream != null ) { try { testInputStream.close(); } catch (Exception e) {}}
            }
        }
    }

    private static void addDebugTestConfiguration() {
        Map<String, UserInformation> testUserPasses = new LinkedHashMap<>();
        UserInformation testOne = new UserInformation("TestUserOne", "my_password");
        UserInformation testTwo = new UserInformation("TestUserTwo", "another_password");
        UserInformation testThree = new UserInformation("TestUserThree", "12345");
        testUserPasses.put(testOne.getUserName(), testOne);
        testUserPasses.put(testTwo.getUserName(), testTwo);
        testUserPasses.put(testThree.getUserName(), testThree);
        ConfigurationVO test = new ConfigurationVO();
        test.setServerBaseEndpoint("https://api.stg.circleone.com");
        test.setApiType(ApiType.DotNet);
        test.setUserInformationMap(testUserPasses);
        test.setEnvironment("TEST");
        instance.configurationEnvironmentMap.put(test.getEnvironment(), test);
    }

    private static void addStgDotNetConfiguration(Properties properties) {
        Map<String, UserInformation> stgDotNetUsers = new LinkedHashMap<>();
        UserInformation stgLenderOne = new UserInformation(properties.getProperty("stg.user.1.name"), properties.getProperty("stg.user.1.password"));
        stgDotNetUsers.put(stgLenderOne.getUserName(), stgLenderOne);
        ConfigurationVO stgDotNetConfig = new ConfigurationVO();
        stgDotNetConfig.setServerBaseEndpoint(properties.getProperty("stg.api.dotnet.base.endpoint"));
        stgDotNetConfig.setApiType(ApiType.DotNet);
        stgDotNetConfig.setUserInformationMap(stgDotNetUsers);
        stgDotNetConfig.setEnvironment("STG (.NET)");
        instance.configurationEnvironmentMap.put(stgDotNetConfig.getEnvironment(), stgDotNetConfig);
    }

    private static void addProdJavaConfiguration(Properties properties) {

        UserInformation mark = new UserInformation("USER_NAME_GOES_HERE", "PASSWORD_GOES_HERE", "CLIENT_KEY_GOES_HERE", "CLIENT_SECRET_GOES_HERE");
        Map<String, UserInformation> userMap = new LinkedHashMap<>();
        userMap.put(mark.getUserName(), mark);
        ConfigurationVO javaProdConfig = new ConfigurationVO();
        javaProdConfig.setServerBaseEndpoint("JAVA_URL_GOES_HERE");
        javaProdConfig.setApiType(ApiType.Java);
        javaProdConfig.setUserInformationMap(userMap);
        javaProdConfig.setEnvironment("PROD (JAVA)");
        instance.configurationEnvironmentMap.put(javaProdConfig.getEnvironment(), javaProdConfig);
    }

    private static void addProdDotNetConfiguration(Properties properties)
    {
        UserInformation mark = new UserInformation(properties.getProperty("prod.user.1.name"), "");
        Map<String, UserInformation> userMap = new LinkedHashMap<>();
        userMap.put(mark.getUserName(), mark);
        ConfigurationVO prodDotNetConfig = new ConfigurationVO();
        prodDotNetConfig.setServerBaseEndpoint(properties.getProperty("prod.api.dotnet.base.endpoint"));
        prodDotNetConfig.setApiType(ApiType.DotNet);
        prodDotNetConfig.setUserInformationMap(userMap);
        prodDotNetConfig.setEnvironment("PROD (.NET)");
        instance.configurationEnvironmentMap.put(prodDotNetConfig.getEnvironment(), prodDotNetConfig);
    }

    public static Map<String, ConfigurationVO> getConfigurationEnvironmentMap() {
        return instance.configurationEnvironmentMap;
    }

    public static void changeConfiguration(ConfigurationVO configVO) {
        instance.currentConfig.setEnvironment(configVO.getEnvironment());
        instance.currentConfig.setApiType(configVO.getApiType());
        instance.currentConfig.setServerBaseEndpoint(configVO.getServerBaseEndpoint());
        instance.currentConfig.setUserInformationMap(configVO.getUserInformationMap());
    }
}
