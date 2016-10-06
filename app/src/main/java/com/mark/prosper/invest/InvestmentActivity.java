package com.mark.prosper.invest;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mark.prosper.invest.api.ApiCallback;
import com.mark.prosper.invest.api.model.AccountVO;
import com.mark.prosper.invest.api.model.InvestmentResultVO;
import com.mark.prosper.invest.api.model.ListingVO;
import com.mark.prosper.invest.api.service.ApiInvestmentExecutionService;
import com.mark.prosper.invest.config.ApplicationState;
import com.mark.prosper.invest.config.Configuration;
import com.mark.prosper.invest.constant.ApplicationConstants;
import com.mark.prosper.invest.model.InvestmentType;
import com.mark.prosper.invest.util.UIAnimation;
import com.mark.prosper.invest.util.UIUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class InvestmentActivity extends Activity implements ApiCallback<InvestmentResultVO> {

    private static final String TAG = ApplicationConstants.LOG_TAG;
    private Button doInvestButton;
    private ListingVO listing;
    private ArrayList<ListingVO> listings;
    private InvestmentType investmentType;
    private EditText investmentAmountEntry;
    private EditText listingCountMultipleEditText;
    private AccountVO accountVO;
    private TextView listingAmountRemaining;
    private TextView investingErrorText;
    private TextView listingAmountMultipleTotalInvestAmount;
    private TextView accountCashBalance;
    private TextView investmentAmountEntryText;
    private TextView listingMultipleAverageEstimatedReturnText;
    private View listingAmountRemainingView;
    private View listingAmountMultipleView;
    private View listingCountMultipleView;
    private View listingMultipleAverageEstimatedReturnView;
    private View thisView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_invest_in_listing);

        // Make sure the width takes full screen width
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
        investmentType = (InvestmentType) getIntent().getExtras().get(ApplicationConstants.INTENT_INVESTMENT_TYPE_KEY);
        accountVO = ApplicationState.getState().getAccountVO();

        doInvestButton = (Button) findViewById(R.id.prosper_listing_detail_do_invest_button);
        thisView = findViewById(R.id.prosper_investment_view);
        listingAmountRemaining = (TextView) findViewById(R.id.prosper_listing_detail_invest_listing_amount_remaining);
        listingAmountRemainingView = findViewById(R.id.prosper_single_listing_amount_remaining_view);
        listingAmountMultipleView = findViewById(R.id.prosper_invest_multiple_listings_total_invest_amount_view);
        listingAmountMultipleTotalInvestAmount = (TextView) findViewById(R.id.prosper_invest_multiple_listings_total_invest_amount);
        listingCountMultipleView = findViewById(R.id.prosper_listing_multiple_listing_count_view);
        listingMultipleAverageEstimatedReturnView = findViewById(R.id.prosper_listing_multiple_estimated_average_return_view);
        listingMultipleAverageEstimatedReturnText = (TextView) findViewById(R.id.prosper_listing_multiple_estimated_average_return);
        listingCountMultipleEditText = (EditText) findViewById(R.id.prosper_listing_multiple_invest_listing_count);
        investingErrorText = (TextView) findViewById(R.id.prosper_listing_invest_amount_error_message);
        accountCashBalance = (TextView) findViewById(R.id.prosper_listing_detail_your_cash_balance);

        investmentAmountEntry = (EditText) findViewById(R.id.prosper_listing_invest_amount);
        investmentAmountEntryText = (TextView) findViewById(R.id.prosper_invest_listing_amount_text);
        investingErrorText.setVisibility(View.GONE);
        accountCashBalance.setText(UIUtil.getMoneyString(accountVO.getCashBalanceAmount()));

        if ( investmentType == InvestmentType.Single) {
            setupViewForSingleListing();
            setTitle("Enter Your Investment Amount");
        }
        else
        {
            setupViewForMultipleListings();
            setTitle("Enter Your Investment Criteria");
        }
        investmentAmountEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (investmentType == InvestmentType.Multiple) {
                    onMultipleListingsInputChange();
                } else {
                    onInvestmentAmountChangedSingleListing(editable.toString());
                }
            }
        });
        investmentAmountEntry.requestFocus(); // get focus
        // show the keypad
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(investmentAmountEntry, InputMethodManager.SHOW_IMPLICIT);
    }

    private void onMultipleListingsInputChange() {
        disableDoInvestButton();
        double cashAvailableValue = accountVO.getCashBalanceAmount();
        String amountToInvestPerListing = investmentAmountEntry.getText().toString();
        String listingCountString = listingCountMultipleEditText.getText().toString();

        int listingCount = UIUtil.tryGetInteger(listingCountString);

//        else if (amountToInvestPerListing.trim().length() == 0) {
//            return; //nothing to do
//        }
        double investAmountPerListing = UIUtil.tryGetInteger(amountToInvestPerListing);
        double investAmountForOrder = investAmountPerListing * listingCount;
        String errorText = "";
        if ( listingCount <= 0)
        {
            errorText = "Amount of listings is incorrect";
        }
        else if ( listingCount > listings.size())
        {
            errorText = "Amount of listings is too great. Max is "+listings.size();
        }
        else if (investAmountPerListing < 25) {
            // nope
            errorText = getResources().getString(R.string.prosper_error_listing_amount_less_than_25);
        } else if (investAmountForOrder > cashAvailableValue) {
            errorText = getResources().getString(R.string.prosper_error_not_enough_funds_in_account);
        }
        if ( errorText.length() > 0)
        {
            thisView.setBackgroundColor(getResources().getColor(R.color.prosper_red));
//            listingAmountMultipleTotalInvestAmount.setTextColor(getResources().getColor(R.color.prosper_white));
//            accountCashBalance.setTextColor(getResources().getColor(R.color.prosper_white));
            accountCashBalance.setText(UIUtil.getMoneyString(cashAvailableValue));
            listingAmountMultipleTotalInvestAmount.setText("0");
//            listingMultipleAverageEstimatedReturnText.setText("");
            investingErrorText.setVisibility(View.VISIBLE);
            investingErrorText.setText(errorText);
        }
        else {
            enableDoInvestButton();
            investingErrorText.setText("");
            investingErrorText.setVisibility(View.GONE);
            thisView.setBackgroundColor(getResources().getColor(R.color.prosper_color));
            double cashBalanceRemaining = cashAvailableValue - investAmountForOrder;
            UIAnimation.animateValue(accountCashBalance, cashAvailableValue, cashBalanceRemaining, UIAnimation.ValueType.MONEY_WITH_FRACTIONAL, 700);
            UIAnimation.animateValue(listingAmountMultipleTotalInvestAmount, 0, investAmountForOrder, UIAnimation.ValueType.MONEY, 700);
//            listingAmountMultipleTotalInvestAmount.setTextColor(getResources().getColor(R.color.prosper_green));
//            accountCashBalance.setTextColor(getResources().getColor(R.color.prosper_white));
            double currentReturn = UIUtil.tryGetDoubleFromPercentString(listingMultipleAverageEstimatedReturnText.getText().toString());
            if ( currentReturn != 0)
            {
                currentReturn /= 100;
            }
            UIAnimation.animateValue(listingMultipleAverageEstimatedReturnText, currentReturn, getEstimatedAverageReturn(listingCount), UIAnimation.ValueType.PERCENTILE_WITH_FRACTIONAL, 1000);
        }
    }

    private void onInvestmentAmountChangedSingleListing(String value) {
        disableDoInvestButton();
        double cashAvailableValue = accountVO.getCashBalanceAmount();
        double listingAmountRemainingValue = listing.getAmountRemaining();

        if (value.trim().length() == 0) {
            return; //nothing to do
        }
        double investAmount = UIUtil.tryGetInteger(value);
        String errorText = "";
        if (investAmount < 25) {
            // nope
            errorText = "Investment amount must be greater than $25!";
        } else if (investAmount > listingAmountRemainingValue + 1) {
            errorText = "This is too much of an investment!";
        } else if (investAmount > cashAvailableValue) {
            errorText = "You don't have enough funds!";
        }
        if ( errorText.length() > 0)
        {
//            listingAmountRemaining.setTextColor(getResources().getColor(R.color.prosper_white));
//            accountCashBalance.setTextColor(getResources().getColor(R.color.prosper_white));
            thisView.setBackgroundColor(getResources().getColor(R.color.prosper_red));
            accountCashBalance.setText(UIUtil.getMoneyString(cashAvailableValue));
            listingAmountRemaining.setText(UIUtil.getMoneyString(listingAmountRemainingValue, false));
            investingErrorText.setVisibility(View.VISIBLE);
            investingErrorText.setText(errorText);
        }
        else {
            enableDoInvestButton();
            investingErrorText.setText("");
            investingErrorText.setVisibility(View.GONE);
            double newAmountRemaining = listingAmountRemainingValue - investAmount;
            double cashBalanceRemaining = cashAvailableValue - investAmount;
            thisView.setBackgroundColor(getResources().getColor(R.color.prosper_color));
            UIAnimation.animateValue(accountCashBalance, cashAvailableValue, cashBalanceRemaining, UIAnimation.ValueType.MONEY_WITH_FRACTIONAL, 700);
            UIAnimation.animateValue(listingAmountRemaining, listingAmountRemainingValue, newAmountRemaining, UIAnimation.ValueType.MONEY, 700);
//            listingAmountRemaining.setTextColor(getResources().getColor(R.color.prosper_green));
//            accountCashBalance.setTextColor(getResources().getColor(R.color.prosper_white));
        }

    }

    private void setupViewForMultipleListings() {
        listingAmountRemainingView.setVisibility(View.GONE);
        listingAmountMultipleView.setVisibility(View.VISIBLE);
        listingMultipleAverageEstimatedReturnView.setVisibility(View.VISIBLE);
        listingCountMultipleView.setVisibility(View.VISIBLE);
        listings = (ArrayList<ListingVO>) getIntent().getExtras().get(ApplicationConstants.INTENT_LISTING_LIST_VO_KEY);
        listingCountMultipleEditText.setText(String.valueOf(listings.size()));
        listingAmountMultipleTotalInvestAmount.setText(String.valueOf(listings.size() * 25));
        investmentAmountEntryText.setText(R.string.prosper_invest_listing_multiple_amount_text);
        investmentAmountEntry.setText("25");

        getEstimatedAverageReturn(listings.size());
        listingMultipleAverageEstimatedReturnText.setText(UIUtil.getPercentageString(getEstimatedAverageReturn(listings.size())));

        listingCountMultipleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                onMultipleListingsInputChange();
            }
        });
        onMultipleListingsInputChange(); // set the validation in motion
    }

    private double getEstimatedAverageReturn(int size) {
        double averageReturn = 0;
        for (int i = 0; i < size; i++)
        {
            averageReturn += listings.get(i).getEstimatedReturn();
        }
        if ( averageReturn != 0 && size > 0)
        {
            averageReturn /= size;
        }
        return averageReturn;
    }

    private void setupViewForSingleListing() {
        listingAmountRemainingView.setVisibility(View.VISIBLE);
        listingAmountMultipleView.setVisibility(View.GONE);
        listingCountMultipleView.setVisibility(View.GONE);
        investmentAmountEntry.setText("25");
        listingMultipleAverageEstimatedReturnView.setVisibility(View.GONE);
        investmentAmountEntryText.setText(R.string.prosper_invest_listing_amount_text);
        listing = (ListingVO) getIntent().getExtras().get(ApplicationConstants.INTENT_LISTING_VO_KEY);
        listingAmountRemaining.setText(UIUtil.getMoneyString(listing.getAmountRemaining(), false));
        onInvestmentAmountChangedSingleListing(investmentAmountEntry.getText().toString());
    }

    private void disableDoInvestButton()
    {
        if (Configuration.getConfiguration().isInDebugMode())
        {
            return; // don't disable the invest button
        }
        doInvestButton.setEnabled(false);
    }

    private void enableDoInvestButton()
    {
        doInvestButton.setEnabled(true);
    }

    public void onConfirmToInvestInListingButtonClicked(View view)
    {
        if ( investmentType == InvestmentType.Single) {
            int investmentAmount = UIUtil.tryGetInteger(investmentAmountEntry.getText().toString());
            if (investmentAmount < 25) {
                return;
            }
            int listingId = listing.getListingNumber();
            new ApiInvestmentExecutionService(this).invoke(listingId, investmentAmount);
        }
        else
        {
            int investmentAmountPerListing = UIUtil.tryGetInteger(investmentAmountEntry.getText().toString());
            int listingCount = UIUtil.tryGetInteger(listingCountMultipleEditText.getText().toString());
            List<ListingVO> listingsToInvestIn = new ArrayList<>();
            for ( int i = 0; i < listingCount; i++)
            {
                listingsToInvestIn.add(listings.get(i));
            }
            if ( listingsToInvestIn.size() > 0)
            {
                new ApiInvestmentExecutionService(this).invoke(listingsToInvestIn, investmentAmountPerListing);
            }
        }
        onCancelInvestInListingButtonClicked(view);
        Toast.makeText(getApplicationContext(), "Investment Order has being placed", Toast.LENGTH_SHORT).show();
    }

    public void onCancelInvestInListingButtonClicked(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        investmentAmountEntry.setText("");
        finish();
    }

    @Override
    public void onApiResponse(InvestmentResultVO resultVO) {

        Intent notificationIntent = new Intent(this, InvestmentResultActivity.class);
        notificationIntent.putExtra(ApplicationConstants.INTENT_INVESTMENT_RESULT_VO_KEY, resultVO);
        PendingIntent contentIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Resources resources = this.getResources();
        Notification.Builder builder = new Notification.Builder(this);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setTicker("Prosper Investment Order Update")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setVibrate(new long[]{0, 1000});

        if ( resultVO != null)
        {
            if ( resultVO.isSuccessful())
            {
                String contextTitle = UIUtil.getMoneyString(resultVO.getAmountInvested()) + " was invested in ";
                // do a success notification
                builder.setContentTitle("Your Prosper Order Executed Successfully");
                if ( resultVO.isPartOfMultiInvestment())
                {
                    contextTitle += "multiple Listings";
                }
                else
                {
                    contextTitle += "Listing Number " + resultVO.getListingId();
                }
                builder.setContentText(contextTitle);
            }
            else
            {
                // something went wrong with the order
                builder.setContentTitle("Your Prosper Order Did Not Execute Successfully").setContentText("The reason for failure is "+resultVO.getMessage());
            }
        }
        else
        {
            // Something went horribly wrong
            builder.setContentTitle("Your Prosper Order Could Not Be Executed").setContentText("Please try again later");
        }
        Notification n = builder.build();
        nm.notify(ApplicationState.getState().getNextUniqueNotificationNumber(), n);
    }
}
