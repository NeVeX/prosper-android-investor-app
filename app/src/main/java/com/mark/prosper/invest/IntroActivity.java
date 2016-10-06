package com.mark.prosper.invest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.mark.prosper.invest.config.Configuration;
import com.mark.prosper.invest.constant.ApplicationConstants;


public class IntroActivity extends Activity {

    private static final String TAG = ApplicationConstants.LOG_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.mark.prosper.invest.R.layout.activity_intro);
        Configuration.initialize(this);

        ActionBar ab = this.getActionBar();
        if ( ab != null)
        {
            ab.hide();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(com.mark.prosper.invest.R.menu.menu_intro, menu);
//        return true;
//    }

    public void onSignInButtonClick(View view)
    {
        Log.d(TAG, "Sign in button clicked");
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    public void onSignUpButtonClick(View view)
    {
        Log.d(TAG, "Sign up button clicked");
        Intent signUpIntent = new Intent(this, SignUpActivity.class);
        startActivity(signUpIntent);
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == com.mark.prosper.invest.R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
