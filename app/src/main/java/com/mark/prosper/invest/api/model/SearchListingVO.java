package com.mark.prosper.invest.api.model;

import com.mark.prosper.invest.api.ApiResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeVeX on 9/24/2015.
 */
public class SearchListingVO implements ApiResponse<SearchListingVO> {

    private List<ListingVO> listings;
    private boolean endOfSearch;

    @Override
    public SearchListingVO convertJSONObject(JSONObject jsonObject) throws JSONException {
        return null;
    }

    @Override
    public SearchListingVO convertJSONArray(JSONArray jsonArray) throws JSONException {
        SearchListingVO searchListingVO = new SearchListingVO();
        endOfSearch = true;
        if ( jsonArray != null && jsonArray.length() > 0)
        {
            searchListingVO.setListings(new ArrayList<ListingVO>());
            ListingVO listingConvertVO = new ListingVO();
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jo = jsonArray.getJSONObject(i);
                searchListingVO.getListings().add(listingConvertVO.convertJSONObject(jo));
            }
            endOfSearch = searchListingVO.getListings().size() == 0;
        }
        return searchListingVO;
    }

    public List<ListingVO> getListings() {
        return listings;
    }

    public void setListings(List<ListingVO> listings) {
        this.listings = listings;
    }

    public boolean isEndOfSearch() {
        return endOfSearch;
    }

    public void setEndOfSearch(boolean endOfSearch) {
        this.endOfSearch = endOfSearch;
    }

}
