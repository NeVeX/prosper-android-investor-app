package com.mark.prosper.invest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mark.prosper.invest.api.model.ListingVO;
import com.mark.prosper.invest.api.model.AccountVO;
import com.mark.prosper.invest.config.ApplicationState;
import com.mark.prosper.invest.constant.ApplicationConstants;
import com.mark.prosper.invest.model.InvestmentType;
import com.mark.prosper.invest.util.UIAnimation;
import com.mark.prosper.invest.util.UIUtil;

import java.util.Date;


public class ListingDetailActivity extends Activity {

    private ScrollView listingDetailScrollView;
    private ListingVO listing;
    private AccountVO accountVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_detail);

        listing = (ListingVO) getIntent().getExtras().get(ApplicationConstants.INTENT_LISTING_VO_KEY);
        accountVO = ApplicationState.getState().getAccountVO();
        final TextView listingProsperRating = (TextView) findViewById(R.id.prosper_listing_detail_rating);
        final TextView listingTitle = (TextView) findViewById(R.id.prosper_listing_detail_title);
        final TextView listingLoanTerm = (TextView) findViewById(R.id.prosper_listing_detail_loan_term);
        final TextView listingAmount = (TextView) findViewById(R.id.prosper_listing_detail_loan_amount);
        final TextView listingEstimatedReturn = (TextView) findViewById(R.id.prosper_listing_detail_estimated_return);
        final TextView estimatedLoss = (TextView) findViewById(R.id.prosper_listing_detail_estimated_loss);
        final TextView effectiveYield = (TextView) findViewById(R.id.prosper_listing_detail_effective_yield);
        final TextView verificationStageText = (TextView) findViewById(R.id.prosper_listing_detail_verification_stage);


        final TextView borrowerRateText = (TextView) findViewById(R.id.prosper_listing_detail_borrower_rate);
        final TextView borrowerDebtToIncomeRatioText = (TextView) findViewById(R.id.prosper_listing_detail_borrower_debt_income_ratio);
        final TextView borrowerFicoScore = (TextView) findViewById(R.id.prosper_listing_detail_borrower_fico_score);
        final TextView borrowerInquiriesInLastSixMonths = (TextView) findViewById(R.id.prosper_listing_detail_borrower_inquiries_last_6_month);
        final TextView borrowerMonthlyPayment = (TextView) findViewById(R.id.prosper_listing_detail_borrower_monthlny_payment);
        final TextView borrowerOccupation = (TextView) findViewById(R.id.prosper_listing_detail_borrower_occupation);


        borrowerRateText.setText(UIUtil.getPercentageString(listing.getBorrowerRate()));
        borrowerDebtToIncomeRatioText.setText(UIUtil.getPercentageString(listing.getBorrowerDebtToIncomeRatio(), false));
        borrowerFicoScore.setText(listing.getBorrowerFicoScore());
        borrowerInquiriesInLastSixMonths.setText(String.valueOf(listing.getBorrowerInquiriesInLastSixMonths()));
        borrowerMonthlyPayment.setText(UIUtil.getMoneyString(listing.getBorrowerMonthlyPayment(), false));
        borrowerOccupation.setText(listing.getBorrowerOccupation());

        verificationStageText.setText(UIUtil.getVerificationStageText(listing.getVerificationStage()));

        listingProsperRating.setText(listing.getProsperRating());
        listingTitle.setText(listing.getListingTitle());
        listingLoanTerm.setText(String.valueOf(listing.getLoanTermInYears()) + " Year Term");
//        amountLeftToFund.setText(UIUtil.getMoneyString(listing.getAmountRemaining(), false));

        estimatedLoss.setText(UIUtil.getPercentageString(listing.getEstimatedLoss()));
        effectiveYield.setText(UIUtil.getPercentageString(listing.getEffectiveYield()));


//        listingPercentFunded.setText(percentFunded + " Funded");

        listingAmount.setText(UIUtil.getMoneyString(listing.getListingAmount(), false));
        listingEstimatedReturn.setText(UIUtil.getPercentageString(listing.getEstimatedReturn()));

        listingDetailScrollView = (ScrollView) findViewById(R.id.prosper_listing_detail_scroll_view);

        final TextView listingTimeLeft = (TextView) findViewById(R.id.prosper_listing_detail_time_left);
        listingTimeLeft.setText(UIUtil.getPresentableTimeBetweenTwoDates(listing.getStartDate(), new Date()));

        setBackgroundColors();

        ActionBar ab = this.getActionBar();
        if ( ab != null)
        {
            ab.setTitle("Listing Detail");
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

    @Override
    protected void onStart()
    {
        super.onStart();
        final TextView listingPercentFunded = (TextView) findViewById(R.id.prosper_listing_detail_percent_funded_text);
        final ProgressBar percentFundedProgressBar = (ProgressBar) findViewById(R.id.prosper_listing_detail_progress_bar_percent_funded);
        String percentFunded = UIUtil.getPercentageString(listing.getPercentFunded(), false);
        double percentFundedValue = UIUtil.tryGetDoubleFromPercentString(percentFunded);
        final TextView amountLeftToFund = (TextView) findViewById(R.id.prosper_listing_detail_amount_left_to_fund_text);
        UIAnimation.animateValue(percentFundedProgressBar, 0, (int) percentFundedValue, 2200);
        UIAnimation.animateValue(listingPercentFunded, 0, listing.getPercentFunded(), UIAnimation.ValueType.PERCENTILE, 2200);
        UIAnimation.animateValue(amountLeftToFund, listing.getListingAmount(), listing.getAmountRemaining(), UIAnimation.ValueType.MONEY, 2200);
    }

    private void setBackgroundColors()
    {
        Integer colorId = UIUtil.getResourceColorIdBasedOnProsperRating(listing.getProsperRating());
        if ( colorId != null)
        {
            listingDetailScrollView.setBackgroundColor(listingDetailScrollView.getResources().getColor(colorId));
            if ( this.getActionBar() != null)
            {
                this.getActionBar().setBackgroundDrawable(new ColorDrawable(listingDetailScrollView.getResources().getColor(colorId)));
            }
        }

    }


    public void onInvestButtonClicked(View view) {

        Intent intent = new Intent(this, InvestmentActivity.class);
        intent.putExtra(ApplicationConstants.INTENT_LISTING_VO_KEY, listing);
        intent.putExtra(ApplicationConstants.INTENT_INVESTMENT_TYPE_KEY, InvestmentType.Single);
        startActivity(intent);
    }
}
