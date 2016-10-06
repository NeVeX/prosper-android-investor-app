package com.mark.prosper.invest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mark.prosper.invest.api.model.AccountVO;
import com.mark.prosper.invest.config.ApplicationState;
import com.mark.prosper.invest.constant.ApplicationConstants;
import com.mark.prosper.invest.model.ListingSearchCriteriaVO;
import com.mark.prosper.invest.util.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListingSearchActivity extends Activity {

    private Map<String, ToggleButton> ratingToggleButtons = new HashMap<>();
    private Map<Integer, ToggleButton> verificationToggleButtons = new HashMap<>();
    private SeekBar percentFundedSeekBar;
    private TextView percentFundedTextView;
    private TextView estimatedReturnTextView;
    private SeekBar estimatedReturnSeekBar;
    private TextView borrowerDebtToIncomeRatioTextView;
    private SeekBar borrowerDebtToIncomeRatioSeekBar;
    private Switch includeListingsAlreadyInvestedIn;

    private static final int LISTING_FETCH_SIZE = 10;
    private AccountVO accountVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_search);
        getAllRatingToggleButtons();
        getAllVerificationStageToggleButtons();
        setUpPercentFundedControls();
        setUpEstimatedReturnControls();
        setUpBorrowerDebtToIncomeRatioControls();
        accountVO = ApplicationState.getState().getAccountVO();
        includeListingsAlreadyInvestedIn = (Switch) findViewById(R.id.prosper_search_listing_include_listings_invested_in_switch);

        ActionBar ab = this.getActionBar();
        if ( ab != null)
        {
            ab.setTitle("Search Listings");
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

    private void setUpBorrowerDebtToIncomeRatioControls() {
        borrowerDebtToIncomeRatioSeekBar = (SeekBar) findViewById(R.id.prosper_borrower_debt_to_income_seekbar);
        borrowerDebtToIncomeRatioTextView = (TextView) findViewById(R.id.prosper_borrower_debt_to_income_seekbar_text);
        borrowerDebtToIncomeRatioTextView.setText(String.valueOf(borrowerDebtToIncomeRatioSeekBar.getProgress()));
        borrowerDebtToIncomeRatioTextView.clearFocus();

        final int seekBarMax = borrowerDebtToIncomeRatioSeekBar.getMax();

        borrowerDebtToIncomeRatioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    borrowerDebtToIncomeRatioTextView.setText(String.valueOf(i));
                    borrowerDebtToIncomeRatioTextView.clearFocus();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        borrowerDebtToIncomeRatioTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int estimatedReturn = UIUtil.tryGetInteger(editable.toString());
                if (estimatedReturn < 0 || estimatedReturn > seekBarMax) {
                    estimatedReturn = 0;
                }
                borrowerDebtToIncomeRatioSeekBar.setProgress(estimatedReturn);
            }
        });
    }

    private void getAllVerificationStageToggleButtons() {

        verificationToggleButtons.put(1, (ToggleButton) findViewById(R.id.prosper_verification_stage_one));
        verificationToggleButtons.put(2, (ToggleButton) findViewById(R.id.prosper_verification_stage_two));
        verificationToggleButtons.put(3, (ToggleButton) findViewById(R.id.prosper_verification_stage_three));
    }

    private void setUpEstimatedReturnControls() {
        estimatedReturnSeekBar = (SeekBar) findViewById(R.id.prosper_estimated_return_seekbar);
        estimatedReturnTextView = (TextView) findViewById(R.id.prosper_estimated_return_seekbar_text);
        estimatedReturnTextView.setText(String.valueOf(estimatedReturnSeekBar.getProgress()));
        estimatedReturnTextView.clearFocus();

        final int seekBarMax = estimatedReturnSeekBar.getMax();

        estimatedReturnSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    estimatedReturnTextView.setText(String.valueOf(i));
                    estimatedReturnTextView.clearFocus();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        estimatedReturnTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int estimatedReturn = UIUtil.tryGetInteger(editable.toString());
                if (estimatedReturn < 0 || estimatedReturn > seekBarMax) {
                    estimatedReturn = 0;
                }
                estimatedReturnSeekBar.setProgress(estimatedReturn);
            }
        });

    }

    private void setUpPercentFundedControls() {
        percentFundedSeekBar = (SeekBar) findViewById(R.id.prosper_percent_funded_seekbar);
        percentFundedTextView = (TextView) findViewById(R.id.prosper_percent_funded_seekbar_text);
        percentFundedTextView.setText(String.valueOf(percentFundedSeekBar.getProgress()));
        percentFundedTextView.clearFocus();
        percentFundedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    percentFundedTextView.setText(String.valueOf(i));
                    percentFundedTextView.clearFocus();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        percentFundedTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int percentValue = UIUtil.tryGetInteger(editable.toString());
                if (percentValue < 0 || percentValue > 100) {
                    percentValue = 0;
                }
                percentFundedSeekBar.setProgress(percentValue);
            }
        });
    }

    private void getAllRatingToggleButtons() {
        ratingToggleButtons.put("AA", (ToggleButton) findViewById(R.id.prosper_rating_aa));
        ratingToggleButtons.put("A", (ToggleButton) findViewById(R.id.prosper_rating_a));
        ratingToggleButtons.put("B", (ToggleButton) findViewById(R.id.prosper_rating_b));
        ratingToggleButtons.put("C", (ToggleButton) findViewById(R.id.prosper_rating_c));
        ratingToggleButtons.put("D", (ToggleButton) findViewById(R.id.prosper_rating_d));
        ratingToggleButtons.put("E", (ToggleButton) findViewById(R.id.prosper_rating_e));
        ratingToggleButtons.put("HR", (ToggleButton) findViewById(R.id.prosper_rating_hr));
    }

    public void onSearchListingsButtonClicked(View view) {
        Intent intent = new Intent(this, ListingSearchResultsActivity.class);
        ListingSearchCriteriaVO searchCriteriaVO = new ListingSearchCriteriaVO();
        searchCriteriaVO.setProsperRatings(getProsperRatingsSelected());
        searchCriteriaVO.setVerificationStages(getVerificationStagesSelected());
        searchCriteriaVO.setPercentFunded(percentFundedSeekBar.getProgress());
        searchCriteriaVO.setEstimatedReturn(estimatedReturnSeekBar.getProgress());
        searchCriteriaVO.setFetchSize(LISTING_FETCH_SIZE);
        searchCriteriaVO.setIncludeListingsAlreadyInvestedIn(includeListingsAlreadyInvestedIn.isChecked());
        searchCriteriaVO.setBorrowerDebtToIncomeRatio(borrowerDebtToIncomeRatioSeekBar.getProgress());
        intent.putExtra(ApplicationConstants.INTENT_INVESTMENT_SEARCH_VO_KEY, searchCriteriaVO);
//        intent.putExtra(ApplicationConstants.INTENT_ACCOUNT_VO_KEY, accountVO);
        startActivity(intent);
    }

    private List<Integer> getVerificationStagesSelected() {
        List<Integer> stages = new ArrayList<>();
        for ( Map.Entry<Integer, ToggleButton> entry : verificationToggleButtons.entrySet())
        {
            if ( entry.getValue().isChecked())
            {
                stages.add(entry.getKey());
            }
        }
        return stages;

    }

    private List<String> getProsperRatingsSelected() {
        List<String> ratingsSelected = new ArrayList<>();
        for ( Map.Entry<String, ToggleButton> pr : ratingToggleButtons.entrySet())
        {
            if ( pr.getValue() != null && pr.getValue().isChecked())
            {
                ratingsSelected.add(pr.getKey());
            }
        }
        return ratingsSelected;
    }

}
