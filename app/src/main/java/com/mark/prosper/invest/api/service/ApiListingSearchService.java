package com.mark.prosper.invest.api.service;

import android.util.Log;

import com.mark.prosper.invest.api.ApiCallback;
import com.mark.prosper.invest.api.AbstractApiClient;
import com.mark.prosper.invest.api.model.ApiRequestVO;
import com.mark.prosper.invest.api.model.InvestmentsVO;
import com.mark.prosper.invest.api.model.ListingVO;
import com.mark.prosper.invest.api.model.SearchListingVO;
import com.mark.prosper.invest.api.util.ApiUtil;
import com.mark.prosper.invest.config.Configuration;
import com.mark.prosper.invest.model.ListingSearchCriteriaVO;
import com.mark.prosper.invest.util.DebugUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeVeX on 9/26/2015.
 */
public class ApiListingSearchService extends AbstractApiClient<SearchListingVO> implements ApiCallback<InvestmentsVO> {

    private final static String INVESTMENT_SEARCH_RESOURCE = "/api/Listings";
    private ListingSearchCriteriaVO searchCriteriaVO;
    private InvestmentsVO investmentsVO;

    public ApiListingSearchService(ApiCallback<SearchListingVO> callback) {
        super(callback);
    }

    public void invoke(ListingSearchCriteriaVO criteriaVO)
    {
        if (Configuration.getConfiguration().isUseMockData())
        {
            SearchListingVO mockResponse = new SearchListingVO();
            mockResponse.setEndOfSearch(DebugUtil.getRandomBoolean());
            int size = mockResponse.isEndOfSearch() ? 3 : criteriaVO.getFetchSize();
            List<ListingVO> listingVOs = new ArrayList<>();
            for ( int i = 0; i < size; i++)
            {
                listingVOs.add(ListingVO.createMock());
            }
            mockResponse.setListings(listingVOs);
            onPostExecute(mockResponse);
        }
        else {
            searchCriteriaVO = criteriaVO;
            if (!criteriaVO.isIncludeListingsAlreadyInvestedIn()) {
                // we need to get the list of already invested listings first
                new ApiInvestmentService(this).invokeGetAllActiveInvestments();
            } else {
                execute(createRequest());
            }
        }
    }

    private ApiRequestVO<SearchListingVO> createRequest()
    {
        return new ApiRequestVO<>(
                        INVESTMENT_SEARCH_RESOURCE,
                        getFilterQuery(searchCriteriaVO),
                        ApiUtil.createDotNetApiHeaders(),
                        null,
                        SearchListingVO.class,
                        searchCriteriaVO.getOffSet(),
                        DEFAULT_FETCH_SIZE,
                        true);
    }

    private String getFilterQuery(ListingSearchCriteriaVO criteriaVO) {
        StringBuilder filterString = new StringBuilder();
        if ( criteriaVO != null ) {
            List<StringBuilder> stringBuilderList = new ArrayList<>();
            StringBuilder sb = getProsperRatingFilter(criteriaVO);
            if ( sb != null && sb.length() > 0 ) { stringBuilderList.add(sb); }
            sb = getPercentFundedFilter(criteriaVO);
            if ( sb != null && sb.length() > 0 ) { stringBuilderList.add(sb); }
            sb = getEstimatedReturnFilter(criteriaVO);
            if ( sb != null && sb.length() > 0 ) { stringBuilderList.add(sb); }
            sb = getBorrowerDebtToIncomeRatio(criteriaVO);
            if ( sb != null && sb.length() > 0 ) { stringBuilderList.add(sb); }
            sb = getVerificationStages(criteriaVO);
            if ( sb != null && sb.length() > 0 ) { stringBuilderList.add(sb); }
            sb = getListingsAlreadyInvestedIn(criteriaVO);
            if ( sb != null && sb.length() > 0 ) { stringBuilderList.add(sb); }

            for ( int i = 0; i < stringBuilderList.size(); i++)
            {
                sb = stringBuilderList.get(i);
                filterString.append(sb);
                if ( i < stringBuilderList.size() - 1)
                {
                    filterString.append(ODATA_AND);
                }

            }
            // safety check
            if ( filterString.length() >= ODATA_AND.length() && filterString.substring(filterString.length() - ODATA_AND.length(), filterString.length()).equals(ODATA_AND))
            {
                filterString.delete(filterString.length() - ODATA_AND.length(), filterString.length()-1);
            }
        }
        String filterQuery = filterString.toString();
        Log.d("TAG", "Filter Query string to use is ["+filterQuery+"]");
        return filterQuery;
    }

