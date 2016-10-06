package com.mark.prosper.invest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mark.prosper.invest.api.model.InvestmentResultVO;
import com.mark.prosper.invest.constant.ApplicationConstants;
import com.mark.prosper.invest.util.UIUtil;


public class InvestmentResultActivity extends Activity {

    private static final String TAG = ApplicationConstants.LOG_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investment_result);

        if ( getIntent().getExtras() != null) {
            InvestmentResultVO resultVO = (InvestmentResultVO) getIntent().getExtras().get(ApplicationConstants.INTENT_INVESTMENT_RESULT_VO_KEY);
            setInvestmentResultDataToView(resultVO);
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

    private void setInvestmentResultDataToView(InvestmentResultVO resultVO)
    {
        final TextView listingNumberText = (TextView) findViewById(R.id.prosper_investment_result_listing_number_text);
        final TextView statusText = (TextView) findViewById(R.id.prosper_investment_result_status_text);
        final TextView messageText = (TextView) findViewById(R.id.prosper_investment_result_message_text);
        final TextView amountRequested = (TextView) findViewById(R.id.prosper_investment_result_requested_amount_text);
        final TextView amountExecuted = (TextView) findViewById(R.id.prosper_investment_result_executed_amount_text);
        final TextView faceText = (TextView) findViewById(R.id.prosper_investment_result_face_text);
        final TextView didSucceedText = (TextView) findViewById(R.id.prosper_investment_result_message_did_it_succeed_text);
        final View summaryView = findViewById(R.id.prosper_investment_result_summary_view);

        View thisView = findViewById(R.id.prosper_investment_result_view);
        if ( resultVO != null) {
            summaryView.setVisibility(View.VISIBLE);
            if ( resultVO.isPartOfMultiInvestment()) {
                listingNumberText.setText("N/A");
            }
            else
            {
                listingNumberText.setText(String.valueOf(resultVO.getListingId()));
            }
            statusText.setText(resultVO.getStatus());
            messageText.setText(resultVO.getMessage());
            amountRequested.setText(UIUtil.getMoneyString(resultVO.getRequestedAmount()));

            if (resultVO.isSuccessful()) {
                amountExecuted.setText(UIUtil.getMoneyString(resultVO.getAmountInvested()));
                thisView.setBackgroundColor(getResources().getColor(R.color.prosper_rating_a_bg));
                faceText.setText(R.string.prosper_happy_face);
                didSucceedText.setText(R.string.prosper_investment_order_result_success_text);
            } else {
                thisView.setBackgroundColor(getResources().getColor(R.color.prosper_red));
                View executedAmountView = findViewById(R.id.prosper_investment_result_executed_amount_view);
                executedAmountView.setVisibility(View.GONE);
                faceText.setText(R.string.prosper_sad_face);
                didSucceedText.setText(R.string.prosper_investment_order_result_unsuccessful_text);
            }
        }
        else
        {
            summaryView.setVisibility(View.GONE);
            faceText.setText(R.string.prosper_sad_face);
            didSucceedText.setText(R.string.prosper_investment_order_result_unknown_error_text);
        }


        ActionBar ab = this.getActionBar();
        if ( ab != null)
        {
            ab.setTitle("Investment Execution Result");
            ab.setBackgroundDrawable(thisView.getBackground());
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

}
