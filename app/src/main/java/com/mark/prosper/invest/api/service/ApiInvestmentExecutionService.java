package com.mark.prosper.invest.api.service;

import com.mark.prosper.invest.api.AbstractApiClient;
import com.mark.prosper.invest.api.ApiCallback;
import com.mark.prosper.invest.api.model.AccountVO;
import com.mark.prosper.invest.api.model.ApiRequestVO;
import com.mark.prosper.invest.api.model.InvestmentResultVO;
import com.mark.prosper.invest.api.model.ListingVO;
import com.mark.prosper.invest.api.util.ApiUtil;
import com.mark.prosper.invest.config.ApplicationState;
import com.mark.prosper.invest.config.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeVeX on 9/26/2015.
 */
public class ApiInvestmentExecutionService extends AbstractApiClient<InvestmentResultVO> implements ApiCallback<AccountVO> {

    private final static String INVEST_RESOURCE = "/api/Invest";
    private List<ListingVO> listings;
    private int amountPerListing;
    private List<InvestmentResultVO> resultVOs;

    public ApiInvestmentExecutionService(ApiCallback<InvestmentResultVO> callback) {
        super(callback);
    }

    @Override
    protected InvestmentResultVO doInBackground(ApiRequestVO... apiRequestVOs) {
        if ( listings != null && listings.size() > 0)
        {
            resultVOs = new ArrayList<>();
            for (ListingVO listingVO : listings)
            {
                InvestmentResultVO result = super.doInBackground(createRequest(listingVO.getListingNumber(), amountPerListing));
                if ( result == null)
                {
                    result = new InvestmentResultVO();
                    result.setAmountInvested(0);
                    result.setListingId(listingVO.getListingNumber());
                    result.setMessage("API_ERROR");
                    result.setRequestedAmount(amountPerListing);
                    result.setStatus("FAILED");
                }
                resultVOs.add(result);
            }
        }
        else
        {
            return super.doInBackground(apiRequestVOs);
        }
        return null;
    }

    @Override
    protected void onPostExecute(InvestmentResultVO responseVO) {
        if ( resultVOs != null )
        {
            InvestmentResultVO resultVO = new InvestmentResultVO();
            resultVO.setListingId(0);
            resultVO.setIsPartOfMultiInvestment(true);
            resultVO.setStatus("SUCCESS");
            resultVO.setMessage("MULTIPLE INVESTMENT ORDER - CHECK YOUR ACCOUNT FOR STATUS ON EACH LISTING");
            for ( InvestmentResultVO i : resultVOs)
            {
                if ( i == null ) { continue; }
                resultVO.setAmountInvested(resultVO.getAmountInvested() + i.getAmountInvested());
                resultVO.setRequestedAmount(resultVO.getRequestedAmount() + i.getRequestedAmount());
            }
            super.onPostExecute(resultVO);
        }
        else {
            super.onPostExecute(responseVO);
        }

        if ( resultVOs != null || responseVO != null) {
            // update the account balance
            new ApiAccountService(this).invoke();
        }

    }

    public void invoke(int listingId, int amount)
    {
        if (Configuration.getConfiguration().isUseMockData())
        {
            onPostExecute(InvestmentResultVO.createMock());
        }
        else {
            execute(createRequest(listingId, amount));
        }
    }


    public void invoke(List<ListingVO> listings, int amount)
    {
        this.listings = listings;
        this.amountPerListing = amount;
        if ( Configuration.getConfiguration().isUseMockData())
        {
            onPostExecute(InvestmentResultVO.createMock());
        }
        else {
            execute(createRequest(-123, -10)); // create dummy request
        }
    }

    private ApiRequestVO<InvestmentResultVO> createRequest(int listingId, int amount)
    {
        String postBody = "{ \"listingId\":\""+listingId+"\", \"amount\": \""+amount+"\"}";
        ApiRequestVO<InvestmentResultVO> requestVO =
                new ApiRequestVO<>(
                        INVEST_RESOURCE,
                        null,
                        ApiUtil.createDotNetApiHeaders(),
                        postBody,
                        InvestmentResultVO.class,
                        0,
                        DEFAULT_FETCH_SIZE);
        requestVO.setConnectionTimeoutMS(60000);
        requestVO.setSocketTimeoutMS(60000);
        return requestVO;
    }

    @Override
    public void onApiResponse(AccountVO data) {
        if ( data != null)
        {
            ApplicationState.getState().setAccountVO(data);
        }
    }
}