    private StringBuilder getBorrowerDebtToIncomeRatio(ListingSearchCriteriaVO criteriaVO) {
        StringBuilder filterString = new StringBuilder();
        if ( criteriaVO.getBorrowerDebtToIncomeRatio() > 0)
        {
            double debtToIncome = Double.valueOf(criteriaVO.getBorrowerDebtToIncomeRatio()) / 100;
            filterString.append("DTIwProsperLoan le ").append(debtToIncome).append("M");
        }
        return filterString;
    }

    private StringBuilder getListingsAlreadyInvestedIn(ListingSearchCriteriaVO criteriaVO) {
        StringBuilder filterString = new StringBuilder();
        if ( !criteriaVO.isIncludeListingsAlreadyInvestedIn() && investmentsVO != null && investmentsVO.getInvestments() != null) {
            for (int i = 0; i < investmentsVO.getInvestments().size(); i++) {
                int listingId = investmentsVO.getInvestments().get(i).getListingId();
                if (i != 0) {
                    filterString.append(ODATA_AND);
                }
                filterString.append("ListingNumber ne ").append(listingId);
            }
            if ( filterString.length() > 0 )
            {
                filterString.insert(0, "(");
                filterString.append(")");
            }
        }
        return filterString;
    }

    private StringBuilder getVerificationStages(ListingSearchCriteriaVO criteriaVO) {
        StringBuilder filterString = new StringBuilder();
        if (criteriaVO.getVerificationStages() != null && criteriaVO.getVerificationStages().size() > 0) {
            for (int i = 0; i < criteriaVO.getVerificationStages().size(); i++) {
                Integer stage = criteriaVO.getVerificationStages().get(i);
                if (i != 0) {
                    filterString.append(ODATA_OR);
                }
                filterString.append("VerificationStage eq ").append(stage);
            }
            if ( filterString.length() > 0 )
            {
                filterString.insert(0, "(");
                filterString.append(")");
            }
        }
        return filterString;
    }

    private StringBuilder getEstimatedReturnFilter(ListingSearchCriteriaVO criteriaVO) {
        StringBuilder filterString = new StringBuilder();
        if ( criteriaVO.getEstimatedReturn() > 0)
        {
            double estimatedReturn = Double.valueOf(criteriaVO.getEstimatedReturn()) / 100;
            filterString.append("EstimatedReturn ge ").append(estimatedReturn).append("M");
        }
        return filterString;
    }

    private StringBuilder getPercentFundedFilter(ListingSearchCriteriaVO criteriaVO) {
        StringBuilder filterString = new StringBuilder();
        if ( criteriaVO.getPercentFunded() > 0)
        {
            double percentFunded = Double.valueOf(criteriaVO.getPercentFunded()) / 100;
            filterString.append("PercentFunded ge ").append(percentFunded).append("M");
        }
        return filterString;
    }

    private StringBuilder getProsperRatingFilter(ListingSearchCriteriaVO criteriaVO) {
        StringBuilder filterString = new StringBuilder();
        if (criteriaVO.getProsperRatings() != null && criteriaVO.getProsperRatings().size() > 0) {
            for (int i = 0; i < criteriaVO.getProsperRatings().size(); i++) {
                String r = criteriaVO.getProsperRatings().get(i);
                if (i != 0) {
                    filterString.append(ODATA_OR);
                }
                filterString.append("ProsperRating eq '").append(r).append("'");
            }
            if ( filterString.length() > 0 )
            {
                filterString.insert(0, "(");
                filterString.append(")");
            }
        }
        return filterString;
    }

    @Override
    protected void onPostExecute(SearchListingVO responseVO) {
        if ( searchCriteriaVO != null && responseVO != null && !responseVO.isEndOfSearch())
        {
            // see if there are any more results against the fetch size we gave
            int listingCount = responseVO.getListings() != null ? responseVO.getListings().size() : 0;
            if ( listingCount < searchCriteriaVO.getFetchSize())
            {
                responseVO.setEndOfSearch(true); // no more to get
            }
        }
        super.onPostExecute(responseVO);
    }

    @Override
    public void onApiResponse(InvestmentsVO data) {
        this.investmentsVO = data;
        execute(createRequest());
    }
}
