package com.mark.prosper.invest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mark.prosper.invest.api.ApiCallback;
import com.mark.prosper.invest.api.model.ListingVO;
import com.mark.prosper.invest.api.model.AccountVO;
import com.mark.prosper.invest.api.model.SearchListingVO;
import com.mark.prosper.invest.api.service.ApiListingSearchService;
import com.mark.prosper.invest.config.ApplicationState;
import com.mark.prosper.invest.constant.ApplicationConstants;
import com.mark.prosper.invest.list.ListingResultEntryAdapter;
import com.mark.prosper.invest.model.InvestmentType;
import com.mark.prosper.invest.model.ListingSearchCriteriaVO;

import java.util.ArrayList;
import java.util.List;


public class ListingSearchResultsActivity extends Activity implements ApiCallback<SearchListingVO> {

    private static final String TAG = ApplicationConstants.LOG_TAG;
    private View resultsView;
    private ListView resultsListView;
    private View retrievalView;
    private View retrievalErrorView;
    ListingResultEntryAdapter resultListAdapter;
    ListingSearchCriteriaVO criteriaVO;
    private AccountVO accountVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_search_results);
        resultListAdapter = null;
        resultsView = findViewById(R.id.search_results_view);
        resultsListView = (ListView) findViewById(R.id.search_results_list_view);
        retrievalView = findViewById(R.id.search_results_status);
        retrievalErrorView = findViewById(R.id.search_results_error_view);
        retrievalErrorView.setVisibility(View.GONE);
        resultsView.setVisibility(View.INVISIBLE);
        retrievalView.setVisibility(View.VISIBLE);
        criteriaVO = (ListingSearchCriteriaVO) getIntent().getExtras().get(ApplicationConstants.INTENT_INVESTMENT_SEARCH_VO_KEY);
        accountVO = ApplicationState.getState().getAccountVO();
        getListings(criteriaVO);
        ActionBar ab = this.getActionBar();
        if ( ab != null)
        {
            ab.setTitle("Listing Search Results");
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

    private void populateListingViewWithData(List<ListingVO> list, boolean endOfResults)
    {
        boolean newResults = list != null && list.size() > 0;
        final View footerView;
        if ( resultListAdapter == null)
        {
            resultListAdapter = new ListingResultEntryAdapter(this, list);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            footerView = inflater.inflate(R.layout.listing_entry_footer_enabled, resultsListView, false);
//            View headerView = inflater.inflate(R.layout.listing_entry_header, listView, false);
            resultsListView.addFooterView(footerView);
//            listView.addHeaderView(headerView);
            resultsListView.setAdapter(resultListAdapter);

            resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int listItem, long l) {
                    ListingVO listingVO = (ListingVO) adapterView.getAdapter().getItem(listItem);
                    Log.e(TAG, "Listing Clicked: " + listingVO.getListingNumber() + " at position " + listItem);
                    Intent intent = new Intent(ListingSearchResultsActivity.this, ListingDetailActivity.class);
                    intent.putExtra(ApplicationConstants.INTENT_LISTING_VO_KEY, listingVO);
//                    intent.putExtra(ApplicationConstants.INTENT_ACCOUNT_VO_KEY, accountVO);
                    startActivity(intent);
                }
            });
        }
        else
        {
            if ( newResults) {
                resultListAdapter.addAll(list);
                resultListAdapter.notifyDataSetChanged();
            }
            footerView = findViewById(R.id.listing_entry_footer_view_enabled);
        }
        if ( endOfResults )
        {
            footerView.setOnClickListener(null);
            resultsListView.removeFooterView(footerView);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View disabledFootView = inflater.inflate(R.layout.listing_entry_footer_disabled, resultsListView, false);
            disabledFootView.setOnClickListener(null);
            resultsListView.addFooterView(disabledFootView);
        }
        else
        {

            final TextView footerTextView = (TextView) footerView.findViewById(R.id.listing_entry_footer_enabled_text_view);
            footerTextView.setText(R.string.prosper_search_results_footer_retrieve_more_investments);
            footerTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    footerTextView.setText(R.string.prosper_search_results_footer_retrieving);
                    criteriaVO.setOffSet(resultListAdapter.getCount());
                    getListings(criteriaVO);
                }
            });
        }
    }

    public void getListings(ListingSearchCriteriaVO criteriaVO) {
        new ApiListingSearchService(this).invoke(criteriaVO);
    }

    @Override
    public void onApiResponse(SearchListingVO data) {
        if ( data != null && data.getListings() != null && data.getListings().size() > 0)
        {
            resultsView.setVisibility(View.VISIBLE);
            retrievalView.setVisibility(View.INVISIBLE);
            populateListingViewWithData(data.getListings(), data.isEndOfSearch());
        }
        else if ( resultListAdapter != null)
        {
            // this is an appending result set (adding to the existing list), so let it flow into there
            populateListingViewWithData(null, true);
        }
        else
        {
            resultsView.setVisibility(View.INVISIBLE);
            retrievalView.setVisibility(View.INVISIBLE);
            retrievalErrorView.setVisibility(View.VISIBLE);
        }
    }

    public void onInvestInMultipleListingsClicked(View view) {
        // get the list of listings selected in the adapter
        ArrayList<ListingVO> listings = new ArrayList<>();
        for (int i = 0; i < resultListAdapter.getCount(); i++)
        {
            listings.add(resultListAdapter.getItem(i));
        }
        if ( listings.size() > 0)
        {
            Intent intent = new Intent(this, InvestmentActivity.class);
            intent.putExtra(ApplicationConstants.INTENT_LISTING_LIST_VO_KEY, listings);
//            intent.putExtra(ApplicationConstants.INTENT_ACCOUNT_VO_KEY, accountVO);
            intent.putExtra(ApplicationConstants.INTENT_INVESTMENT_TYPE_KEY, InvestmentType.Multiple);
            startActivity(intent);
        }
    }
}
