package com.mark.prosper.invest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.mark.prosper.invest.api.ApiCallback;
import com.mark.prosper.invest.api.service.ApiAccountService;
import com.mark.prosper.invest.api.service.ApiAuthorizationService;
import com.mark.prosper.invest.config.ApiType;
import com.mark.prosper.invest.config.ApplicationState;
import com.mark.prosper.invest.config.Configuration;
import com.mark.prosper.invest.config.ConfigurationVO;
import com.mark.prosper.invest.config.UserInformation;
import com.mark.prosper.invest.constant.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class SignInActivity extends Activity implements ApiCallback<Boolean> {

    private static final String TAG = ApplicationConstants.LOG_TAG;
    private static final String SHARED_PREFERENCES_KEY = "UserInformation";
    private static final String SHARED_PREFERENCES_USERNAME_KEY = "Username";

    private EditText userNameText;
    private EditText passwordText;
    private EditText clientKeyText;
    private EditText clientSecretText;
    private TextView logInStatusText;
    private Button logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        userNameText = (EditText) findViewById(R.id.prosper_sign_in_user_name_text);
        passwordText = (EditText) findViewById(R.id.prosper_sign_in_password_text);
//        passwordText.setText("");
        clientKeyText = (EditText) findViewById(R.id.prosper_sign_in_client_key_text);
        clientKeyText.setText("");
        clientKeyText.clearFocus();
        clientSecretText = (EditText) findViewById(R.id.prosper_sign_in_client_secret_text);
        clientKeyText.setText("");
        clientKeyText.clearFocus();
        logInStatusText = (TextView) findViewById(R.id.prosper_sign_in_status_text);
        logInStatusText.setVisibility(View.INVISIBLE);

        logInButton = (Button) findViewById(R.id.prosper_log_in_button);
        logInButton.setText(R.string.prosper_log_in);

        final TextView infoTextView = (TextView) findViewById(R.id.prosper_sign_in_info_text);
        infoTextView.setMovementMethod(LinkMovementMethod.getInstance()); // make links clickable

        setupDebugView();

        setInputFieldVisibilityFromApiType(Configuration.getConfiguration().getApiType());

        ActionBar ab = this.getActionBar();
        if ( ab != null)
        {
            ab.hide();
        }

        SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
        String usernamePref = settings.getString(SHARED_PREFERENCES_USERNAME_KEY, "");
        if (usernamePref != null && usernamePref.length() > 0 )
        {
            userNameText.setText(usernamePref);
        }
        else
        {
            userNameText.setText("");
        }
        userNameText.clearFocus();
    }

    private void setupDebugView() {

        final Map<String, ConfigurationVO> configurationVOMap = Configuration.getConfigurationEnvironmentMap();
        View debugView = findViewById(R.id.prosper_sign_in_account_debug_view);
        if ( !Configuration.getConfiguration().isInDebugMode())
        {
            // make sure the view is gone
            debugView.setVisibility(View.GONE);
            return;
        }
        debugView.setVisibility(View.VISIBLE);
        Switch mockDataSwitch = (Switch) findViewById(R.id.prosper_debug_mock_data_switch);
        mockDataSwitch.setChecked(Configuration.getConfiguration().isUseMockData());
        mockDataSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Configuration.getConfiguration().setUseMockData(b);
            }
        });

        final Spinner environmentSpinner = (Spinner) findViewById(R.id.prosper_debug_environment_spinner);
        final Spinner userNamesSpinner = (Spinner) findViewById(R.id.prosper_debug_users_spinner);
        final View userNamesView = findViewById(R.id.prosper_debug_users_select_view);
        environmentSpinner.setVisibility(View.VISIBLE);
        userNamesSpinner.setVisibility(View.VISIBLE);
        userNamesView.setVisibility(View.GONE);

        // populate the spinner
        populateEnvironmentSpinner(environmentSpinner, configurationVOMap);
        environmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                logInStatusText.setVisibility(View.INVISIBLE);
                String selectedEnvironment = (String) adapterView.getSelectedItem();
                ConfigurationVO environmentConfig = Configuration.getConfigurationEnvironmentMap().get(selectedEnvironment);
                populateUsersSpinner(userNamesView, userNamesSpinner, environmentConfig);
                if ( environmentConfig != null )
                {
                    setInputFieldVisibilityFromApiType(environmentConfig.getApiType());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        userNamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                logInStatusText.setVisibility(View.INVISIBLE);
                // see if we can now set the user name and password fields
                String userName = (String) adapterView.getSelectedItem();
                String environment = (String) environmentSpinner.getSelectedItem();
                onUserNameSelected(environment, userName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // default the debug settings
        ConfigurationVO configurationVO = Configuration.getConfiguration();
        for ( int i = 0; i < environmentSpinner.getCount(); i++)
        {
            String item = (String) environmentSpinner.getItemAtPosition(i);
            if ( item != null && item.equals(configurationVO.getEnvironment()))
            {
                environmentSpinner.setSelection(i);
                break;
            }
        }
    }

    private void setInputFieldVisibilityFromApiType(ApiType apiType) {
        if (apiType == ApiType.Java ) {
            clientSecretText.setVisibility(View.VISIBLE);
            clientKeyText.setVisibility(View.VISIBLE);
            userNameText.setHint(R.string.prosper_sign_in_java_user_name_hint);
            passwordText.setHint(R.string.prosper_sign_in_java_password_hint);
        }
        else
        {
            clientSecretText.setVisibility(View.GONE);
            clientKeyText.setVisibility(View.GONE);
            userNameText.setHint(R.string.prosper_sign_in_dot_net_user_name_hint);
            passwordText.setHint(R.string.prosper_sign_in_dot_net_password_hint);
        }
    }

    private void onUserNameSelected(String environment, String userName)
    {
        ConfigurationVO environmentVO = Configuration.getConfigurationEnvironmentMap().get(environment);
        if ( environmentVO != null && environmentVO.getUserInformationMap() != null)
        {
            UserInformation userInformation = environmentVO.getUserInformationMap().get(userName);
            if ( userInformation != null )
            {
                // set the fields!
                setUserAndPasswordText(userName, userInformation.getPassword(), userInformation.getClientKey(), userInformation.getClientSecret());
                return;
            }
        }
        setUserAndPasswordText("", "", "", "");
    }

    private void populateEnvironmentSpinner(Spinner environmentSpinner, Map<String, ConfigurationVO> mapOfConfigs)
    {
        if ( mapOfConfigs == null )
        {
            return;
        }
        List<String> environments = new ArrayList<>();
        for ( Map.Entry<String, ConfigurationVO> entry : mapOfConfigs.entrySet()) {
            environments.add(entry.getKey());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_style, environments);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item_style);
        environmentSpinner.setAdapter(dataAdapter);
    }

    private void populateUsersSpinner(View userNamesView, Spinner userNamesSpinner, ConfigurationVO configurationVO)
    {
        List<String> users = new ArrayList<>();
        if ( configurationVO != null && configurationVO.getUserInformationMap() != null )
        {
            for ( String userName : configurationVO.getUserInformationMap().keySet())
            {
                users.add(userName);
            }
            userNamesView.setVisibility(View.VISIBLE);
        }
        else
        {
            userNamesView.setVisibility(View.GONE);
            setUserAndPasswordText("", "", "", "");
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_style, users);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item_style);
        userNamesSpinner.setAdapter(dataAdapter);
    }

    private void setUserAndPasswordText(String username, String password, String clientKey, String clientSecret) {
        userNameText.setText(username);
        passwordText.setText(password);
        clientKeyText.setText(clientKey);
        clientSecretText.setText(clientSecret);
    }

    public void onLoginButtonClicked(View view) {
        logInStatusText.setVisibility(View.INVISIBLE);
        logInButton.setText(R.string.prosper_sign_in_logging_in_text);
        logInButton.setEnabled(false);

        // get the user and passes
        String userName = userNameText.getText().toString();
        String password = passwordText.getText().toString();
        String clientKey = clientKeyText.getText().toString();
        String clientSecret = clientSecretText.getText().toString();

        ConfigurationVO configVO = Configuration.getConfiguration();

        if ( Configuration.getConfiguration().isInDebugMode()) {
            final Spinner environmentSpinner = (Spinner) findViewById(R.id.prosper_debug_environment_spinner);
            String environment = (String) environmentSpinner.getSelectedItem();
            configVO = Configuration.getConfigurationEnvironmentMap().get(environment);
        }

        // see if the information we have is correct
        boolean canTryToLogIn = configVO != null && userName.length() > 0 && password.length() > 0;
        if ( canTryToLogIn && configVO.getApiType() == ApiType.Java )
        {
            canTryToLogIn = clientKey.length() > 0 && clientSecret.length() > 0;
        }
        if ( canTryToLogIn )
        {
            UserInformation userInformation = new UserInformation(userName, password, clientKey, clientSecret);
            ApplicationState.getState().setCurrentUser(userInformation);
            Configuration.changeConfiguration(configVO);
            new ApiAuthorizationService(this).invoke();
        }
        else
        {
            onApiResponse(false); // go with the can't log in flow
        }
    }

    @Override
    public void onApiResponse(Boolean canLogIn) {
        logInButton.setText(R.string.prosper_log_in);
        logInButton.setEnabled(true);
        if ( canLogIn )
        {
            logInStatusText.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, HomeActivity.class);

            SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(SHARED_PREFERENCES_USERNAME_KEY, ApplicationState.getState().getCurrentUser().getUserName());
            editor.apply();

            startActivity(intent);
        }
        else
        {
            // Show error
            logInStatusText.setVisibility(View.VISIBLE);
            logInStatusText.setText(R.string.prosper_sign_in_error_text);
            ApplicationState.getState().removeUserInformation(); // remove the failed credientials
        }
    }
}
