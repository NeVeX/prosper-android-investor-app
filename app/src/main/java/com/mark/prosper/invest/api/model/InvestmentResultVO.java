package com.mark.prosper.invest.api.model;

import com.mark.prosper.invest.api.ApiResponse;
import com.mark.prosper.invest.api.util.JSONUtil;
import com.mark.prosper.invest.util.DebugUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by NeVeX on 10/1/2015.
 */
public class InvestmentResultVO implements ApiResponse<InvestmentResultVO>, Serializable {

    private String status;
    private String message;
    private int listingId;
    private double requestedAmount;
    private double amountInvested;
    private boolean isPartOfMultiInvestment = false;


    @Override
    public InvestmentResultVO convertJSONObject(JSONObject jsonObject) throws JSONException {

        InvestmentResultVO investmentResultVO = new InvestmentResultVO();
        investmentResultVO.setStatus(JSONUtil.tryGetString(jsonObject, "Status"));
        investmentResultVO.setMessage(JSONUtil.tryGetString(jsonObject, "Message"));
        investmentResultVO.setListingId(JSONUtil.tryGetInteger(jsonObject, "ListingId"));
        investmentResultVO.setRequestedAmount(JSONUtil.tryGetDouble(jsonObject, "RequestedAmount"));
        investmentResultVO.setAmountInvested(JSONUtil.tryGetDouble(jsonObject, "AmountInvested"));
        return investmentResultVO;
    }

    @Override
    public InvestmentResultVO convertJSONArray(JSONArray jsonArray) throws JSONException {
        return null;
    }

    public boolean isSuccessful()
    {
        if ( status == null )
        {
            return false;
        }
        return status.toLowerCase().contains("success");
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAmountInvested() {
        return amountInvested;
    }

    public void setAmountInvested(double amountInvested) {
        this.amountInvested = amountInvested;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getListingId() {
        return listingId;
    }

    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public boolean isPartOfMultiInvestment() {
        return isPartOfMultiInvestment;
    }

    public void setIsPartOfMultiInvestment(boolean isPartOfMultiInvestment) {
        this.isPartOfMultiInvestment = isPartOfMultiInvestment;
    }

    public static InvestmentResultVO createMock()
    {
        Random r = new Random();
        InvestmentResultVO resultVO = new InvestmentResultVO();
        resultVO.isPartOfMultiInvestment = DebugUtil.getRandomBoolean();
        resultVO.requestedAmount = r.nextInt(200) + 25;
        resultVO.listingId = r.nextInt(5000) + 1;
        resultVO.message = "TEST MESSAGE";
        resultVO.amountInvested = r.nextInt((int)resultVO.getRequestedAmount());
        resultVO.status = DebugUtil.getRandomBoolean() ? "TEST SUCCESS" : "TEST FAILURE";
        return resultVO;
    }


}
