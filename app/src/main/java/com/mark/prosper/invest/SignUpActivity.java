package com.mark.prosper.invest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.mark.prosper.invest.api.ApiCallback;
import com.mark.prosper.invest.api.service.ApiAuthorizationService;
import com.mark.prosper.invest.config.ApiType;
import com.mark.prosper.invest.config.Configuration;
import com.mark.prosper.invest.config.ConfigurationVO;
import com.mark.prosper.invest.config.UserInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SignUpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final WebView webView = (WebView) findViewById(R.id.prosper_sign_up_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://www.prosper.com/investor/register");

        ActionBar ab = this.getActionBar();
        if ( ab != null)
        {
            ab.setTitle("Sign up online");
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);







































































































































































        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
