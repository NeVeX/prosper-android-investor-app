package com.mark.prosper.invest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.mark.prosper.invest.api.model.AccountVO;
import com.mark.prosper.invest.api.ApiCallback;
import com.mark.prosper.invest.api.service.ApiAccountService;
import com.mark.prosper.invest.config.ApplicationState;
import com.mark.prosper.invest.constant.ApplicationConstants;
import com.mark.prosper.invest.util.UIAnimation;
import com.mark.prosper.invest.util.UIUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends Activity implements ApiCallback<AccountVO> {

    private static final String TAG = ApplicationConstants.LOG_TAG;
    private TextView pendingQuickInvestmentAmount;
    private TextView pendingFolioInvestedAmount;
    private TextView outstandingPrincipalOnNotesAmount;
    private TextView chartValueText;
    private TextView chartValueLabelText;
    private View accountRetrievalStatusView;
    private View accountOverviewView;
    private BarChart mChart;
    private boolean showRefreshIcon = true;

    private ArrayList<String> xValues = new ArrayList<>();
    private Map<String, String> chartLabelMap = new LinkedHashMap<>();
    private static final String ACCOUNT_LABEL = "Account";
    private static final String CASH_LABEL = "Cash";
    private static final String INVESTED_LABEL = "Invested";
    private static final String PENDING_LABEL = "Pending";
    private static final String RECEIVED_LABEL = "Received";

    public HomeActivity()
    {
        xValues.add(ACCOUNT_LABEL);
        xValues.add(CASH_LABEL);
        xValues.add(INVESTED_LABEL);
        xValues.add(PENDING_LABEL);
        xValues.add(RECEIVED_LABEL);
        chartLabelMap.put(ACCOUNT_LABEL, "Total Account Balance");
        chartLabelMap.put(CASH_LABEL, "Total Cash Balance");
        chartLabelMap.put(INVESTED_LABEL, "Total Invested Amount");
        chartLabelMap.put(PENDING_LABEL, "Total Pending Investments");
        chartLabelMap.put(RECEIVED_LABEL, "Total Investments Received");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        pendingQuickInvestmentAmount = (TextView) findViewById(R.id.account_pending_quick_investments);
        chartValueText = (TextView) findViewById(R.id.prosper_selected_chart_value);
        chartValueLabelText = (TextView) findViewById(R.id.prosper_selected_chart_value_label);
        pendingFolioInvestedAmount = (TextView) findViewById(R.id.account_pending_folio_investments);
        outstandingPrincipalOnNotesAmount = (TextView) findViewById(R.id.account_outstanding_principal_on_notes);
        accountRetrievalStatusView = findViewById(R.id.account_retrieval_view);
        accountOverviewView = findViewById(R.id.prosper_account_overview_view);
        mChart = (BarChart) findViewById(R.id.account_overview_chart);
        onRefreshAccountData();

        ActionBar ab = this.getActionBar();
        if ( ab != null)
        {
            ab.setTitle("Account Overview");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    private void selectFirstDataInChart()
    {
        if ( mChart != null && mChart.getBarData() != null && mChart.getBarData().getDataSetCount() > 0)
        {
            mChart.highlightTouch(new Highlight(0, 0));
        }
    }

    private void onRefreshAccountData() {
        showRefreshIcon(false);
        ApplicationState.getState().setAccountVO(null);
        showRetrievalStatus(false);
        getAccountInformation();
    }

    private void showRefreshIcon(boolean showRefreshIcon) {
        this.showRefreshIcon = showRefreshIcon;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        if (!showRefreshIcon)
        {
            for ( int i = 0; i < menu.size(); i++)
            {
                MenuItem menuItem = menu.getItem(i);
                if ( menuItem != null && menuItem.getItemId() == R.id.action_bar_refresh)
                {
                    menuItem.setVisible(false);
                }
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_logout:
                // log out
                logout();
                return true;
            case R.id.action_bar_refresh:
                // log out
                onRefreshAccountData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        ApplicationState.getState().removeUserInformation();
        Intent signUpIntent = new Intent(this, SignInActivity.class);
        signUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(signUpIntent);
        Toast.makeText(getApplicationContext(), "You have being logged out", Toast.LENGTH_SHORT).show();
    }

    private void showRetrievalStatus(boolean hasError) {
        accountRetrievalStatusView.setVisibility(View.VISIBLE);
        accountOverviewView.setVisibility(View.GONE);
        if ( hasError)
        {
            // TODO: show error
        }
    }


    private void showAccountOverview() {
        accountRetrievalStatusView.setVisibility(View.GONE);
        accountOverviewView.setVisibility(View.VISIBLE);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                UIAnimation.animateValue(chartValueText,
                        UIUtil.tryGetDoubleFromMoneyString(chartValueText.getText().toString()), entry.getVal(), UIAnimation.ValueType.MONEY_WITH_FRACTIONAL, 500);
                chartValueLabelText.setText(chartLabelMap.get(xValues.get(entry.getXIndex())));
            }

            @Override
            public void onNothingSelected() {

            }
        });
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.setDescription("");
        mChart.setMaxVisibleValueCount(10);
        mChart.setPinchZoom(false);
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setDrawGridBackground(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setAxisLineColor(getResources().getColor(R.color.prosper_white));
        xAxis.setAxisLineWidth(1);
        xAxis.setTextColor(getResources().getColor(R.color.prosper_white));
        xAxis.setLabelsToSkip(0);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(false);
        xAxis.setTextSize(15f);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);
        mChart.getAxisLeft().setEnabled(false);
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.animateY(1000);
        setData(mChart, ApplicationState.getState().getAccountVO());
        mChart.invalidate();
        selectFirstDataInChart();
    }

    @Override
    public void onBackPressed()
    {
        logout();
    }

    private void setData(BarChart mChart, AccountVO accountVO) {
        ArrayList<BarEntry> yValues = new ArrayList<>();
        yValues.add(new BarEntry((float) accountVO.getTotalAccountAmount(), 0));
        yValues.add(new BarEntry((float) accountVO.getCashBalanceAmount(), 1));
        yValues.add(new BarEntry((float) accountVO.getTotalInvestedAmount(), 2));
        yValues.add(new BarEntry((float) accountVO.getPendingInvestmentsAmount(), 3));
        yValues.add(new BarEntry((float) accountVO.getTotalReceivedAmount(), 4));
        BarDataSet set1 = new BarDataSet(yValues, "AccountInformation");
        set1.setBarSpacePercent(35f);
        set1.setColor(getResources().getColor(R.color.prosper_green));
        set1.setHighLightColor(getResources().getColor(R.color.prosper_white));
        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        set1.setBarShadowColor(getResources().getColor(R.color.prosper_white));
        set1.setHighLightAlpha(255);
        set1.setHighlightEnabled(true);
        BarData data = new BarData(xValues, dataSets);
        data.setValueTextSize(15f);
        data.setDrawValues(true);
        data.setValueTextColor(getResources().getColor(R.color.prosper_white));
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                Log.d(TAG, value + ", " + entry.getVal());
                return UIUtil.getCondensedMoneyString(value);
            }
        });
        mChart.setData(data);
    }

    private void getAccountInformation() {
        showRetrievalStatus(false);
        accountRetrievalStatusView.setVisibility(View.VISIBLE);
        new ApiAccountService(this).invoke();
    }

    @Override
    public void onApiResponse(AccountVO data) {
        if ( data != null) {
            pendingQuickInvestmentAmount.setText(UIUtil.getMoneyString(data.getPendingQIOrdersAmount()));
            pendingFolioInvestedAmount.setText(UIUtil.getMoneyString(data.getPendingInvestmentsSecondaryMarket()));
            outstandingPrincipalOnNotesAmount.setText(UIUtil.getMoneyString(data.getOutstandingPrincipalOnActiveNotes()));
            showAccountOverview();
        }
        else
        {
            showRetrievalStatus(true);
        }
        showRefreshIcon(true);
    }

    public void onFindInvestmentsButtonClicked(View view) {
        Intent intent = new Intent(this, ListingSearchActivity.class);
        startActivity(intent);
    }

}
