package com.mark.prosper.invest.api.model;

import com.mark.prosper.invest.api.ApiResponse;
import com.mark.prosper.invest.api.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by NeVeX on 9/24/2015.
 */
public class AccountVO implements ApiResponse<AccountVO>, Serializable {

    private double cashBalanceAmount;
    private double pendingInvestmentsAmount;
    private double pendingQIOrdersAmount;
    private double totalInvestedAmount;
    private double totalAccountAmount;
    private double totalReceivedAmount;
    private double pendingInvestmentsSecondaryMarket;
    private double outstandingPrincipalOnActiveNotes;

    @Override
    public AccountVO convertJSONObject(JSONObject jsonObject) throws JSONException {
        AccountVO apiResponse = new AccountVO();
        apiResponse.setCashBalanceAmount(JSONUtil.tryGetDouble(jsonObject, "AvailableCashBalance"));
        apiResponse.setPendingInvestmentsAmount(JSONUtil.tryGetDouble(jsonObject, "PendingInvestmentsPrimaryMkt"));
        apiResponse.setPendingQIOrdersAmount(JSONUtil.tryGetDouble(jsonObject, "PendingQuickInvestOrders"));
        apiResponse.setTotalAccountAmount(JSONUtil.tryGetDouble(jsonObject, "TotalAccountValue"));
        apiResponse.setTotalInvestedAmount(JSONUtil.tryGetDouble(jsonObject, "TotalAmountInvestedOnActiveNotes"));
        apiResponse.setTotalReceivedAmount(JSONUtil.tryGetDouble(jsonObject, "TotalPrincipalReceivedOnActiveNotes"));
        apiResponse.setPendingInvestmentsSecondaryMarket(JSONUtil.tryGetDouble(jsonObject, "PendingInvestmentsSecondaryMkt"));
        apiResponse.setOutstandingPrincipalOnActiveNotes(JSONUtil.tryGetDouble(jsonObject, "OutstandingPrincipalOnActiveNotes"));
        return apiResponse;
    }

    @Override
    public AccountVO convertJSONArray(JSONArray jsonArray) throws JSONException {
        throw new UnsupportedOperationException("JSONArray conversion not supported");
    }

    public double getCashBalanceAmount() {
        return cashBalanceAmount;
    }

    public void setCashBalanceAmount(double cashBalanceAmount) {
        this.cashBalanceAmount = cashBalanceAmount;
    }

    public double getPendingInvestmentsAmount() {
        return pendingInvestmentsAmount;
    }

    public void setPendingInvestmentsAmount(double pendingInvestmentsAmount) {
        this.pendingInvestmentsAmount = pendingInvestmentsAmount;
    }

    public double getPendingQIOrdersAmount() {
        return pendingQIOrdersAmount;
    }

    public void setPendingQIOrdersAmount(double pendingQIOrdersAmount) {
        this.pendingQIOrdersAmount = pendingQIOrdersAmount;
    }

    public double getTotalInvestedAmount() {
        return totalInvestedAmount;
    }

    public void setTotalInvestedAmount(double totalInvestedAmount) {
        this.totalInvestedAmount = totalInvestedAmount;
    }

    public double getTotalAccountAmount() {
        return totalAccountAmount;
    }

    public void setTotalAccountAmount(double totalAccountAmount) {
        this.totalAccountAmount = totalAccountAmount;
    }

    public double getTotalReceivedAmount() {
        return totalReceivedAmount;
    }

    public void setTotalReceivedAmount(double totalReceivedAmount) {
        this.totalReceivedAmount = totalReceivedAmount;
    }

    public double getPendingInvestmentsSecondaryMarket() {
        return pendingInvestmentsSecondaryMarket;
    }

    public void setPendingInvestmentsSecondaryMarket(double pendingInvestmentsSecondaryMarket) {
        this.pendingInvestmentsSecondaryMarket = pendingInvestmentsSecondaryMarket;
    }

    public double getOutstandingPrincipalOnActiveNotes() {
        return outstandingPrincipalOnActiveNotes;
    }

    public void setOutstandingPrincipalOnActiveNotes(double outstandingPrincipalOnActiveNotes) {
        this.outstandingPrincipalOnActiveNotes = outstandingPrincipalOnActiveNotes;
    }

    public static AccountVO createMock()
    {
        Random r = new Random();
        AccountVO accountVO = new AccountVO();
        accountVO.setCashBalanceAmount(r.nextDouble() * r.nextInt(60000));
        accountVO.setPendingInvestmentsAmount(r.nextDouble() * r.nextInt(60000));
        accountVO.setPendingQIOrdersAmount(r.nextDouble() * r.nextInt(60000));
        accountVO.setTotalAccountAmount(r.nextDouble() * r.nextInt(60000));
        accountVO.setTotalInvestedAmount(r.nextDouble() * r.nextInt(60000));
        accountVO.setTotalReceivedAmount(r.nextDouble() * r.nextInt(60000));
        accountVO.setPendingInvestmentsSecondaryMarket(r.nextDouble() * r.nextInt(60000));
        accountVO.setOutstandingPrincipalOnActiveNotes(r.nextDouble() * r.nextInt(60000));
        return accountVO;
    }

}
